package org.csstudio.utility.pv;

/** Interface that implementors of the PVFactory extension point
 *  must provide.
 *  @author Kay Kasemir
 */
public interface IPVFactory
{
    /** Create a PV for the given channel name.
     *  @return PV
     */
    public PV createPV( String name);
}