package se.fk.github.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class Slf4jUsageArchUnitTest //NOPMD
{
   private static final String MSG = "Use org.slf4j.Logger for logging";

   @Test
   void loggerFieldsShouldBeOfTypeSlf4jLogger()
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("se.fk");

      ArchRule rule = ArchRuleDefinition.fields()
            .that().haveNameMatching("(?i).*log.*")
            .should().haveRawType(Logger.class)
            .because(MSG)
            .allowEmptyShould(true);

      rule.check(classes);
   }

   @Test
   void noDirectDependenceOnOtherLoggingFrameworks()
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("se.fk");

      ArchRule rule = noClasses()
            .should().dependOnClassesThat(resideInAnyPackage(
                  "java.util.logging..",
                  "org.apache.log4j..",
                  "org.apache.logging.log4j..",
                  "org.apache.logging.log4j.*..",
                  "ch.qos.logback.."))
            .because(MSG)
            .allowEmptyShould(true);

      rule.check(classes);
   }

   @Test
   void forbid_system_out_println() //NOPMD
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("se.fk");

      ArchRule rule = noClasses()
            .should().callMethod(System.class, "out")
            .because(MSG);

      ArchRule printlnRule = noClasses()
            .should().callMethod(System.out.getClass(), "println", String.class)
            .because(MSG);

      rule.check(classes);
      printlnRule.check(classes);
   }
}
