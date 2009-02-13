package org.csstudio.utility.jmssendcmd;

public class EDMParser
{
   /** Current value */
   private String value;
   /** Previous value */
   private String oldVal;
   /** host name */
   private String host;
   /** user name */
   private String user;
   /** pv name */
   private String name;
   
   /** pvtext which is "pvname=pvalue" */
   final private String pvtext;
   /** the option that passes the ssh value */
   private String ssh;
   /** option for the display value */
   private String dsp;
   
   private boolean done=false;
   
   public EDMParser(String edm_log_line)
   {
      int posn = 0;
      int space = 0;
      String option = "";
      String optionStr = "";
      while(!done)
      {

         posn = edm_log_line.indexOf("=");

         /**
          * Retrieve the option
          */
         option= edm_log_line.substring(space,posn);
         /**
          * See if this is there are any more options in the string.
          */
         space = edm_log_line.indexOf("\" ");

         /**
          * Retrieve the option string.  If the " character was not found
          * then this is the last option so set done to true
          */
         if(space!=-1)
           optionStr= edm_log_line.substring(posn+2,space);
         else
         { 
           optionStr= edm_log_line.substring(posn+2,edm_log_line.length()-1);
           done = true;
         }
         space++;
        /**
         * Based on the option read, set the appropriate variable to optionStr. 
         */
         if(option.equalsIgnoreCase("host"))
            host=optionStr;
         else if(option.equalsIgnoreCase("user"))
            user=optionStr;
         else if(option.equalsIgnoreCase("ssh"))
            ssh=optionStr;
         else if(option.equalsIgnoreCase("dsp"))
            dsp=optionStr;
         else if(option.equalsIgnoreCase("name"))
            name=optionStr;
         else if(option.equalsIgnoreCase("new"))
            value=optionStr;
         else if(option.equalsIgnoreCase("old"))
            oldVal=optionStr;         
         edm_log_line=edm_log_line.substring(space);
         /**
          * Move past the first " character
          */
         space = 1;

      }
      pvtext=getPVName() + "="+ getValue();
   }
   
   String getUser()
   {
      return user;
   }

   public String getHost()
   {
      return host;
   }

   public String getPVName()
   {
      return name;
   }

   public String getPVText()
   {
      return pvtext;
   }
   
   public String getValue()
   {
      return value;
   }
   
   public String getOriginalValue()
   {
      return oldVal;
   }
   
   public String getSsh()
   {
      return ssh;
   }
   
   public String getDsp()
   {
      return dsp;
   }
   
}
