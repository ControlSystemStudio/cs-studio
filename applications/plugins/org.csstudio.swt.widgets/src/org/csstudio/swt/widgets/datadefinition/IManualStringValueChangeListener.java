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
