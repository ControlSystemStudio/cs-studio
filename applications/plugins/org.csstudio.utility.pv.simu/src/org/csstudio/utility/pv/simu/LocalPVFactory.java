package org.csstudio.utility.pv.simu;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;

/** PV Factory for 'local' PVs
 *  @author Kay Kasemir
 */
public class LocalPVFactory implements IPVFactory
{
    /** All the 'local' PVs, mapped by name */
    private static Map<String, DynamicValue> values =
        new HashMap<String, DynamicValue>();

    /** Create a 'local' PV.
     *  @param name Name of the PV
     */
    public PV createPV(final String name)
    {
        DynamicValue value = values.get(name);
        if (value == null)
        {
            value = new DynamicValue(name);
            values.put(name, value);
        }
        return new LocalPV(value);
    }
}
