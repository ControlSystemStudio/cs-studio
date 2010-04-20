/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.behavior.desy;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.simple.Severity;

/**
 *
 * Default DESY-Behaviour for all widget.<br>
 * Define color and fonts for Alarms and Connection states.
 *
 * @author Sven Wende
 * @author $Author$
 * @version $Revision$
 * @since 20.04.2010
 *
 * @param <W>
 *            The Widget model that have this Behavior
 */
public abstract class AbstractDesyBehavior<W extends AbstractWidgetModel> extends
        AbstractBehavior<W> {
    private static final String YELLOW = ColorAndFontUtil.toHex(255, 168, 222);
    private static final String GREEN = ColorAndFontUtil.toHex(120, 120, 120);
    private static final String RED = ColorAndFontUtil.toHex(255, 9, 163);
    private final Map<ConnectionState, String> colorsByConnectionState = new HashMap<ConnectionState, String>();;
    private final Map<ConnectionState, BorderStyleEnum> borderStyleByConnectionState = new HashMap<ConnectionState, BorderStyleEnum>();
    private final Map<ConnectionState, String> borderColorsByConnectionState = new HashMap<ConnectionState, String>();;
    private final Map<ConnectionState, Integer> borderWidthByConnectionState = new HashMap<ConnectionState, Integer>();

    /**
     *
     * Constructor.
     */
    public AbstractDesyBehavior() {
        initConnectionStateColors();
        initConnectionStateBorderColors();
        initConnectionStateBorderStyle();
        initConnectionStateBorderWidth();
    }

    /**
     * Fill the map with the DESY default border width for the Connection states
     */
    private void initConnectionStateBorderWidth() {
        borderWidthByConnectionState.put(ConnectionState.CONNECTED, 0);
        borderWidthByConnectionState.put(ConnectionState.CONNECTING, 1);
        borderWidthByConnectionState.put(ConnectionState.INITIAL, 1);
        borderWidthByConnectionState.put(ConnectionState.CONNECTION_FAILED, 1);
        borderWidthByConnectionState.put(ConnectionState.CONNECTION_LOST, 1);
        borderWidthByConnectionState.put(ConnectionState.DESTROYED, 1);
        borderWidthByConnectionState.put(ConnectionState.DISCONNECTED, 1);
        borderWidthByConnectionState.put(ConnectionState.DISCONNECTING, 1);
        borderWidthByConnectionState.put(ConnectionState.READY, 0);
    }

    /**
     * Fill the map with the DESY default border style for the Connection states
     */
    private void initConnectionStateBorderStyle() {
        borderStyleByConnectionState.put(ConnectionState.CONNECTED, BorderStyleEnum.NONE);
        borderStyleByConnectionState.put(ConnectionState.CONNECTING, BorderStyleEnum.DASH_DOT);
        borderStyleByConnectionState.put(ConnectionState.INITIAL, BorderStyleEnum.DASH_DOT);
        borderStyleByConnectionState.put(ConnectionState.CONNECTION_FAILED,
                                         BorderStyleEnum.DASH_DOT);
        borderStyleByConnectionState.put(ConnectionState.CONNECTION_LOST, BorderStyleEnum.DASH_DOT);
        borderStyleByConnectionState.put(ConnectionState.DESTROYED, BorderStyleEnum.DASH_DOT);
        borderStyleByConnectionState.put(ConnectionState.DISCONNECTED, BorderStyleEnum.DASH_DOT);
        borderStyleByConnectionState.put(ConnectionState.DISCONNECTING, BorderStyleEnum.DASH_DOT);
        borderStyleByConnectionState.put(ConnectionState.READY, BorderStyleEnum.NONE);
    }

    /**
     * Fill the map with the DESY default border colors for the Connection states
     */
    private void initConnectionStateBorderColors() {
        borderColorsByConnectionState.put(ConnectionState.CONNECTED, GREEN);
        borderColorsByConnectionState.put(ConnectionState.CONNECTING, YELLOW);
        borderColorsByConnectionState.put(ConnectionState.INITIAL, YELLOW);
        borderColorsByConnectionState.put(ConnectionState.CONNECTION_FAILED, RED);
        borderColorsByConnectionState.put(ConnectionState.CONNECTION_LOST, RED);
        borderColorsByConnectionState.put(ConnectionState.DESTROYED, RED);
        borderColorsByConnectionState.put(ConnectionState.DISCONNECTED, RED);
        borderColorsByConnectionState.put(ConnectionState.DISCONNECTING, RED);
        borderColorsByConnectionState.put(ConnectionState.READY, GREEN);
    }

    /**
     * Fill the map with the DESY default colors for the Connection states
     */
    private void initConnectionStateColors() {
        colorsByConnectionState.put(ConnectionState.CONNECTED, GREEN);
        colorsByConnectionState.put(ConnectionState.CONNECTING, YELLOW);
        colorsByConnectionState.put(ConnectionState.INITIAL, YELLOW);
        colorsByConnectionState.put(ConnectionState.CONNECTION_FAILED, RED);
        colorsByConnectionState.put(ConnectionState.CONNECTION_LOST, RED);
        colorsByConnectionState.put(ConnectionState.DESTROYED, RED);
        colorsByConnectionState.put(ConnectionState.DISCONNECTED, RED);
        colorsByConnectionState.put(ConnectionState.DISCONNECTING, RED);
        colorsByConnectionState.put(ConnectionState.READY, GREEN);
    }

    /**
     * Give a DESY default Border style for the given {@link ConnectionState}
     * @param connectionState The Connection State
     * @return the DESY default Border style for the given {@link ConnectionState}
     */
    protected final BorderStyleEnum determineBorderStyle(final ConnectionState connectionState) {
        return connectionState != null ? borderStyleByConnectionState.get(connectionState)
                : borderStyleByConnectionState.get(ConnectionState.INITIAL);
    }

    /**
     * Give a DESY default Background color for the given {@link ConnectionState}
     * @param connectionState The Connection State
     * @return the DESY default Background color for the given {@link ConnectionState}
     */
    protected final String determineBackgroundColor(final ConnectionState connectionState) {
        return connectionState != null ? colorsByConnectionState.get(connectionState)
                : colorsByConnectionState.get(ConnectionState.INITIAL);
    }

    /**
     * Give a DESY default Border color for the given {@link ConnectionState}
     * @param connectionState The Connection State
     * @return the DESY default Border color for the given {@link ConnectionState}
     */
    protected final String determineBorderColor(final ConnectionState connectionState) {
        return connectionState != null ? borderColorsByConnectionState.get(connectionState)
                : borderColorsByConnectionState.get(ConnectionState.INITIAL);
    }

    /**
     * Give a DESY default Border width for the given {@link ConnectionState}
     * @param connectionState The Connection State
     * @return the DESY default Border width for the given {@link ConnectionState}
     */
    protected final Integer determineBorderWidth(final ConnectionState connectionState) {
        return connectionState != null ? borderWidthByConnectionState.get(connectionState)
                : borderWidthByConnectionState.get(ConnectionState.INITIAL);
    }

    /**
     * The new way?
     * Give a DESY default Color for the given {@link Severity}
     *
     * @param severity The Severity
     * @return the DESY default color for the given {@link Severity}
     */
    protected final String determineColorBySeverity(final Severity severity, final String defColor) {
        String color = "#000000";

        if (severity != null) {
            if (severity.isOK()) {
                // .. green
                if(defColor!=null) {
                    return defColor;
                }
                color = ColorAndFontUtil.toHex(0, 216, 0);
            } else if (severity.isMinor()) {
                // .. yellow
                color = ColorAndFontUtil.toHex(251, 243, 74);
            } else if (severity.isMajor()) {
                // .. red
                color = ColorAndFontUtil.toHex(253, 0, 0);
            } else {
                // .. white
                color = ColorAndFontUtil.toHex(255, 255, 255);
            }
        }

        return color;
    }

}
