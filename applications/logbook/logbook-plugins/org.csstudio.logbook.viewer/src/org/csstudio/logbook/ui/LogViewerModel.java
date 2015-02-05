/**
 * 
 */
package org.csstudio.logbook.ui;

import org.csstudio.logbook.LogEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author shroffk
 * 
 */
public class LogViewerModel implements IEditorInput {

    private LogEntry logEntry;

    /**
     * 
     */
    public LogViewerModel(LogEntry logEntry) {
	this.logEntry = logEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
	@Override
    public Object getAdapter(Class adapter) {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    @Override
    public boolean exists() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    @Override
    public String getName() {
	StringBuffer sb = new StringBuffer("Detail View for logEntry");
	if (logEntry != null) {
	    sb.append(": " + logEntry.getId());
	}
	return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    @Override
    public IPersistableElement getPersistable() {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    @Override
    public String getToolTipText() {
	StringBuffer sb = new StringBuffer("Detail View for logEntry");
	if (logEntry != null) {
	    sb.append(": " + logEntry.getId());
	}
	return sb.toString();
    }

    public LogEntry getLogEntry() {
	return this.logEntry;
    }

}
