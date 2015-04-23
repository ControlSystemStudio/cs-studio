/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.eclipse.swt.graphics.RGB;

/**Model of Choice Button.
 * @author Xihui Chen
 *
 */
public class ChoiceButtonModel extends AbstractChoiceModel {

	public final String ID = "org.csstudio.opibuilder.widgets.choiceButton";

	public ChoiceButtonModel() {
		setBackgroundColor(new RGB(240,240,240));
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	

}
