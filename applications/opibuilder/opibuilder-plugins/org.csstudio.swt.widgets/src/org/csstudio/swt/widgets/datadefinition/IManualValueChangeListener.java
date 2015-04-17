/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.datadefinition;

import java.util.EventListener;

/**
 * Definition of listeners that react on manual value change events.
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
