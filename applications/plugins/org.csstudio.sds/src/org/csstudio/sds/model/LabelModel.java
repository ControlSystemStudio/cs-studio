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
package org.csstudio.sds.model;

import java.util.List;

import org.csstudio.sds.cursorservice.AbstractCursor;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.eclipse.swt.graphics.FontData;

/**
 * A label widget model.
 *
 * @author jbercic
 *
 */
public class LabelModel extends AbstractTextTypeWidgetModel {
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
    public static final String PROP_TEXT_ROTATION = "text_rotation";
    /**
     * The ID of the <i>x offset</i> property.
     */
    public static final String PROP_XOFF = "offset.x";
    /**
     * The ID of the <i>y offset</i> property.
     */
    public static final String PROP_YOFF = "offset.y";
    /**
     * Text value.
     */
    public static final String PROP_TEXTVALUE = "value.text";
    /**
     * EGU Value
     */
    public static final String PROP_TEXT_UNIT = "value.text.unit";

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
     * Constructor.
     */
    public LabelModel(final List<AbstractCursor> cursorDescriptors) {
        super(cursorDescriptors);
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
        // Format
        addFontProperty(PROP_FONT, "Font", WidgetPropertyCategory.FORMAT, ColorAndFontUtil.toFontString("Arial", 8), false, PROP_COLOR_FOREGROUND);
        addArrayOptionProperty(PROP_TEXT_ALIGN, "Text Alignment", WidgetPropertyCategory.FORMAT, TextAlignmentEnum.getDisplayNames(),
                TextAlignmentEnum.CENTER.getIndex(), false, PROP_FONT);
        addDoubleProperty(PROP_TEXT_ROTATION, "Text Rotation Angle", WidgetPropertyCategory.FORMAT, 90.0, 0.0, 360.0, false, PROP_TEXT_ALIGN);
        addIntegerProperty(PROP_XOFF, "X Offset", WidgetPropertyCategory.FORMAT, 0, false, PROP_TEXT_ROTATION);
        addIntegerProperty(PROP_YOFF, "Y Offset", WidgetPropertyCategory.FORMAT, 0, false, PROP_XOFF);
        addBooleanProperty(PROP_TRANSPARENT, "Transparent Background", WidgetPropertyCategory.FORMAT, true, true, AbstractWidgetModel.PROP_COLOR_BACKGROUND);

        // Display
        addStringProperty(PROP_TEXTVALUE, "Text Value", WidgetPropertyCategory.DISPLAY, "", true, PROP_TOOLTIP);
        addStringProperty(PROP_TEXT_UNIT, "Value Unit", WidgetPropertyCategory.DISPLAY, "", false, PROP_TEXTVALUE);
        addArrayOptionProperty(PROP_TEXT_TYPE, "Value Type", WidgetPropertyCategory.DISPLAY, TextTypeEnum.getDisplayNames(),
                               TextTypeEnum.DOUBLE.getIndex(), false, PROP_TEXT_UNIT);
        addIntegerProperty(PROP_PRECISION, "Decimal places", WidgetPropertyCategory.DISPLAY, 2, 0, 10, false, PROP_TEXT_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Value:\t");
        buffer.append(createTooltipParameter(PROP_TEXTVALUE));
        return buffer.toString();
    }

    /**
     * Sets the font.
     *
     * @param value
     *            the label text
     */
    public void setFont(final FontData font) {
        setPropertyValue(PROP_FONT, font);
    }

    /**
     * Returns the alignment of the text.
     *
     * @return The alignment of the text
     */
    public int getTextAlignment() {
        return getArrayOptionProperty(PROP_TEXT_ALIGN);
    }

    /**
     * Returns the transparent state of the background.
     *
     * @return True if the background is transparent, false otherwise
     */
    @Override
    public boolean getTransparent() {
        return getBooleanProperty(PROP_TRANSPARENT);
    }

    /**
     * Returns the value for the rotation of the text.
     *
     * @return The value for the rotation of the text
     */
    public double getRotation() {
        return getDoubleProperty(PROP_TEXT_ROTATION);
    }

    /**
     * Returns the value for the x offset.
     *
     * @return The value for the x offset
     */
    public int getXOff() {
        return getIntegerProperty(PROP_XOFF);
    }

    /**
     * Returns the value for the y offset.
     *
     * @return The value for the y offset
     */
    public int getYOff() {
        return getIntegerProperty(PROP_YOFF);
    }

    /**
     * Sets the label text.
     *
     * @param value
     *            the label text
     */
    public void setTextValue(final String value) {
        setPropertyValue(PROP_TEXTVALUE, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringValueID() {
        return PROP_TEXTVALUE;
    }
}
