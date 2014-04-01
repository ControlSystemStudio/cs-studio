/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.local;

import org.csstudio.vtype.pv.PV;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/** Local Process Variable
 *  
 *  @author Kay Kasemir
 */
public class LocalPV extends PV
{
    protected LocalPV(final String name, final String base_name) throws Exception
    {
        super(name);
        
        // Get initial value: Split name off the initial value
        VType value;
        final int sep = base_name.indexOf('(');
        if (sep < 0)
            throw new Exception(getName() + " missing '(' for initial value");
        final int end = base_name.lastIndexOf(')');
        if (end <= sep)
            throw new Exception(getName() + " missing ')' to define initial value");
        String value_text = base_name.substring(sep+1, end);
        
        // Remove "quotes around string constant"
        if (value_text.startsWith("\"")  &&  value_text.endsWith("\""))
            value_text = value_text.substring(1, value_text.length()-1);
        value = VTypeHelper.toVType(value_text);
        notifyListenersOfValue(value);
    }

    @Override
    public void write(Object new_value) throws Exception
    {
        if (new_value instanceof VType)
            notifyListenersOfValue( (VType) new_value);
        else if (new_value instanceof Number)
            notifyListenersOfValue(ValueFactory.newVDouble( ((Number)new_value).doubleValue() ));
        else if (new_value instanceof String)
            notifyListenersOfValue(VTypeHelper.toVType( (String) new_value));
        else
            throw new Exception("Cannot write data of type" + new_value.getClass().getName());
    }
}
