/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.io.BufferedReader;
import java.io.FileReader;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preferences. Defaults are provided in preferences.ini
 *  @author Delphy Nypaver Armstrong - Previous version
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** @return RDB URL 
     *  @throws Exception on error
     */
   public static String getURL() throws Exception
   {
       return getPreference("log_rdb_url");
   }
   
   /** @return User name for listing logbooks 
     *  @throws Exception on error
     */
   public static String getLogListUser() throws Exception
   {        
       return getPreference("logbook_list_user");
   }

   /** @return Password for listing logbooks 
     *  @throws Exception on error
     */
   public static String getLogListPassword() throws Exception
   {        
       return getPreference("logbook_list_pass");
   }

   /** @return Default logbook name 
     *  @throws Exception on error
     */
   public static String getDefaultLogbook() throws Exception
   {        
       return getPreference("logbook_name");
   }
   
   /** @param key Preference key
    *  @return Value from preferences or directly from file for tests
    *  @throws Exception on error
    */
   private static String getPreference(final String key) throws Exception
   {
       final IPreferencesService service = Platform.getPreferencesService();
       if (service != null)
           return service.getString(Activator.ID, key, null, null);
       // Running as test, read setting from file
       final BufferedReader reader = new BufferedReader(new FileReader("preferences.ini"));
       try
       {
           String line;
           while ((line = reader.readLine()) != null)
           {
               if (! line.startsWith(key))
                   continue;
               final int sep = line.indexOf("=");
               if (sep <= 0)
                   continue;
               return line.substring(sep+1).trim();
           }
       }
       finally
       {
           reader.close();
       }
       return null;
   }
}
