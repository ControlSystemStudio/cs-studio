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

import com.google.common.collect.MapMaker;

/** A group of archived channels.
 *  Each channel is in exactly one group.
 *  @author Kay Kasemir
 */
public class ArchiveGroup {
    /** Name of this group */
    private final String _name;

    /** All the channels in this group
     *  <p>
     *  Using thread-safe array to allow HTTPD as well as main
     *  thread to traverse
     */
    private final ConcurrentMap<String, ArchiveChannel<?, ?>> _channelMap;

    /** Set to <code>true</code> while running. */
    private boolean _isRunning = false;

    /**
     * Constructor.
     *
     * @param name the name of the group
     * @param numOfChannels the initial capacity for the number of channels (for performance
     * reasons), doesnt have to be exact.
     */
    public ArchiveGroup(@Nonnull final String name)    {
        _name = name;
        // After Kay's comment, there are two threads that might work on groups.
        _channelMap = new MapMaker().concurrencyLevel(2).makeMap();
    }

    /** @return Name of this group */
    @Nonnull
    public String getName() {
        return _name;
    }

    /** Add channel to group
     *  @param channel Channel to add
     */
    @SuppressWarnings("nls")
    final void add(@Nonnull final ArchiveChannel<?, ?> channel) {
        if (_isRunning) {
            throw new Error("Running"); //$NON-NLS-1$
        }
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
    final void remove(@Nonnull final ArchiveChannel<?, ?> channel) {
        if (_isRunning) {
            throw new Error("Running"); //$NON-NLS-1$
        }
        _channelMap.remove(channel.getName());
        // Was this the enabling channel?
//        if (enabling_channel == channel)
//            enabling_channel = null;
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
    public final ArchiveChannel<?, ?> findChannel(@Nonnull final String name) {
        return _channelMap.get(name);
	}


    /** @return <code>true</code> if group is currently enabled */
    public final boolean isEnabled() {
        return true;
    }

    /** Start all the channels in group
     * @param engineId
     * @return
     * @throws EngineModelException
     */
    @Nonnull
    final void start(@Nonnull final String info) throws EngineModelException {
        if (_isRunning) {
            return;
        }
        _isRunning = true;

        for (final ArchiveChannel<?, ?> channel : _channelMap.values()) {
            channel.start(info);
        }
    }

    /**
     * Stop all the channels in group
     */
    public void stop(@Nonnull final String info) {
        if (!_isRunning) {
            return;
        }
        _isRunning = false;
        for (final ArchiveChannel<?, ?> channel : _channelMap.values()) {
            channel.stop(info);
        }
    }

    public void restart(@Nonnull final String info) throws EngineModelException {
        stop(info);
        start(info);
    }


    @Override
    @Nonnull
    public String toString() {
        return getName();
    }

    @Nonnull
    public Collection<ArchiveChannel<?, ?>>getChannels() {
        return _channelMap.values();
    }
}
