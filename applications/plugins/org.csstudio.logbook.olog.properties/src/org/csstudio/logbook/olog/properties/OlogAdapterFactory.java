/**
 * 
 */
package org.csstudio.logbook.olog.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.logbook.LogEntry;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;


/**
 * @author shroffk
 *
 */
public class OlogAdapterFactory implements IAdapterFactory {

    private static String pvRegex = "^.*pv:(.*?)$";
    private final IPreferencesService service = Platform.getPreferencesService();
    
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
	if (adaptableObject instanceof LogEntry) {
	    LogEntry logEntry = (LogEntry) adaptableObject;
	    Collection<ProcessVariable> result = new ArrayList<ProcessVariable>();
	    try {
		pvRegex = service.getString("org.csstudio.logbook.ui", pvRegex, "^.*pv:(.*?)$", null);
	    } catch (Exception ex) {
		return null;
	    }
	    Pattern pvPattern = Pattern.compile(pvRegex, java.util.regex.Pattern.MULTILINE);
	    Matcher pvMatcher = pvPattern.matcher(logEntry.getText());
	    while (pvMatcher.find()) {
		result.add(new ProcessVariable(pvMatcher.group(1)));
	    }
	    
	    if (adapterType == ProcessVariable.class) {
		if (result != null && result.size() == 1)
		    return result.iterator().next();
	    } else if (adapterType == ProcessVariable[].class) {
		if (result != null && !result.isEmpty())
		    return result.toArray(new ProcessVariable[result.size()]);
	    }
	}
	return null;
    }

    @Override
    public Class[] getAdapterList() {
	return new Class[] { ProcessVariable.class, ProcessVariable[].class };
    }

}
