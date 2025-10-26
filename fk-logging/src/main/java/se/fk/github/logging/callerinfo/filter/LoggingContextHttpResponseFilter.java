package se.fk.github.logging.callerinfo.filter;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.model.MDCKeys;
import se.fk.github.logging.callerinfo.service.MDCPopulatorService;

import java.io.IOException;

@Provider
@Priority(1)
public class LoggingContextHttpResponseFilter implements ContainerResponseFilter
{
   @Inject
   MDCPopulatorService mdcPopulatorService;

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
   {
      addIfNotPresent(responseContext.getHeaders(), HeaderTyp.BREADCRUMB_ID, MDCKeys.BREADCRUMBID);
      addIfNotPresent(responseContext.getHeaders(), HeaderTyp.PROCESSID, MDCKeys.PROCESSID);
      mdcPopulatorService.clearMdc();
   }

   private void addIfNotPresent(MultivaluedMap<String, Object> headers, HeaderTyp header, MDCKeys mdcKey)
   {
      if (headers.containsKey(header.value()))
      {
         return;
      }
      String mdcValue = MDC.get(mdcKey.name());
      if (mdcValue == null || mdcValue.isEmpty())
      {
         return;
      }
      headers.putSingle(header.value(), mdcValue);
   }
}
