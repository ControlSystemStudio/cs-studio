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
package org.csstudio.archive.service.adapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.service.channel.ArchiveChannelDTO;
import org.csstudio.archive.service.channel.ArchiveChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.service.engine.ArchiveEngineId;
import org.csstudio.archive.service.engine.IArchiveEngine;
import org.csstudio.archive.service.sample.IArchiveSample;
import org.csstudio.archive.service.samplemode.ArchiveSampleMode;
import org.csstudio.archive.service.samplemode.ArchiveSampleModeId;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.AbstractArchiveConversionTypeSupport;
import org.csstudio.domain.desy.types.AbstractIValueConversionTypeSupport;
import org.csstudio.domain.desy.types.ConversionTypeSupportException;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupport;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

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
        //AbstractBasicTypeConversionTypeSupport.install();
        AbstractIValueConversionTypeSupport.install();
        AbstractArchiveConversionTypeSupport.install();
    }

    /**
     * @param cfg the archive.rdb type for sample engine config
     * @return the service interface type for the engine
     */
    public IArchiveEngine adapt(@Nonnull final SampleEngineConfig cfg) {

        return new IArchiveEngine() {

            @Override
            @Nonnull
            public ArchiveEngineId getId() {
                return new ArchiveEngineId(cfg.getId());
            }

            @Override
            @CheckForNull
            public URL getUrl() throws MalformedURLException {
                try {
                    return cfg.getUrl();
                } catch (final Exception e) {
                    // FIXME (kasemir) : untyped exception swallows anything, use dedicated exception
                    throw new MalformedURLException();
                }
            }

        };
    }

    /**
     * @param channel the archive.rdb channel configuration
     * @return the service interface for the channel configuration
     */
    @Nonnull
    public IArchiveChannel adapt(@Nonnull final ChannelConfig channel,
                                 @Nullable final ITimestamp lastTimestamp) {

        final IArchiveChannel cfg = new ArchiveChannelDTO(new ArchiveChannelId(channel.getId()),
                                                          channel.getName(),
                                                          new ArchiveChannelGroupId(channel.getGroupId()),
                                                          new ArchiveSampleModeId(channel.getSampleMode().getId()),
                                                          channel.getSampleValue(),
                                                          channel.getSamplePeriod(),
                                                          adapt(lastTimestamp));

        return cfg;
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
     * @param groups the list of archive.rdb group configurations
     * @return the list of service interfaces for the group configs
     */
    @Nonnull
    public List<IArchiveChannelGroup> adapt(@CheckForNull final ChannelGroupConfig[] groups) {
        if (groups == null) {
            return Collections.emptyList();
        }
        final List<ChannelGroupConfig> list = Lists.newArrayList(groups);

        return Lists.transform(list,
                               new Function<ChannelGroupConfig, IArchiveChannelGroup>() {
                                @Override
                                @Nonnull
                                public IArchiveChannelGroup apply(final ChannelGroupConfig from) {
                                    return adapt(from);
                                }
                               });
    }



    /**
     * @param sampleMode
     * @return
     */
    @CheckForNull
    public IArchiveSampleMode adapt(@Nonnull final SampleMode sampleMode) {
        for (final ArchiveSampleMode mode : ArchiveSampleMode.values()) {
            if (sampleMode.getName().toUpperCase().equals(mode.name())) {
                return mode;
            }
        }
        return null;
    }

    /**
     * Converts the service side sample mode DTO to the engine side sample mode type
     * @param sampleModeDTO the sample mode dTO
     * @return the engine side sample mode
     */
    @Nonnull
    public SampleMode adapt(@Nonnull final IArchiveSampleMode sampleModeDTO) {

        final SampleMode smplMode = new SampleMode(sampleModeDTO.getId().intValue(),
                                                   sampleModeDTO.getName(),
                                                   sampleModeDTO.getDescription());
        return smplMode;
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
     * @throws ConversionTypeSupportException
     */
    @CheckForNull
    public <T extends ICssAlarmValueType<?>>
        IArchiveSample<T, EpicsAlarm> adapt(@Nonnull final IValueWithChannelId valueWithId) throws ConversionTypeSupportException {

        final IValue value = valueWithId.getValue();

        final ArchiveChannelId id = new ArchiveChannelId(valueWithId.getChannelId());
        final TimeInstant timestamp = adapt(value.getTime());
        final EpicsAlarm alarm = adapt(value.getSeverity(), value.getStatus());
        final T data = TypeSupport.toCssType(value, alarm, timestamp);
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
