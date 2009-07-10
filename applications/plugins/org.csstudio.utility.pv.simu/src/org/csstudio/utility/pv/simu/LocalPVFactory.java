package org.csstudio.utility.pv.simu;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;

/** PV Factory for 'local' PVs
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LocalPVFactory implements IPVFactory
{
    /** PV type prefix */
    public static final String PREFIX = "loc";
    
    /** All the 'local' PVs, mapped by name */
    private static Map<String, Value> values =
        new HashMap<String, Value>();

    /** @return Number of values */
    public static int getValueCount()
    {
        return values.size();
    }

    /** Create a 'local' PV.
     *  @param name Name of the PV
     */
    public PV createPV(final String name)
    {
        Value value = values.get(name);
        if (value == null)
        {
            value = new Value(name);
            values.put(name, value);
        }
        return new LocalPV(PREFIX, value);
    }
}
