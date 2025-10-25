package se.fk.github.logging.callerinfo.model;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface FKLoggingContext
{
   Optional<String> breadcrumbId();

   Optional<String> processId();

   Optional<String> httpMetod();

   Optional<String> httpURL();

   Optional<String> applikation();
}
