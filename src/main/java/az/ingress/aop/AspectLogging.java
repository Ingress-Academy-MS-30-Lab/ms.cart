package az.ingress.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class AspectLogging {

    @Around("@annotation(az.ingress.aop.ToLog)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String signature = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();
        log.info("-> {} args={}", signature, Arrays.toString(args));
        try {
            Object result = pjp.proceed();
            log.info("<- {} took={}ms", signature, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.error("xx {} failed after {}ms: {}", signature, System.currentTimeMillis() - start, t.getMessage(), t);
            throw t;
        }
    }
}