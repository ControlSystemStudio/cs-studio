/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

import org.csstudio.sds.model.IRule;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 24.10.2011
 */
abstract class AbstractAlarmRule implements IRule {

    /**
     * Constructor.
     */
    public AbstractAlarmRule() {
        super();
    }

    /**
     * @param dynamicValueCondition
     * @return
     */
    protected DynamicValueState getDynamicValueCondition(final DynamicValueCondition dynamicValueCondition) {
        if (dynamicValueCondition.containsAllStates(DynamicValueState.ALARM)) {
            return DynamicValueState.ALARM;
        } else if (dynamicValueCondition
                .containsAllStates(DynamicValueState.WARNING)) {
            return DynamicValueState.WARNING;
        } else if (dynamicValueCondition
                .containsAllStates(DynamicValueState.NORMAL)) {
            return DynamicValueState.NORMAL;
        } else if(dynamicValueCondition.containsAllStates(DynamicValueState.ERROR)) {
            return DynamicValueState.ERROR;
        }
        return DynamicValueState.NO_VALUE;
    }

    /**
     * @param string
     * @return
     */
    protected DynamicValueState getDynamicValueCondition(final String alarmState) {
        if (alarmState.compareToIgnoreCase("NO_ALARM") == 0
                || alarmState.compareToIgnoreCase(DynamicValueState.NORMAL.toString()) == 0) {
            return DynamicValueState.NORMAL;
        } else if (alarmState.compareToIgnoreCase("MINOR") == 0
                || alarmState.compareToIgnoreCase(DynamicValueState.WARNING.toString()) == 0) {
            return DynamicValueState.WARNING;
        } else if (alarmState.compareToIgnoreCase("MAJOR") == 0
                || alarmState.compareToIgnoreCase(DynamicValueState.ALARM.toString()) == 0) {
            return DynamicValueState.ALARM;
        } else if (alarmState.compareToIgnoreCase("INVALID") == 0
                || alarmState.compareToIgnoreCase(DynamicValueState.ERROR.toString()) == 0) {
            return DynamicValueState.ERROR;
        }
        return DynamicValueState.NO_VALUE;
    }

    /**
     * @param alarmState
     * @return
     */
    protected DynamicValueState getDynamicValueCondition(final Long alarmState) {
        return getDynamicValueCondition(alarmState.doubleValue());
    }

    /**
     * @param double1
     * @return
     */
    protected DynamicValueState getDynamicValueCondition(final Double alarmState) {
        if (alarmState > -0.00001) {
            if (Math.abs(alarmState - 0.0) < 0.00001) {
                return DynamicValueState.NORMAL;
            } else if (Math.abs(alarmState - 1.0) < 0.00001) {
                return DynamicValueState.WARNING;
            } else if (Math.abs(alarmState - 2.0) < 0.00001) {
                return DynamicValueState.ALARM;
            } else if (Math.abs(alarmState - 3.0) < 0.00001) {
                return DynamicValueState.ERROR;
            }
        }
        return DynamicValueState.NO_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Object[] arguments) {
        DynamicValueState dvc = null;

        if (arguments != null && arguments.length > 0) {
            for (final Object argument : arguments) {
                DynamicValueState dvcTemp = null;
                if (argument instanceof Double) {
                    dvcTemp = getDynamicValueCondition((Double) argument);
                } else if (argument instanceof Long) {
                    dvcTemp = getDynamicValueCondition((Long) argument);
                } else if (argument instanceof String) {
                    dvcTemp = getDynamicValueCondition((String) argument);
                } else if (argument instanceof DynamicValueCondition) {
                    dvcTemp = getDynamicValueCondition((DynamicValueCondition) argument);
                }
                if (dvc == null || dvcTemp != null && dvc.ordinal()!=0 && (dvc.ordinal() < dvcTemp.ordinal() || dvcTemp.ordinal()==0)) {
                    dvc = dvcTemp;
                }
            }
        }
        final Object color = evaluateWorker(dvc);
        return color;    }

    protected abstract Object evaluateWorker(DynamicValueState dynamicValueState);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String getDescription();

}