/**
 * 
 */
package org.csstudio.logbook.ui;

import java.io.IOException;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @author shroffk
 * 
 */
public class LogEntryAdapterFactory implements IAdapterFactory {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
     * java.lang.Class)
     */
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
	LogEntry logEntry = ((LogEntry) adaptableObject);
	if (adapterType == LogEntryBuilder.class) {
	    try {
		return LogEntryBuilder.logEntry(logEntry);
	    } catch (IOException e) {
		return null;
	    }
	} else {
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    @Override
    public Class[] getAdapterList() {
	return new Class[] { LogEntryBuilder.class };
    }

}
