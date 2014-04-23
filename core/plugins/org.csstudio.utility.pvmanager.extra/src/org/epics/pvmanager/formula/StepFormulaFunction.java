/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.vtype.VNumber;
import org.epics.vtype.table.ListNumberProvider;
import org.epics.vtype.table.VTableFactory;

/**
 *
 * @author carcassi
 */
class StepFormulaFunction implements FormulaFunction {

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
        return "step";
    }

    @Override
    public String getDescription() {
        return "A generator for values based on initial value and incremen";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VNumber.class, VNumber.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("initialValue", "increment");
    }

    @Override
    public Class<?> getReturnType() {
        return ListNumberProvider.class;
    }

    @Override
    public Object calculate(final List<Object> args) {
        VNumber initialValue = (VNumber) args.get(0);
        VNumber increment = (VNumber) args.get(1);
        
        if (initialValue == null || increment == null) {
            return null;
        }
        
        return VTableFactory.step(initialValue.getValue().doubleValue(), increment.getValue().doubleValue());
    }
    
}
