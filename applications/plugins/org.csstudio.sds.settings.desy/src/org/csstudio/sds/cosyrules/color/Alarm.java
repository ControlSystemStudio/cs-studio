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

import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.IRule;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;

/**
 * Color rule Alarm, translated from an ADL file.
 *
 * @author jbercic, jhatje
 *
 */
public final class Alarm implements IRule {
    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "cosyrules.color.alarm";

    /**
     * Standard constructor.
     */
    public Alarm() {
        // Nothing to do.
    }

    /**
     * Map the severity of a pv value to a color. For DAL severities
     * (pv_name[severity]) the parameter 'arguments' is of type
     * DynamicValueState.ID. Using the EPICS field (record.SEVR) the severity is
     * a number.
     *
     * {@inheritDoc}
     */
    public Object evaluate(final Object[] arguments) {
        DynamicValueState dvc = null;
        // Wrong State violet
        String color = "#8000FF"; 
        if ( (arguments != null) && (arguments.length > 0)) {

            if (arguments[0] instanceof Double) {
                dvc = getDynamicValueCondition((Double) arguments[0]);
            } else if (arguments[0] instanceof Long) {
                dvc = getDynamicValueCondition((Long) arguments[0]);
            } else if (arguments[0] instanceof String) {
                dvc = getDynamicValueCondition((String) arguments[0]);
            } else if (arguments[0] instanceof DynamicValueCondition) {
                dvc = getDynamicValueCondition((DynamicValueCondition) arguments[0]);
            }

            if (dvc != null) {
                switch (dvc) {
                    case ALARM:
                        // RED
                    	color = ColorAndFontUtil.toHex(253, 0, 0);
                    	break;
                    case NORMAL:
                        // Green
                    	color =  ColorAndFontUtil.toHex(0, 216, 0);
                    	break;
                    case WARNING:
                        // Yellow
                        return ColorAndFontUtil.toHex(251, 243, 74);
                    case TIMELAG:
                    case LINK_NOT_AVAILABLE:
                    case TIMEOUT:
                    case ERROR:
                        // white
                    	color = ColorAndFontUtil.toHex(255, 255, 255);
                }
            }
        }
        System.out.println("Border color: "+color+"\r\n");
        return color;
    }

    /**
     * @param dynamicValueCondition
     * @return
     */
    private DynamicValueState getDynamicValueCondition(final DynamicValueCondition dynamicValueCondition) {
        if(dynamicValueCondition.containsStates(DynamicValueState.ALARM)) {
            return DynamicValueState.ALARM;
        } else if(dynamicValueCondition.containsStates(DynamicValueState.WARNING)) {
            return DynamicValueState.WARNING;
        } else if(dynamicValueCondition.containsStates(DynamicValueState.NORMAL)) {
            return DynamicValueState.NORMAL;
        }
        return DynamicValueState.ERROR;
    }

    /**
     * @param string
     * @return
     */
    private DynamicValueState getDynamicValueCondition(final String alarmState) {
        if (alarmState.equals("NO_ALARM") ||alarmState.equals(DynamicValueState.NORMAL.toString())) {
            return DynamicValueState.NORMAL;
        } else  if (alarmState.equals("MINOR") || alarmState.equals(DynamicValueState.WARNING.toString())) {
            return DynamicValueState.WARNING;
        } else  if (alarmState.equals("MAJOR") || alarmState.equals(DynamicValueState.ALARM.toString())) {
            return DynamicValueState.ALARM;
        }
        return DynamicValueState.ERROR;
    }

    /**
     * @param alarmState
     * @return
     */
    private DynamicValueState getDynamicValueCondition(final Long alarmState) {
        return getDynamicValueCondition(alarmState.doubleValue());
    }

    /**
     * @param double1
     * @return
     */
    private DynamicValueState getDynamicValueCondition(final Double alarmState) {
        if ( (Math.abs(alarmState - 0.0) < 0.00001)) {
            return DynamicValueState.NORMAL;
        } else  if ( (Math.abs(alarmState - 1.0) < 0.00001)) {
            return DynamicValueState.WARNING;
        } else  if ( (Math.abs(alarmState - 2.0) < 0.00001)) {
            return DynamicValueState.ALARM;
        }
        return DynamicValueState.ERROR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }
}
