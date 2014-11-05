/**
 * 
 */
package org.csstudio.logbook.ui.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import static org.csstudio.logbook.LogEntryBuilder.logEntry;
import static org.csstudio.logbook.ui.util.UpdateLogEntryBuilder.createUpdateLogEntryBuilder;

/**
 * @author shroffk
 * 
 */
public class LogEntryAdapterFactory implements IAdapterFactory {

    @SuppressWarnings("rawtypes")
    @Override
    /**
     * 
     * @param adaptableObject
     * @param adapterType
     * @return
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        LogEntry logEntry = ((LogEntry) adaptableObject);
        final IPreferencesService prefs = Platform.getPreferencesService();
        String LogURLFormatt = prefs.getString("org.csstudio.logbook.ui",
                "Log.url.formatt",
                "http://localhost:8080/Olog/resources/logs/{logId}", null);
        if (adapterType == String.class) {
            return logEntry.getText();
        } else if (adapterType == URL.class) {
            try {
                return new URL(LogURLFormatt.replace("{logId}", logEntry
                        .getId().toString()));
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        } else if (adapterType == UpdateLogEntryBuilder.class) {
            try {
                return createUpdateLogEntryBuilder(logEntry);
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    /**
     * 
     * @return
     */
    public Class[] getAdapterList() {
	return new Class[] { UpdateLogEntryBuilder.class, String.class, URL.class };
    }

}
