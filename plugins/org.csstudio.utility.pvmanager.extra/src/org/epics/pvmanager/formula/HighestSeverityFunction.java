/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
        Alarm alarm = ValueUtil.highestSeverityOf(args, true);
        Time time = ValueUtil.timeOf(alarm);
        if (time == null) {
            time = ValueFactory.timeNow();
        }
        
        return ValueFactory.newVEnum(alarm.getAlarmSeverity().ordinal(), AlarmSeverity.labels(), alarm, time);
    }
    
}
