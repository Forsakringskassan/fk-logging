package se.fk.github.logging.callerinfo.filter;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.model.MDCKeys;

import java.io.IOException;
import java.util.Optional;

@Provider
public class LoggingContextClientResponseFilter implements ClientResponseFilter
{
   private static final Logger LOG = LoggerFactory.getLogger(LoggingContextClientResponseFilter.class);

   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
   {
      logAndGetResponseHeader(HeaderTyp.BREADCRUMB_ID, requestContext, responseContext)
            .ifPresent(s -> {
               MDC.put(MDCKeys.BREADCRUMBID.name(), s);
            });
      logAndGetResponseHeader(HeaderTyp.PROCESSID, requestContext, responseContext)
            .ifPresent(s -> MDC.put(MDCKeys.PROCESSID.name(), s));
   }

   private Optional<String> logAndGetResponseHeader(HeaderTyp headerTyp, ClientRequestContext requestContext,
         ClientResponseContext responseContext)
   {
      Optional<String> requestBreadcrumb = findHeader(requestContext.getStringHeaders(), headerTyp);
      Optional<String> responseBreadcrumb = findHeader(responseContext.getHeaders(), headerTyp);
      logIfNotEqual(headerTyp, requestBreadcrumb, responseBreadcrumb);
      return responseBreadcrumb;
   }

   private Optional<String> findHeader(MultivaluedMap<String, String> headers, HeaderTyp headerTyp)
   {
      String found = headers.getFirst(headerTyp.value());
      if (found == null || found.isEmpty())
      {
         return Optional.empty();
      }
      return Optional.of(found);
   }

   private void logIfNotEqual(HeaderTyp headerTyp, Optional<String> requestBreadcrumb, Optional<String> responseBreadcrumb)
   {
      if (requestBreadcrumb.isEmpty() || responseBreadcrumb.isEmpty())
      {
         return;
      }
      if (requestBreadcrumb.get().equalsIgnoreCase(responseBreadcrumb.get()))
      {
         return;
      }
      LOG.info("Sent request with {} {} got response with {} {}", headerTyp, requestBreadcrumb.get(), headerTyp,
            responseBreadcrumb.get());
   }
}
