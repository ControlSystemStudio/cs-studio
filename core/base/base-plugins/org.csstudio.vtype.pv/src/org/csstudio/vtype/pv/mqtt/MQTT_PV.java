/*******************************************************************************
 * Copyright (c) 2014-2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.mqtt;

import org.csstudio.vtype.pv.PV;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/** MQTT Process Variable
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class MQTT_PV extends PV
{

    protected MQTT_PV(final String name, final String base_name) throws Exception
    {
        super(name);
    }


    @Override
    public void write(final Object new_value) throws Exception
    {
        if (new_value == null)
            throw new Exception(getName() + " got null");

        try
        {
            final VType value = ValueFactory.newVDouble(3.14);
            notifyListenersOfValue(value);
        }
        catch (Exception ex)
        {
            throw new Exception("Failed to write '" + new_value + "' to " + getName(), ex);
        }
    }
}
