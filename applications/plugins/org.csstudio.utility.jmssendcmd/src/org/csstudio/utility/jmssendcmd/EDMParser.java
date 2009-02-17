package org.csstudio.utility.jmssendcmd;

import org.csstudio.platform.logging.CentralLogger;

/** Parse the input EDM string.
 *  @author Delphy Armstrong
 */

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
   /** parsed string error flag */
   private boolean error;
   
  /** Parse the input EDM string.
   *  
   * @param edm_log_line
   */
   public EDMParser(String edm_log_line)
   {
      int space = 0;
      String option = "";
      String optionStr = "";
      boolean done=false;
      error=false;
      while(!done)
      {
         final int posn = edm_log_line.indexOf("=");

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
         final int blank=option.lastIndexOf(" ");
        /**
         * Based on the option read, set the appropriate variable to optionStr. 
         */
         if(blank >=0)
           option=option.substring(option.lastIndexOf(" ")+1);
         space++;

         /** 
          * Retrieve various parts of the parsed EDM string.
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
         /** Move to the next option of the input string */
         edm_log_line=edm_log_line.substring(space);
         /**
          * Move past the first " " character
          */
         space = 1;
      }
      pvtext=getPVName() + "="+ getValue() + " (old: " + getOriginalValue()+")";
      /**
       * If this is a remote ssh connection, set the host to the IP address
       * of the machine the connection was made from.
       */
      if(ssh!=null)
      {
         if(ssh.indexOf("f:")+2!=ssh.lastIndexOf("f:")+2 &&
            ssh.indexOf(' ')!=ssh.lastIndexOf(' '))
           host=ssh.substring(ssh.indexOf("f:")+2, ssh.indexOf(' '));
         else 
         {
            CentralLogger.getInstance().getLogger(this).debug(
                  "ssh entry has an INVALID format. " + ssh);
            error=true;
            return;
         }
      }
      /**
       * If an error exists in the entries for user, host, value or pv name
       * set the error flag to true and return;
       */
      if(user==null)
      {
         CentralLogger.getInstance().getLogger(this).debug(
               "user entry cannot be NULL");
         error=true;
         return;
      }
      if(host==null)
      {
         CentralLogger.getInstance().getLogger(this).debug(
               "host entry cannot be NULL");
         error=true;
         return;
      }
      if(value==null)
      {
         CentralLogger.getInstance().getLogger(this).debug(
               "value entry cannot be NULL");
         error=true;
         return;
      }
      if(name==null)
      {
         CentralLogger.getInstance().getLogger(this).debug(
               "pv name entry cannot be NULL");
         error=true;
         return;
      }
   }
   
   boolean hasError()
   {
      return error;
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
