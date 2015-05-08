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

/**
 * An ellipse widget model.
 *
 * @author Sven Wende, Alexander Will
 * @version $Revision: 1.23 $
 *
 */
public class SimpleSliderModel extends AbstractWidgetModel {

    /**
     * The ID of the "show value as text" property.
     */
    public static final String PROP_SHOW_VALUE_AS_TEXT = "showValueAsText"; //$NON-NLS-1$

    /**
     * The ID of the value property.
     */
    public static final String PROP_VALUE = "value"; //$NON-NLS-1$

    /**
     * The ID of the minimum property.
     */
    public static final String PROP_MIN = "min"; //$NON-NLS-1$

    /**
     * The ID of the maximum property.
     */
    public static final String PROP_MAX = "max"; //$NON-NLS-1$

    /**
     * The ID of the increment property.
     */
    public static final String PROP_INCREMENT = "increment"; //$NON-NLS-1$

    /**
     * The ID of the precision property.
     */
    public static final String PROP_PRECISION = "precision"; //$NON-NLS-1$

    /**
     * The ID of the minimum slider width property.
     */
    public static final String PROP_SLIDER_WIDTH = "sliderWidth"; //$NON-NLS-1$

    /**
     * The ID of the orientation property.
     */
    public static final String PROP_ORIENTATION = "orientation"; //$NON-NLS-1$

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.SimpleSlider"; //$NON-NLS-1$

    /**
     * The default value of the height property.
     */
    private static final int DEFAULT_HEIGHT = 10;

    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 20;

    /**
     * Standard constructor.
     *
     */
    public SimpleSliderModel() {
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
        addDoubleProperty(PROP_VALUE, "Slider Value", WidgetPropertyCategory.DISPLAY, 50.0, true, PROP_TOOLTIP);
        addBooleanProperty(PROP_SHOW_VALUE_AS_TEXT, "Show Value As Text", WidgetPropertyCategory.DISPLAY, false, false);
        addDoubleProperty(PROP_MIN, "Min", WidgetPropertyCategory.DISPLAY, 0.0, false, PROP_SHOW_VALUE_AS_TEXT);
        addDoubleProperty(PROP_MAX, "Max", WidgetPropertyCategory.DISPLAY, 100.0, false, PROP_MIN);
        addDoubleProperty(PROP_INCREMENT, "Increment", WidgetPropertyCategory.DISPLAY, 1.0, 0.001, 1000.0, false);
        // The increment is limited to the range 0.001..1000 because the
        // scrollbar control used internally by the widget causes problems
        // if the value range of the scrollbar gets too large, probably because
        // it uses integer numbers internally.
        addBooleanProperty(PROP_ORIENTATION, "Horizontal orientation", WidgetPropertyCategory.FORMAT, true, false);
        addIntegerProperty(PROP_PRECISION, "Decimal places", WidgetPropertyCategory.FORMAT, 2, 0, 5, false);
        addIntegerProperty(PROP_SLIDER_WIDTH, "Slider wide", WidgetPropertyCategory.FORMAT, 5, 0, Integer.MAX_VALUE, false);
        setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#ffffff");
        setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "#d0d0d0");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Maximum:\t");
        buffer.append(createTooltipParameter(PROP_MAX) + "\n");
        buffer.append("Minimum:\t");
        buffer.append(createTooltipParameter(PROP_MIN) + "\n");
        buffer.append("Value:\t\t");
        buffer.append(createTooltipParameter(PROP_VALUE));
        return buffer.toString();
    }

    /**
     * Return the min value.
     *
     * @return The min value.
     */
    public double getMin() {
        return getDoubleProperty(PROP_MIN);
    }

    /**
     * Return the max value.
     *
     * @return The max value.
     */
    public double getMax() {
        return getDoubleProperty(PROP_MAX);
    }

    /**
     * Return the increment value.
     *
     * @return The increment value.
     */
    public double getIncrement() {
        return getDoubleProperty(PROP_INCREMENT);
    }

    /**
     * Return the current slider value.
     *
     * @return The current slider value.
     */
    public double getValue() {
        return getDoubleProperty(PROP_VALUE);
    }

    /**
     * Return the precision.
     *
     * @return The precision.
     */
    public int getPrecision() {
        return getIntegerProperty(PROP_PRECISION);
    }

    /**
     * Return the slider width.
     *
     * @return The slider width.
     */
    public int getSliderWidth() {
        return getIntegerProperty(PROP_SLIDER_WIDTH);
    }

    /**
     * Return whether the slider has a horizontal or a vertical orientation.
     *
     * @return True if the slider has a horizontal orientation.
     */
    public boolean isHorizontal() {
        return getBooleanProperty(PROP_ORIENTATION);
    }

    /**
     * Return whether the slider value should also be displayed as a text.
     *
     * @return True if the slider value should also be displayed as a text.
     */
    public boolean isShowValueAsText() {
        return getBooleanProperty(PROP_SHOW_VALUE_AS_TEXT);
    }
}
