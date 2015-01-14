/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
    	//Ignore warnings for DBE_PROPERTY from old CAJ.
    	if(ev != null && "event add req with mask=0X8\n".equals(ev.getMessage())){ //$NON-NLS-1$
    		 logger.log(Level.FINE, "Ignored Message from {0}: {1}", 
    				 new Object[] { ev.getSource(), ev.getMessage()});
    		 return;
    	}
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
