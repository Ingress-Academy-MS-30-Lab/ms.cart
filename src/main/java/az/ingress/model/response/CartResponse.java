package az.ingress.model.response;

import az.ingress.model.dto.CartItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long cartId;
    private Long buyerId;
    private String status;
    private List<CartItemDto> items;       // <-- вместо CartItemResponse
    private CartTotalResponse total;
}