package se.fk.github.logging.callerinfo.filter;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.FKLoggingContext;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.model.ImmutableFKLoggingContext;
import se.fk.github.logging.callerinfo.model.MDCKeys;
import se.fk.github.logging.callerinfo.service.CallerinfoService;

import java.util.List;
import java.util.UUID;

import static se.fk.github.logging.callerinfo.model.HeaderTyp.BREADCRUMB_ID;

@Provider
@Priority(1)
public class LoggingContextPopluatorFilter implements ContainerRequestFilter
{
   public static final String APPLIKATION_PROP = "applikation";
   public static final String NOT_SET = "-property-not-set";

   @ConfigProperty(name = APPLIKATION_PROP, defaultValue = APPLIKATION_PROP + NOT_SET)
   private String applikation;

   @Inject
   CallerinfoService callerinfoService;

   @Override
   public void filter(ContainerRequestContext requestContext)
   {
      MultivaluedMap<String, String> headersMap = requestContext.getHeaders();

      FKLoggingContext context = ImmutableFKLoggingContext.builder()
            .applikation(applikation)
            .httpMetod(requestContext.getMethod())
            .httpURL(requestContext.getUriInfo().getRequestUri().toString())
            .processId(header(headersMap, HeaderTyp.PROCESSID, ""))
            .breadcrumbId(header(headersMap, BREADCRUMB_ID, UUID.randomUUID().toString()))
            .build();

      callerinfoService.setContext(context);

      MDC.put(MDCKeys.BREADCRUMBID.name(), context.breadcrumbId().orElse(""));
      MDC.put(MDCKeys.APPLIKATION.name(), context.applikation().orElse(""));
      MDC.put(MDCKeys.METHOD.name(), context.httpMetod().orElse(""));
      MDC.put(MDCKeys.URL.name(), context.httpURL().orElse(""));
      MDC.put(MDCKeys.PROCESSID.name(), context.processId().orElse(""));
   }

   private String header(MultivaluedMap<String, String> headersMap, HeaderTyp headerTyp, String defaultValue)
   {
      return headersMap.getOrDefault(headerTyp.value(), List.of(defaultValue)).getFirst();
   }
}
