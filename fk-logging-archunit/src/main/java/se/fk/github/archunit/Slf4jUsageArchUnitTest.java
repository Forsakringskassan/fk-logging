package se.fk.github.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class Slf4jUsageArchUnitTest //NOPMD
{

   @Test
   void loggerFieldsShouldBeOfTypeSlf4jLogger()
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD
            .importPackages("se.fk");

      ArchRule rule = ArchRuleDefinition.fields()
            .that().haveNameMatching("(?i).*logger.*")
            .should().haveRawType(Logger.class)
            .because("fields named 'logger' (or similar) should use org.slf4j.Logger as the logging API");

      rule.check(classes);
   }

   @Test
   void noDirectDependenceOnOtherLoggingFrameworks()
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD
            .importPackages("se.fk");

      ArchRule rule = noClasses()
            .should().dependOnClassesThat(resideInAnyPackage(
                  "java.util.logging..",
                  "org.apache.log4j..",
                  "org.apache.logging.log4j..",
                  "org.apache.logging.log4j.*..",
                  "ch.qos.logback.."))
            .because("the codebase should use SLF4J as the logging API and avoid direct use of other logging framework APIs");

      rule.check(classes);
   }

   @Test
   void forbid_system_out_println() //NOPMD
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD
            .importPackages("se.fk");

      ArchRule rule = noClasses()
            .should().callMethod(System.class, "out")
            .because("System.out should not be used directly, use SLF4J logger instead");

      ArchRule printlnRule = noClasses()
            .should().callMethod(System.out.getClass(), "println", String.class)
            .because("System.out.println is forbidden, use SLF4J logger instead");

      rule.check(classes);
      printlnRule.check(classes);
   }
}
