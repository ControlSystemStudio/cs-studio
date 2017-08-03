/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config;

/** Archive configuration info
 *  @author Megan Grodowitz
 */

/**
 * This is a bad interface
 *
 * The story here is that the XML Import and Export functions were written
 * specifically for the RDB archive config They used this set of functions to
 * setup the configuration object, except with RDB???Config objects instead of
 * these ???Config object superclasses
 *
 * Passing objects into and out of these functions is not good, because there is
 * not enough information in the superclass to use the objects. For example,
 * when I call getEngine(GroupConfig), each object that implements this
 * interface also has to implement a specific type of GroupConfig, then cast the
 * input object to this group Moreover, passing the objects into the functions
 * is a good way to accidentally let archiveConigs operate on objects that they
 * do not own. This should all be redone passing in Strings or (better yet)
 * unique IDs instead of objects
 *
 * The ArchiveConfig interface has the same problem...
 */
public interface ImportableArchiveConfig extends ArchiveConfig
{
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
