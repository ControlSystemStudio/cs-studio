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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.system.ISystemVariable;

/**
 * TODO (bknerr) : find a better abstraction for this object
 * (@see {@link IArchiveMinMaxSample}).
 *
 * The min max information has to be related to an time interval. This abstraction is
 * due to the IValue stuff of Kasemir.
 *
 * @author bknerr
 * @since 11.01.2011
 * @param <V> the data value type
 * @param <T> the css value type
 */
public class ArchiveMinMaxSample<V extends Serializable,
                                 T extends ISystemVariable<V>>
                                extends ArchiveSample<V, T>
                                implements IArchiveMinMaxSample<V, T> {

    private static final long serialVersionUID = -2937803532878863107L;

    private final V _minimum;
    private final V _maximum;

    /**
     * Constructor.
     */
    public ArchiveMinMaxSample(@Nonnull final ArchiveChannelId channelId,
                               @Nonnull final T data,
                               @Nullable final IAlarm alarm,
                               @Nonnull final V min,
                               @Nonnull final V max) {
        super(channelId, data, alarm);
        _minimum = min;
        _maximum = max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public V getMinimum() {
        return _minimum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public V getMaximum() {
        return _maximum;
    }

}
