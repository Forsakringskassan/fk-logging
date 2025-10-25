package se.fk.github.logging.callerinfo.filter;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.HeaderTyp;
import se.fk.github.logging.callerinfo.model.MDCKeys;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

class MDCHeaders
{
   public static void addMdcHeaders(Headers headers)
   {
      addIfNotPresent(headers, HeaderTyp.BREADCRUMB_ID.value(), mdcValue(MDCKeys.BREADCRUMBID));
      addIfNotPresent(headers, HeaderTyp.PROCESSID.value(), mdcValue(MDCKeys.PROCESSID));
   }

   private static void addIfNotPresent(Headers headers, String headerName, byte[] headerValue)
   {
      Header originalValue = headers.lastHeader(headerName);
      if (originalValue == null || originalValue.value().length == 0)
      {
         headers.add(headerName, headerValue);
      }
   }

   private static byte[] mdcValue(MDCKeys mdcKey)
   {
      return Optional.ofNullable(MDC.get(mdcKey.name())).orElse("").getBytes(StandardCharsets.UTF_8);
   }
}
