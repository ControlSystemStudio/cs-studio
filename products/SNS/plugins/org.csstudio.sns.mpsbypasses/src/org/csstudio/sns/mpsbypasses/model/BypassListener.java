package org.csstudio.sns.mpsbypasses.model;

/** Listener to {@link Bypass} changes
 *  @author Kay Kasemir
 */
public interface BypassListener
{
	/** Invoked when a {@link Bypass} changes its state
	 *  @param bypass Bypass that changed its state
	 */
	public void bypassChanged(Bypass bypass);
}
