package org.csstudio.swt.datadefinition;

import java.util.EventListener;

/**
 * Definition of listeners that react on knob events.
 * 
 * @author Xihui Chen
 * 
 */
public interface IManualValueChangeListener extends EventListener{
	/**
	 * React on a knob event.
	 * 
	 * @param newValue
	 *            The new slider value.
	 */
	void manualValueChanged(double newValue);
}
