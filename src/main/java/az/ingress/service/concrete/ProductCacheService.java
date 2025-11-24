package az.ingress.service.concrete;

import az.ingress.client.ProductClient;
import az.ingress.model.dto.ProductSnapshotDto;
import az.ingress.model.response.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private static final String KEY = "pv:%d";
    private static final Duration TTL = Duration.ofMinutes(20);

    private final RedissonClient redisson;
    private final ProductClient productClient;

    public ProductSnapshotDto getOrLoad(Long variantId) {
        String key = KEY.formatted(variantId);
        RBucket<ProductSnapshotDto> bucket = redisson.getBucket(key);

        var cached = bucket.get();
        if (cached != null) {
            return cached;
        }

        var clientResp = productClient.getVariant(variantId);
        var snap = toSnapshot(clientResp);
        if (snap != null) {
            bucket.set(snap, TTL);
        }
        return snap;
    }


    private ProductSnapshotDto toSnapshot(ProductResponseDto r) {
        if (r == null) {
            return null;
        }
        return ProductSnapshotDto.builder()
                .productId(r.getProductId())
                .productVariantId(r.getProductVariantId())
                .build();
    }
}