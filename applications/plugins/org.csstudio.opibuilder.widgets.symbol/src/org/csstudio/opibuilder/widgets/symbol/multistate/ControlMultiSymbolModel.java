/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.multistate;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * @author Fred Arnaud (Sopra Group)
 */
public class ControlMultiSymbolModel extends CommonMultiSymbolModel {

	/**
	 * Type ID for Multistate Symbol Image Control widget
	 */
	private static final String ID = "org.csstudio.opibuilder.widgets.symbol.multistate.MultistateControlWidget";

	@Override
	public String getTypeID() {
		return ID;
	}

	/** If a confirm dialog should be shown before performing action. */
	public static final String PROP_CONFIRM_DIALOG = "show_confirm_dialog"; //$NON-NLS-1$
	
	/** The password needed to perform action*/
	public static final String PROP_PASSWORD = "password"; //$NON-NLS-1$
	
	/** The message which will be shown on confirm dialog. */
	public static final String PROP_CONFIRM_TIP = "confirm_message"; //$NON-NLS-1$	
	public static final String DEFAULT_CONFIRM_TIP = "Are your sure you want to do this?";	
	
	/**
	 * The action which will be executed when widget is pushed. It is the index the actions in 
	 * actions property.
	 */
	public static final String PROP_PUSH_ACTION_INDEX = "push_action_index"; //$NON-NLS-1$
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new BooleanProperty(PROP_CONFIRM_DIALOG,
				"Show Confirm Dialog", WidgetPropertyCategory.Behavior, false));

		addProperty(new StringProperty(PROP_PASSWORD, "Password",
				WidgetPropertyCategory.Behavior, ""));
		addProperty(new StringProperty(PROP_CONFIRM_TIP, "Confirm Message",
				WidgetPropertyCategory.Behavior, DEFAULT_CONFIRM_TIP));

		addProperty(new IntegerProperty(PROP_PUSH_ACTION_INDEX,
				"Push Action Index", WidgetPropertyCategory.Behavior, 0, 0,
				Integer.MAX_VALUE));
	}
	
	/**
	 * @return true if the confirm dialog should be shown, false otherwise
	 */
	public boolean getShowConfirmDialog() {
		return (Boolean) getProperty(PROP_CONFIRM_DIALOG).getPropertyValue();
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return (String) getProperty(PROP_PASSWORD).getPropertyValue();
	}
	
	/**
	 * @return the confirm tip
	 */
	public String getConfirmTip() {
		return (String) getProperty(PROP_CONFIRM_TIP).getPropertyValue();
	}
	
	/**
	 * Return the index of the selected WidgetAction from the ActionData.
	 * The Action is running when the button is released.
	 * @return The index
	 */
	public int getPushActionIndex() {
		return (Integer) getProperty(PROP_PUSH_ACTION_INDEX).getPropertyValue();
	}
	
}
