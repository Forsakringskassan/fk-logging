package se.fk.github.logging.callerinfo.filter;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
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
      mdcPopulatorService.clearMdc();
   }
}
