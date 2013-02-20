/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;


/**
 * Model for native button widget.
 * 
 * @author Xihui Chen
 * @deprecated the native button is not used anymore. Instead use Style property of Action Button.
 * 
 */
public final class NativeButtonModel extends ActionButtonModel{
	
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.NativeButton"; //$NON-NLS-1$


	@Override
	protected void configureProperties() {
		super.configureProperties();
		removeProperty(PROP_BACKCOLOR_ALARMSENSITIVE);
		removeProperty(PROP_COLOR_BACKGROUND);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}


	
	
	

}
