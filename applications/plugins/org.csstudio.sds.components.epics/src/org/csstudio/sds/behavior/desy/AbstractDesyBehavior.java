/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.behavior.desy;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.Severity;
import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.TextTypeEnum;

/**
 * Default DESY-Behaviour for all widget.<br>
 * Define color and fonts for Alarms and Connection states.
 *
 * @author Sven Wende
 * @author $Author: hrickens $
 * @version $Revision: 1.5 $
 * @since 20.04.2010
 * @param <W> The Widget model that have this Behavior
 */
public abstract class AbstractDesyBehavior<W extends AbstractWidgetModel> extends
        AbstractBehavior<W> {
    private static final String YELLOW = "${Minor}";
    private static final String ININTIAL = "${Initial}";
    private static final String GREEN = "${NoAlarm}";
    private static final String RED = "${Major}";
    private static final String PINK = "${VerbAbbr}";
    private static final String INVALID = "${Invalid}";

    private final Map<ConnectionState, String> colorsByConnectionState = new HashMap<ConnectionState, String>();
    private final Map<ConnectionState, BorderStyleEnum> borderStyleByConnectionState = new HashMap<ConnectionState, BorderStyleEnum>();
    private final Map<ConnectionState, String> borderColorsByConnectionState = new HashMap<ConnectionState, String>();
    private final Map<ConnectionState, Integer> borderWidthByConnectionState = new HashMap<ConnectionState, Integer>();

    /**
     * Constructor.
     */
    public AbstractDesyBehavior() {
        initConnectionState();
    }

    /**
     * Fill the maps with the DESY default values for the Connection states.<br>
     * Defaults<br>
     * - border width<br>
     * - border style<br>
     * - border color<br>
     * - color (mostly used for background color)<br>
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    private void initConnectionState() {
        for (final ConnectionState cs : ConnectionState.values()) {
            switch (cs) {
                case CONNECTED:
                    addConnectionStates(cs,0, BorderStyleEnum.NONE, GREEN, GREEN);
                    break;
                case OPERATIONAL:
                    addConnectionStates(cs,0, BorderStyleEnum.NONE, GREEN, GREEN);
                    break;
                case CONNECTING:
                    addConnectionStates(cs,1, BorderStyleEnum.DASH_DOT, ININTIAL, ININTIAL);
                    break;
                case INITIAL:
                    addConnectionStates(cs,1, BorderStyleEnum.DASH_DOT, ININTIAL, ININTIAL);
                    break;
                case CONNECTION_FAILED:
                    addConnectionStates(cs,1, BorderStyleEnum.DASH_DOT, PINK, PINK);
                    break;
                case CONNECTION_LOST:
                    addConnectionStates(cs,1, BorderStyleEnum.DASH_DOT, PINK, PINK);
                    break;
                case DESTROYED:
                    addConnectionStates(cs,1, BorderStyleEnum.DASH_DOT, PINK, PINK);
                    break;
                case DISCONNECTED:
                    addConnectionStates(cs,1, BorderStyleEnum.DASH_DOT, PINK, PINK);
                    break;
                case DISCONNECTING:
                    addConnectionStates(cs,1, BorderStyleEnum.DASH_DOT, PINK, PINK);
                    break;
                case READY:
                    addConnectionStates(cs,0, BorderStyleEnum.NONE, ININTIAL, ININTIAL);
                    break;
                default:
                    addConnectionStates(cs,0, BorderStyleEnum.NONE, INVALID, INVALID);
                    break;
            }
        }
    }// CHECKSTYLE ON: CyclomaticComplexity

    /**
     * @param cs
     */
    private void addConnectionStates(final ConnectionState cs, final Integer borderWidth, final BorderStyleEnum borderStyle, final String borderColor, final String color) {
        borderWidthByConnectionState.put(cs, borderWidth);
        borderStyleByConnectionState.put(cs, borderStyle);
        borderColorsByConnectionState.put(cs, borderColor);
        colorsByConnectionState.put(cs, color);
    }

    // CHECKSTYLE ON: CyclomaticComplexity
    /**
     * Give a DESY default Border style for the given {@link ConnectionState}
     *
     * @param connectionState The Connection State
     * @return the DESY default Border style for the given {@link ConnectionState}
     */

    protected final BorderStyleEnum determineBorderStyle(final ConnectionState connectionState) {
        return connectionState != null ? borderStyleByConnectionState.get(connectionState)
                : borderStyleByConnectionState.get(ConnectionState.INITIAL);
    }

    /**
     * Give a DESY default Background color for the given {@link ConnectionState}
     *
     * @param connectionState The Connection State
     * @return the DESY default Background color for the given {@link ConnectionState}
     */

    protected final String determineBackgroundColor(final ConnectionState connectionState) {
        final ConnectionState tempConnectionState = connectionState != null ? connectionState : ConnectionState.INITIAL;
        return getColorsByConnectionState(tempConnectionState);
    }

    private String getColorsByConnectionState(final ConnectionState connectionState) {
        if(colorsByConnectionState.containsKey(connectionState)) {
            return colorsByConnectionState.get(connectionState);
        }
        return ININTIAL;
    }

    /**
     * Give a DESY default Border color for the given {@link ConnectionState}
     *
     * @param connectionState The Connection State
     * @return the DESY default Border color for the given {@link ConnectionState}
     */
    protected final String determineBorderColor(final ConnectionState connectionState) {
        return connectionState != null ? borderColorsByConnectionState.get(connectionState)
                : borderColorsByConnectionState.get(ConnectionState.INITIAL);
    }

    /**
     * Give a DESY default Border width for the given {@link ConnectionState}
     *
     * @param connectionState The Connection State
     * @return the DESY default Border width for the given {@link ConnectionState}
     */
    protected final Integer determineBorderWidth(final ConnectionState connectionState) {
        return connectionState != null ? borderWidthByConnectionState.get(connectionState)
                : borderWidthByConnectionState.get(ConnectionState.INITIAL);
    }

    /**
     * Give a DESY default Border width for the given {@link Severity}
     *
     * @param severity The Severity
     * @return the DESY default Border width for the given {@link Severity}
     */
    protected static BorderStyleEnum determineBorderStyleBySeverity(final Severity severity) {
        return severity!=null&&(severity.isOK()||severity.isInvalid())?BorderStyleEnum.NONE:BorderStyleEnum.LINE;
    }

    /**
     * Give a DESY default Border width for the given {@link Severity}
     *
     * @param connectionState The Severity
     * @return the DESY default Border width for the given {@link Severity}
     */
    protected static int determineBorderWidthBySeverity(final Severity severity) {
        return severity!=null&&(severity.isOK()||severity.isInvalid())?0:3;
    }

    /**
     * The new way? Give a DESY default Color for the given {@link Severity}
     *
     * @param severity The Severity
     * @return the DESY default color for the given {@link Severity}
     */

    protected final String determineColorBySeverity(final Severity severity,final String defColor) {
        String color = "#000000";

        if (severity != null) {
            if (severity.isOK()) {
                // .. green
                if (defColor != null) {
                    return defColor;
                }
                color = GREEN;
            } else if (severity.isMinor()) {
                // .. yellow
                color = YELLOW;
            } else if (severity.isMajor()) {
                // .. red
                color = RED;
            } else {
                // .. white
                color = INVALID;
            }
        }

        return color;
    }

    public static void handleValueType(final AbstractWidgetModel model, final TextTypeEnum textTypeEnum, final String propertyId, final AnyData anyData) {
        switch (textTypeEnum) {
            case ALIAS:
                model.setPropertyValue(propertyId, anyData.getMetaData().getName());
                break;
            case DOUBLE:
//                model.setPropertyValue(propertyId, anyData.stringValue());
                model.setPropertyValue(propertyId, anyData.doubleValue());
                break;
            case EXP:
                model.setPropertyValue(propertyId, anyData.stringValue());
                break;
            case HEX:
                model.setPropertyValue(propertyId, anyData.stringValue());
                break;
            case TEXT:
                // TODO (hrickens): The CA Gateway sent wrong formated floating point.
                // when the gateway sent the correct string, the stringValue can sent 1by1.
            	final String stringValue = anyData.stringValue();                
            	model.setPropertyValue(propertyId, stringValue);
                break;
            default:
                break;
        }
    }
//removed because in branch 14x it was not used
    protected static String gatewayPrecisionBugHack(final String stringValue) {
        String tmpValue = stringValue;
        try {
            Double.parseDouble(stringValue);
            final int indexOf = stringValue.indexOf(".");
            if(indexOf>0&&stringValue.length()-indexOf>4) {
                tmpValue = stringValue.substring(0, stringValue.length()+1-indexOf);
            }
        } catch (final NumberFormatException nfe) {
            // do default
        }
        return tmpValue;
    }

}
