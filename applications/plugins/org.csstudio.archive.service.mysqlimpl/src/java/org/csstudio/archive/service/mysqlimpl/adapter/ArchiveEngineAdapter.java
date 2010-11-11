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
package org.csstudio.archive.service.mysqlimpl.adapter;

import javax.annotation.Nonnull;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.service.adapter.IArchiveEngineAdapter;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.joda.time.DateTime;

/**
 * Adapter class to map mysql specific dao classes to dedicated engine classes.
 *
 * @author bknerr
 * @since 10.11.2010
 */
public enum ArchiveEngineAdapter implements IArchiveEngineAdapter {

    INSTANCE;

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelConfig adapt(@Nonnull final String name,
                               @Nonnull final IArchiveChannel channelDTO,
                               @Nonnull final IArchiveSampleMode sampleModeDTO) {

        final ChannelConfig cfg = new ChannelConfig(null,
                                                    channelDTO.getId().intValue(),
                                                    name,
                                                    channelDTO.getGroupId().intValue(),
                                                    adapt(sampleModeDTO),
                                                    channelDTO.getSampleValue(),
                                                    channelDTO.getSamplePeriod());
        return cfg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleMode adapt(@Nonnull final IArchiveSampleMode sampleModeDTO) {

        final SampleMode smplMode = new SampleMode(sampleModeDTO.getId().intValue(),
                                                   sampleModeDTO.getName(),
                                                   sampleModeDTO.getDescription());
        return smplMode;
    }

    /**
     * @param latestTimestamp
     * @return
     */
    @Override
    public ITimestamp adapt(@Nonnull final DateTime time) {
        return TimestampFactory.fromMillisecs(time.getMillis());
    }

}
