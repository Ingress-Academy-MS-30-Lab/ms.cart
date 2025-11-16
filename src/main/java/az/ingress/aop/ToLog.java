package az.ingress.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToLog {
    Level level() default Level.INFO;
    boolean logArgs() default true;
    boolean logResult() default false;
    boolean logExecutionTime() default true;

    enum Level { TRACE, DEBUG, INFO, WARN }
}