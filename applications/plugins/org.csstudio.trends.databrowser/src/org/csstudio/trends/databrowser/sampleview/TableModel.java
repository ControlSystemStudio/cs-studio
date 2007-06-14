package org.csstudio.trends.databrowser.sampleview;

import java.util.ArrayList;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.swt.chart.ChartSampleSearch;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IModelSamples;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelSample;

/** Model of the sample table.
 *  <p>
 *  Keeps a copy of the <u>visible</u> samples in the data browser model.
 *  <p>
 *  Alternatively, it could keep a reference to the model and its
 *  first/last visible sample index, then use a model listener to
 *  re-evalutate the visible range on model changes, and even
 *  update the table.
 *  But table updates are surprisingly expensive, and keeping an array
 *  of value references (not whole value copies) looked acceptable for now.
 *  
 *  @author Kay Kasemir
 */
public class TableModel
{
    /** Name of the PV that provided the data. */
    final private String name;
    
    /** References to all the visible model samples. */
    final private ArrayList<ModelSample> samples =
        new ArrayList<ModelSample>();

    /** Constructor */
    TableModel(final Model model, final IModelItem item)
    {
        name = item.getName();
        final IModelSamples all_samples = item.getSamples();
        synchronized (all_samples)
        {
        	// Determine visible start/end range
            final ITimestamp start = model.getStartTime();
            final ITimestamp end = model.getEndTime();
            final int start_index =
                ChartSampleSearch.findClosestSample(all_samples, start.toDouble());
            final int end_index =
                ChartSampleSearch.findClosestSample(all_samples, end.toDouble());
            // Get array of all those references
            if (start_index >= 0  &&  end_index >= 0)
            {
            	samples.ensureCapacity(end_index - start_index + 1);
            	for (int i=start_index; i<=end_index; ++i)
                    samples.add(all_samples.get(i));
            }
        }
    }
    
    /** @return Name of the PV */
    String getName()
    {   return name; }
    
    /** @return The number of samples in the table. */
    int size()
    {   return samples.size();   }

    /** @return Sample in given row. */
    @SuppressWarnings("nls")
    ModelSample getSample(final int row)
    {
        return samples.get(row);
    }
    
    /** @return TableItem for given row. */
    @SuppressWarnings("nls")
    TableItem getTableItem(int row)
    {
        return new TableItem(this, row);
    }
}
