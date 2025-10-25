package se.fk.github.logging.callerinfo.filter;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fk.github.logging.callerinfo.service.MDCPopulatorService;

@Interceptor
@PopulateLoggingContext
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 1) // Run after incoming interceptor
public class LoggingContextOutgoingMessageInterceptor
{
   private static final Logger LOG = LoggerFactory.getLogger(LoggingContextOutgoingMessageInterceptor.class);

   @Inject
   MDCPopulatorService mdcPopulatorService;

   @AroundInvoke
   public Object intercept(InvocationContext context) throws Exception
   {
      Object result = context.proceed();

      if (result instanceof Message)
      {
         result = addHeadersToMessage((Message<?>) result);
      }

      LOG.debug("Sending");

      return result;
   }

   private <T> Message<T> addHeadersToMessage(Message<T> message)
   {
      try
      {
         RecordHeaders headers = new RecordHeaders();
         MDCHeaders.addMdcHeaders(headers);

         OutgoingKafkaRecordMetadata<?> metadata = OutgoingKafkaRecordMetadata.builder()
               .withHeaders(headers)
               .build();

         return message.addMetadata(metadata);
      }
      catch (Exception e)
      {
         LOG.warn("Failed to add logging context headers to outgoing message", e);
         return message;
      }
   }
}
