package az.ingress.queue;

import az.ingress.dao.entity.CartEntity;
import az.ingress.dao.entity.CartItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

        List<CartItemEntity> items = cart.getItems() == null
                ? List.of()
                : List.copyOf(cart.getItems());

        int itemsCount = items.size();

        long totalQty = items.stream()
                .map(CartItemEntity::getQuantity)  // Integer
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();

        BigDecimal amount = BigDecimal.ZERO;

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