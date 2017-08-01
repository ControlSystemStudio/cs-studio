/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.xml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.GroupConfig;

/** InfluxDB implementation of EngineConfig
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLEngineConfig extends EngineConfig
{
    /**
     * Unique global id of this engine
     */
    final private int engine_id;

    /**
     * Mapping of group names to (global) group ids
     */
    final private Map<String, Integer> group_name2id = new HashMap<String, Integer>();
    /**
     * Mapping of (global) group ids to group configuration objects
     */
    final private Map<Integer, GroupConfig> group_id2obj = new HashMap<Integer, GroupConfig>();

    /** Initialize
     *  @param engine_id
     *  @param name
     *  @param description
     *  @param url
     *  @throws Exception if url is not a valid URL
     */
    public XMLEngineConfig(final int engine_id, final String name, final String description, final String url) throws Exception
    {
        super(name, description, url);
        this.engine_id = engine_id;
    }

    /** @return InfluxDB ID of engine */
    public int getId()
    {
        return engine_id;
    }

    /**
     * @return list of configured groups in this engine
     */
    public GroupConfig[] getGroupsArray()
    {
        return group_id2obj.values().toArray(new GroupConfig[group_id2obj.size()]);
    }

    public Collection<GroupConfig> getGroupObjs()
    {
        return group_id2obj.values();
    }

    public XMLGroupConfig addGroup(int group_id, String group_name, String enabling_channel) throws Exception
    {
        if (group_name2id.containsKey(group_name))
            throw new Exception("Cannot re-add extant group " + group_name + " to engine " + getName());

        group_name2id.put(group_name, group_id);
        XMLGroupConfig group = new XMLGroupConfig(group_id, group_name, enabling_channel, engine_id);
        group_id2obj.put(group_id, group);
        return group;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return super.toString() + " [" + engine_id + "]";
    }
}
