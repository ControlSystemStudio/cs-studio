package org.csstudio.sns.mpsbypasses.model;

/** Listener to updates from {@link BypassModel}
 *  @author Kay Kasemir
 */
public interface BypassModelListener extends BypassListener
{
	/** The model loaded all the bypass info from the RDB,
	 *  it's ready to be started.
	 *  
	 *  @param model Model that has loaded
	 *  @param error Exception that happened while loading model,
	 *               or <code>null</code> if OK
	 *  @see BypassModel#selectMachineMode(MachineMode)
	 */
	void modelLoaded(final BypassModel model, Exception error);

	/** Invoked when bypass counts have changed:
	 *  A previously filtered bypass because visible
	 *  or vice versa, so an overall refresh is needed.
	 */
	void bypassesChanged();
}
