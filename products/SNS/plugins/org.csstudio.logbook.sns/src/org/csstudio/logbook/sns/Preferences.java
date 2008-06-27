package org.csstudio.logbook.sns;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

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
       final IPreferencesService service = Platform.getPreferencesService();
       return service.getString(Activator.ID, "LOG_RDB_URL", null, null);
   }
   
   /** @return Name of SNS logbook */
   public static String getLogBookName()
   {        
       final IPreferencesService service = Platform.getPreferencesService();
       return service.getString(Activator.ID, "LOGBOOK_NAME", null, null);
   }
}
