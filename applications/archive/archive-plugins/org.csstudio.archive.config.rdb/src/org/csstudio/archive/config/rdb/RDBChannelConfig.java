/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import java.time.Instant;

import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.SampleMode;

/** RDB implementation of {@link ChannelConfig}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBChannelConfig extends ChannelConfig
{
    final private int id;

    /** Initialize
     *  @param id Channel ID in RDB
     *  @param name Channel name
     *  @param sample_mode Sample mode
     *  @param last_sample_time Time stamp of last sample in archive or <code>null</code>
     */
    public RDBChannelConfig(final int id, final String name, final SampleMode sample_mode,
            final Instant last_sample_time)
    {
        super(name, sample_mode, last_sample_time);
        this.id = id;
    }

    /** @return RDB id of channel */
    public int getId()
    {
        return id;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return super.toString() + " (" + id + ")";
    }
}
