package org.csstudio.ui.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.widgets.Composite;

/**
 * Implements the bean bound property notification scheme on top of a composite.
 * Subclasses should specify in the documentation which properties are bound.
 * 
 * @author Gabriele Carcassi
 */
public abstract class BeanComposite extends Composite {
	
	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	/**
	 * Pass through constructor to Composite.
	 * 
	 * @param parent composite parent
	 * @param style SWT style
	 */
	public BeanComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * Adds a new listener.
	 * 
	 * @param listener a new listener
	 */
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    /**
     * Removes the given listener.
     * 
     * @param listener a listener
     */
    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }

}
