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
package org.csstudio.archive.service.mysqlimpl.channel;

import javax.annotation.Nonnull;

import org.csstudio.archive.service.channel.ArchiveChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.service.samplemode.ArchiveSampleModeId;
import org.joda.time.DateTime;

/**
 * Immutable data transfer object for DAOs.
 *
 * @author bknerr
 * @since 10.11.2010
 */
public class ArchiveChannelDTO implements IArchiveChannel {

    private final ArchiveChannelId _id;

    private final ArchiveChannelGroupId _groupId;

    private final ArchiveSampleModeId _sampleModeId;

    private final double _sampleValue;

    private final double _samplePeriod;

    private final DateTime _latestTimestamp;

    /** The channel's meta data */
    //private final IMetaData _metaData;

    /**
     * Constructor.
     * @param id
     * @param grpId
     * @param sampleModeId
     * @param smplVal
     * @param smplPer
    //* @param metaData
     * @param ltstTimestamp
     */
    public ArchiveChannelDTO(@Nonnull final ArchiveChannelId id,
                             @Nonnull final ArchiveChannelGroupId grpId,
                             @Nonnull final ArchiveSampleModeId sampleModeId,
                             final double smplVal,
                             final double smplPer,
                             /* @Nonnull final IMetaData metaData */
                             @Nonnull final DateTime ltstTimestamp) {
        _id = id;
        _groupId = grpId;
        _sampleModeId = sampleModeId;
        _sampleValue = smplVal;
        _samplePeriod = smplPer;
        //_metaData = metaData;
        _latestTimestamp = ltstTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ArchiveChannelId getId() {
        return _id;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ArchiveChannelGroupId getGroupId() {
        return _groupId;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ArchiveSampleModeId getSampleModeId() {
        return _sampleModeId;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public double getSampleValue() {
        return _sampleValue;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public double getSamplePeriod() {
        return _samplePeriod;
    }
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Nonnull
//    public IMetaData getMetaData() {
//        return _metaData;
//    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public DateTime getLatestTimestamp() {
        return _latestTimestamp;
    }

}
