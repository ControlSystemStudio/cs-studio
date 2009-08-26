package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.FontData;

/**
 * An action button widget model.
 * 
 * @author Xihui Chen
 * 
 */
public final class ActionButtonModel extends AbstractWidgetModel {
	/**
	 * The ID of the label property.
	 */
	public static final String PROP_TEXT = "text"; //$NON-NLS-1$
	
	/**
	 * The ID of the label property.
	 */
	public static final String PROP_IMAGE = "image"; //$NON-NLS-1$
	
	/**
	 * The ID of the font property.
	 */
	public static final String PROP_FONT = "font"; //$NON-NLS-1$

	
	/**
	 * The ID of the ActionData property.
	 */
	public static final String PROP_ACTION_INDEX = "action_index"; //$NON-NLS-1$
	
	
	/**
	 * The ID of the ActionData property.
	 */
	public static final String PROP_RELEASED_ACTION_INDEX = "pop_action_index"; //$NON-NLS-1$
	
	
	/**
	 * The ID of the ToggelButton property.
	 */
	public static final String PROP_TOGGLE_BUTTON= "toggleButton"; //$NON-NLS-1$
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.ActionButton"; //$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 40;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 80;
	
	/**
	 * The default value of the Button style.  
	 */
    private static final boolean DEFAULT_TOGGLE_BUTTON = false;

	/**
	 * Standard constructor.
	 */
	public ActionButtonModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_TEXT, "Text",
				WidgetPropertyCategory.Display, "")); //$NON-NLS-1$
		addProperty(new FontProperty(PROP_FONT, "Font",
				WidgetPropertyCategory.Display, CustomMediaFactory.FONT_ARIAL)); //$NON-NLS-1$
		addProperty(new IntegerProperty(PROP_ACTION_INDEX, "Click Action Index",
				WidgetPropertyCategory.Behavior, 0, 0, Integer.MAX_VALUE));
		
		addProperty(new IntegerProperty(PROP_RELEASED_ACTION_INDEX, "Released Action Index",
				WidgetPropertyCategory.Behavior, 0, 0, Integer.MAX_VALUE));
		
		addProperty(new BooleanProperty(PROP_TOGGLE_BUTTON, "Toggle Button",
		        WidgetPropertyCategory.Behavior,DEFAULT_TOGGLE_BUTTON));
		
		removeProperty(PROP_BORDER_COLOR);
		removeProperty(PROP_BORDER_STYLE);
		removeProperty(PROP_BORDER_WIDTH);
		setPropertyVisible(PROP_RELEASED_ACTION_INDEX, DEFAULT_TOGGLE_BUTTON);
		
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
	
	/**
	 * Return the label text.
	 * 
	 * @return The label text.
	 */
	public String getText() {
		return (String) getProperty(PROP_TEXT).getPropertyValue();
	}

	/**
	 * Return the label font.
	 * 
	 * @return The label font.
	 */
	public FontData getFont() {
		return (FontData) getProperty(PROP_FONT).getPropertyValue();
	}
	
	/**
	 * Returns whether the button is a toggle button.
	 *  @return false = Push, true=Toggle
	 */
	public boolean isToggleButton(){
	    return (Boolean)getProperty(PROP_TOGGLE_BUTTON).getPropertyValue();
	}
}
