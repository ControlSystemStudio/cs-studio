/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.jca;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.event.ContextExceptionEvent;
import gov.aps.jca.event.ContextExceptionListener;
import gov.aps.jca.event.ContextMessageEvent;
import gov.aps.jca.event.ContextMessageListener;
import gov.aps.jca.event.ContextVirtualCircuitExceptionEvent;

import org.csstudio.vtype.pv.internal.Preferences;

import com.cosylab.epics.caj.CAJContext;

/** Handler for JCA context
 *  @author Kay Kasemir
 */
public class JCAContext implements ContextMessageListener, ContextExceptionListener
{
    final private static Logger logger = Logger.getLogger(JCAContext.class.getName());

    private static JCAContext instance;
    
    final private JCALibrary jca = JCALibrary.getInstance();
    final private Context context;

    private JCAContext() throws Exception
    {
        final String type;
        if (Preferences.usePureJava())
        {
            type = JCALibrary.CHANNEL_ACCESS_JAVA;
            logger.log(Level.CONFIG, "Using Pure Java CAJ");
        }
        else
        {
            type = JCALibrary.JNI_THREAD_SAFE;
            logger.log(Level.CONFIG, "Using JNI JCA");
        }
        context = jca.createContext(type);
        
        // PVPool will try to re-use channels, but
        // if user creates the same PV with and without prefix,
        // that would result in the same CAJ channel,
        // and CAJContext must keep them separate,
        // because otherwise closing one would also close the 'other'.
        if (context instanceof CAJContext)
            ((CAJContext) context).setDoNotShareChannels(true);
        
        context.addContextMessageListener(this);
        context.addContextExceptionListener(this);
    }

    public static synchronized JCAContext getInstance() throws Exception
    {
        if (instance == null)
            instance = new JCAContext();
        return instance;
    }
    
    // TODO Thread for periodic flushIO, to run certain JNI calls on same thread?
    
    public Context getContext()
    {
        return context;
    }

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

    @Override
    public void contextVirtualCircuitException(final ContextVirtualCircuitExceptionEvent ev)
    {
        // Ignore
    }

    @Override
    public void contextMessage(final ContextMessageEvent ev)
    {
        logger.log(Level.INFO, "Channel Access Message from {0}: {1}",
                new Object[] { ev.getSource(), ev.getMessage() });
    }
}
