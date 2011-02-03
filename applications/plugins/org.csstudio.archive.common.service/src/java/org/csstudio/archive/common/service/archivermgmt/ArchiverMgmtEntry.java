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
package org.csstudio.archive.common.service.archivermgmt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * DB bean for ArchiverMgmtEntry. 
 * 
 * @author bknerr
 * @since 02.02.2011
 */
public class ArchiverMgmtEntry implements IArchiverMgmtEntry {

    public static final String ARCHIVER_START = "Startup"; 
    
    private final ArchiverMgmtEntryId _id;
    private final ArchiveChannelId _channelId;
    private final ArchiverMonitorStatus _status;
    private final ArchiveEngineId _engineId;
    private final TimeInstant _timestamp;
    private final String _info;

    /**
     * Constructor.
     */
    public ArchiverMgmtEntry(@Nonnull final ArchiverMgmtEntryId id,
                             @Nonnull final ArchiveChannelId channelId,
                             @Nonnull final ArchiverMonitorStatus status,
                             @Nonnull final ArchiveEngineId engineId,
                             @Nonnull final TimeInstant time,
                             @Nullable final String info) {
        _id = id;
        _channelId = channelId;
        _status = status;
        _engineId = engineId;
        _timestamp = time;
        _info = info;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ArchiverMgmtEntryId getId() {
        return _id;
    }

    @Override
    public TimeInstant getTimestamp() {
        return _timestamp;
    }

    @Override
    public ArchiveEngineId getEngineId() {
        return _engineId;
    }

    @Override
    public ArchiverMonitorStatus getStatus() {
        return _status;
    }

    @Override
    public ArchiveChannelId getChannelId() {
        return _channelId;
    }

    @Override
    public String getInfo() {
        return _info;
    }
}
