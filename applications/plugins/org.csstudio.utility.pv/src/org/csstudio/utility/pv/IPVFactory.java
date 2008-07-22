package org.csstudio.utility.pv;

/** Interface that implementors of the PVFactory extension point
 *  must provide.
 *  @author Kay Kasemir
 */
public interface IPVFactory
{
    /** Create a PV for the given channel name.
     *  @param name Name of the Process Variable
     *  @return PV
     */
    public PV createPV(String name);
}