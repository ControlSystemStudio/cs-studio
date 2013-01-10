package org.csstudio.channel.widgets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractConfigurationComposite extends Composite {
	
	// TODO (shroffk) fix access modifier 
	public final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public AbstractConfigurationComposite(Composite parent, int style) {
		super(parent, style);
	}
	
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }

}
