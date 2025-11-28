package az.ingress.service.abstraction;

import az.ingress.model.request.AddCartItemRequest;
import az.ingress.model.request.UpdateCartItemRequest;
import az.ingress.model.response.CartResponse;

public interface CartService {

    CartResponse getCart(Long buyerId);

    void addItem(Long buyerId, AddCartItemRequest request);

    void updateItem(Long buyerId, Long productVariantId, UpdateCartItemRequest request);

    void removeItem(Long buyerId, Long productVariantId);
}
