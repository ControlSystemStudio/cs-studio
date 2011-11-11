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
import javax.annotation.Nullable;

import org.csstudio.archive.common.reader.facade.IArchiveServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannel;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
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
@SuppressWarnings("unchecked")
public final class TestUtils {

    public static final String CHANNEL_NAME_1 = "TEST_CHANNEL_1";
    public static final String CHANNEL_NAME_2 = "TEST_CHANNEL_2";
    public static final String CHANNEL_NAME_3 = "TEST_CHANNEL_3";

    public static final IArchiveChannel CHANNEL_1 = new ArchiveChannel(new ArchiveChannelId(1L),
                                                                       CHANNEL_NAME_1,
                                                                       Double.class,
                                                                       new ArchiveChannelGroupId(1L),
                                                                       TimeInstantBuilder.fromMillis(0L),
                                                                       new ArchiveControlSystem("EPICS", ControlSystemType.EPICS_V3),
                                                                       true);
    public static final IArchiveChannel CHANNEL_2 = new ArchiveChannel(new ArchiveChannelId(1L),
                                                                       CHANNEL_NAME_2,
                                                                       Double.class,
                                                                       new ArchiveChannelGroupId(2L),
                                                                       TimeInstantBuilder.fromMillis(0L),
                                                                       new ArchiveControlSystem("EPICS", ControlSystemType.EPICS_V3),
                                                                       true);

    public static final IArchiveChannel CHANNEL_3 = new ArchiveChannel(new ArchiveChannelId(3L),
                                                                       CHANNEL_NAME_3,
                                                                       Double.class,
                                                                       new ArchiveChannelGroupId(3L),
                                                                       TimeInstantBuilder.fromMillis(0L),
                                                                       new ArchiveControlSystem("EPICS", ControlSystemType.EPICS_V3),
                                                                       true);

    public static final Collection<IArchiveSample<Double, ISystemVariable<Double>>> CHANNEL_1_SAMPLES =
        new ArrayList<IArchiveSample<Double, ISystemVariable<Double>>>();

    public static final Collection<IArchiveSample<Double, ISystemVariable<Double>>> CHANNEL_2_SAMPLES =
        new ArrayList<IArchiveSample<Double, ISystemVariable<Double>>>();

    public static final Collection<IArchiveSample<Double, ISystemVariable<Double>>> CHANNEL_3_SAMPLES =
        new ArrayList<IArchiveSample<Double, ISystemVariable<Double>>>();

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

        CHANNEL_2_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_2, TimeInstantBuilder.fromMillis(125L), 5.0));
        CHANNEL_2_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_2, TimeInstantBuilder.fromMillis(135L), 15.0));
        CHANNEL_2_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_2, TimeInstantBuilder.fromMillis(170L), 1.0));
        addSamplesChannel3();
    }

    /**
     * Constructor.
     */
    private TestUtils() {
        // Empty
    }

    @SuppressWarnings("rawtypes")
    @Nonnull
    public static IArchiveMinMaxSample createArchiveMinMaxDoubleSample(@Nonnull final String channelName,
                                                                       @Nonnull final TimeInstant ts,
                                                                       @Nonnull final Double value) {
        return new ArchiveMinMaxSample<Double, ISystemVariable<Double>>(new ArchiveChannelId(1L),
                                                                        new EpicsSystemVariable<Double>(channelName,
                                                                                                        value,
                                                                                                        ControlSystem.EPICS_DEFAULT,
                                                                                                        ts,
                                                                                                        EpicsAlarm.UNKNOWN),
                                                                         EpicsAlarm.UNKNOWN,
                                                                         value, value);
    }


    @SuppressWarnings("rawtypes")
    @Nonnull
    public static IArchiveServiceProvider createCustomizedMockedServiceProvider(@Nonnull final String channelName,
                                                                         @Nonnull final TimeInstant start,
                                                                         @Nonnull final TimeInstant end,
                                                                         @Nonnull final Collection expectedResult) {
        return createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, null, null, null);
    }

    @SuppressWarnings("rawtypes")
    @Nonnull
    public static IArchiveServiceProvider createCustomizedMockedServiceProvider(@Nonnull final String channelName,
                                                                         @Nonnull final TimeInstant start,
                                                                         @Nonnull final TimeInstant end,
                                                                         @Nonnull final Collection expectedResult,
                                                                         @Nullable final IArchiveChannel expectedChannel,
                                                                         @Nullable final Limits expLimits,
                                                                         @Nullable final IArchiveSample expLastSampleBefore) {
        final IArchiveServiceProvider provider =
            new IArchiveServiceProvider() {
                @Override
                @Nonnull
                public IArchiveReaderFacade getReaderFacade() throws OsgiServiceUnavailableException {
                    final IArchiveReaderFacade mock = Mockito.mock(IArchiveReaderFacade.class);
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

    // CHECKSTYLE OFF: MethodLength|ExecutableStatementCount
    private static void addSamplesChannel3() {
        //bin1
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(568013L), 19.18821));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(1248021L), 19.29075));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(1824035L), 19.40062));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(2214036L), 19.50133));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(2524039L), 19.61486));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(2974039L), 19.71557));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(3406039L), 19.82543));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(3836038L), 19.92798));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(4558044L), 20.04151));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(5370052L), 20.14405));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(5726053L), 20.25025));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(6254064L), 20.35279));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(6686063L), 20.45351));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(7242077L), 20.56154));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(7768072L), 20.66408));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(8110076L), 20.55605));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(8264090L), 20.66774));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(8484078L), 20.76846));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(8776084L), 20.88382));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(9556087L), 20.99551));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(10100102L), 21.09622));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(11343103L), 21.22806));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(12071109L), 21.34342));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(12389113L), 21.45695));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(12917129L), 21.55766));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(13813126L), 21.68767));
        // bin5
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(14735143L), 21.80303));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(16065151L), 21.90374));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(17375157L), 22.01544));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(20823194L), 22.11982));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(22263206L), 22.01910));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(23127205L), 21.90374));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(24806225L), 21.80303));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(26782254L), 21.68767));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(27642251L), 21.57231));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(28550262L), 21.45695));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(29070264L), 21.34526));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(29992281L), 21.24454));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(30806281L), 21.13102));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(31738293L), 21.03030));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(32788293L), 20.92044));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(33292304L), 20.81240));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(33512300L), 20.70437));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(33842297L), 20.60183));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(34150303L), 20.50111));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(34484307L), 20.39308));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(34812312L), 20.28687));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(35316316L), 20.18616));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(35860322L), 20.08362));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(36482333L), 19.97925));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(37250336L), 19.86755));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(38100336L), 19.76501));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(39084349L), 19.66430));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(40022366L), 19.54894));

        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(41581371L), 19.43724));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(43225394L), 19.32188));

        //bin13
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(44993411L), 19.22117));
        CHANNEL_3_SAMPLES.add(createArchiveMinMaxDoubleSample(CHANNEL_NAME_3, TimeInstantBuilder.fromMillis(46637412L), 19.09665));
        //bin14,15
    }
 // CHECKSTYLE ON: MethodLength|ExecutableStatementCount

}
