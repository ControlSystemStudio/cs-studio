/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;

/** PV Factory for simulated PVs
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SystemPVFactory implements IPVFactory
{
    /** PV type prefix */
    public static final String PREFIX = "sys";

    /** All the 'system' PVs, mapped by name */
    private static Map<String, Value> values =
        new HashMap<String, Value>();

    /** @return Number of values */
    public static int getValueCount()
    {
        return values.size();
    }

    /** Create a 'dynamic' PV.
     *  @param name Name of the PV
     */
    @Override
    public PV createPV(final String name)
    {
        Value value = values.get(name);
        if (value == null)
        {
            if (name.equals("pv_count"))
                value = new PVCountValue(name);
            else if (name.equals("time"))
                value = new TimeValue(name);
            else if (name.equals("free_mb"))
                value = new FreeMemValue(name);
            else if (name.equals("used_mb"))
                value = new UsedMemValue(name);
            else if (name.equals("max_mb"))
                value = new MaxMemValue(name);
            else if (name.startsWith("system.")){
            	String prop = name.substring(7);
            	String prop_value = System.getProperty(prop);
            	if(prop_value != null)
            		value = new TextValue(name, prop_value);
            	else
            		value = new TextValue(name,
                        "Unknown system property '" + prop + "'", false);
            }
            else if (name.equals("host_name"))
				try {
					value = new TextValue(name, InetAddress.getLocalHost().getHostName());
				} catch (UnknownHostException e) {
					value = new TextValue(name, "Unknown Host", false);
				}
			else if (name.equals("qualified_host_name"))
				try {
					value = new TextValue(name, InetAddress.getLocalHost().getCanonicalHostName());
				} catch (UnknownHostException e) {
					value = new TextValue(name, "Unknown Host", false);
				}
			else
                value = new TextValue(name,
                        "Unknown system PV '" + name + "'", false);
            values.put(name, value);
        }
        if (value instanceof DynamicValue)
            return new SimulatedPV(PREFIX, (DynamicValue) value);
        return new StaticPV(PREFIX, value);
    }
}
