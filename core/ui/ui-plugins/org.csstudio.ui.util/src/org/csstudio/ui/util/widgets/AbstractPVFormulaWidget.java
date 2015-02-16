package org.csstudio.ui.util.widgets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.widgets.Composite;

/**
 * An abstract class that handles the pvFormula property.
 * 
 * @author carcassi
 *
 */
public class AbstractPVFormulaWidget extends Composite {
	
	private String pvFormula;
	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public AbstractPVFormulaWidget(Composite parent, int style) {
		super(parent, style);
	}
	
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }
	
	public String getPVFormula() {
		return pvFormula;
	}
	
	public void setPVFormula(String pvFormula) {
		// If new query is the same, don't change -- you may lose the cached result
		if (getPVFormula() != null && getPVFormula().equals(pvFormula))
			return;
		if (getPVFormula() == null && pvFormula == null)
			return;
		
		String oldValue = this.pvFormula;
		this.pvFormula = pvFormula;
		changeSupport.firePropertyChange("pvFormula", oldValue, pvFormula);
	}
	
}
