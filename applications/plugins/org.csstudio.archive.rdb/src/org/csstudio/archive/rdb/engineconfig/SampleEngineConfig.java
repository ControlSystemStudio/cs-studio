/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.engineconfig;

import java.net.URL;

import org.csstudio.archive.rdb.RDBArchive;

/** Info for a sample engine.
 *  @author Kay Kasemir
 */
public class SampleEngineConfig
{
    final private RDBArchive archive;
    final private int id;
    final private String name;
    final private String description;
    final private String url;

    public SampleEngineConfig(final RDBArchive archive,
            final int id, final String name,
            final String description, final String url)
    {
        this.archive = archive;
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
    }
    
    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public URL getUrl() throws Exception
    {
        return new URL(url);
    }

    /** Add group of channels
     *  @param group_name Name that identifies the group
     *  @return ChannelGroup
     *  @throws Exception on error
     */
    public ChannelGroupConfig addGroup(final String group_name)
        throws Exception
    {
        return archive.addGroup(this, group_name);
    }

    /** Get all groups under this engine.
     *  @return ChannelGroup array
     */
    public ChannelGroupConfig[] getGroups() throws Exception
    {
        ChannelGroupHelper groups = new ChannelGroupHelper(archive);
        return groups.get(getId());
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return String.format("Engine %s (%s): %s, URL %s",
                name, id, description, url.toString());
    }

    /** Hash based on id, name, description, URL but not archive
     *  {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result
	            + ((description == null) ? 0 : description.hashCode());
	    result = prime * result + id;
	    result = prime * result + ((name == null) ? 0 : name.hashCode());
	    result = prime * result + ((url == null) ? 0 : url.hashCode());
	    return result;
    }

    /** Check equality based on id, name, description, URL but not archive
     *  {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof SampleEngineConfig))
            return false;
        final SampleEngineConfig other = (SampleEngineConfig) obj;
        return id == other.id && 
            name.equals(other.name) &&
            description.equals(other.description) &&
            url.equals(other.url);
    }
}
