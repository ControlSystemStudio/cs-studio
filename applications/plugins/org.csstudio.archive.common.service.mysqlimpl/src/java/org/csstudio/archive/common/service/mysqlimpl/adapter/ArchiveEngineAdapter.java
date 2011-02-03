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
package org.csstudio.archive.common.service.mysqlimpl.adapter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;

/**
 * This adapter translates the originally used types in the archive.rdb to new interface types.
 *
 * These new types shall decouple the two layers AND be as slim as possible, so that data that is
 * not used by the client (engine or writer) is not present in the client.
 *
 * @author bknerr
 * @since 12.11.2010
 */
public enum ArchiveEngineAdapter {

    INSTANCE;

    /**
     * Constructor.
     */
    private ArchiveEngineAdapter() {
        ArchiveTypeConversionSupport.install();
    }

    /**
     *
     * @param cfg cfg the archive.rdb channel group config
     * @return the service interface for this config
     */
    @Nonnull
    public IArchiveChannelGroup adapt(@Nonnull final ChannelGroupConfig cfg) {
        return new IArchiveChannelGroup() {
            @Override
            @Nonnull
            public ArchiveChannelGroupId getId() {
                return new ArchiveChannelGroupId(cfg.getId());
            }
            @Override
            @CheckForNull
            public String getName() {
                return cfg.getName();
            }
            @Override
            @CheckForNull
            public ArchiveChannelId getEnablingChannelId() {
                return new ArchiveChannelId(cfg.getId());
            }
        };
    }


    /**
     * Converts the service impl side time instant to the engine side timestamp
     * @param time instant (service impl side)
     * @return the engine side timestamp
     */
    @CheckForNull
    public ITimestamp adapt(@CheckForNull final TimeInstant time) {
        return time == null ? null :
                              TimestampFactory.fromMillisecs(time.getMillis());
    }


    /**
     * Take care, the IArchiveSample is dedicated to the db abstraction, which is of course very
     * similar but NOT the same as the fundamental system variable capturing the state of a part
     * of the system.

     * This placeholder shall replace the IValueWithChannelId workaround and should find its way
     * up to the layers into the engine and the namely the pvValueUpdate, where it shall
     * be properly instantiated. See Carcassi's types for that, very likely to replace IValue.
     * @throws TypeSupportException
     */
//    @CheckForNull
//    public <V, T extends ICssAlarmValueType<V>>
//        IArchiveSample<T, EpicsAlarm> adapt(@Nonnull final IValueWithChannelId valueWithId) throws TypeSupportException {
//
//        final IValue value = valueWithId.getValue();
//
//        final ArchiveChannelId id = new ArchiveChannelId(valueWithId.getChannelId());
//        final T data = EpicsIValueTypeSupport.toCssType(value);
//
//        if (data == null) {
//            return null;
//        }
//
//        return new ArchiveSample<V, T, EpicsAlarm>(id, data, data.getTimestamp(), (EpicsAlarm) data.getAlarm());
//    }
}
