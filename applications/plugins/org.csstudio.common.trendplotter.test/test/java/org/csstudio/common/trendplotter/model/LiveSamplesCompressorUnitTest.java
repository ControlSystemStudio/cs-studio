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

import java.util.List;

import org.csstudio.domain.common.collection.LimitedArrayCircularQueue;
import org.junit.Assert;
import org.junit.Test;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 13.10.2011
 */
public class LiveSamplesCompressorUnitTest {

    // Timestamps in secs: 0, 4, 8, ...232, 236
    private static final List<PlotSample> PSAMPLES0 =
        TestSampleBuilder.makePlotSamplesWithStep(60, 0, 4);

    @Test
    public void testRegular() {
        LimitedArrayCircularQueue<PlotSample> samples =
            new LimitedArrayCircularQueue<PlotSample>(60);
        samples.addAll(PSAMPLES0);

        // Two compression levels...
        final LiveSamplesCompressor compressor =
            new LiveSamplesCompressor(16000L, 32000L);
        // over the full range
        samples = compressor.transform(samples);

        // 6 in first level (0..76), 10 in second level (84..160), 19 rest (164..236)
        Assert.assertTrue(samples.size() == 6 + 11 + 20);
        // first stage until 0 + 240000/3 = 80000
        Assert.assertTrue(Double.compare(samples.get(0).getYValue(), 0.0) == 0);
        Assert.assertTrue(Double.compare(samples.get(1).getYValue(), 32.0) == 0);//w
        Assert.assertTrue(Double.compare(samples.get(2).getYValue(), 36.0) == 0);
        Assert.assertTrue(Double.compare(samples.get(3).getYValue(), 64.0) == 0);//w
        Assert.assertTrue(Double.compare(samples.get(4).getYValue(), 68.0) == 0);
        Assert.assertTrue(Double.compare(samples.get(5).getYValue(), 76.0) == 0);//w/c
        // second stage from 80000 to 160000
        Assert.assertTrue(Double.compare(samples.get(6).getYValue(), 80.0) == 0);//w

        Assert.assertTrue(Double.compare(samples.get(7).getYValue(), 84.0) == 0);
        Assert.assertTrue(Double.compare(samples.get(8).getYValue(), 96.0) == 0);//w
        // ...
        Assert.assertTrue(Double.compare(samples.get(15).getYValue(), 148.0) == 0);
        Assert.assertTrue(Double.compare(samples.get(16).getYValue(), 156.0) == 0);//w/c
        // last stage
        Assert.assertTrue(Double.compare(samples.get(17).getYValue(), 160.0) == 0);
        // ...
        Assert.assertTrue(Double.compare(samples.get(36).getYValue(), 236.0) == 0);
    }
}
