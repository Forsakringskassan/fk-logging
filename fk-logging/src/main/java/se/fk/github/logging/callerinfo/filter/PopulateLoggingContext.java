package se.fk.github.logging.callerinfo.filter;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Automatically populates Kafka headers in outgoing messages with the current logging context.
 * This interceptor activates for all methods annotated with @PopulateLoggingContext.
 * Use with @Outgoing, @Incoming, or methods that return Message objects to propagate logging context.
 */
@InterceptorBinding
@Target(
{
      ElementType.TYPE, ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface PopulateLoggingContext
{
}
