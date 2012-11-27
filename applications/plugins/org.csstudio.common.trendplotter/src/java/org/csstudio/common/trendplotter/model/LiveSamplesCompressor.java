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

import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.common.collection.LimitedArrayCircularQueue;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * Compress live sample list to prevent performance problems drawing samples
 * in graph.
 *
 * @author jhatje
 * @since 12.09.2011
 */
public class LiveSamplesCompressor {

    private List<Long> _windowLengthsMS;
    private int _noUncompressed;

    /**
     * Constructor.
     */
    public LiveSamplesCompressor(@Nonnull final Integer uncompressed,
                                 @Nonnull final Long...windowsMS) {
        _noUncompressed = uncompressed.intValue();
        setCompressionWindows(windowsMS);
    }
    /**
     * Constructor.
     */
    public LiveSamplesCompressor(@Nonnull final Long...windowsMS) {
        this(Integer.valueOf(0), windowsMS);
    }

    public void setCompressionWindows(@Nonnull final Long...windowsMS) {
        _windowLengthsMS = Ordering.natural().reverse().sortedCopy(Lists.newArrayList(windowsMS));
    }

    public void setNoUncompressed(final int noUncompressed) {
        _noUncompressed = noUncompressed;
    }
    public int getNoUncompressed() {
        return _noUncompressed;
    }
    @Nonnull
    public LimitedArrayCircularQueue<PlotSample> reTransform(@Nonnull final LimitedArrayCircularQueue<PlotSample> samples, int cap) {
        List<PlotSample> targetList = Lists.newLinkedList();
        PlotSample next = samples.peek();
        PlotSample min = next;
        PlotSample max = next;
        int i=0;
        while(2*i<(cap- getNoUncompressed())){
            for(int j=0;j<4 && 2*i<(cap- getNoUncompressed());j++){
                i++; 
                min = next.getYValue() < min.getYValue() ? next : min;
                max = next.getYValue() > max.getYValue() ? next : max;
                samples.remove();
                next = samples.peek();  
            }
            targetList = storeMinMax(min, max, targetList);
            min = next;
            max = next;
           }
    
        synchronized (samples) {
            samples.drainTo(targetList); // the rest remains uncompressed
            samples.clear();
            samples.addAll(targetList);
        }
        return samples;
    }
    @Nonnull
    public LimitedArrayCircularQueue<PlotSample> transform(@Nonnull final LimitedArrayCircularQueue<PlotSample> samples,
                                                           PlotSample[] samplesArray) {
        final PlotSample firstCompressionSample = samples.element();
        final PlotSample firstCompressionSample2 = samplesArray[0];
        final PlotSample lastCompressionSample = samples.get(samples.size() - _noUncompressed - 1);
        final PlotSample lastCompressionSample2 = samplesArray[samplesArray.length-_noUncompressed-1];
        if (firstCompressionSample==firstCompressionSample2 && lastCompressionSample==lastCompressionSample2) {
        } else {
            System.out.println("ERROR XXXXXXXXXXX   UNGLEICH");
        }
        if (samples.size() < _noUncompressed) {
            return samples;
        }

        final long startMillis = BaseTypeConversionSupport.toTimeInstant(firstCompressionSample.getTime()).getMillis();
        final long endMillis = BaseTypeConversionSupport.toTimeInstant(lastCompressionSample.getTime()).getMillis();
        final long compressionStageLength = (endMillis - startMillis + 1)/_windowLengthsMS.size();

        final List<PlotSample> targetList = Lists.newLinkedList();
        int i = 0;
        for (final Long window : _windowLengthsMS) { 
            targetList.addAll(compressMinMax(samples,
                                             startMillis + i*compressionStageLength,
                                             startMillis + (i+1)*compressionStageLength,
                                             window.longValue())); // high compression
            i++;
        }

        synchronized (samples) {
            samples.drainTo(targetList); // the rest remains uncompressed
            samples.clear();
            samples.addAll(targetList);
        }
        return samples;
    }

    @Nonnull
    private Collection<? extends PlotSample> compressMinMax(@Nonnull final LimitedArrayCircularQueue<PlotSample> samples,
                                                            final long startOfCompressionMS,
                                                            final long endOfCompressionMS,
                                                            final long windowLengthMS) {
        // Either no samples...
        if (samples.isEmpty()) {
            return samples;
        }
        // ...or first sample lies already beyond the end of this compression stage
        final TimeInstant first = BaseTypeConversionSupport.toTimeInstant(samples.element().getTime());
        if (first.getMillis() > endOfCompressionMS) {
            return samples;
        }

        PlotSample next = samples.peek();
        PlotSample min = next;
        PlotSample max = next;
        long nextWindowEnd = Math.min(startOfCompressionMS + windowLengthMS, endOfCompressionMS);
        //System.out.println("-w: " + nextWindowEnd);

        List<PlotSample> result = Lists.newLinkedList();

        while ( next != null ) {

            if (!isSampleBefore(next, nextWindowEnd)) {
                nextWindowEnd = Math.min(nextWindowEnd + windowLengthMS, endOfCompressionMS);
                //System.out.println("-w: " + nextWindowEnd);
                if (!isSampleBefore(next, endOfCompressionMS)) {
                    break; // stop compression stage
                } else {
                    continue; // go to next window
                }
            }
            min = next.getYValue() < min.getYValue() ? next : min;
            max = next.getYValue() > max.getYValue() ? next : max;

            samples.remove();

            next = samples.peek();
            if (!isSampleBefore(next, nextWindowEnd)) {
                result = storeMinMax(min, max, result);
                min = next;
                max = next;
            }
        }
       return result;
    }

    private boolean isSampleBefore(@CheckForNull final PlotSample sample,
                                   final long nextWindowEnd) {
        if (sample == null) {
            return false;
        }
        final TimeInstant time = BaseTypeConversionSupport.toTimeInstant(sample.getTime());
        return time.getMillis() <= nextWindowEnd;
    }

    @Nonnull
    private List<PlotSample> storeMinMax(@Nonnull final PlotSample min,
                                         @Nonnull final PlotSample max,
                                         @Nonnull final List<PlotSample> result) {

        if (Double.compare(min.getYValue(), max.getYValue()) == 0) {
            result.add(min); // they feature the same value - so take just one (it's a min max compressor!)
            //System.out.println(min);
        } else if (min.getTime().isLessThan(max.getTime())) {
            result.add(min);
            //System.out.println(min);
            result.add(max);
            //System.out.println(max);
        } else {
            result.add(max);
            //System.out.println(max);
            result.add(min);
            //System.out.println(min);
        }
        return result;
    }

}
