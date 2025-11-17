package az.ingress.scheduler;

import az.ingress.service.concrete.ProductCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationsRefreshJob {

    private final ProductCacheService productCacheService;

    @Scheduled(cron = "${jobs.recommendations.cron:0 */10 * * * *}")
    @SchedulerLock(name = "recommendations_refresh", lockAtLeastFor = "PT5S", lockAtMostFor = "PT30S")
    public void refresh() {
        log.info("Recalculating recommendations and refreshing cache...");

        log.info("Recalculation finished.");
    }
}