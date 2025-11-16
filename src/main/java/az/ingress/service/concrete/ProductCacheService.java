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
    private final ProductSnapshotMapper mapper; // MapStruct: ProductResponseDto -> ProductSnapshotDto

    public ProductSnapshotDto getOrLoad(Long variantId) {
        String key = KEY.formatted(variantId);
        RBucket<ProductSnapshotDto> bucket = redisson.getBucket(key);

        ProductSnapshotDto cached = bucket.get();
        if (cached != null) return cached;

        ProductResponseDto clientResp = productClient.getVariant(variantId);
        ProductSnapshotDto snap = mapper.toSnapshot(clientResp);
        if (snap != null) bucket.set(snap, TTL);
        return snap;
    }
}