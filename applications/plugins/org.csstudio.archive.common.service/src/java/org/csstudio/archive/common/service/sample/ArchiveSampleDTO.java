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
package org.csstudio.archive.common.service.sample;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.ICssAlarmValueType;

/**
 * Data transfer object for sample.
 *
 * @author bknerr
 * @since 21.12.2010
 * @param <V> the data value type
 * @param <T> the css value type
 * @param <A> the alarm type
 */
public class ArchiveSampleDTO<V,
                              T extends ICssAlarmValueType<V>,
                              A extends IAlarm & Comparable<? super A>> implements IArchiveSample<T, A> {

    private final ArchiveChannelId _channelId;
    private final T _value;
    private final TimeInstant _timestamp;
    private final A _alarm;

    /**
     * Constructor.
     */
    public ArchiveSampleDTO(@Nonnull final ArchiveChannelId channelId,
                            @Nonnull final T data,
                            @Nonnull final TimeInstant ts,
                            @Nonnull final A alarm) {
        _channelId = channelId;
        _value = data;
        _timestamp = ts;
        _alarm = alarm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveChannelId getChannelId() {
        return _channelId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getData() {
        return _value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeInstant getTimestamp() {
        return _timestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public A getAlarm() {
        return _alarm;
    }

}
