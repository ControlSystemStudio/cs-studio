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
 * A line widget model.
 *
 * @author Sven Wende, Alexander Will
 */
public final class PolylineModel extends AbstractPolyModel {

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Polyline"; //$NON-NLS-1$

    /**
     * The ID of the width of the line.
     */
    public static final String PROP_LINE_WIDTH = "linewidth";

    /**
     * The ID of the width of the line.
     */
    public static final String PROP_LINE_STYLE = "linestyle";

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
        super.configureProperties();
        addIntegerProperty(PROP_LINE_WIDTH, "Line Width", WidgetPropertyCategory.DISPLAY, 1, 1, 100, true, PROP_TOOLTIP);
        addArrayOptionProperty(PROP_LINE_STYLE, "Line Style", WidgetPropertyCategory.DISPLAY, new String[] { "Solid", "Dash", "Dot", "DashDot",
                "DashDotDot" }, 0, false, PROP_LINE_STYLE);

        // .. hide properties
        hideProperty(AbstractWidgetModel.PROP_BORDER_COLOR, getTypeID());
        hideProperty(AbstractWidgetModel.PROP_BORDER_WIDTH, getTypeID());
        hideProperty(AbstractWidgetModel.PROP_BORDER_STYLE, getTypeID());
    }

    /**
     * Gets the width of the line.
     *
     * @return int The width of the line
     */
    public int getLineWidth() {
        return getIntegerProperty(PROP_LINE_WIDTH);
    }

    /**
     * Gets the style of the line.
     *
     * @return int The style of the line
     */
    public int getLineStyle() {
        return getArrayOptionProperty(PROP_LINE_STYLE);
    }
}
