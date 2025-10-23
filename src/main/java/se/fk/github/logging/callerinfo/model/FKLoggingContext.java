package se.fk.github.logging.callerinfo.model;

import org.immutables.value.Value;

@Value.Immutable
public interface FKLoggingContext
{
   String breadcrumbId();

   String processId();

   String httpMetod();

   String httpURL();

   String applikation();
}
