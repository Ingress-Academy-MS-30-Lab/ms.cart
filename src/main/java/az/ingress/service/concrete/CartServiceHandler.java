package az.ingress.service.concrete;

import az.ingress.aop.ToLog;
import az.ingress.dao.entity.CartEntity;
import az.ingress.dao.entity.CartItemEntity;
import az.ingress.dao.repository.CartItemRepository;
import az.ingress.dao.repository.CartRepository;
import az.ingress.exception.NotFoundException;
import az.ingress.mapper.CartItemMapper;
import az.ingress.mapper.CartResponseMapper;
import az.ingress.model.dto.CartItemDto;
import az.ingress.model.enums.CartStatus;
import az.ingress.model.request.AddCartItemRequest;
import az.ingress.model.request.UpdateCartItemRequest;
import az.ingress.model.response.CartResponse;
import az.ingress.model.response.CartTotalResponse;
import az.ingress.queue.CartChangedEvent;
import az.ingress.queue.CartEventPublisher;
import az.ingress.service.abstraction.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToLog(level = ToLog.Level.INFO)
@Service
@RequiredArgsConstructor
public class CartServiceHandler implements CartService {

    private static final String CART_NOT_FOUND      = "Cart not found";
    private static final String CART_ITEM_NOT_FOUND = "Cart item not found";

    private final CartRepository      cartRepository;
    private final CartItemRepository  cartItemRepository;
    private final CartResponseMapper  cartResponseMapper;
    private final CartCacheService    cartCacheService;
    private final ProductCacheService productCacheService;
    private final CartItemMapper      cartItemMapper;
    private final CartEventPublisher  cartEventPublisher;


    @Override
    public CartResponse getCart(Long buyerId) {
        var cached = cartCacheService.get(buyerId);
        if (cached != null) {
            return cached;
        }

        var cart = findActiveCartOrThrow(buyerId);

        List<CartItemDto> items = buildItemDtos(cart);
        var total = buildTotals(cart);

        var resp = cartResponseMapper.toResponse(cart, items, total);
        cartCacheService.put(buyerId, resp);
        return resp;
    }

    @Override
    public void addItem(Long buyerId, AddCartItemRequest request) {
        var cart = findOrCreateActiveCart(buyerId);

        Optional<CartItemEntity> itemOpt =
                cartItemRepository.findByCartIdAndProductVariantId(
                        cart.getId(),
                        request.getProductVariantId()
                );

        if (itemOpt.isPresent()) {
            var item = itemOpt.get();

            long baseQty = (item.getQuantity() == null) ? 0L : item.getQuantity();
            long reqQty  = (request.getQuantity() == null) ? 0L : request.getQuantity();

            long newQty = Math.toIntExact(Math.max(1L, baseQty + reqQty));
            item.setQuantity(newQty);

            cartItemRepository.save(item);
        } else {

            productCacheService.getOrLoad(request.getProductVariantId());

            var newItem = cartItemMapper.toEntity(request);
            newItem.setCart(cart);
            cartItemRepository.save(newItem);
        }

        evictCartCache(buyerId);
        cartEventPublisher.publishCartChanged(cart, CartChangedEvent.Action.ADDED);
    }

    @Override
    public void updateItem(Long buyerId, Long productVariantId, UpdateCartItemRequest request) {
        var cart = findActiveCartOrThrow(buyerId);
        var item = findItemOrThrow(cart.getId(), productVariantId);

        long reqQty = (request.getQuantity() == null) ? 0L : request.getQuantity();

        if (reqQty <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(reqQty);
            cartItemRepository.save(item);
        }

        evictCartCache(buyerId);
        cartEventPublisher.publishCartChanged(cart, CartChangedEvent.Action.UPDATED);
    }

    @Override
    public void removeItem(Long buyerId, Long productVariantId) {
        var cart  = findActiveCartOrThrow(buyerId);
        var item = findItemOrThrow(cart.getId(), productVariantId);

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        evictCartCache(buyerId);
        cartEventPublisher.publishCartChanged(cart, CartChangedEvent.Action.REMOVED);
    }


    private void evictCartCache(Long buyerId) {
        cartCacheService.evict(buyerId);
    }

    private CartEntity findActiveCartOrThrow(Long buyerId) {
        return cartRepository
                .findByBuyerIdAndStatusNot(buyerId, CartStatus.DELETED)
                .orElseThrow(() -> new NotFoundException(CART_NOT_FOUND));
    }

    private CartEntity findOrCreateActiveCart(Long buyerId) {
        return cartRepository
                .findByBuyerIdAndStatusNot(buyerId, CartStatus.DELETED)
                .orElseGet(() -> {
                    CartEntity c = new CartEntity();
                    c.setBuyerId(buyerId);
                    c.setStatus(CartStatus.CREATED);
                    return cartRepository.save(c);
                });
    }

    private CartItemEntity findItemOrThrow(Long cartId, Long variantId) {
        return cartItemRepository
                .findByCartIdAndProductVariantId(cartId, variantId)
                .orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_FOUND));
    }


    private List<CartItemDto> buildItemDtos(CartEntity cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return List.of();
        }

        List<CartItemDto> result = new ArrayList<>();
        for (CartItemEntity item : cart.getItems()) {
            CartItemDto dto = CartItemDto.builder()
                    .productId(item.getProductId())
                    .productVariantId(item.getProductVariantId())
                    .quantity(item.getQuantity() == null ? 0L : item.getQuantity().longValue())
                    .build();
            result.add(dto);
        }
        return result;
    }

    private CartTotalResponse buildTotals(CartEntity cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return CartTotalResponse.builder()
                    .itemsCount(0)
                    .totalQty(0L)
                    .amount(BigDecimal.ZERO)
                    .build();
        }

        List<CartItemEntity> items = new ArrayList<>(cart.getItems());

        int itemsCount = items.size();

        long totalQty = items.stream()
                .map(CartItemEntity::getQuantity)
                .filter(q -> q != null)
                .mapToLong(Long::longValue)
                .sum();

        BigDecimal amount = BigDecimal.ZERO;

        return CartTotalResponse.builder()
                .itemsCount(itemsCount)
                .totalQty(totalQty)
                .amount(amount)
                .build();
    }
}