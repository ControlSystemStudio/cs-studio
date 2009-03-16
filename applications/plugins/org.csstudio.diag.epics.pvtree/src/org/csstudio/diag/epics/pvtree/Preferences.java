package org.csstudio.diag.epics.pvtree;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    public static final String FIELDS = "fields";

    /** @return Field info for all record types
     *  @throws Exception on error in the preference setting
     *  @see FieldParser
     */
    public static HashMap<String, List<String>> getFieldInfo() throws Exception
    {
        final IPreferencesService preferences = Platform.getPreferencesService();
        final String fields_pref =
            preferences.getString(Plugin.ID, FIELDS, null, null);
        if (fields_pref == null)
            throw new Exception("Missing preference setting");
        return FieldParser.parse(fields_pref);
    }
}
