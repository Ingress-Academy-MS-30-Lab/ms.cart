package az.ingress.service.abstraction;

import az.ingress.model.request.AddCartItemRequest;
import az.ingress.model.request.UpdateCartItemRequest;
import az.ingress.model.response.CartResponse;

public interface CartService {

    CartResponse getCart(Long buyerId);

    CartResponse addItem(Long buyerId, AddCartItemRequest request);

    CartResponse updateItem(Long buyerId, Long productVariantId, UpdateCartItemRequest request);

    CartResponse removeItem(Long buyerId, Long productVariantId);

}
