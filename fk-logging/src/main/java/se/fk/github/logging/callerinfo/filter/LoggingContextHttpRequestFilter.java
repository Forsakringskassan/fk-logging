package se.fk.github.logging.callerinfo.filter;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import se.fk.github.logging.callerinfo.model.FKLoggingContext;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.model.ImmutableFKLoggingContext;
import se.fk.github.logging.callerinfo.service.MDCPopulatorService;

import java.util.List;
import java.util.UUID;

import static se.fk.github.logging.callerinfo.model.HeaderTyp.BREADCRUMB_ID;

@Provider
@Priority(1)
public class LoggingContextHttpRequestFilter implements ContainerRequestFilter
{
   @Inject
   private MDCPopulatorService mdcPopulatorService;

   public void setMdcPopulatorService(MDCPopulatorService mdcPopulatorService)
   {
      this.mdcPopulatorService = mdcPopulatorService;
   }

   @Override
   public void filter(ContainerRequestContext requestContext)
   {
      MultivaluedMap<String, String> headersMap = requestContext.getHeaders();

      FKLoggingContext context = ImmutableFKLoggingContext.builder()
            .httpMetod(requestContext.getMethod())
            .httpURL(requestContext.getUriInfo().getRequestUri().toString())
            .processId(header(headersMap, HeaderTyp.PROCESSID, ""))
            .breadcrumbId(header(headersMap, BREADCRUMB_ID, UUID.randomUUID().toString()))
            .build();

      mdcPopulatorService.setContext(context);
   }

   private String header(MultivaluedMap<String, String> headersMap, HeaderTyp headerTyp, String defaultValue)
   {
      return headersMap.getOrDefault(headerTyp.value(), List.of(defaultValue)).getFirst();
   }
}
