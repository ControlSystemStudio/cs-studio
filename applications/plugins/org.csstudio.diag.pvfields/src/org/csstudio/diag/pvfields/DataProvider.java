package org.csstudio.diag.pvfields;

/** Interface to code the provides {@link PVInfo}
 *  @author Kay Kasemir
 */
public interface DataProvider
{
	/** Perform lookup
	 *  <p>Will be invoked in background thread,
	 *  does not need to start its own thread for long-running activities.
	 *  @param name Name of PV/Channel
	 *  @return {@link PVInfo}
	 *  @throws Exception on error
	 */
    public PVInfo lookup(String name) throws Exception;
}
