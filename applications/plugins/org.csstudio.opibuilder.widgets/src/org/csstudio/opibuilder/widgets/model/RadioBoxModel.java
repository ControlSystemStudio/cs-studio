/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.ui.util.CustomMediaFactory;


/**Model of Radio Box.
 * @author Xihui Chen
 *
 */
public class RadioBoxModel extends AbstractChoiceModel {

	public final String ID = "org.csstudio.opibuilder.widgets.radioBox";

	public RadioBoxModel() {
		setPropertyValue(PROP_SELECTED_COLOR, CustomMediaFactory.COLOR_BLACK);
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	

}
