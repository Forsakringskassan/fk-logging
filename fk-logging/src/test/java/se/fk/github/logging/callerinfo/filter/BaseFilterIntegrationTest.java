package se.fk.github.logging.callerinfo.filter;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.service.MDCPopulatorService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class BaseFilterIntegrationTest extends JerseyTest
{
   protected static final ConcurrentMap<String, String> capturedMDC = new ConcurrentHashMap<>();

   protected static final String PATH_TEST = "/test";
   protected static final String SUBPATH_CREATE = "/create";
   protected static final String SUBPATH_WITH_HEADER = "/with-header";
   protected static final String SUBPATH_WITH_CUSTOM_HEADERS = "/with-custom-headers";

   protected static final String PATH_TEST_CREATE = PATH_TEST + SUBPATH_CREATE;
   protected static final String PATH_TEST_WITH_HEADER = PATH_TEST + SUBPATH_WITH_HEADER;
   protected static final String PATH_TEST_WITH_CUSTOM_HEADERS = PATH_TEST + SUBPATH_WITH_CUSTOM_HEADERS;

   @Path(PATH_TEST)
   public static class TestResource
   {
      @GET
      public Response get()
      {
         capturedMDC.putAll(MDC.getCopyOfContextMap());
         return Response.ok("OK").build();
      }

      @POST
      @Path(SUBPATH_CREATE)
      public Response post()
      {
         capturedMDC.putAll(MDC.getCopyOfContextMap());
         return Response.ok("CREATED").build();
      }

      @GET
      @Path(SUBPATH_WITH_HEADER)
      public Response getWithExistingHeader()
      {
         return Response.ok("OK")
               .header(HeaderTyp.BREADCRUMB_ID.value(), "existing-breadcrumb")
               .build();
      }

      @GET
      @Path(SUBPATH_WITH_CUSTOM_HEADERS)
      public Response getWithCustomHeaders()
      {
         return Response.ok("OK")
               .header("Content-Type", "application/json")
               .header("X-Custom-Header", "custom-value")
               .build();
      }
   }

   @Override
   protected Application configure()
   {
      final MDCPopulatorService mdcService = new MDCPopulatorService();
      mdcService.setApplikation("test-application");

      return new ResourceConfig()
            .register(TestResource.class)
            .register(LoggingContextHttpRequestFilter.class)
            .register(LoggingContextHttpResponseFilter.class)
            .register(new AbstractBinder()
            {
               @Override
               protected void configure()
               {
                  bind(mdcService).to(MDCPopulatorService.class);
               }
            });
   }

   @BeforeEach
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      capturedMDC.clear();
      MDC.clear();
   }

   @AfterEach
   @Override
   public void tearDown() throws Exception
   {
      capturedMDC.clear();
      MDC.clear();
      super.tearDown();
   }
}
