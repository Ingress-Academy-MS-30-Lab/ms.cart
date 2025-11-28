package az.ingress.scheduler;

import az.ingress.service.concrete.ProductCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationsRefreshJob {

    private final ProductCacheService productCacheService;

    @Scheduled(cron = "${jobs.recommendations.cron:0 */10 * * * *}")
    @SchedulerLock(
            name = "recommendations_refresh",
            lockAtLeastFor = "PT5S",
            lockAtMostFor = "PT30S"
    )
    public void refresh() {
        log.info("Recalculating recommendations and refreshing cache...");

        List<Long> popularVariantIds = List.of(1L, 2L, 3L);

        popularVariantIds.forEach(variantId -> {
            try {
                var snapshot = productCacheService.getOrLoad(variantId);
                log.debug("Cached product variant {} => {}", variantId, snapshot);
            } catch (Exception ex) {
                log.warn("Failed to refresh cache for variantId={}", variantId, ex);
            }
        });

        log.info("Recalculation finished.");
    }
}