/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Version;

/**
 * An action button widget model.
 * 
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen
 * 
 */
public class ActionButtonModel extends AbstractPVWidgetModel implements ITextModel{
	public enum Style{
		CLASSIC("Classic"), //$NON-NLS-1$
		NATIVE("Native");//$NON-NLS-1$
		
		private String description;
		private Style(String description) {
			this.description = description;
		}
		
		@Override
		public String toString() {
			return description;
		}
		
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i =0 ;
			for(Style f : values()){
				result[i++] = f.toString();
			}
			return result;
		}
	}
	
	/**
	 * Button Style
	 */
	public static final String PROP_STYLE = "style"; //$NON-NLS-1$
	
	/**
	 * Text on the button.
	 */
	public static final String PROP_TEXT = "text"; //$NON-NLS-1$
	
	/**
	 * Image on the button.
	 */
	public static final String PROP_IMAGE = "image"; //$NON-NLS-1$
	
	/**
	 * The index of the action to be executed when button is pushed. 
	 * It is corresponding to the action sort index in actions property. 
	 */
	public static final String PROP_ACTION_INDEX = "push_action_index"; //$NON-NLS-1$
	
	
	/**
	 * The index of the action to be executed when released. 
	 * It is corresponding to the action sort index in actions property. 
	 */
	public static final String PROP_RELEASED_ACTION_INDEX = "release_action_index"; //$NON-NLS-1$
	
	
	/**
	 * If yes, the button will not bounce up when clicked. 
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
    
	private static final String[] FILE_EXTENSIONS =
			new String[] {"jpg", "jpeg", "gif", "bmp", "png"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$


	/**
	 * Standard constructor.
	 */
	public ActionButtonModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
		setPropertyValue(PROP_BORDER_ALARMSENSITIVE, false);
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
		
		addProperty(new ComboProperty(PROP_STYLE, "Style", WidgetPropertyCategory.Display,
				Style.stringValues(), Style.NATIVE.ordinal()));
		
		addProperty(new StringProperty(PROP_TEXT, "Text",
				WidgetPropertyCategory.Display, "$(actions)", true)); //$NON-NLS-1$
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
		
		
		setPropertyVisible(PROP_RELEASED_ACTION_INDEX, DEFAULT_TOGGLE_BUTTON);
		
	}
	
	
	@Override
	public void processVersionDifference() {		
		//There was no style property before 2.0.0
		if(getVersionOnFile().getMajor() <2){			
			// convert native button widget to native style		
			if (getWidgetType().equals("Button")){ //$NON-NLS-N$
				setStyle(Style.NATIVE);
				setPropertyValue(PROP_WIDGET_TYPE, "Action Button");
			}
			else
				setStyle(Style.CLASSIC);			
		}		
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
	
	@Override
	public void setText(String text) {
		setPropertyValue(PROP_TEXT, text);
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
	
	public Style getStyle(){
		return Style.values()[(Integer)getProperty(PROP_STYLE).getPropertyValue()];
	}
	
	@Override
	public Version getVersion() {
		return new Version(2, 0, 0);
	}
	
	public void setStyle(Style style){
		setPropertyValue(PROP_STYLE, style.ordinal());
	}
	
}
