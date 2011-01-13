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
package org.csstudio.archive.common.service.sample;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.ICssAlarmValueType;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 11.01.2011
 * @param <V> the data value type
 * @param <T> the css value type
 * @param <A> the alarm type
 */
public class ArchiveMinMaxSample<V,
                                 T extends ICssAlarmValueType<V>,
                                 A extends IAlarm & Comparable<? super A>>
                                extends ArchiveSampleDTO<V, T, A>
                                implements IArchiveMinMaxSample<V, T, A> {

    private final V _minimum;
    private final V _maximum;

    /**
     * Constructor.
     */
    public ArchiveMinMaxSample(@Nonnull final ArchiveChannelId channelId,
                               @Nonnull final T data,
                               @Nonnull final TimeInstant ts,
                               @Nonnull final A alarm,
                               @Nullable final V min,
                               @Nullable final V max) {
        super(channelId, data, ts, alarm);
        _minimum = min;
        _maximum = max;
    }
    /**
     * Constructor.
     */
    public ArchiveMinMaxSample(@Nonnull final ArchiveChannelId channelId,
                               @Nonnull final T data,
                               @Nonnull final TimeInstant ts,
                               @Nonnull final A alarm) {
        super(channelId, data, ts, alarm);
        _minimum = null;
        _maximum = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public V getMinimum() {
        return _minimum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public V getMaximum() {
        return _maximum;
    }

}
