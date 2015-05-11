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
package org.csstudio.sds.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The display model. The term "Display" refers to a synoptic display. A display
 * model contains widget models and supports the handling of property change
 * events.
 *
 * This model defines "the bridge" between the persistence and UI layer. It will
 * be created when a certain display is loaded from a file, gets manipulated
 * during runtime and will be saved to a file, when the display gets closed.
 *
 * @author Alexander Will, Sven Wende, Kai Meyer
 *
 * @version $Revision: 1.53 $
 *
 */
public final class DisplayModel extends ContainerModel {
    /**
     * ID for the <i>Display Border Visibility</i> of the model.
     */
    public static final String PROP_DISPLAY_BORDER_VISIBILITY = "display_border_visibility";

    public static final String PROP_GRID_ON = "grid_on";
    public static final String PROP_RULER_ON = "ruler_on";
    public static final String PROP_GEOMETRY_ON = "geometry_on";
    public static final String PROP_OPEN_RELATIVE_ON = "open_relative_on";

    /**
     * The runtime context. (This is transient and will not get persisted).
     */
    private RuntimeContext runtimeContext;


    /**
     * Constructor.
     */
    public DisplayModel(boolean parentChecksEnabled) {
        super(parentChecksEnabled);
    }

    /**
     * Standard constructor.
     */
    public DisplayModel() {
        super();
        setWidth(800);
        setHeight(600);
    }

    @Override
    public DisplayModel getRoot() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureProperties() {
        addBooleanProperty(PROP_DISPLAY_BORDER_VISIBILITY,
                "Show Display Border", WidgetPropertyCategory.BORDER, true, false);
        addBooleanProperty(PROP_GRID_ON, "Grid Visible",
                WidgetPropertyCategory.DISPLAY, false, false);
        addBooleanProperty(PROP_RULER_ON, "Ruler Visible",
                WidgetPropertyCategory.DISPLAY, false, false);
        addBooleanProperty(PROP_GEOMETRY_ON, "Snap to Geometry activated",
                WidgetPropertyCategory.DISPLAY, false, false);
        addBooleanProperty(PROP_OPEN_RELATIVE_ON, "Open Relative",
                WidgetPropertyCategory.POSITION, false, true, AbstractWidgetModel.PROP_POS_X);

        // .. hide properties
        hideProperty(PROP_BORDER_COLOR, getTypeID());
        hideProperty(PROP_BORDER_STYLE, getTypeID());
        hideProperty(PROP_BORDER_WIDTH, getTypeID());
        hideProperty(PROP_LAYER, getTypeID());
        hideProperty(PROP_ENABLED, getTypeID());
        hideProperty(PROP_TOOLTIP, getTypeID());
        hideProperty(PROP_CURSOR, getTypeID());
        hideProperty(PROP_GRID_ON, getTypeID());
        hideProperty(PROP_RULER_ON, getTypeID());
        hideProperty(PROP_GEOMETRY_ON, getTypeID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeID() {
        return "display";
    }

    public boolean getGridState() {
        return getBooleanProperty(PROP_GRID_ON);
    }

    public boolean getRulerState() {
        return getBooleanProperty(PROP_RULER_ON);
    }

    public boolean getGeometryState() {
        return getBooleanProperty(PROP_GEOMETRY_ON);
    }

    public boolean getOpenRelative() {
        return getBooleanProperty(PROP_OPEN_RELATIVE_ON);
    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        return "";
    }

    @Override
    public Map<String, String> getAliases() {
        Map<String, String> aliases = new HashMap<String, String>();

        if (runtimeContext != null && runtimeContext.getAliases() != null) {
            aliases.putAll(runtimeContext.getAliases());

            Map<String, String> staticAliases = super.getAliases();

            if (staticAliases != null) {
                aliases.putAll(staticAliases);
            }
        } else {
            aliases = super.getAliases();
        }

        return Collections.unmodifiableMap(aliases);
    }
}
