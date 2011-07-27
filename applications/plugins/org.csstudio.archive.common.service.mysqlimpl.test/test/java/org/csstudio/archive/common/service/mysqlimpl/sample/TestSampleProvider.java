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
package org.csstudio.archive.common.service.mysqlimpl.sample;

import java.util.LinkedList;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

import com.google.common.collect.Lists;

/**
 * Provides collection of test samples.
 *
 * @author bknerr
 * @since 27.07.2011
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TestSampleProvider {


    public static final LinkedList<IArchiveSample<Object, ISystemVariable<Object>>> SAMPLES =
        Lists.newLinkedList();

    public static TimeInstant START;
    public static TimeInstant END;

    public static final ArchiveChannelId CHANNEL_ID = new ArchiveChannelId(2L);

    static {
        START = TimeInstantBuilder.fromNow();

        Double d = 0.0;
        // for half an hour, every half a minute a sample, values are ramping = 50h
        // intended for filling of sample_m table
        TimeInstant time = START;
        for (int i = 0; i < 100; i++) {
            time = time.plusMillis(1000*30);
            final IArchiveSample sample =
                new ArchiveSample<Double, ISystemVariable<Double>>(CHANNEL_ID,
                                                                   new EpicsSystemVariable<Double>("fuup",
                                                                                                   d += 1.0,
                                                                                                   ControlSystem.EPICS_DEFAULT,
                                                                                                   time,
                                                                                                   EpicsAlarm.UNKNOWN),
                                                                   EpicsAlarm.UNKNOWN);

            SAMPLES.add(sample);
        }

        // for 5 days, every half hour, values still ramping
        d = 0.0;
        for (int i = 0; i < 5*24*2; i++) {
            time = time.plusMillis(1000*60*30);
            final IArchiveSample sample =
                new ArchiveSample<Double, ISystemVariable<Double>>(CHANNEL_ID,
                                                                   new EpicsSystemVariable<Double>("fuup",
                                                                                                   d += 1.0,
                                                                                                   ControlSystem.EPICS_DEFAULT,
                                                                                                   time,
                                                                                                   EpicsAlarm.UNKNOWN),
                                                                   EpicsAlarm.UNKNOWN);

            SAMPLES.add(sample);

        }

        END = time;
    }
}
