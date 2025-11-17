package az.ingress.queue;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartChangedEvent {
    private Long cartId;
    private Long buyerId;
    private Action action;
    private Integer itemsCount;
    private Long totalQty;
    private BigDecimal amount;
    private LocalDateTime occurredAt;

    public enum Action { ADDED, UPDATED, REMOVED, CLEARED, DELETED }
}
