package org.csstudio.utility.pv.simu;

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

    /** All the 'simulated' PVs, mapped by name */
    private static Map<String, Value> values =
        new HashMap<String, Value>();

    /** Create a 'dynamic' PV.
     *  @param name Name of the PV
     */
    public PV createPV(final String name)
    {
        Value value = values.get(name);
        if (value == null)
        {
            if (name.equals("time"))
                value = new TimeValue(name);
            else if (name.equals("free_mb"))
                value = new FreeMemValue(name);
            else if (name.equals("used_mb"))
                value = new UsedMemValue(name);
            else if (name.equals("max_mb"))
                value = new MaxMemValue(name);
            else if (name.equals("user"))
                value = new TextValue(name, System.getProperty("user.name"));
            else
                value = new TextValue(name,
                        "Unknown system property '" + name + "'", false);
            values.put(name, value);
        }
        if (value instanceof DynamicValue)
            return new SimulatedPV(PREFIX, (DynamicValue) value);
        return new StaticPV(PREFIX, value);
    }
}
