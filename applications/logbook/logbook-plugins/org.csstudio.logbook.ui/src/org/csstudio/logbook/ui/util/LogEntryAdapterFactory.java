/**
 *
 */
package org.csstudio.logbook.ui.util;

import static org.csstudio.logbook.ui.util.UpdateLogEntryBuilder.createUpdateLogEntryBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.csdata.TimestampedPV;
import org.csstudio.logbook.LogEntry;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 *
 * An adapter factory from adaption an LogEntry to
 * 1. A string
 * 2. A {@link URL}
 * 3. A list of {@link ProcessVariable}
 * 4. A list of {@link TimestampedPV}
 * @author shroffk
 *
 */
public class LogEntryAdapterFactory implements IAdapterFactory {

    private static String pvRegex = "^.*pv.*?:(.*?)$";
    private final IPreferencesService prefs = Platform.getPreferencesService();

    @SuppressWarnings("rawtypes")
    @Override
    /**
     *
     * @param adaptableObject
     * @param adapterType
     * @return
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        final LogEntry logEntry = ((LogEntry) adaptableObject);

        Collection<String> pvNames = new ArrayList<String>();
        try {
            pvRegex = prefs.getString("org.csstudio.logbook.ui", pvRegex, "^.*pv.*?:(.*?)$", null);
        } catch (Exception ex) {
            return null;
        }
        Pattern pvPattern = Pattern.compile(pvRegex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Matcher pvMatcher = pvPattern.matcher(logEntry.getText());
        while (pvMatcher.find()) {
            pvNames.add(pvMatcher.group(1));
        }

        if (adapterType == String.class) {
            return logEntry.getText();
        } else if (adapterType == URL.class) {
            String LogURLFormatt = prefs.getString("org.csstudio.logbook.ui",
                    "Log.url.format",
                    "http://localhost:8080/Olog/resources/logs/{logId}", null);
            try {
                return new URL(LogURLFormatt.replace("{logId}", logEntry
                        .getId().toString()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        } else if (adapterType == UpdateLogEntryBuilder.class) {
            try {
                return createUpdateLogEntryBuilder(logEntry);
            } catch (IOException e) {
                return null;
            }
        } else if (adapterType == ProcessVariable.class) {
            if (pvNames != null && pvNames.size() == 1)
                return new ProcessVariable(pvNames.iterator().next());
        } else if (adapterType == ProcessVariable[].class) {
            if (pvNames != null && !pvNames.isEmpty())
                return pvNames.stream().map((name) -> {
                    return new ProcessVariable(name);
                }).toArray(ProcessVariable[]::new);
        } else if (adapterType == TimestampedPV.class) {
            if (pvNames != null && pvNames.size() == 1)
                return new TimestampedPV(pvNames.iterator().next(), logEntry.getCreateDate().getTime());
        } else if (adapterType == TimestampedPV[].class) {
            if (pvNames != null && !pvNames.isEmpty())
                return pvNames.stream().map((name) -> {
                    return new TimestampedPV(name, logEntry.getCreateDate().getTime());
                }).toArray(TimestampedPV[]::new);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    /**
     *
     * @return
     */
    public Class[] getAdapterList() {
    return new Class[] { UpdateLogEntryBuilder.class, String.class, URL.class, ProcessVariable.class, ProcessVariable[].class,
                TimestampedPV.class, TimestampedPV[].class };
    }

}
