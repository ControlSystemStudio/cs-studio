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
import javax.annotation.concurrent.GuardedBy;

import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;

import com.google.common.collect.MapMaker;

/** A group of archived channels.
 *  Each channel is in exactly one group.
 *  @author Kay Kasemir
 */
public class ArchiveGroup {

    private final ArchiveChannelGroupId _id;

    /** Name of this group */
    private final String _name;

    /** All the channels in this group
     *  <p>
     *  Using thread-safe array to allow HTTPD as well as main
     *  thread to traverse
     */
    private final ConcurrentMap<String, ArchiveChannelBuffer<?, ?>> _channelMap;

    /** Set to <code>true</code> while running. */
    @GuardedBy("this")
    private boolean _isStarted;


    /**
     * Constructor.
     * @param archiveChannelGroupId
     *
     * @param name the name of the group
     * @param numOfChannels the initial capacity for the number of channels (for performance
     * reasons), doesnt have to be exact.
     */
    public ArchiveGroup(@Nonnull final ArchiveChannelGroupId id,
                        @Nonnull final String name)    {
        _id = id;
        _name = name;
        // After Kay's comment, there are two threads that might work on groups.
        _channelMap = new MapMaker().concurrencyLevel(2).makeMap();
    }

    /** @return Id of this group */
    @Nonnull
    public ArchiveChannelGroupId getId() {
        return _id;
    }

    /** @return Name of this group */
    @Nonnull
    public String getName() {
        return _name;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Nonnull
    public Collection<ArchiveChannelBuffer> getChannels() {
        return (Collection) _channelMap.values();
    }

    /** Add channel to group
     *  @param channel Channel to add
     */
    @SuppressWarnings("nls")
    final synchronized void add(@Nonnull final ArchiveChannelBuffer<?, ?> channel) {
        _channelMap.put(channel.getName(), channel);
    }

    /** Remove channel from group */
    final synchronized void remove(@Nonnull final ArchiveChannelBuffer<?, ?> channel) {
        _channelMap.remove(channel.getName());
    }
    /** Remove channel from group */
    final synchronized void remove(@Nonnull final String name) {
        _channelMap.remove(name);
    }

    /**
     * Locate a channel by name.
     *
     *  @param channel_name
     *  @return Channel or <code>null</code>s
     */
    @CheckForNull
    public final ArchiveChannelBuffer<?, ?> findChannel(@Nonnull final String name) {
        return _channelMap.get(name);
    }


    /**
     * @return <code>true</code> if group is currently started
     */
    public final synchronized boolean isStarted() {
        return _isStarted;
    }

    /**
     * Start all the channels in group that are flagged to be enabled.
     * @throws EngineModelException
     */
    @Nonnull
    public final void start(@Nonnull final String info) throws EngineModelException {
        synchronized (this) {
            if (_isStarted) {
                return;
            }
            _isStarted = true;
        }
        for (final ArchiveChannelBuffer<?, ?> channel : _channelMap.values()) {
            if (channel.isEnabled()) {
                channel.start(info);
            }
        }
    }

    /**
     * Stops all the channels in group that have been started before.
     * @throws EngineModelException
     */
    public void stop(@Nonnull final String info) throws EngineModelException {
        synchronized (this) {
            if (!_isStarted) {
                return;
            }
            _isStarted = false;
        }
        for (final ArchiveChannelBuffer<?, ?> channel : _channelMap.values()) {
            if (channel.isStarted()) {
                channel.stop(info);
            }
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
}
