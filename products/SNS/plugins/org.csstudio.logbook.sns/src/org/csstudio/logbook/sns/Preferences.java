package org.csstudio.logbook.sns;

/** Preferences. Defaults are provided in preferences.ini
 *  @author nypaver
 */
@SuppressWarnings("nls")
public class Preferences
{
   /** Name of the preference for the RDB URL */
   final public static String LOG_RDB_URL="log_rdb_url";

   /** Name of the preference for the SNS logbook to use */
   final public static String LOGBOOK_NAME="logbook_name";

   /** @return RDB URL */
   public static String getURL()
   {        
      return Activator.getDefault().getPluginPreferences().getString(LOG_RDB_URL);
   }
   
   /** @return Name of SNS logbook */
   public static String getLogBookName()
   {        
      return Activator.getDefault().getPluginPreferences().getString(LOGBOOK_NAME);
   }
}
