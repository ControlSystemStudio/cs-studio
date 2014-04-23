/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.table.Column;
import org.epics.vtype.table.VTableFactory;

/**
 * Constructs a table column from a string array.
 *
 * @author carcassi
 */
class ColumnFromVStringArrayFunction implements FormulaFunction {

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
        return "column";
    }

    @Override
    public String getDescription() {
        return "Constructs a table column from a string array";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VString.class, VStringArray.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("columnName", "stringArray");
    }

    @Override
    public Class<?> getReturnType() {
        return Column.class;
    }

    @Override
    public Object calculate(final List<Object> args) {
        VString name = (VString) args.get(0);
        VStringArray data = (VStringArray) args.get(1);
        
        if (name == null || data == null) {
            return null;
        }

        return VTableFactory.column(name.getValue(), data);
    }
    
}
