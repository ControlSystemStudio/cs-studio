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
 * This class defines an rectangle widget model.
 *
 * @author Sven Wende, Alexander Will
 * @version $Revision: 1.18 $
 *
 */
public final class RectangleModel extends AbstractWidgetModel {
    /**
     * The ID of the fill grade property.
     */
    public static final String PROP_FILL = "fill"; //$NON-NLS-1$

    /**
     * The ID of the orientation property.
     */
    public static final String PROP_ORIENTATION = "orientation"; //$NON-NLS-1$

    /**
     * The ID of the transparent property.
     */
    public static final String PROP_TRANSPARENT = "transparency"; //$NON-NLS-1$

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Rectangle"; //$NON-NLS-1$

    /**
     * The default value of the height property.
     */

    private static final int DEFAULT_HEIGHT = 10;

    /**
     * The default value of the orientation property.
     */
    private static final boolean DEFAULT_ORIENTATION_HORIZONTAL = true;

    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 20;

    /**
     * The default value of the fill grade property.
     */
    private static final double DEFAULT_FILL = 100.0;

    /**
     * Standard constructor.
     */
    public RectangleModel() {
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
        addDoubleProperty(PROP_FILL, "Value", WidgetPropertyCategory.DISPLAY, DEFAULT_FILL, 0.0, 100.0, true, PROP_TOOLTIP);
        addBooleanProperty(PROP_ORIENTATION, "Horizontal Orientation", WidgetPropertyCategory.DISPLAY, DEFAULT_ORIENTATION_HORIZONTAL, false, PROP_FILL);
        addBooleanProperty(PROP_TRANSPARENT, "Transparent Background", WidgetPropertyCategory.FORMAT, false, true, PROP_COLOR_BACKGROUND);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Value:\t");
        buffer.append(createTooltipParameter(PROP_FILL));
        return buffer.toString();
    }

    /**
     * Gets the fill level.
     *
     * @return the fill level
     */
    public double getFillLevel() {
        return getDoubleProperty(PROP_FILL);
    }

    /**
     * Gets the orientation.
     *
     * @return the orientation.
     */
    public boolean getOrientation() {
        return getBooleanProperty(PROP_ORIENTATION);
    }

    /**
     * Returns if the background is transparent.
     *
     * @return The state of the background.
     */
    public boolean getTransparent() {
        return getBooleanProperty(PROP_TRANSPARENT);
    }

}
