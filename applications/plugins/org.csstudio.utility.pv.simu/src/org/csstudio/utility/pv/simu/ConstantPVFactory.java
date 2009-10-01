package org.csstudio.utility.pv.simu;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;

/** PV Factory for 'constant' PVs
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ConstantPVFactory implements IPVFactory
{
    /** PV type prefix */
    public static final String PREFIX = "const";

    /** Create a 'local' PV.
     *  @param name Name of the PV
     *  @throws Exception on error
     */
    public PV createPV(final String name) throws Exception
    {
        return new ConstantPV(name);
    }
}
