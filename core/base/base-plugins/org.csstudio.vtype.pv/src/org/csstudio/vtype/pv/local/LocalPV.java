/*******************************************************************************
 * Copyright (c) 2014-2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.local;

import java.util.List;
import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;
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
    private final Class<? extends VType> type;
    private final List<String> initial_value;

    protected LocalPV(final String actual_name, final Class<? extends VType> type, final List<String> initial_value) throws Exception
    {
        super(actual_name);
        this.type = type;
        this.initial_value = initial_value;

        // Set initial value
        notifyListenersOfValue(ValueHelper.getInitialValue(initial_value, type));
    }

    protected void checkInitializer(final Class<? extends VType> type, final List<String> initial_value)
    {
        if (type != this.type  ||  !initial_value.equals(this.initial_value))
            logger.log(Level.WARNING, "PV " + getName() + " was initialized as " + formatInit(this.type, this.initial_value) +
                    " and is now requested as " +  formatInit(type, initial_value));
    }

    private String formatInit(Class<? extends VType> type, List<String> value)
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("<").append(type.getSimpleName()).append(">(");
        for (int i=0; i<value.size(); ++i)
        {
            if (i > 0)
                buf.append(",");
            buf.append(value.get(i));
        }
        buf.append(")");
        return buf.toString();
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

    @Override
    protected void close()
    {
        super.close();
        LocalPVFactory.releasePV(this);
    }
}
