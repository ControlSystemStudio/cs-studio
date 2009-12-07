package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * An action button widget model.
 * 
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen
 * 
 */
public final class ActionButtonModel extends AbstractPVWidgetModel {
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
	public static final String PROP_ACTION_INDEX = "push_action_index"; //$NON-NLS-1$
	
	
	/**
	 * The ID of the ActionData property.
	 */
	public static final String PROP_RELEASED_ACTION_INDEX = "release_action_index"; //$NON-NLS-1$
	
	
	/**
	 * The ID of the ToggelButton property.
	 */
	public static final String PROP_TOGGLE_BUTTON= "toggle_button"; //$NON-NLS-1$
	
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
    
	private static final String[] FILE_EXTENSIONS = new String[] {"jpg", "jpeg", "gif", "bmp", "png"};


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
				WidgetPropertyCategory.Behavior, 0, -1, Integer.MAX_VALUE));
		
		addProperty(new IntegerProperty(PROP_RELEASED_ACTION_INDEX, "Release Action Index",
				WidgetPropertyCategory.Behavior, 0, -1, Integer.MAX_VALUE));
		
		addProperty(new BooleanProperty(PROP_TOGGLE_BUTTON, "Toggle Button",
		        WidgetPropertyCategory.Behavior,DEFAULT_TOGGLE_BUTTON));
		
		addProperty(new FilePathProperty(PROP_IMAGE, "Icon File",
				WidgetPropertyCategory.Display, new Path(""), FILE_EXTENSIONS)); //$NON-NLS-1$
		
		removeProperty(PROP_ACTIONS);		
		addProperty(new ActionsProperty(PROP_ACTIONS, "Actions", 
				WidgetPropertyCategory.Behavior, false));
		
		
		removeProperty(PROP_BORDER_COLOR);
		removeProperty(PROP_BORDER_STYLE);
		removeProperty(PROP_BORDER_WIDTH);
		removeProperty(PROP_BORDER_ALARMSENSITIVE);
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
	public OPIFont getFont() {
		return (OPIFont) getProperty(PROP_FONT).getPropertyValue();
	}
	
	public IPath getImagePath(){
		IPath absolutePath = (IPath) getProperty(PROP_IMAGE).getPropertyValue();
		if(!absolutePath.isAbsolute())
			absolutePath = ResourceUtil.buildAbsolutePath(this, absolutePath);
		return absolutePath;
	}
	
	/**
	 * Returns whether the button is a toggle button.
	 *  @return false = Push, true=Toggle
	 */
	public boolean isToggleButton(){
	    return (Boolean)getProperty(PROP_TOGGLE_BUTTON).getPropertyValue();
	}
}
