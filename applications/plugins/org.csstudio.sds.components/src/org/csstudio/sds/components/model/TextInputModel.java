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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.optionEnums.TextAlignmentEnum;
import org.csstudio.sds.model.optionEnums.TextTypeEnum;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * A widget model for text inputs.
 * 
 * @author Alexander Will, Kai Meyer
 * @version $Revision$
 * 
 */
public final class TextInputModel extends AbstractWidgetModel {
	/**
	 * The ID of the text input.
	 */
	public static final String PROP_INPUT_TEXT = "inputText"; //$NON-NLS-1$

	/**
	 * The ID of the font property.
	 */
	public static final String PROP_FONT = "font"; //$NON-NLS-1$
	
	/**
	 * The ID of the text alignment property.
	 */
	public static final String PROP_TEXT_ALIGNMENT = "textAlignment"; //$NON-NLS-1$
	
	/**
	 * The ID of the precision property.
	 */
	public static final String PROP_PRECISION = "precision"; //$NON-NLS-1$

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.Textinput"; //$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 20;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 80;	
	/**
	 * The ID of the <i>transparent</i> property.
	 */
	public static final String PROP_TRANSPARENT = "transparent";
	/**
     * Type of the displayed text.
     */
    public static final String PROP_TYPE = "value_type";
    /**
     * The ID of the <i>double type</i> property.
     */
    public static final int TYPE_DOUBLE = 1;

	/**
	 * Standard constructor.
	 */
	public TextInputModel() {
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
		addProperty(PROP_INPUT_TEXT, new StringProperty("Input Text", 
				WidgetPropertyCategory.Behaviour, "")); //$NON-NLS-1$
		addProperty(PROP_FONT, new FontProperty("Font",
				WidgetPropertyCategory.Display, new FontData("Arial", 8, SWT.NONE))); //$NON-NLS-1$
		addProperty(PROP_TEXT_ALIGNMENT, new OptionProperty("Text Alignment", 
				WidgetPropertyCategory.Display, TextAlignmentEnum.getDisplayNames() ,TextAlignmentEnum.CENTER.getIndex()));
		addProperty(PROP_TRANSPARENT, new BooleanProperty("Transparent Background",WidgetPropertyCategory.Display,true));
		addProperty(PROP_TYPE, new OptionProperty("Value Type",WidgetPropertyCategory.Behaviour, TextTypeEnum.getDisplayNames(), TextTypeEnum.DOUBLE.getIndex()));
		addProperty(PROP_PRECISION, new IntegerProperty("Decimal places",
				WidgetPropertyCategory.Behaviour, 2, 0, 6));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createParameter(PROP_NAME)+"\n");
		buffer.append("Text:\t");
		buffer.append(createParameter(PROP_INPUT_TEXT));
		return buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_INPUT_TEXT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColorTestProperty() {
		return PROP_COLOR_BACKGROUND;
	}
	
	/**
	 * Gets the input text.
	 * @return the input text
	 */
	public String getInputText() {
		return (String) getProperty(PROP_INPUT_TEXT).getPropertyValue();
	}
	
	/**
	 * Gets the font.
	 * @return the font
	 */
	public FontData getFont() {
		return (FontData) getProperty(PROP_FONT).getPropertyValue();
	}
	
	/**
	 * Gets, if the marks should be shown or not.
	 * @return int
	 * 				0 = Center, 1 = Top, 2 = Bottom, 3 = Left, 4 = Right
	 */
	public int getTextAlignment() {
		return (Integer) getProperty(PROP_TEXT_ALIGNMENT).getPropertyValue();
	}
	
	/**
	 * Returns, if this widget should have a transparent background.
	 * @return boolean
	 * 				True, if it should have a transparent background, false otherwise
	 */
	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	/**
     * Returns the type of the text (Double or String).
     * @return The type of the text
     */
    public int getType() {
        return (Integer) getProperty(PROP_TYPE).getPropertyValue();
    }
    
    /**
	 * Return the precision.
	 * 
	 * @return The precision.
	 */
	public int getPrecision() {
		return (Integer) getProperty(PROP_PRECISION).getPropertyValue();
	}
}
