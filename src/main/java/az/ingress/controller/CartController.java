package az.ingress.controller;

import az.ingress.model.request.AddCartItemRequest;
import az.ingress.model.request.UpdateCartItemRequest;
import az.ingress.model.response.CartResponse;
import az.ingress.service.abstraction.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{buyerId}")
    public CartResponse getCart(@PathVariable Long buyerId) {
        return cartService.getCart(buyerId);
    }

    @PostMapping("/{buyerId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@PathVariable Long buyerId,
                        @RequestBody @Valid AddCartItemRequest request) {
        cartService.addItem(buyerId, request);
    }

    @PutMapping("/{buyerId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateItem(@PathVariable Long buyerId,
                           @PathVariable Long itemId,
                           @RequestBody @Valid UpdateCartItemRequest request) {
        cartService.updateItem(buyerId, itemId, request);
    }

    @DeleteMapping("/{buyerId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long buyerId,
                           @PathVariable Long itemId) {
        cartService.removeItem(buyerId, itemId);
    }
}