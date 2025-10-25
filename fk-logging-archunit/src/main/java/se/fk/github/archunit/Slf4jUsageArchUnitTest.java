package se.fk.github.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@EnabledIfSystemProperty(named = "archunit.packageToScan", matches = ".+")
public class Slf4jUsageArchUnitTest //NOPMD
{
   private static final String PACKAGE_TO_SCAN = System.getProperty("archunit.packageToScan", "se.fk.github");
   private static final String MSG = "Use org.slf4j.Logger for logging";

   @Test
   void noDirectDependenceOnOtherLoggingFrameworks()
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD - JavaClasses is the ArchUnit API, not a loose coupling issue
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(PACKAGE_TO_SCAN);

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
   void forbid_system_out_println()
   {
      JavaClasses classes = new ClassFileImporter() //NOPMD - JavaClasses is the ArchUnit API, not a loose coupling issue
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(PACKAGE_TO_SCAN);

      noClasses()
            .that().resideInAnyPackage(PACKAGE_TO_SCAN + "..")
            .should().callMethodWhere(new DescribedPredicate<>("calls java.io.PrintStream.println")
            {
               @Override
               public boolean test(JavaMethodCall call)
               {
                  return "java.io.PrintStream".equals(call.getTarget().getOwner().getFullName())
                        && call.getTarget().getName().startsWith("println");
               }
            })
            .because(MSG)
            .check(classes);
   }
}
