/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.epics.vtype.VTable;
import org.epics.vtype.table.Column;
import org.epics.vtype.table.ListNumberProvider;
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
        List<Object> argsNoNull = new ArrayList<>(args);
        
        // Remove null columns if there are any
        boolean removedNull = false;
        while (argsNoNull.remove(null)) {
            removedNull = true;
        }
        
        // If null was removed, check whether all the remaining columns
        // are generated. In that case, return null.
        // This needs to be here because ListNumberProvider are usually
        // static, while the other columns may be from waveforms coming from
        // the network. So, at connection, it's often the case
        // that only variable columns are connected. This is a temporary
        // problem, so we don't want the warning that at least
        // one column must be fixed size.
        if (removedNull) {
            boolean allGenerated = true;
            for (Object object : argsNoNull) {
                Column column = (Column) object;
                if (!column.isGenerated()) {
                    allGenerated = false;
                }
            }
            if (allGenerated) {
                return null;
            }
        }
        
        Column[] columns = argsNoNull.toArray(new Column[argsNoNull.size()]);
        
        return VTableFactory.newVTable(columns);
    }
    
}
