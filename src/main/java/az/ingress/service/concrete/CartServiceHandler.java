package az.ingress.service.concrete;

import az.ingress.aop.ToLog;
import az.ingress.dao.entity.CartEntity;
import az.ingress.dao.entity.CartItemEntity;
import az.ingress.dao.repository.CartItemRepository;
import az.ingress.dao.repository.CartRepository;
import az.ingress.exception.NotFoundException;
import az.ingress.mapper.CartResponseMapper;
import az.ingress.model.enums.CartStatus;
import az.ingress.model.request.AddCartItemRequest;
import az.ingress.model.request.UpdateCartItemRequest;
import az.ingress.model.response.CartResponse;
import az.ingress.service.abstraction.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static az.ingress.exception.ErrorMessage.CART_NOT_FOUND;

@ToLog(level = ToLog.Level.INFO, logArgs = true, logResult = false)
@Service
@RequiredArgsConstructor
public class CartServiceHandler implements CartService {

    private static final String CART_ITEM_NOT_FOUND = "Cart item not found";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartResponseMapper cartResponseMapper;
    private final CartCacheService cartCacheService;

    @ToLog
    @Override
    public CartResponse getCart(Long buyerId) {
        var cached = cartCacheService.get(buyerId);
        if (cached != null) return cached;

        var cart = findActiveCartOrThrow(buyerId);
        var resp = cartResponseMapper.toResponse(cart);
        cartCacheService.put(buyerId, resp);
        return resp;
    }

    @ToLog
    @Override
    public void addItem(Long buyerId, AddCartItemRequest request) {
        var cart = findOrCreateActiveCart(buyerId);

        var itemOpt = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId());

        if (itemOpt.isPresent()) {
            var item = itemOpt.get();
            long base = item.getQty() == null ? 0 : item.getQty();
            long newQty = Math.max(1, base + request.getQty());
            item.setQty(newQty);
            cartItemRepository.save(item);
        } else {
            var newItem = new CartItemEntity();
            newItem.setCart(cart);
            newItem.setProductId(request.getProductId());
            newItem.setProductVariantId(request.getProductVariantId());
            newItem.setQty(Math.max(1, request.getQty()));
            cartItemRepository.save(newItem);
        }

        evictCartCache(buyerId);
    }

    @ToLog
    @Override
    public void updateItem(Long buyerId, Long productVariantId, UpdateCartItemRequest request) {
        var cart = findActiveCartOrThrow(buyerId);
        var item = findItemOrThrow(cart.getId(), productVariantId);

        long qty = request.getQty();
        if (qty <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQty(qty);
            cartItemRepository.save(item);
        }

        evictCartCache(buyerId);
    }

    @ToLog
    @Override
    public void removeItem(Long buyerId, Long productVariantId) {
        var cart = findActiveCartOrThrow(buyerId);
        var item = findItemOrThrow(cart.getId(), productVariantId);

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        evictCartCache(buyerId);
    }

    private void evictCartCache(Long buyerId) {
        cartCacheService.evict(buyerId);
    }

    private CartEntity findActiveCartOrThrow(Long buyerId) {
        return cartRepository.findByBuyerIdAndStatusNot(buyerId, CartStatus.DELETED)
                .orElseThrow(() -> new NotFoundException(CART_NOT_FOUND));
    }

    private CartEntity findOrCreateActiveCart(Long buyerId) {
        return cartRepository.findByBuyerIdAndStatusNot(buyerId, CartStatus.DELETED)
                .orElseGet(() -> {
                    var c = new CartEntity();
                    c.setBuyerId(buyerId);
                    c.setStatus(CartStatus.CREATED);
                    return cartRepository.save(c);
                });
    }

    private CartItemEntity findItemOrThrow(Long cartId, Long variantId) {
        return cartItemRepository.findByCartIdAndProductVariantId(cartId, variantId)
                .orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_FOUND));
    }
}