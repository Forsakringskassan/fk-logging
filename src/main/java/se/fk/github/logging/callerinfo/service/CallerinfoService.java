package se.fk.github.logging.callerinfo.service;

import jakarta.enterprise.context.RequestScoped;
import se.fk.github.logging.callerinfo.model.FKLoggingContext;

@RequestScoped
public class CallerinfoService
{
   private FKLoggingContext context;

   public FKLoggingContext getContext()
   {
      return context;
   }

   public void setContext(FKLoggingContext context)
   {
      this.context = context;
   }
}
