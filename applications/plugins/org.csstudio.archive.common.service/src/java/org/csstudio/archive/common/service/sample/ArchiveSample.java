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

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data transfer object for sample.
 *
 * @author bknerr
 * @since 24.01.2011
 * @param <V> the data value type
 * @param <T> the css value type with alarm information
 */
public class ArchiveSample<V extends Serializable,
                           T extends ISystemVariable<V>>
                          implements IArchiveSample<V, T> {

    private static final long serialVersionUID = -2244316283884247177L;
    private static final Logger LOG = LoggerFactory.getLogger(ArchiveSample.class);

    private final ArchiveChannelId _channelId;
    private final T _sysVar;
    private final IAlarm _alarm;

    /**
     * Constructor.
     */
    public ArchiveSample(@Nonnull final ArchiveChannelId channelId,
                         @Nonnull final T sysVar,
                         @Nullable final IAlarm alarm) {
        if (sysVar.getTimestamp().getNanos() <= 0L) {
            LOG.error("Timestamp for sample of channel {} is <= 0! Invalid for archive samples.", sysVar.getName());
            throw new IllegalStateException("Invalid sample timestamp");
        }

        _channelId = channelId;
        _sysVar = sysVar;
        _alarm = alarm;
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
    public V getValue() {
        return _sysVar.getData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public T getSystemVariable() {
        return _sysVar;
    }

    @CheckForNull
    public IAlarm getAlarm() {
        return _alarm;
    }
}
