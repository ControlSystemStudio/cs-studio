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
package org.csstudio.archive.common.reader.testdata;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
/**
 * TODO (bknerr) : 
 * 
 * @author bknerr
 * @since 20.06.2011
 */
public final class TestSamples {
    
    
    public static final String CHANNEL_1 = "TEST_CHANNEL_1";
 
    @SuppressWarnings("rawtypes")
    public static final Collection<IArchiveSample> CHANNEL_1_SAMPLES = 
        new ArrayList<IArchiveSample>();
    
    static {
        int id = 0;
        CHANNEL_1_SAMPLES.add(new ArchiveSample<Double, ISystemVariable<Double>>(new ArchiveChannelId(id++),
                                                                                 new EpicsSystemVariable<Double>(CHANNEL_1,
                                                                                                                 10.0,
                                                                                                                 ControlSystem.EPICS_DEFAULT,
                                                                                                                 TimeInstantBuilder.fromMillis(10L),
                                                                                                                 EpicsAlarm.UNKNOWN),
                                                                                 EpicsAlarm.UNKNOWN));
        CHANNEL_1_SAMPLES.add(new ArchiveSample<Double, ISystemVariable<Double>>(new ArchiveChannelId(id++),
                                                                                 new EpicsSystemVariable<Double>(CHANNEL_1,
                                                                                                                 20.0,
                                                                                                                 ControlSystem.EPICS_DEFAULT,
                                                                                                                 TimeInstantBuilder.fromMillis(20L),
                                                                                                                 EpicsAlarm.UNKNOWN),
                                                                                  EpicsAlarm.UNKNOWN));
    }
}
