/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.ui.util.ColorConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Model for the ThumbWheel.
 *  @author Alen Vrecko, Jozef Stefan Institute
 *  @author Joerg Rathlev, Universitaet Hamburg
 *  @author Jose Ortega, Xihui Chen
 *  @author Takashi Nakamoto, Cosylab
 *
 */
public class ThumbWheelModel extends AbstractPVWidgetModel {

    public static final String PROP_MIN = "minimum"; //$NON-NLS-1$

    public static final String PROP_MAX = "maximum"; //$NON-NLS-1$

    public static final String PROP_INTERNAL_FRAME_THICKNESS = "internalFrameSize"; //$NON-NLS-1$

    public static final String PROP_INTERNAL_FRAME_COLOR = "internalFrameColor"; //$NON-NLS-1$

    public static final String PROP_INTERNAL_FOCUSED_FRAME_COLOR = "focusedFrameColor"; //$NON-NLS-1$

    public static final String PROP_INTEGER_DIGITS_PART = "integerDigits"; //$NON-NLS-1$

    public static final String PROP_DECIMAL_DIGITS_PART = "decimalDigits"; //$NON-NLS-1$
    /** Load limit from PV. */
    public static final String PROP_LIMITS_FROM_PV = "limits_from_pv"; //$NON-NLS-1$

    public static final String PROP_SHOW_BUTTONS = "show_buttons"; //$NON-NLS-1$

    public static final String ID = "org.csstudio.opibuilder.widgets.ThumbWheel"; //$NON-NLS-1$

//    public static final String PROP_VALUE = "value"; //$NON-NLS-1$

    private static final int DEFAULT_HEIGHT = 60;

    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 120;

    /** The default value of the minimum property. */
    private static final double DEFAULT_MIN = 0;
    /** The default value of the maximum property. */
    private static final double DEFAULT_MAX = 100;

    /** The default value of the number of integer digits property. */
    private static final int DEFAULT_INTEGER_DIGITS = 3;

    /** The default value of the number of decimal digits property. */
    private static final int DEFAULT_DECIMAL_DIGITS = 2;

    /**
     * Standard constructor.
     */
    public ThumbWheelModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setForegroundColor(new RGB(0, 0, 0));
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
//        addProperty(new DoubleProperty(PROP_VALUE, "Value",
//                WidgetPropertyCategory.Behavior, 0));
        addProperty(new DoubleProperty(PROP_MIN, "Minimum",
                WidgetPropertyCategory.Behavior, DEFAULT_MIN));
        addProperty(new DoubleProperty(PROP_MAX, "Maximum",
                WidgetPropertyCategory.Behavior, DEFAULT_MAX));
        addProperty(new IntegerProperty(PROP_INTEGER_DIGITS_PART,
                "Integer Digits", WidgetPropertyCategory.Behavior, DEFAULT_INTEGER_DIGITS));
        addProperty(new IntegerProperty(PROP_DECIMAL_DIGITS_PART,
                "Decimal Digits", WidgetPropertyCategory.Behavior, DEFAULT_DECIMAL_DIGITS));
        addProperty(new ColorProperty(PROP_INTERNAL_FRAME_COLOR,
                "Internal Frame Color", WidgetPropertyCategory.Display,
                ColorConstants.black.getRGB()));
        addProperty(new ColorProperty(PROP_INTERNAL_FOCUSED_FRAME_COLOR,
                "Focused Frame Color", WidgetPropertyCategory.Display,
                ColorConstants.blue.getRGB()));

        addProperty(new IntegerProperty(PROP_INTERNAL_FRAME_THICKNESS,
                "Internal Frame Thickness", WidgetPropertyCategory.Display, 1));
        addProperty(new BooleanProperty(PROP_LIMITS_FROM_PV, "Limits From PV",
                WidgetPropertyCategory.Behavior, false));

        addProperty(new BooleanProperty(PROP_SHOW_BUTTONS, "Show Buttons",
                WidgetPropertyCategory.Display, true));

    }


    public void setFont(OPIFont font){
        setPropertyValue(PROP_FONT, font);
    }

    public int getWholePartDigits() {
        return (Integer)getProperty(PROP_INTEGER_DIGITS_PART).getPropertyValue();
    }

    public void setWholePartDigits(int val) {
        setPropertyValue(PROP_INTEGER_DIGITS_PART, val);
    }

    public int getDecimalPartDigits() {
        return (Integer)getProperty(PROP_DECIMAL_DIGITS_PART).getPropertyValue();
    }

    public void setDecimalPartDigits(int val) {
        setPropertyValue(PROP_DECIMAL_DIGITS_PART, val);
    }

//    public double getValue() {
//        return (Double)getProperty(PROP_VALUE).getPropertyValue();
//    }

    public int getInternalFrameThickness() {
        return (Integer)getProperty(PROP_INTERNAL_FRAME_THICKNESS).getPropertyValue();
    }

    public RGB getInternalFrameColor() {
        return getRGBFromColorProperty(PROP_INTERNAL_FRAME_COLOR);
    }

    public RGB getInternalFocusedFrameColor() {
        return getRGBFromColorProperty(PROP_INTERNAL_FOCUSED_FRAME_COLOR);
    }

    public double getMinimum() {
        return (Double)getProperty(PROP_MIN).getPropertyValue();
    }

    public double getMaximum() {
        return (Double)getProperty(PROP_MAX).getPropertyValue();
    }

    public Color getInternalBorderColor() {
        return getSWTColorFromColorProperty(PROP_INTERNAL_FRAME_COLOR);
    }

    public Color getInternalFocusedBorderColor() {
        return getSWTColorFromColorProperty(PROP_INTERNAL_FOCUSED_FRAME_COLOR);
    }

    public int getInternalBorderWidth() {
        return (Integer)getProperty(PROP_INTERNAL_FRAME_THICKNESS).getPropertyValue();
    }

    /**
     * @return true if limits will be load from DB, false otherwise
     */
    public boolean isLimitsFromPV() {
        return (Boolean) getProperty(PROP_LIMITS_FROM_PV).getPropertyValue();
    }

    public boolean isButtonVisible() {
        return (Boolean) getProperty(PROP_SHOW_BUTTONS).getPropertyValue();
    }
}
