/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.model;

import org.csstudio.sds.components.internal.localization.Messages;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * An action button widget model.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class ActionButtonModel extends AbstractWidgetModel {
	/**
	 * The ID of the label property.
	 */
	public static final String PROP_LABEL = "label"; //$NON-NLS-1$
	/**
	 * The ID of the font property.
	 */
	public static final String PROP_FONT = "font"; //$NON-NLS-1$
	/**
	 * The ID of the text alignment property.
	 */
	public static final String PROP_TEXT_ALIGNMENT = "textAlignment"; //$NON-NLS-1$
	/**
	 * The ID of the ActionData property.
	 */
	public static final String PROP_ACTION_PRESSED_INDEX = "action_pressed_index"; //$NON-NLS-1$
	/**
	 * The ID of the ActionData property.
	 */
	public static final String PROP_ACTION_RELEASED_INDEX = "action_released_index"; //$NON-NLS-1$
	/**
	 * The ID of the ToggelButton property.
	 */
	public static final String PROP_TOGGLE_BUTTON= "toggelButton"; //$NON-NLS-1$
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.ActionButton"; //$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 20;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 80;
	
	/**
	 * The default value of the text alignment property.
	 */
	private static final int DEFAULT_TEXT_ALIGNMENT = 0;
	
	/**
	 * The default value of the Button style.  
	 */
    private static final boolean DEFAULT_TOGGLE_BUTTON = false;
    
	/**
	 * The labels for the text alignment property.
	 */
	private static final String[] SHOW_LABELS = new String[] {"Center", "Top", "Bottom", "Left", "Right"};

	/**
	 * Standard constructor.
	 */
	public ActionButtonModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
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
		addProperty(PROP_LABEL, new StringProperty(Messages.LabelElement_LABEL,
				WidgetPropertyCategory.Display, "")); //$NON-NLS-1$
		addProperty(PROP_FONT, new FontProperty("Font",
				WidgetPropertyCategory.Display, new FontData(
						"Arial", 8, SWT.NONE))); //$NON-NLS-1$
		addProperty(PROP_TEXT_ALIGNMENT, new OptionProperty("Text Alignment", 
				WidgetPropertyCategory.Display, SHOW_LABELS, DEFAULT_TEXT_ALIGNMENT));
		addProperty(PROP_ACTION_RELEASED_INDEX, new IntegerProperty("Action Index (released)",
				WidgetPropertyCategory.Behaviour, 0, -1, Integer.MAX_VALUE));
		addProperty(PROP_ACTION_PRESSED_INDEX, new IntegerProperty("Action Index (pressed)",
				WidgetPropertyCategory.Behaviour, -1, -1, Integer.MAX_VALUE));
		addProperty(PROP_TOGGLE_BUTTON, new BooleanProperty("Toggel Button",
		        WidgetPropertyCategory.Behaviour,DEFAULT_TOGGLE_BUTTON));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createParameter(PROP_NAME)+"\n");
		buffer.append(createParameter(PROP_ACTIONDATA)+"\n");
		buffer.append("Performed Action: ");
		buffer.append(createParameter(PROP_ACTION_PRESSED_INDEX));
		buffer.append(createParameter(PROP_ACTION_RELEASED_INDEX));
		return buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_LABEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColorTestProperty() {
		return PROP_COLOR_BACKGROUND;
	}
	
	/**
	 * Return the index of the selected WidgetAction from the ActionData.
	 * The Action is running when the button is released.
	 * @return The index
	 */
	public int getChoosenReleasedActionIndex() {
		return (Integer) getProperty(PROP_ACTION_RELEASED_INDEX).getPropertyValue();
	}
	
	/**
	 * Return the index of the selected WidgetAction from the ActionData.
	 * The Action is running when the button is pressed.
	 * @return The index
	 */
	public int getChoosenPressedActionIndex() {
		return (Integer) getProperty(PROP_ACTION_PRESSED_INDEX).getPropertyValue();
	}

	/**
	 * Return the label text.
	 * 
	 * @return The label text.
	 */
	public String getLabel() {
		return (String) getProperty(PROP_LABEL).getPropertyValue();
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
	 * Returns the alignment for the text.
	 * @return int 
	 * 			0 = Center, 1 = Top, 2 = Bottom, 3 = Left, 4 = Right
	 */
	public int getTextAlignment() {
		return (Integer) getProperty(PROP_TEXT_ALIGNMENT).getPropertyValue();
	}

	/**
	 * Return the button style.
	 *  @return false = Push, true=Toggle
	 */
	public boolean getButtonStyle(){
	    return (Boolean)getProperty(PROP_TOGGLE_BUTTON).getPropertyValue();
	}
}
