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
package org.csstudio.archive.common.service.channel;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.Limits;

/**
 * Archive channel with display ranges.
 *
 * @author bknerr
 * @since Mar 7, 2011
 * @param <V> the basic value type (comparable)
 */
public class ArchiveLimitsChannel<V extends Comparable<? super V>> extends ArchiveChannel {

    private final Limits<V> _limits;

    /**
     * Constructor.
     */
    public ArchiveLimitsChannel(@Nonnull final IArchiveChannel channel,
                                @Nonnull final V lo,
                                @Nonnull final V hi) {
        super(channel.getId(), channel.getName(), channel.getDataType(),
              channel.getGroupId(), channel.getLatestTimestamp(), channel.getControlSystem(),
              channel.isEnabled());
        _limits = Limits.create(lo, hi);
    }
    /**
     * Constructor.
     * CHECKSTYLE OFF: ParameterNumber
     */
    public ArchiveLimitsChannel(@Nonnull final ArchiveChannelId id,
                                @Nonnull final String name,
                                @Nonnull final Class<V> type,
                                @Nonnull final ArchiveChannelGroupId grpId,
                                @Nullable final TimeInstant ltstTimestamp,
                                @Nonnull final IArchiveControlSystem system,
                                final boolean isEnabled,
                                @Nonnull final V lo,
                                @Nonnull final V hi) {
        // CHECKSTYLE  ON : ParameterNumber
        super(id, name, type, grpId, ltstTimestamp, system, isEnabled);
        _limits = Limits.create(lo, hi);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @CheckForNull
    public Class<V> getDataType() {
        return (Class<V>) super.getDataType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Limits<V> getDisplayLimits() {
        return _limits;
    }
}
