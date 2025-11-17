package az.ingress.controller;

import az.ingress.model.request.AddCartItemRequest;
import az.ingress.model.request.UpdateCartItemRequest;
import az.ingress.model.response.CartResponse;
import az.ingress.service.abstraction.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{buyerId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long buyerId) {
        return ResponseEntity.ok(cartService.getCart(buyerId));
    }

    @PostMapping("/{buyerId}/items")
    public ResponseEntity<Void> addItem(@PathVariable Long buyerId,
                                        @Valid @RequestBody AddCartItemRequest request) {
        cartService.addItem(buyerId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{buyerId}/items/{variantId}")
    public ResponseEntity<Void> updateItem(@PathVariable Long buyerId,
                                           @PathVariable Long variantId,
                                           @Valid @RequestBody UpdateCartItemRequest request) {
        cartService.updateItem(buyerId, variantId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{buyerId}/items/{variantId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long buyerId,
                                           @PathVariable Long variantId) {
        cartService.removeItem(buyerId, variantId);
        return ResponseEntity.noContent().build();
    }
}