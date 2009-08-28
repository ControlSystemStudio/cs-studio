package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * Abstract model for boolean controls.
 * @author Xihui Chen
 *
 */
public class AbstractBoolControlModel extends AbstractBoolWidgetModel {


	/**
	 * The ID of the ToggelButton property.
	 */
	public static final String PROP_TOGGLE_BUTTON= "toggleButton"; //$NON-NLS-1$
	
	/** The ID of the show confirm dialog property. */
	public static final String PROP_CONFIRM_DIALOG = "confirmDialog"; //$NON-NLS-1$
	
	/** The ID of the password property. */
	public static final String PROP_PASSWORD = "password"; //$NON-NLS-1$
	
	/** The ID of the confirm tip property. */
	public static final String PROP_CONFIRM_TIP = "confirmTip"; //$NON-NLS-1$	

	
	public static final String DEFAULT_CONFIRM_TIP = "Are your sure you want to do this?";
	
	/**
	 * The ID of the pv name property.
	 */
	public static final String PROP_CONTROL_PV= "control_pv"; //$NON-NLS-1$
	
	/**
	 * The ID of the pv value property.
	 */
	public static final String PROP_CONTROL_PV_VALUE= "control_pv_value"; //$NON-NLS-1$
	
	/**
	 * The ID of the ActionData property.
	 */
	public static final String PROP_ACTION_INDEX = "action_index"; //$NON-NLS-1$
	
	
	/**
	 * The ID of the ActionData property.
	 */
	public static final String PROP_RELEASED_ACTION_INDEX = "pop_action_index"; //$NON-NLS-1$

	private static final boolean DEFAULT_TOGGLE_BUTTON = true;
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		//addPVProperty(new StringProperty(PROP_CONTROL_PV, "Control PV", WidgetPropertyCategory.Basic,
		//		""), new PVValueProperty(PROP_CONTROL_PV_VALUE, null));
		
		addProperty(new BooleanProperty(PROP_TOGGLE_BUTTON, "Toggle Button", 
				WidgetPropertyCategory.Behavior, DEFAULT_TOGGLE_BUTTON));
		addProperty(new BooleanProperty(PROP_CONFIRM_DIALOG, "Show Confirm Dialog", 
				WidgetPropertyCategory.Behavior, false));
		addProperty(new StringProperty(PROP_PASSWORD, "Password", 
				WidgetPropertyCategory.Behavior, ""));		
		addProperty(new StringProperty(PROP_CONFIRM_TIP, "Confirm Message", 
				WidgetPropertyCategory.Behavior, DEFAULT_CONFIRM_TIP));		
	
		addProperty(new IntegerProperty(PROP_ACTION_INDEX, "Click Action Index",
				WidgetPropertyCategory.Behavior, 0, 0, Integer.MAX_VALUE));
		
		addProperty(new IntegerProperty(PROP_RELEASED_ACTION_INDEX, "Released Action Index",
				WidgetPropertyCategory.Behavior, 0, 0, Integer.MAX_VALUE));
		
		setPropertyVisible(PROP_RELEASED_ACTION_INDEX, DEFAULT_TOGGLE_BUTTON );
	}
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sns.widgets.LED"; //$NON-NLS-1$	
	
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * @return true if the button is toggle.
	 */
	public boolean isToggleButton() {
		return (Boolean) getProperty(PROP_TOGGLE_BUTTON).getPropertyValue();
	}
	
	/**
	 * @return true if the confirm dialog should be shown, false otherwise
	 */
	public boolean isShowConfirmDialog() {
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
	public int getActionIndex() {
		return (Integer) getProperty(PROP_ACTION_INDEX).getPropertyValue();
	}
	
	
	/**
	 * Return the index of the selected WidgetAction from the ActionData.
	 * The Action is running when the button is released.
	 * @return The index
	 */
	public int getReleasedActionIndex() {
		return (Integer) getProperty(PROP_RELEASED_ACTION_INDEX).getPropertyValue();
	}
}
