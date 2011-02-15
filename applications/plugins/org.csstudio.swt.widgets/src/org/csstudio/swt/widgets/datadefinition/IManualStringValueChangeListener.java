package org.csstudio.swt.widgets.datadefinition;

import java.util.EventListener;

/**
 * Definition of listeners that react on string value change.
 * 
 * @author Xihui Chen
 * 
 */
public interface IManualStringValueChangeListener extends EventListener{
	/**
	 * React on a manual value changing.
	 * 
	 * @param newValue
	 *            The new value.
	 */
	void manualValueChanged(String newValue);
}
