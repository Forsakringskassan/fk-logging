package se.fk.github.logging.callerinfo.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import se.fk.github.jaxrsclientfactory.JaxrsClientFactory;
import se.fk.github.jaxrsclientfactory.JaxrsClientOptionsBuilder;
import se.fk.github.jaxrsclientfactory.JaxrsClientOptionsBuilders;
import se.fk.github.logging.callerinfo.model.HeaderTyp;

@ApplicationScoped
public class JaxrsClientFactoryService
{
   @Inject
   private Callerinfo anropsinfo;

   public <T> T createClient(String baseUrl, Class<T> clazz)
   {
      JaxrsClientOptionsBuilder<T> optionsBuilder = createClientOptions(baseUrl, clazz);
      return new JaxrsClientFactory()
            .create(optionsBuilder.build());
   }

   public <T> JaxrsClientOptionsBuilder<T> createClientOptions(String baseUrl, Class<T> clazz)
   {
      return JaxrsClientOptionsBuilders.createClient(baseUrl, clazz)
            .header(HeaderTyp.BREADCRUMB_ID.value(), anropsinfo.getContext().breadcrumbId())
            .header(HeaderTyp.PROCESSID.value(), anropsinfo.getContext().processId());
   }
}
