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
import java.util.Arrays;
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
import org.csstudio.archive.service.sample.ArchiveSampleDTO;
import org.csstudio.archive.service.sample.IArchiveSample;
import org.csstudio.archive.service.samplemode.ArchiveSampleMode;
import org.csstudio.archive.service.samplemode.ArchiveSampleModeId;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;
import org.csstudio.domain.desy.SystemVariableId;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.epics.alarm.EpicsSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

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

        return TimeInstant.fromNanos(time.seconds()*1000000000 + time.nanoseconds());
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
     * Take care, the IArchiveSample is dedicated to the db abstraction, which is of course very
     * similar but NOT the same as the fundamental system variable capturing the state of a part
     * of the system.

     * This placeholder shall replace the IValueWithChannelId workaround and should find its way
     * up to the alyers into the engine and the namely the scan thread, where it shall
     * be properly instantiated.
     */
    @Nonnull
    public <T> IArchiveSample<T> adapt(@Nonnull final EpicsSystemVariable<T> var) {
        return new ArchiveSampleDTO<T>(new ArchiveChannelId(var.getId().intValue()),
                                       var.getValue(),
                                       var.getTimestamp());
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
    public ITimestamp adapt(@Nonnull final TimeInstant time) {
        return TimestampFactory.fromMillisecs(time.getMillis());
    }


    /**
     * Severity (and status).
     *
     * Severity is used synonymously with the 'epics alarm types'
     * OK, MINOR, MAJOR, INVALID.
     *
     * Status is omitted here, because it is quite overlapping and is apparently optional (for an
     * explanation @see {@link EpicsAlarm}), as not defined for all data types.
     *
     * @param severity
     * @return
     */
    @CheckForNull
    private EpicsAlarm adapt(@CheckForNull final ISeverity sev,
                             @Nullable final String status) {

        // once before...
        if (sev == null) {
            return null;
        }
        EpicsAlarmSeverity severity = null;
        // Unfortunately ISeverity is not an enum (if it were, I would have used it)
        // so dispatch
        if (sev.isOK()) {
            severity = EpicsAlarmSeverity.NO_ALARM;
        }
        if (sev.isMinor()) {
            severity = EpicsAlarmSeverity.MINOR;
        }
        if (sev.isMajor()) {
            severity = EpicsAlarmSeverity.MAJOR;
        }
        if (sev.isInvalid()) {
            severity = EpicsAlarmSeverity.INVALID;
        }
        // and once after... I love this interface
        if (severity == null) {
            return null;
        }
        return new EpicsAlarm(severity, EpicsAlarmStatus.parseStatus(status));
    }

    /**
     * TODO (kasemir, bknerr) :
     * I think, we'll need a bit more science in our code:
     *
     * starting with a definition a (CSS) system variable (in general)
     * (apparently  neither PV, nor IProcessVariable, nor IControlSystemItem, nor the DAL stuff have
     * been designed under strict definitions
     *
     * next: a definition of an (CSS) alarm (in general, not only epics),
     *       and then certainly (CSS) EpicsAlarm, (CSS) TineAlarm, etc.
     * next: a definition of the relation between a sys var and an alarm
     *       has-a, is-a, loosely coupled by third entity
     *
     * Unfortunately, even at DESY, there isn't a common notion, let alone definition, of such
     * things and certainly nowhere in the code (hence org.csstudio.domain.desy for a first attempt)
     *
     * @param <T>
     * @param valueWithId
     * @return
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    public <T> EpicsSystemVariable<T> adapt(@Nonnull final IValueWithChannelId valueWithId) {
        // Actually, we would expect a proper encapsulation of the information retrieved by the
        // engine for a system variable for EPICS, there isn't any, let alone a generic one.

        // so we convert in a real internal system variable object

        // And this is not my abstraction: asking a value container for the alarm severity to see
        // whether the value container actually has a value and then casting it to the value
        // container type (with instanceof cascades) to finally get the value...
        final IValue value = valueWithId.getValue();
        if (!value.getSeverity().hasValue()) {
            return null;
        }
        final int id = valueWithId.getChannelId();

        final SystemVariableId varId = new SystemVariableId(id);
        final String name = ""; // here empty, but not when generated in the scan thread when Kay agrees
        final TimeInstant instant = adapt(value.getTime());
        final EpicsAlarm alarm = adapt(value.getSeverity(), value.getStatus());


        if (value instanceof IDoubleValue) {
            final IDoubleValue doubleVal = (IDoubleValue) value;
            final double[] values = doubleVal.getValues();
            if (values.length > 1) {
                return (EpicsSystemVariable<T>) new EpicsSystemVariable<List<Double>>(varId,
                                                                                      name,
                                                                                      Doubles.asList(values),
                                                                                      instant,
                                                                                      alarm);
            } else {
                return (EpicsSystemVariable<T>) new EpicsSystemVariable<Double>(varId,
                                                                                name,
                                                                                Double.valueOf(doubleVal.getValue()),
                                                                                instant,
                                                                                alarm);
            }
        }
        if (value instanceof ILongValue) {
            final ILongValue longVal = (ILongValue) value;
            final long[] values = longVal.getValues();
            if (values.length > 1) {
                return (EpicsSystemVariable<T>) new EpicsSystemVariable<List<Long>>(varId,
                                                                                    name,
                                                                                    Longs.asList(values),
                                                                                    instant,
                                                                                    alarm);
            } else {
                return (EpicsSystemVariable<T>) new EpicsSystemVariable<Long>(varId,
                                                                              name,
                                                                              Long.valueOf(longVal.getValue()),
                                                                              instant,
                                                                              alarm);
            }
        }
        if (value instanceof IEnumeratedValue) {
            // FIXME : why not a dedicated type instead of integers
            // (and where are the describing strings as said in {@link IEnumeratedValue}?)
            final IEnumeratedValue enumVal = (IEnumeratedValue) value;
            final int[] values = enumVal.getValues();
            if (values.length > 1) {
                return (EpicsSystemVariable<T>) new EpicsSystemVariable<List<Integer>>(varId,
                                                                                       name,
                                                                                       Ints.asList(values),
                                                                                       instant,
                                                                                       alarm);
            } else {
                return (EpicsSystemVariable<T>) new EpicsSystemVariable<Integer>(varId,
                                                                                 name,
                                                                                 Integer.valueOf(enumVal.getValue()),
                                                                                 instant,
                                                                                 alarm);
            }
        }
        if (value instanceof IStringValue) {
            final IStringValue strVal = (IStringValue) value;
            final String[] values = strVal.getValues();
            if (values.length > 1) {
                return (EpicsSystemVariable<T>) new EpicsSystemVariable<List<String>>(varId,
                                                                                      name,
                                                                                      Arrays.asList(values),
                                                                                      instant,
                                                                                      alarm);
            } else {
                return (EpicsSystemVariable<T>) new EpicsSystemVariable<String>(varId,
                                                                                name,
                                                                                strVal.getValue(),
                                                                                instant,
                                                                                alarm);
            }
        }

        return null;
    }

}
