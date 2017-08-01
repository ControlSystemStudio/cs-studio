/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.xml;

import java.time.Instant;

import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.SampleMode;

/** InfluxDB implementation of {@link ChannelConfig}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLChannelConfig extends ChannelConfig
{
    final private int channel_id;
    final private int group_id;
    private String retention;

    /** Initialize
     *  @param channel_id Channel ID in InfluxDB
     *  @param name Channel name
     *  @param sample_mode Sample mode
     *  @param last_sample_time Time stamp of last sample in archive or <code>null</code>
     */
    public XMLChannelConfig(final int channel_id, final String name, final SampleMode sample_mode,
            final Instant last_sample_time, final int group_id)
    {
        this(channel_id, name, null, sample_mode, last_sample_time, group_id);
    }

    /** Initialize
     *  @param channel_id Channel ID in InfluxDB
     *  @param retention Data retention policy
     * @param name Channel name
     * @param sample_mode Sample mode
     * @param last_sample_time Time stamp of last sample in archive or <code>null</code>
     */
    public XMLChannelConfig(final int channel_id, final String name, final String retention,
            final SampleMode sample_mode, final Instant last_sample_time, final int group_id)
    {
        super(name, sample_mode, last_sample_time);
        this.retention = retention;
        this.channel_id = channel_id;
        this.group_id = group_id;
    }

    public XMLChannelConfig cloneReplaceSampleTime(final Instant new_last_sample_time)
    {
        return new XMLChannelConfig(this.channel_id, this.getName(), this.getRetention(), this.getSampleMode(), new_last_sample_time, this.group_id);
    }

    /** @return InfluxDB id of channel */
    public int getChannelId()
    {
        return channel_id;
    }

    public int getGroupId()
    {
        return group_id;
    }

    @Override
    public String getRetention()
    {
        return retention;
    }

    @Override
    public void setRetention(String retention)
    {
        this.retention = retention;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return super.toString() + " (" + channel_id + ")";
    }
}
