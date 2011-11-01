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
package org.csstudio.archive.common.reader;

import static org.csstudio.archive.common.reader.testdata.TestUtils.CHANNEL_3;
import static org.csstudio.archive.common.reader.testdata.TestUtils.CHANNEL_3_SAMPLES;
import static org.csstudio.archive.common.reader.testdata.TestUtils.CHANNEL_NAME_3;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.reader.facade.IArchiveServiceProvider;
import org.csstudio.archive.common.reader.testdata.TestUtils;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.domain.desy.epics.typesupport.EpicsSystemVariableSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for {@link EquidistantTimeBinsIterator}.
 *
 * @author bknerr
 * @since 21.06.2011
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EquidistantTimeBinsIteratorUnitTest {


    @BeforeClass
    public static void setup() {
        EpicsSystemVariableSupport.install();
        BaseTypeConversionSupport.install();
    }

    @Test(expected=NoSuchElementException.class)
    public void testEmptyIteratorWithoutLastSampleBefore() throws Exception {

        final String channelName = "";
        final TimeInstant instant = TimeInstantBuilder.fromMillis(1L);
        final Collection<IArchiveSample> expectedResult = Collections.<IArchiveSample>emptyList();
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_1;
        final Limits expLimits = Limits.<Double>create(0.0, 10.0);
        final IArchiveSample expLastSampleBefore = null;

        final IArchiveServiceProvider provider =
            TestUtils.createCustomizedMockedServiceProvider(channelName, instant, instant, expectedResult, expectedChannel, expLimits, expLastSampleBefore);

        final EquidistantTimeBinsIterator iter =
            new EquidistantTimeBinsIterator(provider, expectedResult, channelName, instant, instant, 100);

        Assert.assertFalse(iter.hasNext());

        iter.next(); // expect NSEE
    }


    @Test(expected=NoSuchElementException.class)
    public void testEmptyIteratorWithLastSampleBefore() throws Exception {

        final String channelName = "";
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(300L);
        final Collection<IArchiveSample> expectedResult = Collections.<IArchiveSample>emptyList();
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_1;
        final Limits expLimits = Limits.<Double>create(0.0, 10.0);
        final IArchiveSample expLastSampleBefore =
            TestUtils.createArchiveMinMaxDoubleSample(channelName, TimeInstantBuilder.fromMillis(start.getMillis() - 1), 5.0);

        final IArchiveServiceProvider provider =
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore);

        EquidistantTimeBinsIterator iter =
            new EquidistantTimeBinsIterator(provider, expectedResult, channelName, start, start, 1);
        Assert.assertTrue(iter.hasNext());
        IMinMaxDoubleValue iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 5.0);

        iter =
            new EquidistantTimeBinsIterator(provider, expectedResult, channelName, start, start, 3);

        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 5.0);

        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 5.0);

        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 5.0);

        Assert.assertFalse(iter.hasNext());
        iter.next();
    }

    /**
     * This test relies on the following situation: <br/>
     * timebins  5<br/>
     * <pre>
     *         start                                                  end
     *         100 -------------------------------------------------- 200
     *          |          |          |          |          |          |
     * samples                 x   x                   x
     * values                  5   15                  1
     *
     * </pre>
     * exp: 4 iterables with value@time: (10@140, 15@160, 8@180, 1@200).<br/>
     *
     * @throws Exception
     */
    @Test(expected=NoSuchElementException.class)
    public void testFilledIteratorWithoutLastSampleBefore() throws Exception {
        final String channelName = TestUtils.CHANNEL_NAME_2;
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(200L);
        final Collection expectedResult = TestUtils.CHANNEL_2_SAMPLES;
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_2;
        final Limits expLimits = Limits.<Double>create(0.0, 20.0);
        final IArchiveSample expLastSampleBefore = null;

        final IArchiveServiceProvider provider =
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore);

        final EquidistantTimeBinsIterator iter =
            new EquidistantTimeBinsIterator(provider, expectedResult, channelName, start, end, 5);

        assertSample(iter, 10.0, 15.0, 5.0, 140L);

        assertSample(iter, 15.0, 15.0, 15.0, 160L);

        assertSample(iter, 8.0, 15.0, 1.0, 180L);

        assertSample(iter, 1.0, 1.0, 1.0, 200L);

        Assert.assertFalse(iter.hasNext());
        iter.next();
    }

    private void assertSample(@Nonnull final EquidistantTimeBinsIterator iter,
                              final double expVal,
                              final double expMax,
                              final double expMin,
                              final long expTimestamp) throws Exception {
        Assert.assertTrue(iter.hasNext());
        final IMinMaxDoubleValue iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == expVal);
        Assert.assertTrue(iValue.getMaximum() == expMax);
        Assert.assertTrue(iValue.getMinimum() == expMin);
        final TimeInstant ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == expTimestamp);
    }

    /**
     * Setup as above but this time only one time bin.
     *
     * exp: 1 iterable with value@time: (7@200).<br/>
     * @throws Exception
     */
    @Test(expected=NoSuchElementException.class)
    public void testFilledIteratorWithoutLastSampleBeforeOneBin() throws Exception {

        final String channelName = TestUtils.CHANNEL_NAME_2;
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(200L);
        final Collection expectedResult = TestUtils.CHANNEL_2_SAMPLES;
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_2;
        final Limits expLimits = Limits.<Double>create(0.0, 20.0);
        final IArchiveSample expLastSampleBefore = null;

        final IArchiveServiceProvider provider =
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore);

        final EquidistantTimeBinsIterator iter =
            new EquidistantTimeBinsIterator(provider, expectedResult, channelName, start, end, 1);

        assertSample(iter, 7.0, 15.0, 1.0, 200L);

        Assert.assertFalse(iter.hasNext());
        iter.next();
    }


    /**
     * Setup as above but this time only one time bin and a sample before with value -7.0.
     *
     * exp: 1 iterable with value@time: (-5+5+15+1/4==4@200).<br/>
     *
     * @throws Exception
     */

    @Test(expected=NoSuchElementException.class)
    public void testFilledIteratorWithLastSampleBeforeOneBin() throws Exception {

        final String channelName = TestUtils.CHANNEL_NAME_2;
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(200L);
        final Collection expectedResult = TestUtils.CHANNEL_2_SAMPLES;
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_2;
        final Limits expLimits = Limits.<Double>create(0.0, 20.0);
        final IArchiveSample expLastSampleBefore =
            TestUtils.createArchiveMinMaxDoubleSample(TestUtils.CHANNEL_NAME_2, start.minusMillis(1L), -5.0);

        final IArchiveServiceProvider provider =
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore);

        final EquidistantTimeBinsIterator iter =
            new EquidistantTimeBinsIterator(provider, expectedResult, channelName, start, end, 1);

        assertSample(iter, 4.0, 15.0, -5.0, 200L);

        Assert.assertFalse(iter.hasNext());
        iter.next();
    }

    /**
     * Setup as above but this time and a sample before with value -5.0.
     *
     * exp: 4 iterables with value@time: (-5@120, (-5+5+15)/3==5@140, 15@160, (15+1)/2==8@180, 1@200).<br/>
     *
     * @throws Exception
     */

    @Test(expected=NoSuchElementException.class)
    public void testFilledIteratorWithLastSampleBefore() throws Exception {

        final String channelName = TestUtils.CHANNEL_NAME_2;
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(200L);
        final Collection expectedResult = TestUtils.CHANNEL_2_SAMPLES;
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_2;
        final Limits expLimits = Limits.<Double>create(0.0, 20.0);
        final IArchiveSample expLastSampleBefore =
            TestUtils.createArchiveMinMaxDoubleSample(TestUtils.CHANNEL_NAME_2, start.minusMillis(1L), -5.0);

        final IArchiveServiceProvider provider =
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore);

        final EquidistantTimeBinsIterator iter =
            new EquidistantTimeBinsIterator(provider, expectedResult, channelName, start, end, 5);

        assertSample(iter, -5.0, -5.0, -5.0, 120L);

        assertSample(iter, 5.0, 15.0, -5.0, 140L);

        assertSample(iter, 15.0, 15.0, 15.0, 160L);

        assertSample(iter, 8.0, 15.0, 1.0, 180L);

        assertSample(iter, 1.0, 1.0, 1.0, 200L);

        Assert.assertFalse(iter.hasNext());
        iter.next();
    }

    /**
     * Setup with original data from channel WK:K:16d:2:TDL_Desy_ai.VAL
     * Range (1317027377 sec. to 1317082937 sec.).
     *
     * @throws Exception
     */

    @Test(expected = NoSuchElementException.class)
    public void testFilledIteratorWithRealSamples() throws Exception {

        final int bins = 15;
        final long startL = 2L;
        final long endL = 55099997L;
        final long binLength = (endL - startL)/bins;
        final TimeInstant start = TimeInstantBuilder.fromMillis(startL);
        final TimeInstant end = TimeInstantBuilder.fromMillis(endL);

        final Limits expLimits = Limits.<Double> create(0.0, 60.0);
        final IArchiveSample expLastSampleBefore =
            TestUtils.createArchiveMinMaxDoubleSample(TestUtils.CHANNEL_NAME_3,
                                                      TimeInstantBuilder.fromMillis(1L),
                                                      19.07651);

        final IArchiveServiceProvider provider =
            TestUtils .createCustomizedMockedServiceProvider(CHANNEL_NAME_3,
                                                             start,
                                                             end,
                                                             CHANNEL_3_SAMPLES,
                                                             CHANNEL_3,
                                                             expLimits,
                                                             expLastSampleBefore);

        final EquidistantTimeBinsIterator iter =
            new EquidistantTimeBinsIterator(provider,
                                            (Collection) CHANNEL_3_SAMPLES,
                                            CHANNEL_NAME_3,
                                            start,
                                            end,
                                            15);
        Assert.assertTrue(iter.hasNext());
        assertSample(iter, 19.451659999999997, 19.82543, 19.07651, 3673335L);
        int i = 1;
        while (i++ < 4) {
            iter.next();
        }
        assertSample(iter, 21.85247, 22.01544, 21.68767, binLength*5 + 2);

        i = 1;
        while (i++ < 11) {
            iter.next();
        }

        Assert.assertFalse(iter.hasNext());
        iter.next();
    }
}
