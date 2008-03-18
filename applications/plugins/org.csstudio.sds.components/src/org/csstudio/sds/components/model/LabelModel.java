/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.ArrayOptionProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * A label widget model.
 * 
 * @author jbercic
 * 
 */
public final class LabelModel extends AbstractWidgetModel {
	/**
	 * Unique identifier.
	 */
	public static final String ID = "org.csstudio.sds.components.Label";
	
	/**
	 * The ID of the <i>font</i> property.
	 */
	public static final String PROP_FONT = "font";
	/**
	 * The ID of the <i>text alignment</i> property.
	 */
	public static final String PROP_TEXT_ALIGN = "textAlignment";
	/**
	 * The ID of the <i>transparent</i> property.
	 */
	public static final String PROP_TRANSPARENT = "transparent_background";
	/**
	 * The ID of the <i>rotation</i> property.
	 */
	public static final String PROP_ROTATION = "text_rotation";
	/**
	 * The ID of the <i>x offset</i> property.
	 */
	public static final String PROP_XOFF = "offset.x";
	/**
	 * The ID of the <i>y offset</i> property.
	 */
	public static final String PROP_YOFF = "offset.y";
	/**
	 * The ID of the precision property.
	 */
	public static final String PROP_PRECISION = "precision"; //$NON-NLS-1$
	/**
	 * Type of the displayed text.
	 */
	public static final String PROP_TYPE = "value_type";
	/**
	 * Text value.
	 */
	public static final String PROP_TEXTVALUE = "value.text";
	/**
	 * The ID of the <i>text type</i> property.
	 */
	public static final int TYPE_TEXT = 0;
	/**
	 * The ID of the <i>double type</i> property.
	 */
	public static final int TYPE_DOUBLE = 1;

	/**
	 * Constructor.
	 */
	public LabelModel() {
		setWidth(100);
		setHeight(30);
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
		addProperty(PROP_FONT, new FontProperty("Font",WidgetPropertyCategory.Display, new FontData("Arial", 8, SWT.NONE)));
		addProperty(PROP_TEXT_ALIGN, new ArrayOptionProperty("Text Alignment",WidgetPropertyCategory.Display, TextAlignmentEnum.getDisplayNames() ,TextAlignmentEnum.CENTER.getIndex()));
		addProperty(PROP_TRANSPARENT, new BooleanProperty("Transparent Background",WidgetPropertyCategory.Display,true));
		addProperty(PROP_ROTATION, new DoubleProperty("Text Rotation Angle",WidgetPropertyCategory.Display,90.0,0.0,360.0));
		addProperty(PROP_XOFF, new IntegerProperty("X Offset",WidgetPropertyCategory.Display,0));
		addProperty(PROP_YOFF, new IntegerProperty("Y Offset",WidgetPropertyCategory.Display,0));
		
		//value properties
		addProperty(PROP_TYPE, new ArrayOptionProperty("Value Type",WidgetPropertyCategory.Behaviour, TextTypeEnum.getDisplayNames(), TextTypeEnum.DOUBLE.getIndex()));
		addProperty(PROP_TEXTVALUE, new StringProperty("Text Value",WidgetPropertyCategory.Display,""));
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
		buffer.append("Value:\t");
		buffer.append(createParameter(PROP_TEXTVALUE));
		return buffer.toString();
	}

	/**
	 * Returns the font for the label.
	 * @return The font
	 */
	public FontData getFont() {
		return (FontData) getProperty(PROP_FONT).getPropertyValue();
	}
	
	/**
	 * Return the precision.
	 * 
	 * @return The precision.
	 */
	public int getPrecision() {
		return (Integer) getProperty(PROP_PRECISION).getPropertyValue();
	}

	/**
	 * Returns the alignment of the text.
	 * @return The alignment of the text
	 */
	public int getTextAlignment() {
		return (Integer) getProperty(PROP_TEXT_ALIGN).getPropertyValue();
	}
	
	/**
	 * Returns the transparent state of the background.
	 * @return True if the background is transparent, false otherwise
	 */
	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	/**
	 * Returns the value for the rotation of the text.
	 * @return The value for the rotation of the text
	 */
	public double getRotation() {
		return (Double) getProperty(PROP_ROTATION).getPropertyValue();
	}
	
	/**
	 * Returns the value for the x offset.
	 * @return The value for the x offset
	 */
	public int getXOff() {
		return (Integer) getProperty(PROP_XOFF).getPropertyValue();
	}
	
	/**
	 * Returns the value for the y offset.
	 * @return The value for the y offset
	 */
	public int getYOff() {
		return (Integer) getProperty(PROP_YOFF).getPropertyValue();
	}
	
	/**
	 * Returns the type of the text (Double or String).
	 * @return The type of the text
	 */
	public int getType() {
		return (Integer) getProperty(PROP_TYPE).getPropertyValue();
	}
	
	/**
	 * Returns the text.
	 * @return The text
	 */
	public String getTextValue() {
		return (String) getProperty(PROP_TEXTVALUE).getPropertyValue();
	}
	
}
