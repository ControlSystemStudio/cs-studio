/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.ContextExceptionListener;
import gov.aps.jca.event.ContextMessageListener;

import java.util.HashMap;
import java.util.logging.Level;

import org.csstudio.platform.libs.epics.EpicsPlugin.MonitorMask;

/** Handle PV context, pool PVs by name.
 *
 *  When using the pure java CA client implementation, it returns the
 *  same 'channel' when trying to access the same PV name multiple times.
 *  That's good, but I don't know how to determine if the channel for this
 *  EPICS_V3_PV is actually shared.
 *  Calling destroy() on such a shared channel creates problems.<br>
 *  The PVContext adds its own hash map of channels and keeps a reference
 *  count.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVContext
{
    /** In principle, we like to close the context when it is no longer needed.
     *
     *  This is in fact required for CAJ to close all threads.
     *
     *  For JCA, however, there are problems:
     *  With R3.14.8.2 on Linux there were errors
     *  "pthread_create error Invalid argument".
     *  With R3.14.11 on OS X 10.5.8 the call to jca_context.destroy()
     *  caused an "Invalid memory access ..." crash.
     *
     *  -> We keep the context open.
     */
    final private static boolean cleanup = false;

    /** Set to <code>true</code> if the pure Java CA context should be used.
     *  <p>
     *  Changes only have an effect before the very first channel is created.
     */
    public static boolean use_pure_java = true;

    /** Mask used for CA monitors */
    public static MonitorMask monitor_mask = MonitorMask.VALUE;

    /** Support the DBE_PROPERTY subscription? */
    public static boolean support_dbe_property = false;

    /** The Java CA Library instance. */
    static private JCALibrary jca = null;

    /** The JCA context. */
    static private volatile Context jca_context = null;

    /** The JCA context reference count. */
    static private long jca_refs = 0;

    /** map of channels. */
    static private HashMap<String, RefCountedChannel> channels =
                                new HashMap<String, RefCountedChannel>();

    static private JCACommandThread command_thread = null;

    /** Initialize the JA library, start the command thread. */
    static private void initJCA() throws Exception
    {
        if (jca_refs == 0)
        {
            if (jca == null)
            {
                Activator.getLogger().config("Initializing JCA "
                                + (use_pure_java ? "(pure Java)" : "(JNI)"));
                jca = JCALibrary.getInstance();
                final String type = use_pure_java ?
                    JCALibrary.CHANNEL_ACCESS_JAVA : JCALibrary.JNI_THREAD_SAFE;
                jca_context = jca.createContext(type);

                // Per default, JNIContext adds a logger to System.err,
                // but we want this one:
                final ContextErrorHandler log_handler = new ContextErrorHandler();
                jca_context.addContextExceptionListener(log_handler);
                jca_context.addContextMessageListener(log_handler);

                // Debugger shows that JNIContext adds the System.err
                // loggers during initialize(), which for example happened
                // in response to the last addContext... calls, so fix
                // it after the fact:
                final ContextExceptionListener[] ex_lsnrs =
                    jca_context.getContextExceptionListeners();
                for (ContextExceptionListener exl : ex_lsnrs)
                    if (exl != log_handler)
                        jca_context.removeContextExceptionListener(exl);

                // Same with message listeners
                final ContextMessageListener[] msg_lsnrs =
                    jca_context.getContextMessageListeners();
                for (ContextMessageListener cml : msg_lsnrs)
                    if (cml != log_handler)
                        jca_context.removeContextMessageListener(cml);
            }
            command_thread = new JCACommandThread(jca_context);
        }
        ++jca_refs;
    }

    /** Disconnect from the JA library.
     *  <p>
     *  Without this step, JCA threads can stay around and prevent the
     *  application from quitting.
     */
    static private void exitJCA()
    {
        --jca_refs;
        if (jca_refs > 0)
            return;
        command_thread.shutdown();
        command_thread = null;
        if (cleanup == false)
        {
            Activator.getLogger().fine("JCA no longer used, but kept open.");
            return;
        }
        try
        {
            jca_context.destroy();
            jca_context = null;
            jca = null;
            Activator.getLogger().fine("Finalized JCA");
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING, "exitJCA", ex);
        }
    }

    /** Get a new channel, or a reference to an existing one.
     *  @param name Channel name
     *  @return reference to channel
     *  @throws Exception on error
     *  @see #releaseChannel(RefCountedChannel)
     */
    public synchronized static RefCountedChannel getChannel(final String name,
                     final ConnectionListener conn_callback) throws Exception
    {
        initJCA();
        RefCountedChannel channel_ref = channels.get(name);
        if (channel_ref == null)
        {
            Activator.getLogger().log(Level.FINER, "Creating CA channel {0}", name);
            final Channel channel = jca_context.createChannel(name, conn_callback);
            if (channel == null)
                throw new Exception("Cannot create channel '" + name + "'");
            channel_ref = new RefCountedChannel(channel);
            channels.put(name, channel_ref);
            // Start the command thread after the first channel is created.
            // This starts it in any case, but follow-up calls are NOPs.
            command_thread.start();
        }
        else
        {
            channel_ref.incRefs();
            // TODO: Saw null pointer exception here.
            // Must have been getChannel() == null, but how is that possible?
            channel_ref.getChannel().addConnectionListener(conn_callback);
            Activator.getLogger().log(Level.FINER, "Re-using CA channel {0}", name);
        }
        return channel_ref;
    }

    /** Release a channel.
     *  @param channel_ref Channel to release.
     *  @see #getChannel(String)
     */
    public synchronized static void releaseChannel(final RefCountedChannel channel_ref,
                    final ConnectionListener conn_callback)
    {
        final String name = channel_ref.getChannel().getName();
        try
        {
            channel_ref.getChannel().removeConnectionListener(conn_callback);
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING, "Remove connection listener", ex);
        }
        if (channel_ref.decRefs() <= 0)
        {
            Activator.getLogger().finer("Deleting CA channel " + name);
            channels.remove(name);
            channel_ref.dispose();
        }
        else
            Activator.getLogger().finer("CA channel " + name + " still ref'ed");
        exitJCA();
    }

    /** Add a command to the JCACommandThread.
     *  <p>
     *  @param command Command to schedule.
     *  @throws NullPointerException when JCA not active
     */
    public static void scheduleCommand(final Runnable command)
    {
    	// Debug: Run immediately
    	// command.run();
        command_thread.addCommand(command);
    }

    /** Helper for unit test.
     *  @return <code>true</code> if all has been release.
     */
    static boolean allReleased()
    {
        return jca_refs == 0;
    }
}
