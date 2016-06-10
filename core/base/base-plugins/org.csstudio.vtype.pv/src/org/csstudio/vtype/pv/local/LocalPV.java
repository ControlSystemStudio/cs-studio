/*******************************************************************************
 * Copyright (c) 2014-2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.local;

import java.util.Arrays;
import java.util.List;

import org.csstudio.vtype.pv.PV;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VDoubleArray;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VLong;
import org.diirt.vtype.VString;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VType;

/** Local Process Variable
 *
 *  <p>Syntax:
 *  <ul>
 *  <li>loc://name(3.14), same as loc://name&lt;VDouble>(3.14)
 *  <li>loc://name("Fred"), same as loc://name&lt;VString>("Fred")
 *  <li>loc://name(1, 2, 3), same as loc://name&lt;VDoubleArray>(1, 2, 3)
 *  <li>loc://name&lt;VDoubleArray>(1), forces array type
 *  <li>loc://name("a", "b", "c"), same as loc://name&lt;VStringArray>("a", "b", "c")
 *  <li>loc://name&lt;VLong>(1e10), forces long integer data type
 *  <li>loc://name&lt;VEnum>(0, "a", "b", "c"), declares enumerated type with initial value and labels
 *  <li>loc://name&lt;VTable>, declares PV as table (initially empty)
 *  <li>loc://name&lt;VTable>("X", "Y"), declares PV as table with given column names (initially empty)
 *  </ul>
 *  @author Kay Kasemir, based on similar code in org.csstudio.utility.pv and diirt
 */
@SuppressWarnings("nls")
public class LocalPV extends PV
{
    private Class<? extends VType> type;

    protected LocalPV(final String name, final String base_name) throws Exception
    {
        super(name);

        final String[] ntv = ValueHelper.parseName(base_name);

        // Info for initial value, default to "0"
        final List<String> initial_value_items;
        if (ntv[2] == null)
            initial_value_items = Arrays.asList("0");
        else
            initial_value_items = ValueHelper.splitInitialItems(ntv[2]);

        // Determine type from initial value or use given type
        if (ntv[1] == null)
            type = determineValueType(initial_value_items);
        else
            type = parseType(ntv[1]);

        // Set initial value
        notifyListenersOfValue(ValueHelper.getInitialValue(initial_value_items, type));
    }

    private Class<? extends VType> determineValueType(final List<String> items) throws Exception
    {
        if (ValueHelper.haveInitialStrings(items))
        {
            if (items.size() == 1)
                return VString.class;
            else
                return VStringArray.class;
        }
        else
        {
            if (items.size() == 1)
                return VDouble.class;
            else
                return VDoubleArray.class;
        }
    }

    private Class<? extends VType> parseType(final String type) throws Exception
    {   // Lenient check, ignore case and allow partial match
        final String lower = type.toLowerCase();
        if (lower.contains("doublearray"))
            return VDoubleArray.class;
        if (lower.contains("double"))
            return VDouble.class;
        if (lower.contains("stringarray"))
            return VStringArray.class;
        if (lower.contains("string"))
            return VString.class;
        if (lower.contains("enum"))
            return VEnum.class;
        if (lower.contains("long"))
            return VLong.class;
        if (lower.contains("table"))
            return VTable.class;
        throw new Exception("Local PV cannot handle type '" + type + "'");
    }

    @Override
    public void write(final Object new_value) throws Exception
    {
        if (new_value == null)
            throw new Exception(getName() + " got null");

        try
        {
            final VType value = ValueHelper.adapt(new_value, type, read());
            notifyListenersOfValue(value);
        }
        catch (Exception ex)
        {
            throw new Exception("Failed to write '" + new_value + "' to " + getName(), ex);
        }
     }
}
