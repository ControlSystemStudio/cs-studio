package org.csstudio.shift.ui;

import gov.bnl.shiftClient.Shift;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class ShiftViewerModel implements IEditorInput {

    private Shift shift;

    /**
     * 
     */
    public ShiftViewerModel(final Shift shift) {
    	this.shift = shift;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter(final Class adapter) {
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
    	final StringBuffer sb = new StringBuffer("Detail View for shift");
		if (shift != null) {
		    sb.append(": " + shift.getId());
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
    	final StringBuffer sb = new StringBuffer("Detail View for shift");
		if (shift != null) {
		    sb.append(": " + shift.getId());
		}
		return sb.toString();
    }

    public Shift getShift() {
    	return this.shift;
    }

}
