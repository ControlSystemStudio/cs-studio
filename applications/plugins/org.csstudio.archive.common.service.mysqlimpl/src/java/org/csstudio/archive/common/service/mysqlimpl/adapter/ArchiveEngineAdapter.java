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
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.adapter.IValueWithChannelId;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.epics.types.AbstractIValueConversionTypeSupport;
import org.csstudio.domain.desy.epics.types.EpicsTypeSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
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
        AbstractIValueConversionTypeSupport.install();
        ArchiveTypeConversionSupport.install();
    }

    /**
     * @param time the archive.rdb timestamp
     * @return the service interface type for a time instant
     */
    @Nonnull
    public TimeInstant adapt(@Nonnull final ITimestamp time) {
        return TimeInstantBuilder.buildFromSeconds(time.seconds()).plusNanosPerSecond(time.nanoseconds());
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
    @Nonnull
    public ITimestamp adapt(@CheckForNull final TimeInstant time) {
        return time == null ? null :
                              TimestampFactory.fromMillisecs(time.getMillis());
    }


    /**
     * Severity and status transferred to typed (epics) alarm.
     *
     * @param severity
     * @param status
     * @return the epics alarm object
     */
    @CheckForNull
    private EpicsAlarm adapt(@CheckForNull final ISeverity sev, @Nullable final String status) {

        // once before...
        if (sev == null) {
            return null;
        }
        EpicsAlarmSeverity severity = null;
        // Unfortunately ISeverity is not an enum (if it were, I would have used it)
        // so dispatch - btw whoever implements ISeverity may just returned true or false anytime...
        if (sev.isOK()) {
            severity = EpicsAlarmSeverity.NO_ALARM;
        } else if (sev.isMinor()) {
            severity = EpicsAlarmSeverity.MINOR;
        } else if (sev.isMajor()) {
            severity = EpicsAlarmSeverity.MAJOR;
        } else if (sev.isInvalid()) {
            severity = EpicsAlarmSeverity.INVALID;
        }
        // and once after... in case anything was false... this interface is lovely
        if (severity == null) {
            return null;
        }
        return new EpicsAlarm(severity, EpicsAlarmStatus.parseStatus(status));
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
    @CheckForNull
    public <T extends ICssAlarmValueType<?>>
        IArchiveSample<T, EpicsAlarm> adapt(@Nonnull final IValueWithChannelId valueWithId) throws TypeSupportException {

        final IValue value = valueWithId.getValue();

        final ArchiveChannelId id = new ArchiveChannelId(valueWithId.getChannelId());
        final TimeInstant timestamp = adapt(value.getTime());
        final EpicsAlarm alarm = adapt(value.getSeverity(), value.getStatus());
        final T data = EpicsTypeSupport.toCssType(value, alarm, timestamp);
        if (data == null) {
            return null;
        }

        final IArchiveSample<T, EpicsAlarm> sample = new IArchiveSample<T, EpicsAlarm>() {
            @Nonnull
            @Override
            public ArchiveChannelId getChannelId() {
                return id;
            }
            @Nonnull
            @Override
            public T getData() {
                return data;
            }
            @Override
            @Nonnull
            public TimeInstant getTimestamp() {
                return data.getTimestamp();
            }
            @Override
            @Nonnull
            public EpicsAlarm getAlarm() {
                return (EpicsAlarm) data.getAlarm();
            }
        };

        return sample;
    }
}
