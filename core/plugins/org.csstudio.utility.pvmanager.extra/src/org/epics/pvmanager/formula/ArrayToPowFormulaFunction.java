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
import org.epics.util.array.ListNumber;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueFactory;

/**
 * @author Mark Davis, NSCL/FRIB
 * 
 */
public class ArrayToPowFormulaFunction implements FormulaFunction {

    @Override
    public boolean isPure() { return true; }

    @Override
    public boolean isVarArgs() { return false; }

    @Override
    public String getName() { return "arrayPow"; }

    @Override
    public String getDescription() { return "Result[x] = pow(array[x], expon)"; }

    @Override
    public List<Class<?>> getArgumentTypes() {
	  return Arrays.<Class<?>> asList(VNumberArray.class, VNumber.class);
    }

    @Override
    public List<String> getArgumentNames() { return Arrays.asList("array", "expon"); }

    @Override
    public Class<?> getReturnType() { return VNumberArray.class; }

    @Override
    public Object calculate(final List<Object> args) {
	  return ValueFactory.newVNumberArray(
              ListMath.listToPow( ((VNumberArray) args.get(0)).getData(),
            	                  (((VNumber) args.get(1)).getValue()).doubleValue() ),
		      alarmNone(), newTime(Timestamp.now()), displayNone() );
    }
}

