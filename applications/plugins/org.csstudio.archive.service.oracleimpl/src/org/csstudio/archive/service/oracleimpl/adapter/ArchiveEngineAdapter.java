/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.service.oracleimpl.adapter;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.service.channel.ArchiveChannelDTO;
import org.csstudio.archive.service.channel.ArchiveChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.service.engine.IArchiveEngine;
import org.csstudio.archive.service.oracleimpl.OracleArchiveServiceImpl;
import org.csstudio.archive.service.samplemode.ArchiveSampleModeId;
import org.csstudio.platform.data.ITimestamp;
import org.joda.time.DateTime;

/**
 * This adapter translates the originally used types in the archive.rdb to new interface types
 * that are as slim as possible. Data that is not used by the client (engine or writer) shall not
 * be present in the client.
 *
 * @author bknerr
 * @since 12.11.2010
 */
public enum ArchiveEngineAdapter {
    INSTANCE;

    /**
     * @param cfg the archive.rdb type for sample engine config
     * @return the service interface type for the engine
     */
    public IArchiveEngine adapt(@Nonnull final SampleEngineConfig cfg) {

        return new IArchiveEngine() {

            public int getId() {
                return cfg.getId();
            }

            @CheckForNull
            public URL getUrl() throws MalformedURLException {
                try {
                    return cfg.getUrl();
                } catch (final Exception e) {
                    // FIXME (kasemir) : untyped exception swallows anything, use dedicated exception
                    throw new MalformedURLException();
                }
            }

        };
    }

    /**
     * @param channel the archive.rdb channel configuration
     * @return the service inteface for the channel configuration
     * @throws Exception
     */
    @Nonnull
    public IArchiveChannel adapt(@Nonnull final ChannelConfig channel) throws Exception {

        final ITimestamp lastTimeStamp =
            OracleArchiveServiceImpl.INSTANCE.getLatestTimestampByChannel(channel.getName());

        final IArchiveChannel cfg = new ArchiveChannelDTO(new ArchiveChannelId(channel.getId()),
                                                          new ArchiveChannelGroupId(channel.getGroupId()),
                                                          new ArchiveSampleModeId(channel.getSampleMode().getId()),
                                                          channel.getSampleValue(),
                                                          channel.getSamplePeriod(),
                                                          adapt(lastTimeStamp));

        return cfg;
    }

    /**
     * FIXME (bknerr) : apparently the allmighty jodatime cannot offer nano precision - find a replacement
     *
     * @param time the archive.rdb timestamp
     * @return the service interface type for a time instant
     */
    @Nonnull
    public DateTime adapt(@Nonnull final ITimestamp time) {

        return new DateTime(time.seconds()*1000 + time.nanoseconds()/1000);
    }
}
