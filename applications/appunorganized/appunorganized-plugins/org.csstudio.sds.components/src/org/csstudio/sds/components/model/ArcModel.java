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
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * An arc widget model.
 *
 * @author jbercic
 *
 */
public class ArcModel extends AbstractWidgetModel {
    /**
     * Unique identifier.
     */
    public static final String ID = "org.csstudio.sds.components.Arc";

    /**
     * The ID of the <i>transparent</i> property.
     */
    public static final String PROP_TRANSPARENT = "transparent_background";
    /**
     * The IDs of the <i>startangle</i> property.
     */
    public static final String PROP_STARTANGLE = "start_angle";
    /**
     * The IDs of the <i>angle</i> property.
     */
    public static final String PROP_ANGLE = "angle";
    /**
     * The IDs of the <i>linewidth</i> property.
     */
    public static final String PROP_LINEWIDTH = "linewidth";
    /**
     * The IDs of the <i>filled</i> property.
     */
    public static final String PROP_FILLED = "filled";
    /**
     * The IDs of the <i>fillcolor</i> property.
     */
    public static final String PROP_FILLCOLOR = "color.fill";

    /**
     * Constructor.
     */
    public ArcModel() {
        setWidth(50);
        setHeight(50);
        this.setPropertyValue(PROP_BORDER_STYLE, BorderStyleEnum.SHAPE.getIndex());
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
        addBooleanProperty(PROP_TRANSPARENT, "Transparent Background", WidgetPropertyCategory.FORMAT, true, true, PROP_COLOR_BACKGROUND);

        addIntegerProperty(PROP_STARTANGLE, "Start Angle", WidgetPropertyCategory.DISPLAY, 0, 0, 360, true, PROP_TOOLTIP);
        addIntegerProperty(PROP_ANGLE, "Angle", WidgetPropertyCategory.DISPLAY, 90, 0, 360, false, PROP_STARTANGLE);
        addIntegerProperty(PROP_LINEWIDTH, "Line Width", WidgetPropertyCategory.DISPLAY, 1, false, PROP_ANGLE);
        addBooleanProperty(PROP_FILLED, "Filled", WidgetPropertyCategory.DISPLAY, false, false, PROP_LINEWIDTH);
        addColorProperty(PROP_FILLCOLOR, "Fill Color", WidgetPropertyCategory.DISPLAY, "#ff0000", false, PROP_FILLED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Start Angle:\t");
        buffer.append(createTooltipParameter(PROP_STARTANGLE) + "\n");
        buffer.append("Angle:\t");
        buffer.append(createTooltipParameter(PROP_ANGLE));
        return buffer.toString();
    }

    /**
     * Returns the transparent state of the background.
     *
     * @return true, if the background is transparent, false otherwise
     */
    public boolean getTransparent() {
        return getBooleanProperty(PROP_TRANSPARENT);
    }

    /**
     * Returns the value for the start angle.
     *
     * @return The value for the start angle
     */
    public int getStartAngle() {
        return getIntegerProperty(PROP_STARTANGLE);
    }

    /**
     * Returns the value for the angle.
     *
     * @return The value for the angle
     */
    public int getAngle() {
        return getIntegerProperty(PROP_ANGLE);
    }

    /**
     * Returns the width of the arc.
     *
     * @return The width of the arc
     */
    public int getLineWidth() {
        return getIntegerProperty(PROP_LINEWIDTH);
    }

    /**
     * Returns the fill state of the arc.
     *
     * @return true, if the arc should be filled, false otherwise
     */
    public boolean getFill() {
        return getBooleanProperty(PROP_FILLED);
    }
}
