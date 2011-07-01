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

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

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
public class EquidistantTimeBinsIteratorUnitTest {
    
    
    @BeforeClass
    public static void setup() {
        EpicsSystemVariableSupport.install();
        BaseTypeConversionSupport.install();
    }
    
    @SuppressWarnings("rawtypes")
    @Test(expected=NoSuchElementException.class)
    public void testEmptyIteratorWithoutLastSampleBefore() throws Exception {

        final String channelName = "";
        final TimeInstant instant = TimeInstantBuilder.fromMillis(1L);
        final Collection expectedResult = (Collection) Collections.emptyList();
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_1;
        final Limits expLimits = (Limits) Limits.<Double>create(0.0, 10.0);
        final IArchiveSample expLastSampleBefore = null;
        
        final IArchiveServiceProvider provider = 
            TestUtils.createCustomizedMockedServiceProvider(channelName, instant, instant, expectedResult, expectedChannel, expLimits, expLastSampleBefore); 
        
        final EquidistantTimeBinsIterator<Double> iter = 
            new EquidistantTimeBinsIterator<Double>(provider, channelName, instant, instant, null, 100);
        
        Assert.assertFalse(iter.hasNext());
        
        iter.next(); // expect NSEE
    }

    @SuppressWarnings("rawtypes")
    @Test(expected=NoSuchElementException.class)
    public void testEmptyIteratorWithLastSampleBefore() throws Exception {
        
        final String channelName = "";
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(300L);
        final Collection expectedResult = (Collection) Collections.emptyList();
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_1;
        final Limits expLimits = (Limits) Limits.<Double>create(0.0, 10.0);
        final IArchiveSample expLastSampleBefore = 
            TestUtils.createArchiveMinMaxDoubleSample(channelName, TimeInstantBuilder.fromMillis(start.getMillis() - 1), 5.0);
        
        final IArchiveServiceProvider provider = 
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore); 
        
