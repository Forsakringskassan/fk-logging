package se.fk.github.logging.callerinfo.filter;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.model.MDCKeys;

import java.io.IOException;

@Provider
public class LoggingContextClientRequestFilter implements ClientRequestFilter
{
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException
   {
      addIfNotPresent(requestContext.getHeaders(), HeaderTyp.BREADCRUMB_ID, MDCKeys.BREADCRUMBID);
      addIfNotPresent(requestContext.getHeaders(), HeaderTyp.PROCESSID, MDCKeys.PROCESSID);
   }

   private void addIfNotPresent(MultivaluedMap<String, Object> headers, HeaderTyp headerTyp, MDCKeys mdcKeys)
   {
      if (headers.containsKey(headerTyp.value())
            && headers.get(headerTyp.value()) != null
            && !headers.get(headerTyp.value()).isEmpty())
      {
         return;
      }
      headers.add(headerTyp.value(), MDC.get(mdcKeys.name()));
   }
}
