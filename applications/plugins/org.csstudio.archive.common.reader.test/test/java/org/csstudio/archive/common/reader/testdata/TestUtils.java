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

import org.csstudio.archive.common.reader.facade.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channel.ArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.controlsystem.ArchiveControlSystem;
import org.csstudio.archive.common.service.sample.ArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ControlSystemType;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.junit.Assert;
import org.mockito.Mockito;
/**
 * Sample arrays for test purposes. 
 * 
 * @author bknerr
 * @since 20.06.2011
 */
public final class TestUtils {
    
    
    public static final String CHANNEL_NAME_1 = "TEST_CHANNEL_1";
    
    public static final IArchiveChannel CHANNEL = new ArchiveChannel(new ArchiveChannelId(1L),
                                                                     CHANNEL_NAME_1,
                                                                     "Double",
                                                                     new ArchiveChannelGroupId(1L),
                                                                     TimeInstantBuilder.fromMillis(0L),
                                                                     new ArchiveControlSystem("EPICS", ControlSystemType.EPICS_V3));
 
    @SuppressWarnings("rawtypes")
    public static final Collection<IArchiveMinMaxSample> CHANNEL_1_SAMPLES = 
        new ArrayList<IArchiveMinMaxSample>();
    
    static {
        int id = 0;
        CHANNEL_1_SAMPLES.add(new ArchiveMinMaxSample<Double, ISystemVariable<Double>>(new ArchiveChannelId(id++),
                                                                                       new EpicsSystemVariable<Double>(CHANNEL_NAME_1,
                                                                                                                 10.0,
                                                                                                                 ControlSystem.EPICS_DEFAULT,
                                                                                                                 TimeInstantBuilder.fromMillis(10L),
                                                                                                                 EpicsAlarm.UNKNOWN),
                                                                                       EpicsAlarm.UNKNOWN,
                                                                                       9.0, 11.0));
        CHANNEL_1_SAMPLES.add(new ArchiveMinMaxSample<Double, ISystemVariable<Double>>(new ArchiveChannelId(id++),
                                                                                       new EpicsSystemVariable<Double>(CHANNEL_NAME_1,
                                                                                                                 20.0,
                                                                                                                 ControlSystem.EPICS_DEFAULT,
                                                                                                                 TimeInstantBuilder.fromMillis(20L),
                                                                                                                 EpicsAlarm.UNKNOWN),
                                                                                       EpicsAlarm.UNKNOWN,
                                                                                       19.0, 21.0));
    }
    
    @SuppressWarnings("rawtypes")
    @Nonnull
    public static IServiceProvider createCustomizedMockedServiceProvider(@Nonnull final String channelName, 
                                                                         @Nonnull final TimeInstant start,
                                                                         @Nonnull final TimeInstant end,
                                                                         @Nonnull final Collection expectedResult) {
        return createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, null, null, null);
    }
    
    @SuppressWarnings("rawtypes")
    @Nonnull
    public static IServiceProvider createCustomizedMockedServiceProvider(@Nonnull final String channelName, 
                                                                         @Nonnull final TimeInstant start,
                                                                         @Nonnull final TimeInstant end,
                                                                         @Nonnull final Collection expectedResult,
                                                                         @Nonnull final IArchiveChannel expectedChannel,
                                                                         @Nonnull final Limits expLimits,
                                                                         @Nonnull final IArchiveSample expLastSampleBefore) {
        final IServiceProvider provider = 
            new IServiceProvider() {
                @SuppressWarnings({ "unchecked" })
                @Override
                @Nonnull
                public IArchiveReaderFacade getReaderFacade() throws OsgiServiceUnavailableException {
                    IArchiveReaderFacade mock = Mockito.mock(IArchiveReaderFacade.class);
                    try {
                        Mockito.when(mock.readSamples(channelName, start, end, null)).thenReturn(expectedResult);
                        Mockito.when(mock.getChannelByName(channelName)).thenReturn(expectedChannel);
                        Mockito.when(mock.readDisplayLimits(channelName)).thenReturn(expLimits);
                        Mockito.when(mock.readLastSampleBefore(channelName, start)).thenReturn(expLastSampleBefore);

                    } catch (final ArchiveServiceException e) {
                        Assert.fail("Only reachable by intention.");
                    }
                    return mock;
                }
            };
        return provider;
    }
}
