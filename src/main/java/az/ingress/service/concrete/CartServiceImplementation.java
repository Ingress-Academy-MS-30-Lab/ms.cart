package az.ingress.service.concrete;

import az.ingress.dao.entity.CartEntity;
import az.ingress.dao.entity.CartItemEntity;
import az.ingress.dao.repository.CartItemRepository;
import az.ingress.dao.repository.CartRepository;
import az.ingress.mapper.CartItemMapper;
import az.ingress.mapper.CartMapper;
import az.ingress.mapper.CartResponseMapper;
import az.ingress.model.dto.CartCreateDto;
import az.ingress.model.dto.ProductSnapshotDto;
import az.ingress.model.request.AddCartItemRequest;
import az.ingress.model.request.UpdateCartItemRequest;
import az.ingress.model.response.CartResponse;
import az.ingress.service.abstraction.CartService;
import az.ingress.service.abstraction.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import static az.ingress.model.enums.CartStatus.CREATED;
import static az.ingress.model.enums.CartStatus.DELETED;
import static az.ingress.model.enums.CartStatus.ORDERED;

@Service
@RequiredArgsConstructor
public class CartServiceImplementation implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartResponseMapper cartResponseMapper;


    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long buyerId) {
        CartEntity cart = findActiveCartOrThrow(buyerId);
        return cartResponseMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(Long buyerId, AddCartItemRequest request) {

        CartEntity cart = cartRepository
                .findByBuyerIdAndStatusNot(buyerId, DELETED)
                .orElseGet(() -> createNewCart(buyerId));

        CartItemEntity item = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId())
                .orElse(null);

        if (item != null) {

            long newQty = item.getQty() == null ? 0 : item.getQty();
            newQty += Math.max(1, request.getQty());
            item.setQty(newQty);
        } else {

            ProductSnapshotDto snapshot =
                    productClient.getVariantSnapshot(request.getProductVariantId());


            CartItemEntity newItem = cartItemMapper.toEntity(request, snapshot);
            newItem.setCart(cart);

            cartItemRepository.save(newItem);
        }

        updateCartStatusAfterChange(cart,cart.getCreatedAt() == null);

        CartEntity saved = cartRepository.save(cart);
        return cartResponseMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long buyerId, Long productVariantId, UpdateCartItemRequest request) {
        CartEntity cart = findActiveCartOrThrow(buyerId);

        CartItemEntity item = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), productVariantId)
                .orElseThrow(() -> new IllegalStateException("Cart item not found"));

        long qty = request.getQty();

        if (qty <= 0) {

            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQty(qty);
        }


        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            markCartDeleted(cart);
        } else {
            cart.setStatus(ORDERED);
        }

        CartEntity saved = cartRepository.save(cart);
        return cartResponseMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long buyerId, Long productVariantId) {
        CartEntity cart = findActiveCartOrThrow(buyerId);

        CartItemEntity item = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), productVariantId)
                .orElseThrow(() -> new IllegalStateException("Cart item not found"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            markCartDeleted(cart);
        } else {
            cart.setStatus(ORDERED);
        }

        CartEntity saved = cartRepository.save(cart);
        return cartResponseMapper.toResponse(saved);
    }



    private CartEntity createNewCart(Long buyerId) {
        CartCreateDto dto = CartCreateDto.builder()
                .buyerId(buyerId)
                .build();
        CartEntity cart = cartMapper.toEntity(dto);

        return cartRepository.save(cart);
    }

    private CartEntity findActiveCartOrThrow(Long buyerId) {
        return cartRepository
                .findByBuyerIdAndStatusNot(buyerId, DELETED)
                .orElseThrow(() -> new IllegalStateException("Cart not found"));
    }



    private void updateCartStatusAfterChange(CartEntity cart, boolean isNewCart) {
        if (isNewCart) {
            cart.setStatus(CREATED);
        } else if (cart.getStatus() != DELETED) {
            cart.setStatus(ORDERED);
        }
    }

    private void markCartDeleted(CartEntity cart) {
        cart.setStatus(DELETED);
        cart.setDeletedAt(LocalDateTime.now());
    }
}
