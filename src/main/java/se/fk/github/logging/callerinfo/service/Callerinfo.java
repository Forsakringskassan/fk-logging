package se.fk.github.logging.callerinfo.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import se.fk.github.logging.callerinfo.model.FKLoggingContext;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.model.ImmutableFKLoggingContext;

import java.util.List;

import static se.fk.github.logging.callerinfo.model.HeaderTyp.BREADCRUMB_ID;

@RequestScoped
public class Callerinfo
{

   public static final String APPLIKATION_PROP = "applikation";
   public static final String NOT_SET = "-property-not-set";

   @ConfigProperty(name = APPLIKATION_PROP, defaultValue = APPLIKATION_PROP + NOT_SET)
   private String applikation;

   private FKLoggingContext context;

   public FKLoggingContext getContext()
   {
      return context;
   }

   public void setAttribut(ContainerRequestContext requestContext)
   {
      MultivaluedMap<String, String> headersMap = requestContext.getHeaders();

      this.context = ImmutableFKLoggingContext.builder()
            .applikation(applikation)
            .httpMetod(requestContext.getMethod())
            .httpURL(requestContext.getUriInfo().getRequestUri().toString())
            .processId(header(headersMap, HeaderTyp.PROCESSID))
            .breadcrumbId(header(headersMap, BREADCRUMB_ID))
            .build();
   }

   private String header(MultivaluedMap<String, String> headersMap, HeaderTyp headerTyp)
   {
      return headersMap.getOrDefault(headerTyp.value(), List.of("")).getFirst();
   }
}
