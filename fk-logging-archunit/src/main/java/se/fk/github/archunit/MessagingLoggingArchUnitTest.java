package se.fk.github.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.junit.jupiter.api.Test;
import se.fk.github.logging.callerinfo.filter.PopulateLoggingContext;

public class MessagingLoggingArchUnitTest //NOPMD
{

   @Test
   void incomingMethodsMustAlsoHavePopulateLoggingContext()
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD
            .importPackages("se.fk");

      ArchRule rule = ArchRuleDefinition.methods()
            .that().areAnnotatedWith(Incoming.class)
            .should().beAnnotatedWith(PopulateLoggingContext.class)
            .allowEmptyShould(true)
            .because("methods annotated with @Incoming must populate the logging context to extract " +
                  "BREADCRUMB-ID and PROCESSID from incoming Kafka message headers");

      rule.check(classes);
   }

   @Test
   void outgoingMethodsMustAlsoHavePopulateLoggingContext()
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD
            .importPackages("se.fk");

      ArchRule rule = ArchRuleDefinition.methods()
            .that().areAnnotatedWith(Outgoing.class)
            .should().beAnnotatedWith(PopulateLoggingContext.class)
            .allowEmptyShould(true)
            .because("methods annotated with @Outgoing must populate the logging context to add " +
                  "BREADCRUMB-ID and PROCESSID headers to outgoing Kafka messages");

      rule.check(classes);
   }
}
