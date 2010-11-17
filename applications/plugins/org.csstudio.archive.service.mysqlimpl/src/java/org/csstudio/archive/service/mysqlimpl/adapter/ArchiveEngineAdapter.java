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
package org.csstudio.archive.service.mysqlimpl.adapter;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.service.adapter.IArchiveEngineAdapter;
import org.csstudio.archive.service.adapter.IValueWithChannelId;
import org.csstudio.archive.service.channel.ArchiveChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.sample.ArchiveSampleDTO;
import org.csstudio.archive.service.sample.IArchiveSample;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;
import org.csstudio.domain.desy.SystemVariableId;
import org.csstudio.domain.desy.alarm.epics.EpicsAlarm;
import org.csstudio.domain.desy.alarm.epics.EpicsSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.joda.time.DateTime;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * Adapter class to map mysql specific dao classes to dedicated engine classes.
 *
 * @author bknerr
 * @since 10.11.2010
 */
public enum ArchiveEngineAdapter implements IArchiveEngineAdapter {

    INSTANCE;

    /**
     * Quite exhausting
     *
     * @param <T>
     * @param valueWithId
     * @return
     */
    @SuppressWarnings("unchecked")
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


    /**
     Take care, the IArchiveSample is dedicated to the db abstraction, which is of course very
     similar but NOT the same as the fundamental system variable capturing the state of a part
     of the system.

     This placeholder shall replace the IValueWithChannelId workaround and should find its way
     up to the alyers into the engine and the namely the scan thread, where it shall
     be properly instantiated.
     */
    public <T> IArchiveSample<T> adapt(@Nonnull final EpicsSystemVariable<T> var) {
        return new ArchiveSampleDTO<T>(new ArchiveChannelId(var.getId().intValue()),
                                       var.getValue(),
                                       var.getTimestamp());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelConfig adapt(@Nonnull final String name,
                               @Nonnull final IArchiveChannel channelDTO,
                               @Nonnull final IArchiveSampleMode sampleModeDTO) {

        final ChannelConfig cfg = new ChannelConfig(null,
                                                    channelDTO.getId().intValue(),
                                                    name,
                                                    channelDTO.getGroupId().intValue(),
                                                    adapt(sampleModeDTO),
                                                    channelDTO.getSampleValue(),
                                                    channelDTO.getSamplePeriod());
        return cfg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleMode adapt(@Nonnull final IArchiveSampleMode sampleModeDTO) {

        final SampleMode smplMode = new SampleMode(sampleModeDTO.getId().intValue(),
                                                   sampleModeDTO.getName(),
                                                   sampleModeDTO.getDescription());
        return smplMode;
    }

    /**
     * @param latestTimestamp
     * @return
     */
    @Override
    public ITimestamp adapt(@Nonnull final DateTime time) {
        return TimestampFactory.fromMillisecs(time.getMillis());
    }

}
