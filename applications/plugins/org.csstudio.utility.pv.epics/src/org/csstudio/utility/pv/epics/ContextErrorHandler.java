package org.csstudio.utility.pv.epics;

import java.util.logging.Level;
import java.util.logging.Logger;

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
    final Logger logger = Logger.getLogger(getClass().getName());

    /** @see ContextExceptionListener */
    @Override
    public void contextException(final ContextExceptionEvent ev)
    {
        logger.log(Level.WARNING, "Channel Access Exception from {0}: {1}",
                new Object[] { ev.getSource(), ev.getMessage() });
    }

    /** @see ContextExceptionListener */
    @Override
    public void contextVirtualCircuitException(ContextVirtualCircuitExceptionEvent ev)
    {
      // nop
    }

    /** @see ContextMessageListener */
    @Override
    public void contextMessage(final ContextMessageEvent ev)
    {
        logger.log(Level.INFO, "Channel Access Message from {0}: {1}",
                new Object[] { ev.getSource(), ev.getMessage() });
    }
}
