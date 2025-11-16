package az.ingress.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AspectLogging {

    @Around("@within(az.ingress.aop.ToLog) || @annotation(az.ingress.aop.ToLog)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();

        ToLog cfg = Optional.ofNullable(method.getAnnotation(ToLog.class))
                .orElse(Optional.ofNullable(method.getDeclaringClass().getAnnotation(ToLog.class)).orElse(null));


        if (cfg == null) return pjp.proceed();

        String methodName = sig.getDeclaringType().getSimpleName() + "." + sig.getName();
        long start = System.currentTimeMillis();

        if (cfg.logArgs()) {
            logAt(cfg.level(), () -> "→ " + methodName + " args=" + Arrays.toString(pjp.getArgs()));
        } else {
            logAt(cfg.level(), () -> "→ " + methodName);
        }

        try {
            Object result = pjp.proceed();
            long took = System.currentTimeMillis() - start;

            if (cfg.logResult()) {
                logAt(cfg.level(), () -> "← " + methodName + " result=" + truncate(result) + " took=" + took + "ms");
            } else if (cfg.logExecutionTime()) {
                logAt(cfg.level(), () -> "← " + methodName + " took=" + took + "ms");
            }
            return result;
        } catch (Throwable ex) {
            long took = System.currentTimeMillis() - start;
            log.error("✖ {} failed after {}ms: {}", methodName, took, ex.toString(), ex);
            throw ex;
        }
    }

    private void logAt(ToLog.Level level, Supplier<String> msg) {
        switch (level) {
            case TRACE -> { if (log.isTraceEnabled()) log.trace(msg.get()); }
            case DEBUG -> { if (log.isDebugEnabled()) log.debug(msg.get()); }
            case INFO  -> { if (log.isInfoEnabled()) log.info(msg.get()); }
            case WARN  -> { if (log.isWarnEnabled()) log.warn(msg.get()); }
        }
    }

    private String truncate(Object obj) {
        String s = String.valueOf(obj);
        return s.length() > 500 ? s.substring(0, 500) + "…" : s; // чтобы не зафлудить логи
    }
}