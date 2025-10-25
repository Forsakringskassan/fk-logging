package se.fk.github.logging.callerinfo.filter;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.apache.kafka.common.header.Headers;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fk.github.logging.callerinfo.model.FKLoggingContext;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.model.ImmutableFKLoggingContext;
import se.fk.github.logging.callerinfo.service.MDCPopulatorService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import org.apache.kafka.common.header.Header;

@Interceptor
@PopulateLoggingContext
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
public class LoggingContextIncomingMessageInterceptor
{
   private static final Logger LOG = LoggerFactory.getLogger(LoggingContextIncomingMessageInterceptor.class);

   @Inject
   MDCPopulatorService mdcPopulatorService;

   @AroundInvoke
   public Object intercept(InvocationContext context) throws Exception
   {
      for (Object param : context.getParameters())
      {
         if (param instanceof Message)
         {
            populateContext((Message<?>) param);
            break;
         }
      }

      LOG.debug("Receiving");

      return context.proceed();
   }

   private void populateContext(Message<?> message)
   {
      try
      {
         final String processId = extractHeader(message, HeaderTyp.PROCESSID.value());
         final String breadcrumbId = extractHeader(message, HeaderTyp.BREADCRUMB_ID.value());

         FKLoggingContext fkContext = ImmutableFKLoggingContext.builder()
               .breadcrumbId(Optional.ofNullable(breadcrumbId))
               .processId(Optional.ofNullable(processId))
               .build();

         mdcPopulatorService.setContext(fkContext);
      }
      catch (Exception e)
      {
         // Silently fail - don't break message processing
         LOG.warn("Failed to populate logging context from message", e);
      }
   }

   private String extractHeader(Message<?> message, String headerName)
   {
      try
      {
         Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
         if (metadata.isPresent())
         {
            Headers headers = metadata.get().getHeaders();
            Header header = headers.lastHeader(headerName);
            if (header != null && header.value() != null)
            {
               return new String(header.value(), StandardCharsets.UTF_8);
            }
         }
      }
      catch (Exception e)
      {
         // Silently fail - don't break message processing
         LOG.warn("Could not extract Kafka header " + headerName, e);
      }
      return null;
   }
}
