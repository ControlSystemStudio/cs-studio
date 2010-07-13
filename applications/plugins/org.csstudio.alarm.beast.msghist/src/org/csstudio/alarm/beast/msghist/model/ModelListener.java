package org.csstudio.alarm.beast.msghist.model;

/** Listener gets notified when model changes.
 *  @author Kay Kasemir
 */
public interface ModelListener
{
	/** Invoked when the model changed in some way.
	 *  <p>
	 *  <b>Note:</b> Call can originate from non-GUI thread.
	 *  
	 *  @param model Model that has new data or is somehow different
	 */
    public void modelChanged(Model model);
}
