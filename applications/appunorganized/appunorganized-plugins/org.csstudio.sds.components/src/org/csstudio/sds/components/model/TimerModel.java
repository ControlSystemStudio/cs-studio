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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * This class defines a timer widget model.
 *
 * @author Kai Meyer
 * @version $Revision: 1.13 $
 *
 */
public final class TimerModel extends AbstractWidgetModel {
    /**
     * The ID of the script property.
     */
    public static final String PROP_SCRIPT = "script"; //$NON-NLS-1$

    /**
     * The ID of the delay property.
     */
    public static final String PROP_DELAY = "delay"; //$NON-NLS-1$

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Timer"; //$NON-NLS-1$

    /**
     * The default value of the height property.
     */

    private static final int DEFAULT_HEIGHT = 16;

    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 16;

    /**
     * The default value of the fill grade property.
     */
    private static final int DEFAULT_DELAY = 1000;

    /**
     * Standard constructor.
     */
    public TimerModel() {
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
        addResourceProperty(PROP_SCRIPT, "Script", WidgetPropertyCategory.DISPLAY, new Path(""), new String[] { "css-sdss" }, true, PROP_TOOLTIP);
        addIntegerProperty(PROP_DELAY, "Delay (in ms)", WidgetPropertyCategory.DISPLAY, DEFAULT_DELAY, 0, Integer.MAX_VALUE, false, PROP_SCRIPT);

        // .. hide properties
        hideProperty(PROP_ACTIONDATA,getTypeID());
        hideProperty(PROP_BORDER_COLOR,getTypeID());
        hideProperty(PROP_BORDER_STYLE,getTypeID());
        hideProperty(PROP_BORDER_WIDTH,getTypeID());
        hideProperty(PROP_COLOR_BACKGROUND,getTypeID());
        hideProperty(PROP_COLOR_FOREGROUND,getTypeID());
        hideProperty(PROP_HEIGHT,getTypeID());
        hideProperty(PROP_POS_X,getTypeID());
        hideProperty(PROP_POS_Y,getTypeID());
        // hideProperty(PROP_VISIBILITY,getTypeID());
        hideProperty(PROP_WIDTH,getTypeID());
        hideProperty(PROP_CURSOR,getTypeID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Script:\t");
        buffer.append(createTooltipParameter(PROP_SCRIPT) + "\n");
        buffer.append("Delay:\t");
        buffer.append(createTooltipParameter(PROP_DELAY));
        return buffer.toString();
    }

    /**
     * Gets the script.
     *
     * @return the script
     */
    public IPath getScriptPath() {
        return getResourceProperty(PROP_SCRIPT);
    }

    /**
     * Gets the delay.
     *
     * @return the delay.
     */
    public int getDelay() {
        return getIntegerProperty(PROP_DELAY);
    }

}
