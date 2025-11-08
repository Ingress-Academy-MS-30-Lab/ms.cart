package az.ingress.model.response;

import az.ingress.model.enums.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long id;
    private Long buyerId;
    private CartStatus status;
    private List<CartItemResponse> items;
    private CartTotalResponse totals;
}
