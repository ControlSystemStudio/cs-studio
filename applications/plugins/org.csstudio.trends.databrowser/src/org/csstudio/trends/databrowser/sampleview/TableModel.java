package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.csstudio.swt.chart.ChartSampleSearch;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IModelSamples;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelSample;

public class TableModel
{
    /** All the model samples. */
    final private IModelSamples samples;

    /** Start index of visible samples.
     *  <p>
     *  Don't use if <code>num</code> is zero!
     */
    final private int start_index;

    /** Number of visible samples. */
    final private int num;
    
    /** IPV-with-samples interface to the visible samples. */
    final private IProcessVariableWithSamples ipv_with_samples;

    /** Constructor */
    TableModel(Model model, IModelItem item)
    {
        samples = item.getSamples();
        synchronized (samples)
        {
            final ITimestamp start = model.getStartTime();
            final ITimestamp end = model.getEndTime();
            
            start_index =
                ChartSampleSearch.findClosestSample(samples, start.toDouble());
            final int end_index =
                ChartSampleSearch.findClosestSample(samples, end.toDouble());
            if (start_index > 0  &&  end_index > 0)
            {
                num = end_index - start_index + 1;
                ipv_with_samples = createIPVwithSamples(item.getName(), samples,
                                                        start_index, num);
            }
            else
            {
                num = 0;
                ipv_with_samples = null;
            }
        }
    }
    
    /** @return The number of samples in the table. */
    int size()
    {   return num;   }
    
    TableItem getTableItem(int row)
    {
        if (row < num)
        {
            synchronized (samples)
            {
                final int i = start_index + row;
                if (i >= 0  &&  i < samples.size())
                    return new TableItem(samples.get(i), ipv_with_samples);
            }
        }
        return null;
    }
    
    /** Create an IProcessVariableWithSample for the given item and
     *  sample range.
     *  @param name Name of the PV
     *  @param samples The samples
     *  @param start Index of first sample
     *  @param num Sample count
     *  @return IProcessVariableWithSample.
     */
    private static IProcessVariableWithSamples createIPVwithSamples(
                                                    final String name,
                                                    final IModelSamples samples,
                                                    final int start,
                                                    final int num)
    {
        final IValue[] values = new IValue[num];
        synchronized (samples)
        {
            for (int i=0; i<num; ++i)
            {
                final ModelSample sample = samples.get(start + i);
                values[i] = sample.getSample();
            }
        }
        return CentralItemFactory.createProcessVariableWithSamples(name,
                                                                   values);
    }
}
