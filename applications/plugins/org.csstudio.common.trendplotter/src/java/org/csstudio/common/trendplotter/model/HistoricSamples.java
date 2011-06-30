/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;

import com.google.common.collect.Maps;


/** Holder for 'historic' orgSamples.
 *  <p>
 *  In addition to holding 'all' historic orgSamples, this class
 *  allows for a 'border' time beyond which no orgSamples will
 *  be provided.
 *  When setting this border to the start of the 'live' orgSamples,
 *  this class will thus assert that the live orgSamples have
 *  precedence because no 'historic' sample is provided
 *  for the 'live' time range.
 *  When the start of the 'live' time range moves because
 *  the live data ring buffer rolls around, the 'border' time adjustments
 *  might then uncover historic orgSamples that were previously
 *  hidden below the 'live' time range.
 *
 *  @author Kay Kasemir
 */
public class HistoricSamples extends PlotSamples
{
    /** "All" historic orgSamples */
//    private PlotSample[] orgSamples = new PlotSample[0];
//    private PlotSample[] intSamples = new PlotSample[0];

    private final Map<RequestType, PlotSample[]> sample_map = Maps.newEnumMap(RequestType.class);
    
    /** If non-null, orgSamples beyond this time are hidden from access */
    private ITimestamp border_time = null;

    /** 
     * Subset of orgSamples.length that's below border_time
     *  @see #computeVisibleSize()
     */
    private int visible_size = 0;
    
    private boolean adel_info_complete = false;

    
    /**
     * Constructor.
     */
    public HistoricSamples(PVItem item) 
    {
        updateRequestType(item.getRequestType());
        for (RequestType type : RequestType.values()) {
            sample_map.put(type, new PlotSample[0]);
        }
    }
    
    
    /** Define a new 'border' time beyond which no orgSamples
     *  are returned from the history
     *  @param border_time New time or <code>null</code> to access all orgSamples
     */
    public void setBorderTime(final ITimestamp border_time)
    {   // Anything new?
        if (border_time == null)
        {
            if (this.border_time == null)
                return;
        }
        else if (border_time.equals(this.border_time))
                return;
        // New border, recompute, mark as 'new data'
        this.border_time = border_time;
        computeVisibleSize(sample_map.get(request_type));
        have_new_samples = true;
    }

    /** Update visible size */
    synchronized private void computeVisibleSize(PlotSample[] samples)
    {
        if (border_time == null)
            visible_size = samples.length;
        else
        {
            final int last_index = 
                PlotSampleSearch.findSampleLessThan(samples, border_time);
            visible_size = (last_index < 0)   ?   0   :   last_index + 1;
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    synchronized public PlotSample getSample(final int i)
    {
        if (i >= visible_size)
            throw new IndexOutOfBoundsException("Index " + i + " exceeds visible size " + visible_size);
        return sample_map.get(request_type)[i];
    }

    /** {@inheritDoc} */
    @Override
    synchronized public int getSize()
    {
        return visible_size;
    }

    /** Merge newly received archive data into historic orgSamples
     *  @param source Info about data source
     *  @param result Samples to add/merge
     */
    synchronized public void mergeArchivedData(final String source, 
                                               final List<IValue> result)
    {
        // Anything new at all?
        if (result.size() <= 0)
            return;
        // Turn IValues into PlotSamples
        final PlotSample new_samples[] = new PlotSample[result.size()];
        for (int i=0; i<new_samples.length; ++i)
            new_samples[i] = new PlotSample(source, result.get(i));

        // Merge with existing samples
        PlotSample[] samples = sample_map.get(request_type);
        PlotSample[] merged = PlotSampleMerger.merge(samples, new_samples);
        if (merged == samples)
            return;
        sample_map.put(request_type, merged);

        computeVisibleSize(merged);

        have_new_samples = true;
        adel_info_complete = false;
    }

    /** Delete all orgSamples */
    synchronized public void clear()
    {
        visible_size = 0;
        for (RequestType type : RequestType.values()) {
            sample_map.put(type, new PlotSample[0]);
        }
        have_new_samples = true;
    }
    
    public void setAdelInfoComplete(boolean b) {
        adel_info_complete = b;
    }
    public boolean adelInfoComplete() {
        return adel_info_complete;
    }
}
