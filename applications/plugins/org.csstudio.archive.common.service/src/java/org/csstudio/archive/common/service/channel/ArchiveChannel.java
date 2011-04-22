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
package org.csstudio.archive.common.service.channel;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.Limits;

/**
 * Immutable data transfer object for DAOs.
 *
 * @author bknerr
 * @since 10.11.2010
 */
public class ArchiveChannel implements IArchiveChannel {

    private final ArchiveChannelId _id;

    private final String _name;

    private final ArchiveChannelGroupId _groupId;

    private final String _dataType;

    private final TimeInstant _latestTimestamp;

    private final IArchiveControlSystem _system;

    /**
     * Constructor.
     * @param id
     * @param name
     * @param type
     * @param grpId
     * @param ltstTimestamp
     */
    public ArchiveChannel(@Nonnull final ArchiveChannelId id,
                          @Nonnull final String name,
                          @Nonnull final String type,
                          @Nonnull final ArchiveChannelGroupId grpId,
                          @Nullable final TimeInstant ltstTimestamp,
                          @Nonnull final IArchiveControlSystem system) {
        _id = id;
        _name = name;
        _groupId = grpId;
        _dataType = type;
        _latestTimestamp = ltstTimestamp;
        _system = system;
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
    public String getName() {
        return _name;
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
    public String getDataType() {
        return _dataType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public TimeInstant getLatestTimestamp() {
        return _latestTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveControlSystem getControlSystem() {
        return _system;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Limits<?> getDisplayLimits() {
        return null;
    }
}
