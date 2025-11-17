package az.ingress.service.concrete;

import az.ingress.model.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CartCacheService {
    private static final String KEY = "cart:active:%d";
    private static final Duration TTL = Duration.ofMinutes(30);

    private final RedissonClient redisson;

    public CartResponse get(Long buyerId) {
        return bucket(buyerId).get();
    }

    public void put(Long buyerId, CartResponse value) {
        bucket(buyerId).set(value, TTL);
    }

    public void evict(Long buyerId) {
        bucket(buyerId).delete();
    }

    private RBucket<CartResponse> bucket(Long buyerId) {
        return redisson.getBucket(KEY.formatted(buyerId));
    }
}