package se.fk.github.logging.callerinfo.filter;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.MDCKeys;
import se.fk.github.logging.callerinfo.service.Callerinfo;

@Provider
@Priority(1)
public class LoggingContextPopluatorFilter implements ContainerRequestFilter
{

   @Inject
   Callerinfo callerinfo;

   @Override
   public void filter(ContainerRequestContext requestContext)
   {
      callerinfo.setAttribut(requestContext);

      MDC.put(MDCKeys.BREADCRUMBID.name(), callerinfo.getContext().breadcrumbId());
      MDC.put(MDCKeys.APPLIKATION.name(), callerinfo.getContext().applikation());
      MDC.put(MDCKeys.METHOD.name(), callerinfo.getContext().httpMetod());
      MDC.put(MDCKeys.URL.name(), callerinfo.getContext().httpURL());
      MDC.put(MDCKeys.PROCESSID.name(), callerinfo.getContext().processId());
   }
}
