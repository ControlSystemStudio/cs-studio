package org.csstudio.utility.pv.epics;

import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.event.ConnectionListener;

import java.util.HashMap;

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
    final public static boolean debug = false;
    
    /** In principle, we like to close the context when it is no longer needed.
     *  But as long as JCA with R3.14.8.2 causes
     *  "pthread_create error Invalid argument" errors,
     *  we keep the context open.
     *  TODO Remove when JCA/R3.14.8.2 have been fixed.
     */
    final private static boolean cleanup = false;
    
    /** Set to <code>true</code> if the pure Java CA context should be used.
     *  <p>
     *  Changes only have an effect before the very first channel is created.
     */
    public static boolean use_pure_java = true;

    /** The Java CA Library instance. */
    static private JCALibrary jca = null;

    /** The JCA context. */
    static private volatile Context jca_context = null;

    /** The JCA context reference count. */
    static private long jca_refs = 0;
    
    /** map of channels. */
    static private HashMap<String, RefCountedChannel> channels =
                                new HashMap<String, RefCountedChannel>();
    
    final static private JCACommandThread command_thread = new JCACommandThread();

    /** Initialize the JA library. */
    static private void initJCA() throws Exception
    {
        if (jca_refs == 0 && jca == null)
        {
            if (debug)
                System.out.println("Initializing JCA "
                                + (use_pure_java ? "(pure Java)" : "(JNI)"));
            jca = JCALibrary.getInstance();
            final String type = use_pure_java ?
                JCALibrary.CHANNEL_ACCESS_JAVA : JCALibrary.JNI_THREAD_SAFE;
            jca_context = jca.createContext(type);
            command_thread.start();
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
        if (cleanup == false)
            return;
        try
        {
            jca_context.destroy();
            jca_context = null;
            jca = null;
            if (debug)
                System.out.println("Finalized JCA");
        }
        catch (Exception ex)
        {
            Activator.logException("exitJCA", ex);
        }
    }

    /** Get a new channel, or a reference to an existing one.
     *  @param name Channel name
     *  @return reference to channel
     *  @throws Exception on error
     *  @see #releaseChannel(RefCountedChannel)
     */
    synchronized static RefCountedChannel getChannel(final String name,
                     final ConnectionListener conn_callback) throws Exception
    {
        initJCA();
        RefCountedChannel channel_ref = channels.get(name);
        if (channel_ref == null)
        {
            if (debug)
                System.out.println("Creating CA channel " + name);
            final Channel channel = jca_context.createChannel(name, conn_callback);
            if (channel == null)
                throw new Exception("Cannot create channel '" + name + "'");
            channel_ref = new RefCountedChannel(channel);
            channels.put(name, channel_ref);
        }
        else
        {
            channel_ref.incRefs();
            channel_ref.getChannel().addConnectionListener(conn_callback);
            if (debug)
                System.out.println("Re-using CA channel " + name);
        }
        jca_context.flushIO();
        return channel_ref;
    }
    
    /** Release a channel.
     *  @param channel_ref Channel to release.
     *  @see #getChannel(String)
     */
    synchronized static void releaseChannel(final RefCountedChannel channel_ref,
                    final ConnectionListener conn_callback)
    {
        final String name = channel_ref.getChannel().getName();
        try
        {
            channel_ref.getChannel().removeConnectionListener(conn_callback);
        }
        catch (Exception ex)
        {
            Activator.logException("Remove connection listener", ex);
        }
        if (channel_ref.decRefs() <= 0)
        {
            if (debug)
                System.out.println("Deleting CA channel " + name);
            channels.remove(name);
            channel_ref.dispose();
        }
        else if (debug)
            System.out.println("CA channel " + name + " still ref'ed");
        exitJCA();
    }
    
    /** Flush the CA context. */
    static void flush()
    {
        try
        {
            jca_context.flushIO();
        }
        catch (Throwable ex)
        {
            Activator.logException("JCA Flush failed", ex);
        }
    }
    
    /** Helper for unit test.
     *  @return <code>true</code> if all has been release.
     */
    synchronized static boolean allReleased()
    {
        return jca_refs == 0;
    }
}
