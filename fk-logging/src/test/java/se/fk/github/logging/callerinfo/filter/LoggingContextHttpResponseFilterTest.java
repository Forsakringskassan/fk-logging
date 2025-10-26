package se.fk.github.logging.callerinfo.filter;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import se.fk.github.logging.callerinfo.model.HeaderTyp;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingContextHttpResponseFilterTest extends BaseFilterIntegrationTest
{

   @Test
   void shouldAddBreadcrumbIdHeaderFromMDC()
   {
      Response response = target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "test-breadcrumb-123")
            .get();

      assertThat(response.getHeaderString(HeaderTyp.BREADCRUMB_ID.value()))
            .isEqualTo("test-breadcrumb-123");
   }

   @Test
   void shouldAddProcessIdHeaderFromMDC()
   {
      Response response = target(PATH_TEST)
            .request()
            .header(HeaderTyp.PROCESSID.value(), "process-456")
            .get();

      assertThat(response.getHeaderString(HeaderTyp.PROCESSID.value()))
            .isEqualTo("process-456");
   }

   @Test
   void shouldAddBothHeadersWhenBothPresentInMDC()
   {
      Response response = target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "breadcrumb-abc")
            .header(HeaderTyp.PROCESSID.value(), "process-xyz")
            .get();

      assertThat(response.getHeaderString(HeaderTyp.BREADCRUMB_ID.value()))
            .isEqualTo("breadcrumb-abc");
      assertThat(response.getHeaderString(HeaderTyp.PROCESSID.value()))
            .isEqualTo("process-xyz");
   }

   @Test
   void shouldGenerateBreadcrumbIdWhenNotProvided()
   {
      Response response = target(PATH_TEST).request().get();

      String breadcrumbId = response.getHeaderString(HeaderTyp.BREADCRUMB_ID.value());
      assertThat(breadcrumbId)
            .isNotNull()
            .matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
   }

   @Test
   void shouldNotOverwriteExistingHeaderInResponse()
   {
      Response response = target(PATH_TEST_WITH_HEADER)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "request-breadcrumb")
            .get();

      assertThat(response.getHeaderString(HeaderTyp.BREADCRUMB_ID.value()))
            .isEqualTo("existing-breadcrumb");
   }

   @Test
   void shouldClearMDCAfterRequest()
   {
      target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "first-breadcrumb")
            .header(HeaderTyp.PROCESSID.value(), "first-process")
            .get();

      Response response = target(PATH_TEST).request().get();

      String secondBreadcrumb = response.getHeaderString(HeaderTyp.BREADCRUMB_ID.value());
      assertThat(secondBreadcrumb)
            .isNotNull()
            .isNotEqualTo("first-breadcrumb");

      String secondProcess = response.getHeaderString(HeaderTyp.PROCESSID.value());
      assertThat(secondProcess).isNullOrEmpty();
   }

   @Test
   void shouldPreserveOtherResponseHeaders()
   {
      Response response = target(PATH_TEST_WITH_CUSTOM_HEADERS)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "breadcrumb-123")
            .get();

      assertThat(response.getHeaderString("Content-Type")).contains("application/json");
      assertThat(response.getHeaderString("X-Custom-Header")).isEqualTo("custom-value");

      assertThat(response.getHeaderString(HeaderTyp.BREADCRUMB_ID.value()))
            .isEqualTo("breadcrumb-123");
   }

   @Test
   void shouldHandleEmptyProcessIdGracefully()
   {
      Response response = target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "breadcrumb-only")
            .header(HeaderTyp.PROCESSID.value(), "")
            .get();

      assertThat(response.getHeaderString(HeaderTyp.BREADCRUMB_ID.value()))
            .isEqualTo("breadcrumb-only");

      String processId = response.getHeaderString(HeaderTyp.PROCESSID.value());
      assertThat(processId).isNullOrEmpty();
   }

   @Test
   void shouldWorkAcrossMultipleRequests()
   {
      Response response1 = target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "req1-breadcrumb")
            .get();

      Response response2 = target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "req2-breadcrumb")
            .get();

      Response response3 = target(PATH_TEST)
            .request()
            .header(HeaderTyp.BREADCRUMB_ID.value(), "req3-breadcrumb")
            .get();

      assertThat(response1.getHeaderString(HeaderTyp.BREADCRUMB_ID.value()))
            .isEqualTo("req1-breadcrumb");
      assertThat(response2.getHeaderString(HeaderTyp.BREADCRUMB_ID.value()))
            .isEqualTo("req2-breadcrumb");
      assertThat(response3.getHeaderString(HeaderTyp.BREADCRUMB_ID.value()))
            .isEqualTo("req3-breadcrumb");
   }
}
