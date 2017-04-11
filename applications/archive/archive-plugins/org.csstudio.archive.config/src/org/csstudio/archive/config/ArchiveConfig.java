/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config;

/** Archive configuration info
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto - Added an option to skip reading last sample time.
 */
public interface ArchiveConfig
{
    /** List names of all engines
     *  @return Array of {@link EngineConfig}
     *  @throws Exception on error, e.g. RDB access problem
     */
    public EngineConfig[] getEngines() throws Exception;

    /** Locate archive sample engine configuration
     *  @param name Name of the engine, e.g. "Vacuum"
     *  @return EngineConfig or <code>null</code> when not found
     *  @throws Exception on error, e.g. RDB access problem
     */
    public EngineConfig findEngine(String name) throws Exception;

    /** Locate all groups of an engine
     *  @param engine Engine for which to locate channel groups
     *  @return {@link GroupConfig} array
     *  @throws Exception on error, e.g. RDB access problem
     */
    public GroupConfig[] getGroups(EngineConfig engine) throws Exception;

    /** Locate all channels of a group
     *  @param group Group for which to locate channels
     *  @param skip_last Skip reading last sample time
     *  @return {@link ChannelConfig} array
     *  @throws Exception on error, e.g. RDB access problem
     */
    public ChannelConfig[] getChannels(GroupConfig group, boolean skip_last) throws Exception;

    /** Must be called when configuration is no longer used to release resources */
    public void close();

    /** Remove an engine configuration */
    public void deleteEngine(EngineConfig engine) throws Exception;

    /** Create a new engine */
    public EngineConfig createEngine(String engine_name, String description, String engine_url) throws Exception;

    /** Create a new group under this engine */
    public GroupConfig addGroup(EngineConfig engine, String name) throws Exception;

    /** Create a new channel under this group */
    public ChannelConfig addChannel(GroupConfig group, String name, SampleMode mode) throws Exception;

    /** Return the engine to which this group belongs */
    public EngineConfig getEngine(GroupConfig other_group) throws Exception;

    /** Return the group to which this channel belongs */
    public GroupConfig getChannelGroup(String name) throws Exception;

    /**
     * Get the sample mode indicated by the monitor, sample value, and period
     */
    public SampleMode getSampleMode(boolean monitor, double sample_value, double period) throws Exception;

    /** Set the enabling channel for this group configuration */
    public void setEnablingChannel(GroupConfig group, ChannelConfig channel) throws Exception;

}
