package org.csstudio.utility.jmssendcmd;

/** Description of a JMS EDM 'LOG' message
 *  @author Delphy Armstrong
 */
@SuppressWarnings("nls")
public class JMSLogEDMMessage
{
   /** JMS Message Element that holds the PV's value */
   final public static String PVVALUE = "VALUE";
   /** JMS Message Element that holds the pv name */
   public static String PVNAME = "NAME";
   /** JMS Message Element that holds the text "pvname=pvalue" */
   public static String PVTEXT = "TEXT";
   /** JMS Message Element that holds the original pv value */
   public static String OLDPVVALUE = "OLDVALUE";
   /** JMS Message Element that holds the ssh string */
   public static String SSH = "SSH";
   /** JMS Message Element that holds the display string */
   public static String DSP = "DSP";
}
