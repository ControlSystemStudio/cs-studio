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
package org.csstudio.sds.util;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

/**
 * An utility, which should be used to enable the antialiasing feature for a
 * {@link Graphics} or {@link GC} graphics object. The util checks, if advanced
 * graphics are enabled (e.g. for Window, the GDI lib has to be installed) and
 * enables antialiasing only, if the requirements are fit.
 *
 * @author Sven Wende
 */
public final class AntialiasingUtil {

    /**
     * Indicator for the system ability to support anti aliasing.
     */
    private static boolean _advancedGraphicsPossible = false;

    /**
     * The shared instance.
     */
    private static AntialiasingUtil _instance;

    /**
     * Private constructor to avoid instantiation.
     */
    private AntialiasingUtil() {
        GC gc = new GC(Display.getCurrent());
        gc.setAdvanced(true);
        // gc.dispose();

        _advancedGraphicsPossible = gc.getAdvanced();
    }

    /**
     * Gets the singleton instance.
     *
     * @return the singleton instance
     */
    public static AntialiasingUtil getInstance() {
        if (_instance == null) {
            _instance = new AntialiasingUtil();
        }

        return _instance;
    }

    /**
     * Enables Antialiasing for the specified graphics.
     *
     * @param graphics
     *            the graphics
     */
    public void enableAntialiasing(final GC graphics) {
        if (antialiasingTurnedOn() && _advancedGraphicsPossible) {
            graphics.setAntialias(SWT.ON);
        }
    }

    /**
     * Enables Antialiasing for the specified graphics.
     *
     * @param graphics
     *            the graphics
     */
    public void enableAntialiasing(final Graphics graphics) {
        if (antialiasingTurnedOn() && _advancedGraphicsPossible) {
            graphics.setAntialias(SWT.ON);
            graphics.setTextAntialias(SWT.ON);
        }
    }

    /**
     * Disables Antialiasing for the specified graphics.
     *
     * @param graphics
     *            the graphics
     */
    public void disableAntialiasing(final Graphics graphics) {
        if (antialiasingTurnedOn()) {
            graphics.setAntialias(SWT.OFF);
        }
    }

    private boolean antialiasingTurnedOn() {
        return SdsPlugin.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.PROP_ANTIALIASING);
    }
}
