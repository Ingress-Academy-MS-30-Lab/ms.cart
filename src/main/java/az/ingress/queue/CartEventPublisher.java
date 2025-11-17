package az.ingress.queue;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CartEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;
    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public void publishCartChanged(CartEntity cart, CartChangedEvent.Action action) {
        var evt = buildEvent(cart, action);
        rabbitTemplate.convertAndSend(exchange, routingKey, evt);
    }

    private CartChangedEvent buildEvent(CartEntity cart, CartChangedEvent.Action action) {
        List<CartItemEntity> items = cart.getItems() == null ? List.of() : cart.getItems().stream().toList();

        int itemsCount = items.size();
        long totalQty = items.stream()
                .map(CartItemEntity::getQty)
                .filter(q -> q != null)
                .mapToLong(Long::longValue)
                .sum();

        BigDecimal amount = items.stream()
                .map(i -> {
                    var price = i.getUnitPriceSnapshot() == null ? BigDecimal.ZERO : i.getUnitPriceSnapshot();
                    var qty   = i.getQty() == null ? 0L : i.getQty();
                    return price.multiply(BigDecimal.valueOf(qty));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartChangedEvent.builder()
                .cartId(cart.getId())
                .buyerId(cart.getBuyerId())
                .action(action)
                .itemsCount(itemsCount)
                .totalQty(totalQty)
                .amount(amount)
                .occurredAt(LocalDateTime.now())
                .build();
    }
}