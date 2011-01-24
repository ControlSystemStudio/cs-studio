/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.meta.When;

import com.google.common.collect.MapMaker;

/** A group of archived channels.
 *  Each channel is in exactly one group.
 *  @author Kay Kasemir
 */
public class ArchiveGroup
{
    /** Name of this group */
    final private String _name;
    
    /** All the channels in this group
     *  <p>
     *  Using thread-safe array to allow HTTPD as well as main
     *  thread to traverse
     */
    private final ConcurrentMap<String, ArchiveChannel<?>> _channelMap;
    
    /** (At most) one of the channels might be 'enabling' or 'disabling' */
//    private ArchiveChannel enabling_channel = null;
        
    /** Is the group currently enabled? */
    private boolean enabled = true;
    
    /** Set to <code>true</code> while running. */
    private boolean is_running = false;
    
    /**
     * Constructor.
     * 
     * @param name the name of the group
     * @param numOfChannels the initial capacity for the number of channels (for performance 
     * reasons), doesnt have to be exact.
     */
    public ArchiveGroup(@Nonnull final String name,
                        @Nonnull final Long groupId)    {
        _name = name;
        // After Kay's comment, there are two threads that might work on groups.
        _channelMap = new MapMaker().concurrencyLevel(2).makeMap();
    }
    
    /** @return Name of this group */
    final public String getName() {
        return _name;
    }
    
    /** Add channel to group 
     *  @param channel Channel to add
     *  @exception When trying to add multiple enabling channels
     */
    @SuppressWarnings("nls")
    final void add(final ArchiveChannel<?> channel) throws Exception
    {
        if (is_running)
            throw new Error("Running"); //$NON-NLS-1$
        // Is this an 'active' channel?
//        if (channel.getEnablement() != Enablement.Passive)
//        {
//            if (enabling_channel != null)
//                throw new Exception(
//                    String.format("Group '%s': "
//                                  + "Cannot add enabling channel '%s', "
//                                  + "already enabled by '%s'",
//                                  name, channel.getName(), enabling_channel.getName()));
//            enabling_channel = channel;
//        }
        _channelMap.put(channel.getName(), channel);
    }

    /** Remove channel from group */
    final void remove(final ArchiveChannel<?> channel)
    {
        if (is_running)
            throw new Error("Running"); //$NON-NLS-1$
        _channelMap.remove(channel.getName());
        // Was this the enabling channel?
//        if (enabling_channel == channel)
//            enabling_channel = null;
    }
    
    /** @return Number of channels in group
     *  @see #getChannel(int)
     */
    final public int getChannelCount() {
        return _channelMap.size();
    }
    
    /** @return Channel
     *  @param i Channel index
     *  @see #getChannelCount()
     */
//    final public ArchiveChannel<?> getChannel(final int i) {
//        return channels.get(i);
//    }

    /** Locate a channel by name.
	 *  
	 *  @param channel_name
	 *  @return Channel or <code>null</code>s
	 */
    @CheckForNull
    public final ArchiveChannel<?> findChannel(@Nonnull final String name) {
        return _channelMap.get(name);
	}

//    final public ArchiveChannel getEnablingChannel()
//    {
//        return enabling_channel;
//    }

    /** @return <code>true</code> if group is currently enabled */
    final public boolean isEnabled()
    {
        return enabled ;
    }

    /** Start all the channels in group */
    final void start() throws Exception
    {
        if (is_running)
        	return;
        is_running = true;
        // If we have an 'enabling' channel,
        // disable the group until we get the OK from that channel
//        if (enabling_channel != null  &&
//            enabling_channel.getEnablement() == Enablement.Enabling)
//            enable(false);
        for (ArchiveChannel<?> channel : _channelMap.values()) {
            channel.start();
        }
    }

    /** Stop all the channels in group */
    final void stop()
    {
        if (!is_running)
        	return;
        is_running = false;
        for (ArchiveChannel<?> channel : _channelMap.values())
            channel.stop();
    }

    /** Enable or disable the group (and all channels in it) */
    final void enable(final boolean enable)
    {
        enabled = enable;
//        for (ArchiveChannel<?> channel : channels)
//            channel.computeEnablement();
    }

    @Override
    final public String toString()
    {
        return "ArchiveGroup " + getName(); //$NON-NLS-1$
    }

    @Nonnull
    public Collection<ArchiveChannel<?>>getChannels() {
        return _channelMap.values();
    }
}
