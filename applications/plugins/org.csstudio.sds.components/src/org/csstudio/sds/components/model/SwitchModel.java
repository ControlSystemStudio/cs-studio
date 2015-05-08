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

import org.csstudio.sds.components.common.SwitchPlugins;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * A switch widget model.
 *
 * @author jbercic
 *
 */
public final class SwitchModel extends AbstractWidgetModel {
    /**
     * Unique identifier.
     */
    public static final String ID = "org.csstudio.sds.components.Switch";

    /**
     * The IDs of the properties.
     */
    public static final String PROP_TRANSPARENT = "transparency";
    public static final String PROP_TYPE = "switch.type";
    public static final String PROP_STATE = "switch.state";
    public static final String PROP_ROTATE = "rotation";
    public static final String PROP_LINEWIDTH = "linewidth";

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
        if (SwitchPlugins.names.length > 0) {
            addArrayOptionProperty(PROP_TYPE, "Switch Type", WidgetPropertyCategory.DISPLAY, SwitchPlugins.names, 0, true, PROP_TOOLTIP);
        }
        addIntegerProperty(PROP_STATE, "Switch State", WidgetPropertyCategory.DISPLAY, 0, false, PROP_TYPE);
        addIntegerProperty(PROP_ROTATE, "Rotation", WidgetPropertyCategory.DISPLAY, 0, 0, 360, false, PROP_STATE);
        addIntegerProperty(PROP_LINEWIDTH, "Line Width", WidgetPropertyCategory.DISPLAY, 4, false, PROP_ROTATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Type:\t");
        buffer.append(createTooltipParameter(PROP_TYPE) + "\n");
        buffer.append("State:\t");
        buffer.append(createTooltipParameter(PROP_STATE));
        return buffer.toString();
    }

    public boolean getTransparent() {
        return getBooleanProperty(PROP_TRANSPARENT);
    }

    public int getType() {
        return getArrayOptionProperty(PROP_TYPE);
    }

    public int getState() {
        return getIntegerProperty(PROP_STATE);
    }

    public int getRotation() {
        return getIntegerProperty(PROP_ROTATE);
    }

    public int getLineWidth() {
        return getIntegerProperty(PROP_LINEWIDTH);
    }
}
