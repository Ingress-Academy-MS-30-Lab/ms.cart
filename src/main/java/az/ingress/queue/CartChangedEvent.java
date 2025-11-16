package az.ingress.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartChangedEvent {
    public enum Operation { ADD, UPDATE, REMOVE }

    private Long cartId;
    private Long buyerId;
    private Long productVariantId;
    private Long qty;
    private Operation operation;
    private Instant occurredAt;
}
