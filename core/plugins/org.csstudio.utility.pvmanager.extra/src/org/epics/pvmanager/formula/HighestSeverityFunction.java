/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.AbstractList;
import org.epics.vtype.ValueFactory;
import java.util.Arrays;
import java.util.List;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;
import org.epics.vtype.VEnum;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;

/**
 * Retrieves the highest alarm from the values.
 *
 * @author carcassi
 */
class HighestSeverityFunction implements FormulaFunction {

    @Override
    public boolean isPure() {
        return true;
    }

    @Override
    public boolean isVarArgs() {
        return true;
    }

    @Override
    public String getName() {
        return "highestSeverity";
    }

    @Override
    public String getDescription() {
        return "Returns the highest severity";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VType.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("values");
    }

    @Override
    public Class<?> getReturnType() {
        return VEnum.class;
    }

    @Override
    public Object calculate(final List<Object> args) {
        Alarm finalAlarm = ValueFactory.alarmNone();
        Time time = null;
        for (Object object : args) {
            Alarm newAlarm;
            if (object == null) {
                newAlarm = ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "No Value");
            } else {
                newAlarm = ValueUtil.alarmOf(object);
                if (newAlarm == null) {
                    newAlarm = ValueFactory.alarmNone();
                }
            }
            if (newAlarm.getAlarmSeverity().compareTo(finalAlarm.getAlarmSeverity()) > 0) {
                finalAlarm = newAlarm;
                time = ValueUtil.timeOf(object);
                if (time == null) {
                    time = ValueFactory.timeNow();
                }
            }
            if (time == null) {
                time = ValueUtil.timeOf(object);
            }
        }
        if (time == null) {
            time = ValueFactory.timeNow();
        }
        
        return ValueFactory.newVEnum(finalAlarm.getAlarmSeverity().ordinal(), AlarmSeverity.labels(), finalAlarm, time);
    }
    
}
