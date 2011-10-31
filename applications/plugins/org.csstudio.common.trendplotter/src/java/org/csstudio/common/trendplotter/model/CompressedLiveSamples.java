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

import java.util.Queue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.common.collection.LimitedArrayCircularQueue;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.joda.time.Interval;


/**
 * Live samples that are compressed (transformed) on adding a new sample.
 *
 * @author bknerr
 * @since 11.10.2011
 */
public class CompressedLiveSamples extends LiveSamples {

    public interface Transformer<S, T> {
        @Nonnull T transform(@Nonnull final S s);
    }

    private final LiveSamplesCompressor _compressor;
    private final IIntervalProvider _intervalPovider;
    private boolean _dynamicCompression;

    /**
     * Constructor.
     */
    public CompressedLiveSamples(@Nonnull final LiveSamplesCompressor c,
                                 final int cap,
                                 @Nullable final IIntervalProvider prov) {
        super(cap);
        _compressor = c;
        _intervalPovider = prov;
    }

    public void setDynamicCompression(final boolean dynamicCompression) {
        _dynamicCompression = dynamicCompression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected synchronized void add(@Nonnull final PlotSample sample) {
        super.add(sample);
        if (isCompressionDue(_samples)) {
            final Interval interval = _intervalPovider.getTimeInterval();
            if (interval != null) {
                _samples = compress(_samples, interval);
            }
        }
    }

    private boolean isCompressionDue(@Nonnull final LimitedArrayCircularQueue<PlotSample> samples) {
        return samples.size() >= Math.max(samples.getCapacity(), 2);
    }

    @Nonnull
    private LimitedArrayCircularQueue<PlotSample> compress(@Nonnull final LimitedArrayCircularQueue<PlotSample> samples,
                                                           @Nonnull final Interval interval) {
            removeSamplesBeforeStart(samples, interval.getStartMillis());

            if (_dynamicCompression) {
                final Long[] windowsMS = determinePerfectWindowForCompressedSamples(getCapacity() - _compressor.getNoUncompressed(),
                                                                                    samples,
                                                                                    interval);
                _compressor.setCompressionWindows(windowsMS);
            }

            return _compressor.transform(samples);
    }

    /**
     * Simple strategy to determine a good compression rate.
     * The 'perfect' window length is the displayed time interval divided by the capacity (that
     * has been already diminished by the number of permanently uncompressed samples).
     * And the last modification is due to that we expect two samples per window, min and max.
     *
     * @param cap the capacity for compressed samples (cap - uncompressed)
     * @param intvlMS the length of the interval wherein the compressed samples shall be displayed
     * @param the buffer reserve not considered for the window calculation
     * @return
     */
    @Nonnull
    private Long[] determinePerfectWindowForCompressedSamples(final int cap,
                                                              @Nonnull final LimitedArrayCircularQueue<PlotSample> samples,
                                                              final Interval intvl) {
        final long endMillis = BaseTypeConversionSupport.toTimeInstant(samples.get(samples.size() - 1).getTime()).getMillis();
        final long realEndMillis = Math.min(endMillis, intvl.getEndMillis());
        final long windowLengthMS = (int) ((realEndMillis - intvl.getStartMillis()) / cap); // perfect
        return new Long[] {windowLengthMS*4}; // double - and don't forget - min and max are 2 samples per window
    }

    private void removeSamplesBeforeStart(@Nonnull final Queue<PlotSample> samples,
                                          @Nonnull final long startTimeInMS) {
        PlotSample next;
        while ((next = samples.peek()) != null) {
            final TimeInstant time = BaseTypeConversionSupport.toTimeInstant(next.getTime());
            if (time.getMillis() < startTimeInMS) {
                samples.poll();
            } else {
                break;
            }
        }
    }
}
