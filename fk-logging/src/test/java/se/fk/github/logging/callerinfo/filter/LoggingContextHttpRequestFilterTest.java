package se.fk.github.logging.callerinfo.filter;

import org.junit.jupiter.api.Test;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.model.MDCKeys;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingContextHttpRequestFilterTest extends BaseFilterIntegrationTest
{

   @Test
   void shouldPopulateMDCWithBreadcrumbIdFromHeader()
   {
      target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "test-breadcrumb-123")
            .get();

      assertThat(capturedMDC.get(MDCKeys.BREADCRUMBID.name())).isEqualTo("test-breadcrumb-123");
   }

   @Test
   void shouldGenerateRandomBreadcrumbIdWhenNotProvidedInHeader()
   {
      target(PATH_TEST).request().get();

      String breadcrumbId = capturedMDC.get(MDCKeys.BREADCRUMBID.name());
      assertThat(breadcrumbId)
            .isNotNull()
            .isNotEmpty()
            .matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
   }

   @Test
   void shouldPopulateMDCWithProcessIdFromHeader()
   {
      target(PATH_TEST)
            .request()
            .header(HeaderTyp.PROCESSID.value(), "process-456")
            .get();

      assertThat(capturedMDC.get(MDCKeys.PROCESSID.name())).isEqualTo("process-456");
   }

   @Test
   void shouldPopulateMDCWithHttpMethodAndUrl()
   {
      target(PATH_TEST_CREATE).request().post(null);

      assertThat(capturedMDC.get(MDCKeys.METHOD.name())).isEqualTo("POST");
      assertThat(capturedMDC.get(MDCKeys.URL.name())).contains("/test/create");
   }

   @Test
   void shouldPopulateAllMDCFieldsWhenAllHeadersArePresent()
   {
      target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "breadcrumb-abc")
            .header(HeaderTyp.PROCESSID.value(), "process-xyz")
            .get();

      assertThat(capturedMDC.get(MDCKeys.BREADCRUMBID.name())).isEqualTo("breadcrumb-abc");
      assertThat(capturedMDC.get(MDCKeys.PROCESSID.name())).isEqualTo("process-xyz");
      assertThat(capturedMDC.get(MDCKeys.METHOD.name())).isEqualTo("GET");
      assertThat(capturedMDC.get(MDCKeys.URL.name())).contains("/test");
      assertThat(capturedMDC.get(MDCKeys.APPLIKATION.name())).isEqualTo("test-application");
   }

   @Test
   void shouldHandleEmptyProcessIdGracefully()
   {
      target(PATH_TEST)
            .request()
            .header(HeaderTyp.PROCESSID.value(), "")
            .get();

      assertThat(capturedMDC.get(MDCKeys.PROCESSID.name())).isEmpty();
   }

   @Test
   void shouldHandleMultipleSequentialRequests()
   {
      target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "first-breadcrumb")
            .get();
      String firstBreadcrumb = capturedMDC.get(MDCKeys.BREADCRUMBID.name());

      capturedMDC.clear();

      target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "second-breadcrumb")
            .get();
      String secondBreadcrumb = capturedMDC.get(MDCKeys.BREADCRUMBID.name());

      assertThat(firstBreadcrumb).isEqualTo("first-breadcrumb");
      assertThat(secondBreadcrumb).isEqualTo("second-breadcrumb");
   }
}
