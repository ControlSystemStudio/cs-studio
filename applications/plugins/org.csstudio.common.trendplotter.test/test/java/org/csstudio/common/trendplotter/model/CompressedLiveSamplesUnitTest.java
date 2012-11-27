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
package org.csstudio.common.trendplotter.model;


import javax.annotation.Nonnull;

import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link LiveSamples}.
 *
 * @author jhatje
 * @since 09.09.2011
 */
public class CompressedLiveSamplesUnitTest {


    @Test
    public void testStaticCompression() throws Exception {

        final CompressedLiveSamples samples =
            new CompressedLiveSamples(new LiveSamplesCompressor(8, 11000L, 6000L),
                                      23,
                                      4,
                                      new IIntervalProvider() {
                                          @Override
                                          @Nonnull
                                          public Interval getTimeInterval() {
                                              return new Interval(0L, 100000L);
                                          }
                                      });
        for (final PlotSample sample : TestSampleBuilder.makePlotSamplesWithStep(10, 0, 2)) {
            samples.add(sample); // 0, 2, .., 18
        }
        // first compression window until 68/2 = 34 sec
        samples.add(TestSampleBuilder.makePlotSample(60));
        samples.add(TestSampleBuilder.makePlotSample(62));
        samples.add(TestSampleBuilder.makePlotSample(63));
        samples.add(TestSampleBuilder.makePlotSample(64));
        samples.add(TestSampleBuilder.makePlotSample(68));
        // second compression window until 68 sec

        // 8 samples not to be compressed, start here
        for (final PlotSample sample : TestSampleBuilder.makePlotSamplesWithStep(5, 69, 5)) {
            samples.add(sample); // 70..90
        }
        samples.add(TestSampleBuilder.makePlotSample(96));
        samples.add(TestSampleBuilder.makePlotSample(99));
        samples.add(TestSampleBuilder.makePlotSample(100));
        // just hit capacity - compression has been triggered!!!

        Assert.assertTrue(samples.getSize() == 15);
        Assert.assertTrue(Double.compare(samples.getSample(0).getYValue(), 0.0) ==0);
        Assert.assertTrue(Double.compare(samples.getSample(1).getYValue(), 10.0) ==0); //w
        Assert.assertTrue(Double.compare(samples.getSample(2).getYValue(), 12.0) ==0);
        Assert.assertTrue(Double.compare(samples.getSample(3).getYValue(), 18.0) ==0); //w/c
        Assert.assertTrue(Double.compare(samples.getSample(4).getYValue(), 60.0) ==0); //w
        Assert.assertTrue(Double.compare(samples.getSample(5).getYValue(), 64.0) ==0);
        Assert.assertTrue(Double.compare(samples.getSample(6).getYValue(), 68.0) ==0); //w/c
        // rest
        Assert.assertTrue(Double.compare(samples.getSample(7).getYValue(), 69.0) ==0);
        Assert.assertTrue(Double.compare(samples.getSample(14).getYValue(), 100.0) ==0);
    }

    @Test
    public void testLargeCompression() {
        final LiveSamplesCompressor c = new LiveSamplesCompressor(Integer.valueOf(400));
        final CompressedLiveSamples samples =
            new CompressedLiveSamples(c,
                                      2000,
                                      100,
                                      new IIntervalProvider() {
                                          @Override
                                          @Nonnull
                                          public Interval getTimeInterval() {
                                              return new Interval(0L, (long) 1000*3600*24*14*4);
                                          }
                                      });
        samples.setDynamicCompression(true);

        for (int i = 0; i < 360000*24*14*4; i++) {
            samples.add(TestSampleBuilder.makePlotSample(i));
        }
        Assert.assertTrue(samples.getSize() == 1000);
    }

    @Test
    public void testZeroCompression() {
        final LiveSamplesCompressor c = new LiveSamplesCompressor(Integer.valueOf(0));
        final CompressedLiveSamples samples =
            new CompressedLiveSamples(c,
                                      1,
                                      0,
                                      new IIntervalProvider() {
                @Override
                @Nonnull
                public Interval getTimeInterval() {
                    return new Interval(0L, 1L);
                }
            });
        samples.setDynamicCompression(true);


        samples.add(TestSampleBuilder.makePlotSample(0));

        Assert.assertTrue(samples.getSize() == 1);
    }
}
