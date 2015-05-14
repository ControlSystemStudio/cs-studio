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
package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.util.ColorAndFontUtil;
import org.csstudio.dal.DynamicValueState;

/**
 * Color rule Alarm, translated from an ADL file.
 *
 * @author jbercic, jhatje
 *
 */
public final class Alarm extends AbstractAlarmRule  {
    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "cosyrules.color.alarm";

    /**
     * The rule can not calculate the State color!<br>
     * Purple
     */
    public static final String UNKNOWN = "#8000FF";
    /**
     *  Red.
     */
    public static final String ALARM = ColorAndFontUtil.toHex(253, 0, 0);
    /**
     * Green.
     */
    public static final String NORMAL = ColorAndFontUtil.toHex(0, 216, 0);
    /**
     * Yellow.
     */
    public static final String WARNING = ColorAndFontUtil.toHex(251, 243, 74);
    /**
     * Black.
     */
    public static final String ERROR = ColorAndFontUtil.toHex(255, 255, 255);

    /**
     * Standard constructor.
     */
    public Alarm() {
        // Nothing to do.
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        final String msg = "Kann für ein oder mehrere Channels die Severity als Farbe zurückgeben. Ist mehr als ein Channel angegeben wird die höchste Severity zurückgegeben.";
        return msg;
    }

    /**
     * Map the severity of a pv value to a color. For DAL severities
     * (pv_name[severity]) the parameter 'arguments' is of type
     * DynamicValueState.ID. Using the EPICS field (record.SEVR) the severity is
     * a number.
     *
     * {@inheritDoc}
     */
    @Override
    protected Object evaluateWorker(final DynamicValueState dvc) {
        String result = UNKNOWN;
        if (dvc != null) {
            switch (dvc) {
            case ALARM:
                // RED
                result = ALARM;
                break;
            case NORMAL:
                // Green
                result = ColorAndFontUtil.toHex(0, 216, 0);
                break;
            case WARNING:
                // Yellow
                return ColorAndFontUtil.toHex(251, 243, 74);
            case TIMELAG:
            case LINK_NOT_AVAILABLE:
            case TIMEOUT:
            case HAS_LIVE_DATA:
            case HAS_METADATA:
            case NO_VALUE:
                break;
            case ERROR:
                // white
                result = ColorAndFontUtil.toHex(255, 255, 255);
            }
        }
        return result;
    }
}
