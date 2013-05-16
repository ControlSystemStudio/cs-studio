/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.vtype.VTable;
import org.epics.vtype.table.Column;
import org.epics.vtype.table.VTableFactory;

/**
 *
 * @author carcassi
 */
class TableOfFormulaFunction implements FormulaFunction {

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
        return "tableOf";
    }

    @Override
    public String getDescription() {
        return "Constructs a table from a series of columns";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(Column.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("columns");
    }

    @Override
    public Class<?> getReturnType() {
        return VTable.class;
    }

    @Override
    public Object calculate(final List<Object> args) {
        Column[] columns = args.toArray(new Column[args.size()]);
        
        return VTableFactory.newVTable(columns);
    }
    
}
