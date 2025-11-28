package az.ingress.aop;

import az.ingress.logger.ApplicationLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AspectLogging {

    @Around("within(@az.ingress.aop.ToLog *) || @annotation(az.ingress.aop.ToLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        var methodName = joinPoint.getSignature().getName();
        var params = Arrays.toString(joinPoint.getArgs());
        var log = ApplicationLogger.getLogger(joinPoint.getTarget().getClass());

        try {
            log.info("ActionLog.{}.start {}", methodName, params);

            var response = joinPoint.proceed();

            if (response != null) {
                log.info("ActionLog.{}.end response {}", methodName, response);
            } else {
                log.info("ActionLog.{}.end", methodName);
            }

            return response;
        } catch (Throwable throwable) {
            log.error("ActionLog.{}.error ", methodName, throwable);
            throw throwable;
        }
    }
}