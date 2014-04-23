/**
 * 
 */
package org.csstudio.logbook.ui.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.csstudio.logbook.LogEntry;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * @author Kunal Shroff
 *
 */
public class AdapterFactory implements IAdapterFactory {

    @Override
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
		    return new URL(LogURLFormatt.replace("{logId}", logEntry.getId().toString()));
		} catch (MalformedURLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    return null;
		}
	} else {
		return null;
	}
    }

    @Override
    public Class[] getAdapterList() {
	return new Class[] { String.class, URL.class };
    }

}
