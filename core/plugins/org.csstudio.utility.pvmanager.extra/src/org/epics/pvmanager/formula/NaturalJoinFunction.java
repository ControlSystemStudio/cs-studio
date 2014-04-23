/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.ArrayList;
import org.epics.vtype.ValueFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.table.VTableFactory;

/**
 * Natural join of a set of tables.
 *
 * @author carcassi
 */
class NaturalJoinFunction implements FormulaFunction {

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
        return "join";
    }

    @Override
    public String getDescription() {
        return "Natural join between tables";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VTable.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("tables");
    }

    @Override
    public Class<?> getReturnType() {
        return VTable.class;
    }

    @Override
    public Object calculate(final List<Object> args) {
        List<VTable> tables = new ArrayList<>();
        for (Object object : args) {
            if (object != null) {
                tables.add((VTable) object);
            }
        }
        
        return VTableFactory.join(tables);
    }
    
}
