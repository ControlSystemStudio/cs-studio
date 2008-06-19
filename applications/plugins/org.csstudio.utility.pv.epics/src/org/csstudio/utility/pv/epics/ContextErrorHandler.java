package org.csstudio.utility.pv.epics;

import gov.aps.jca.event.ContextExceptionEvent;
import gov.aps.jca.event.ContextExceptionListener;
import gov.aps.jca.event.ContextMessageEvent;
import gov.aps.jca.event.ContextMessageListener;
import gov.aps.jca.event.ContextVirtualCircuitExceptionEvent;

/** Handler for JCA Context errors and messages; places them in log.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ContextErrorHandler implements ContextExceptionListener,
                                        ContextMessageListener
{
    /** @see ContextExceptionListener */
    public void contextException(final ContextExceptionEvent ev)
    {
        Activator.getLogger().error("Exception from " + ev.getSource()
                + " " + ev.getMessage());
    }

    /** @see ContextExceptionListener */
    public void contextVirtualCircuitException(ContextVirtualCircuitExceptionEvent ev)
    {
      // nop
    }

    /** @see ContextMessageListener */
    public void contextMessage(final ContextMessageEvent ev)
    {
        Activator.getLogger().info("Message from " + ev.getSource()
                + ": " + ev.getMessage());
    }
}
