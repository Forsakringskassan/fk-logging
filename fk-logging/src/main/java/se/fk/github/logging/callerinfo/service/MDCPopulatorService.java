package se.fk.github.logging.callerinfo.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.FKLoggingContext;
import se.fk.github.logging.callerinfo.model.ImmutableFKLoggingContext;
import se.fk.github.logging.callerinfo.model.MDCKeys;

import java.util.UUID;

@ApplicationScoped
public class MDCPopulatorService
{
   public static final String APPLIKATION_PROP = "applikation";
   public static final String NOT_SET = "-property-not-set";

   @ConfigProperty(name = APPLIKATION_PROP, defaultValue = APPLIKATION_PROP + NOT_SET)
   private String applikation;

   public void setApplikation(String applikation)
   {
      this.applikation = applikation;
   }

   public void setContext(FKLoggingContext fkContext)
   {
      if (fkContext.breadcrumbId().orElse("").isEmpty())
      {
         fkContext = ImmutableFKLoggingContext.copyOf(fkContext)
               .withBreadcrumbId(UUID.randomUUID().toString());
      }

      fkContext = ImmutableFKLoggingContext.copyOf(fkContext)
            .withApplikation(applikation);

      MDC.put(MDCKeys.APPLIKATION.name(), fkContext.applikation().get());
      MDC.put(MDCKeys.BREADCRUMBID.name(), fkContext.breadcrumbId().get());
      MDC.put(MDCKeys.PROCESSID.name(), fkContext.processId().orElse(""));
      MDC.put(MDCKeys.METHOD.name(), fkContext.httpMetod().orElse(""));
      MDC.put(MDCKeys.URL.name(), fkContext.httpURL().orElse(""));
   }

   public void clearMdc()
   {
      MDC.clear();
   }
}
