/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config;

import java.time.Instant;

/** Configuration of a channel
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChannelConfig
{
    final private String name;
    final private SampleMode sample_mode;
    final private Instant last_sample_time;

    /** Initialize
     *  @param name Channel name
     *  @param sample_mode Sample mode
     *  @param last_sample_time Time stamp of last sample in archive or <code>null</code>
     */
    public ChannelConfig(final String name, final SampleMode sample_mode, final Instant last_sample_time)
    {
        this.name = name;
        this.sample_mode = sample_mode;
        this.last_sample_time = last_sample_time;
    }

    /** @return Channel name */
    public String getName()
    {
        return name;
    }

    /** @return Sample mode */
    public SampleMode getSampleMode()
    {
        return sample_mode;
    }

    // TODO Maybe the last sample time of a channel should be accessed via
    // the archive(.)reader API, not the archive.config?
    // But changing archive(.)reader needs to be coordinated with all its
    // implementations.
    /** @return Time stamp of last sample in archive or <code>null</code> */
    public Instant getLastSampleTime()
    {
        return last_sample_time;
    }

    /** @return String representing a data retention policy, or null for default/not supported */
    public String getRetention()
    {
        return null;
    }

    /** Set data retention policy; may be null if default/not supported.
     * In the ChannelConfig class, this is a no-op. Sub-classes should
     * override this method if they support retention policies.*/
    public void setRetention(String retain)
    {
        //TODO: Log a warning? (What logger?)
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return "Channel '" + name + "', " + sample_mode;
    }
}
