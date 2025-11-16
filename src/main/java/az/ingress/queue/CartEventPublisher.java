package az.ingress.queue;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CartEventPublisher {

    private final AmqpTemplate amqpTemplate;

    public void publishCartChanged(Long cartId, Long buyerId) {
        amqpTemplate.convertAndSend("cart.exchange","cart.changed",
                Map.of("cartId", cartId, "buyerId", buyerId, "ts", System.currentTimeMillis()));
    }
}