package table_editor;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preferences. Defaults are provided in preferences.ini
 *  @author Delphy Nypaver Armstrong
 */
@SuppressWarnings("nls")

public class Preferences
{
   /** @return RDB URL */
   public static String getURL()
   {
       final IPreferencesService service = Platform.getPreferencesService();
       return service.getString(Activator.ID, "log_rdb_url", null, null);
   }
   
   /** @return User name for listing logbooks */
   public static String getLogListUser()
   {        
       final IPreferencesService service = Platform.getPreferencesService();
       return service.getString(Activator.ID, "logbook_list_user", null, null);
   }

   /** @return Password for listing logbooks */
   public static String getLogListPassword()
   {        
       final IPreferencesService service = Platform.getPreferencesService();
       return service.getString(Activator.ID, "logbook_list_pass", null, null);
   }

   /** @return Default logbook name */
   public static String getDefaultLogbook()
   {        
       final IPreferencesService service = Platform.getPreferencesService();
       return service.getString(Activator.ID, "logbook_name", null, null);
   }

}