        EquidistantTimeBinsIterator<Double> iter = 
            new EquidistantTimeBinsIterator<Double>(provider, channelName, start, start, null, 1);
        Assert.assertTrue(iter.hasNext());
        IMinMaxDoubleValue iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 5.0);
        
        iter = 
            new EquidistantTimeBinsIterator<Double>(provider, channelName, start, start, null, 3);
        
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
    @SuppressWarnings("rawtypes")
    @Test(expected=NoSuchElementException.class)
    public void testFilledIteratorWithoutLastSampleBefore() throws Exception {
        
        final String channelName = TestUtils.CHANNEL_NAME_2;
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(200L);
        final Collection expectedResult = TestUtils.CHANNEL_2_SAMPLES;
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_2;
        final Limits expLimits = (Limits) Limits.<Double>create(0.0, 20.0);
        final IArchiveSample expLastSampleBefore = null;
        
        final IArchiveServiceProvider provider = 
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore); 

        EquidistantTimeBinsIterator<Double> iter = 
            new EquidistantTimeBinsIterator<Double>(provider, channelName, start, end, null, 5);

        Assert.assertTrue(iter.hasNext());
        IMinMaxDoubleValue iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 10.0);
        Assert.assertTrue(iValue.getMaximum() == 15.0);
        Assert.assertTrue(iValue.getMinimum() == 5.0);
        TimeInstant ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 140L);
        
        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 15.0);
        Assert.assertTrue(iValue.getMaximum() == 15.0);
        Assert.assertTrue(iValue.getMinimum() == 15.0);
        ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 160L);
        
        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 8.0);
        Assert.assertTrue(iValue.getMaximum() == 15.0);
        Assert.assertTrue(iValue.getMinimum() == 1.0);
        ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 180L);

        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 1.0);
        Assert.assertTrue(iValue.getMaximum() == 1.0);
        Assert.assertTrue(iValue.getMinimum() == 1.0);
        ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 200L);
        
        Assert.assertFalse(iter.hasNext());
        iter.next();
    }

    /**
     * Setup as above but this time only one time bin.
     * 
     * exp: 1 iterable with value@time: (7@200).<br/>
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    @Test(expected=NoSuchElementException.class)
    public void testFilledIteratorWithoutLastSampleBeforeOneBin() throws Exception {
        
        final String channelName = TestUtils.CHANNEL_NAME_2;
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(200L);
        final Collection expectedResult = TestUtils.CHANNEL_2_SAMPLES;
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_2;
        final Limits expLimits = (Limits) Limits.<Double>create(0.0, 20.0);
        final IArchiveSample expLastSampleBefore = null;
        
        final IArchiveServiceProvider provider = 
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore); 

        EquidistantTimeBinsIterator<Double> iter = 
            new EquidistantTimeBinsIterator<Double>(provider, channelName, start, end, null, 1);

        Assert.assertTrue(iter.hasNext());
        IMinMaxDoubleValue iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 7.0);
        Assert.assertTrue(iValue.getMaximum() == 15.0);
        Assert.assertTrue(iValue.getMinimum() == 1.0);
        TimeInstant ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 200L);
        
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
    @SuppressWarnings("rawtypes")
    @Test(expected=NoSuchElementException.class)
    public void testFilledIteratorWithLastSampleBeforeOneBin() throws Exception {
        
        final String channelName = TestUtils.CHANNEL_NAME_2;
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(200L);
        final Collection expectedResult = TestUtils.CHANNEL_2_SAMPLES;
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_2;
        final Limits expLimits = (Limits) Limits.<Double>create(0.0, 20.0);
        final IArchiveSample expLastSampleBefore = 
            TestUtils.createArchiveMinMaxDoubleSample(TestUtils.CHANNEL_NAME_2, start.minusMillis(1L), -5.0);
    
        final IArchiveServiceProvider provider = 
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore); 

        EquidistantTimeBinsIterator<Double> iter = 
            new EquidistantTimeBinsIterator<Double>(provider, channelName, start, end, null, 1);

        Assert.assertTrue(iter.hasNext());
        IMinMaxDoubleValue iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 4.0);
        Assert.assertTrue(iValue.getMaximum() == 15.0);
        Assert.assertTrue(iValue.getMinimum() == -5.0);
        TimeInstant ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 200L);
        
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
    @SuppressWarnings("rawtypes")
    @Test(expected=NoSuchElementException.class)
    public void testFilledIteratorWithLastSampleBefore() throws Exception {
        
        final String channelName = TestUtils.CHANNEL_NAME_2;
        final TimeInstant start = TimeInstantBuilder.fromMillis(100L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(200L);
        final Collection expectedResult = TestUtils.CHANNEL_2_SAMPLES;
        final IArchiveChannel expectedChannel = TestUtils.CHANNEL_2;
        final Limits expLimits = (Limits) Limits.<Double>create(0.0, 20.0);
        final IArchiveSample expLastSampleBefore = 
            TestUtils.createArchiveMinMaxDoubleSample(TestUtils.CHANNEL_NAME_2, start.minusMillis(1L), -5.0);
        
        final IArchiveServiceProvider provider = 
            TestUtils.createCustomizedMockedServiceProvider(channelName, start, end, expectedResult, expectedChannel, expLimits, expLastSampleBefore); 
        
        EquidistantTimeBinsIterator<Double> iter = 
            new EquidistantTimeBinsIterator<Double>(provider, channelName, start, end, null, 5);
        
        Assert.assertTrue(iter.hasNext());
        IMinMaxDoubleValue iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == -5.0);
        Assert.assertTrue(iValue.getMaximum() == -5.0);
        Assert.assertTrue(iValue.getMinimum() == -5.0);
        TimeInstant ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 120L);

        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 5.0);
        Assert.assertTrue(iValue.getMaximum() == 15.0);
        Assert.assertTrue(iValue.getMinimum() == -5.0);
        ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 140L);
        
        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 15.0);
        Assert.assertTrue(iValue.getMaximum() == 15.0);
        Assert.assertTrue(iValue.getMinimum() == 15.0);
        ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 160L);
        
        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 8.0);
        Assert.assertTrue(iValue.getMaximum() == 15.0);
        Assert.assertTrue(iValue.getMinimum() == 1.0);
        ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 180L);

        Assert.assertTrue(iter.hasNext());
        iValue = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(iValue.getValue() == 1.0);
        Assert.assertTrue(iValue.getMaximum() == 1.0);
        Assert.assertTrue(iValue.getMinimum() == 1.0);
        ts = BaseTypeConversionSupport.toTimeInstant(iValue.getTime());
        Assert.assertTrue(ts.getMillis() == 200L);
        
        Assert.assertFalse(iter.hasNext());
        iter.next();
    }
    
}
