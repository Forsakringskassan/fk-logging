package se.fk.github.logging.callerinfo.model;

public enum HeaderTyp
{
   BREADCRUMB_ID("BREADCRUMB-ID"), PROCESSID("PROCESSID");

   private final String value;

   HeaderTyp(String value)
   {
      this.value = value;
   }

   @Override
   public String toString()
   {
      return value();
   }

   public String value()
   {
      return value;
   }
}
