/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.alarmNone;
import static org.epics.vtype.ValueFactory.displayNone;
import static org.epics.vtype.ValueFactory.newTime;
import static org.epics.vtype.ValueFactory.newVNumberArray;

import java.util.Arrays;
import java.util.List;

import org.epics.util.array.ListDouble;
import org.epics.util.array.ListMath;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueFactory;

/**
 * @author Mark Davis
 * 
 */
public class MultArrayFormulaFunction implements FormulaFunction {

    @Override
    public boolean isPure() {
	return true;
    }

    @Override
    public boolean isVarArgs() {
	return false;
    }

    @Override
    public String getName() {
	return "arrayMult";
    }

    @Override
    public String getDescription() {
	return "Result[x] = array1[x] * array2[x]";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
	return Arrays.<Class<?>> asList(VNumberArray.class, VNumberArray.class);
    }

    @Override
    public List<String> getArgumentNames() {
	return Arrays.asList("array1", "array2");
    }

    @Override
    public Class<?> getReturnType() {
	return VNumberArray.class;
    }

    @Override
    public Object calculate(final List<Object> args) {
	  return ValueFactory.newVNumberArray(
			ListMath.mult( ((VNumberArray) args.get(0)).getData(), 
					       ((VNumberArray) args.get(1)).getData() ),
			alarmNone(), newTime(Timestamp.now()), displayNone() );
    }
}
