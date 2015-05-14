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
 * @version $Revision: 1.19 $
 *
 */
public final class AdvancedSliderModel extends AbstractWidgetModel {

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
     * The ID of the orientation property.
     */
    public static final String PROP_ORIENTATION = "orientation"; //$NON-NLS-1$

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.AdvancedSlider"; //$NON-NLS-1$

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
    public AdvancedSliderModel() {
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
        addDoubleProperty(PROP_MIN, "Min", WidgetPropertyCategory.DISPLAY, 0.0, false, PROP_VALUE);
        addDoubleProperty(PROP_MAX, "Max", WidgetPropertyCategory.DISPLAY, 100.0, false, PROP_MIN);
        // The increment is limited to the range 0.001..1000 because the
        // scrollbar control used internally by the widget causes problems
        // if the value range of the scrollbar gets too large, probably because
        // it uses integer numbers internally.
        addDoubleProperty(PROP_INCREMENT, "Increment", WidgetPropertyCategory.DISPLAY, 1, 0.001, 1000.0, false, PROP_MAX);
        addArrayOptionProperty(PROP_ORIENTATION, "Orientation", WidgetPropertyCategory.FORMAT, new String[] { "Horizontal", "Vertical" }, 0, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Maximum:\t");
        buffer.append(createTooltipParameter(PROP_MAX) + "\n");
        buffer.append("Minimum:\t");
        buffer.append(createTooltipParameter(PROP_MIN) + "\n");
        buffer.append("Current Value:\t");
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
     * Return whether the slider has a horizontal or a vertical orientation.
     *
     * @return True if the slider has a horizontal orientation.
     */
    public boolean isHorizontal() {
        int orientation = getArrayOptionProperty(PROP_ORIENTATION);
        return orientation == 0;
    }
}
