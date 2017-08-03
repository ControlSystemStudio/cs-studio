/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config;

/** Channel Group description
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GroupConfig
{
    final private String name;
    protected String enabling_channel;

    /** Initialize
     *  @param name Group name
     *  @param enabling_channel Name of enabling channel or <code>null</code>
     */
    public GroupConfig(final String name, final String enabling_channel)
    {
        this.name = name;
        this.enabling_channel = enabling_channel;
    }

    /** @return Channel Group name */
    public String getName()
    {
        return name;
    }

    /** @return Name of enabling channel or <code>null</code> */
    public String getEnablingChannel()
    {
        return enabling_channel;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return "Group '" + name + "'";
    }

    public void setEnablingChannel(ChannelConfig channel) {
        enabling_channel = channel.getName();
    }
}
