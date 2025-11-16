package az.ingress.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


public final class ErrorMessage {
    private ErrorMessage() {}

    public static final String CART_ITEM_NOT_FOUND = "Cart item not found";
    public static final String CART_NOT_FOUND      = "Cart not found";
}