/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.service.channelstatus;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Data transfer object for channel status.
 *
 * @author bknerr
 * @since Feb 26, 2011
 */
public class ArchiveChannelStatus implements IArchiveChannelStatus {

    private final ArchiveChannelStatusId _id;
    private final ArchiveChannelId _channelId;
    private final Boolean _connected;
    private final String _info;
    private final TimeInstant _time;

    public ArchiveChannelStatus(@Nonnull final ArchiveChannelId channelId,
                                @Nonnull final boolean connected,
                                @Nonnull final String info,
                                @Nonnull final TimeInstant time) {
        this(ArchiveChannelStatusId.NONE, channelId, connected, info, time);
    }

    public ArchiveChannelStatus(@Nonnull final ArchiveChannelStatusId id,
                                @Nonnull final ArchiveChannelId channelId,
                                @Nonnull final boolean connected,
                                @Nonnull final String info,
                                @Nonnull final TimeInstant time) {
        _id = id;
        _channelId = channelId;
        _connected = Boolean.valueOf(connected);
        _info = info;
        _time = time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ArchiveChannelStatusId getId() {
        return _id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getInfo() {
        return _info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public TimeInstant getTime() {
        return _time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ArchiveChannelId getChannelId() {
        return _channelId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Boolean isConnected() {
        return _connected;
    }

}

