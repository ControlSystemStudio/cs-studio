/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.jca;

import static org.csstudio.vtype.pv.PV.logger;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;
import java.util.logging.Level;

import com.cosylab.epics.caj.CAJContext;

import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Version;
import gov.aps.jca.event.ContextExceptionEvent;
import gov.aps.jca.event.ContextExceptionListener;
import gov.aps.jca.event.ContextMessageEvent;
import gov.aps.jca.event.ContextMessageListener;
import gov.aps.jca.event.ContextVirtualCircuitExceptionEvent;

/** Handler for JCA context
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JCAContext implements ContextMessageListener, ContextExceptionListener
{
    private static JCAContext instance;

    final private JCALibrary jca = JCALibrary.getInstance();
    final private Context context;
    final private boolean is_var_array_supported;

    private JCAContext() throws Exception
    {
        final boolean use_caj = JCA_Preferences.getInstance().usePureJava();
        if (use_caj)
        {
            logger.log(Level.CONFIG, "Using Pure Java CAJ");
            // API used to be jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA),
            // but JCA plugin cannot locate CAJContext in separate CAJ plugin.
            // This plugin, however, can see both and thus create a CAJContext.
            context = new CAJContext();
        }
        else
        {
            logger.log(Level.CONFIG, "Using JNI JCA");
            context = jca.createContext(JCALibrary.JNI_THREAD_SAFE);
        }

        // PVPool will try to re-use channels, but
        // if user creates the same PV with and without prefix,
        // that would result in the same CAJ channel,
        // and CAJContext must keep them separate,
        // because otherwise closing one would also close the 'other'.
        if (context instanceof CAJContext)
            ((CAJContext) context).setDoNotShareChannels(true);

        context.addContextMessageListener(this);
        context.addContextExceptionListener(this);

        // Potentially check version for variable array support,
        // based on diirt JCADataSourceConfiguration#isVarArraySupported().
        boolean supported;
        switch (JCA_Preferences.getInstance().isVarArraySupported())
        {
        case AUTO:
            if (use_caj)
            {   // Variable array support was added to CAJ 1.1.10
                final Version version = ((CAJContext)context).getVersion();
                logger.log(Level.CONFIG, Objects.toString(version));
                supported = ! (version.getMajorVersion() <= 1 &&
                               version.getMinorVersion() <= 1 &&
                               version.getMaintenanceVersion() <=9);
            }
            else
            {   // Variable array support was added to EPICS 3.14.12
                try
                {   // For JNI calls require that native jca lib was loaded,
                    // which is the case after the above addContext*Listener() calls.
                    final Class<?> jni = Class.forName("gov.aps.jca.jni.JNI");
                    final int version = invokePrivateMethod(jni, "_ca_getVersion");
                    final int ref = invokePrivateMethod(jni, "_ca_getRevision");
                    final int mod = invokePrivateMethod(jni, "_ca_getModification");
                    logger.log(Level.CONFIG, "JCA version " + version + "." + ref + "." + mod);
                    supported = ! (version <= 3  &&  ref <= 14  &&  mod <= 11);
                }
                catch (Exception ex)
                {
                    logger.log(Level.SEVERE, "Couldn't detect JCA JNI version", ex);
                    supported = false;
                }
            }
            break;
        case TRUE:
            supported = true;
            break;
        case FALSE:
        default:
            supported = false;
            break;
        }
        is_var_array_supported = supported;
    }

    /** Invoke a private(!) static method
     *  @param clazz Class on which to invoke the method
     *  @param method_name Method name, must return Integer
     *  @return Result of method call
     *  @throws Exception on error
     */
    private static int invokePrivateMethod(final Class<?> clazz, final String method_name) throws Exception
    {
        final Method method = clazz.getDeclaredMethod(method_name, new Class<?>[0]);
        AccessController.doPrivileged(new PrivilegedAction<Object>()
        {
            @Override
            public Object run()
            {
                method.setAccessible(true);
                return null;
            }
        });
        final Integer value = (Integer) method.invoke(null, (Object[])null);
        return value.intValue();
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

    /** Determine how many array elements to request
     *  @param channel
     *  @return Array request count
     */
    public int getRequestCount(final Channel channel)
    {
        final int actual = channel.getElementCount();
        // For scalar, get that one element
        if (actual == 1)
            return 1;
        // For arrays, try to use variable size
        if (is_var_array_supported)
            return 0;
        return actual;
    }

    @Override
    public void contextException(final ContextExceptionEvent ev)
    {
        //Ignore warnings for DBE_PROPERTY from old CAJ.
        if(ev != null && "event add req with mask=0X8\n".equals(ev.getMessage()))
            logger.log(Level.FINE, "Ignored Exception from {0} for channel {1}: {2}",
                       new Object[] { ev.getSource(), ev.getChannel(), ev.getMessage() });
        else
            logger.log(Level.WARNING, "Channel Access Exception from {0} for channel {1}: {2}",
                       new Object[] { ev.getSource(), ev.getChannel(), ev.getMessage() });
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
