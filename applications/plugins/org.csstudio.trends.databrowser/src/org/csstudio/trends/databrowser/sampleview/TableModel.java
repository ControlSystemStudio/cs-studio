package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariableWithSample;
import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.csstudio.swt.chart.ChartSampleSearch;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IModelSamples;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelSample;

public class TableModel
{
    private IModelSamples samples;

    private IProcessVariableWithSamples ipv_with_samples;
    
    TableModel(Model model, IModelItem item)
    {
        System.out.println("SampleView TableModel");
        samples = item.getSamples();
        synchronized (samples)
        {
//            final ITimestamp start = model.getStartTime();
//            // Determine which samples are actually visible
//            // TODO move this into Model.getFirstVisibleSample() ...
//            int start_index = 0;
//                ChartSampleSearch.findClosestSample(samples, start.toDouble());
//            final ITimestamp end = model.getEndTime();
//            int end_index = samples.size() - 1;
//                ChartSampleSearch.findClosestSample(samples, end.toDouble());
//            final int N = samples.size();
//            
//            System.out.format("Model: %d samples from %s to %s\n",
//                              N, start, end);
//            System.out.format("Visible: %d .. %d\n",
//                              start_index, end_index);

//The methods 'model.getStartTime' and 'model.getEndTime' return the same TimeStamp
//-> I only use the sample size of the model independent of the chart        	
            
            int start_index = 0;
            int end_index = samples.size() - 1;
            
            ipv_with_samples = createIPVwithSamples(item, start_index, end_index);
        }
    }
    
    /** @return The number of samples in the table. */
    int size()
    {   return samples.size(); }
    
    TableItem getTableItem(int row)
    {
        synchronized (samples)
        {
            if (row >= 0  &&  row < samples.size())
            {
                System.out.println("SampleView TableModel creating item " + row);
                return new TableItem(samples.get(row), ipv_with_samples);
            }
        }
        return null;
    }
    
    /** Create an IProcessVariableWithSample for the given item and
     *  sample range.
     *  @param item Item that has all the samples
     *  @param start Index of first sample
     *  @param end Index of last sample
     *  @return IProcessVariableWithSample or <code>null</code>.
     */
    private IProcessVariableWithSamples createIPVwithSamples(
                    final IModelItem item, final int start, final int end)
    {
        final int num = end - start + 1;
        if (num <= 0)
            return null;

        IValue[] values = new IValue[num];
        synchronized (samples)
        {
            for (int i=0; i<num; ++i)
            {
                final ModelSample sample = samples.get(start+i);
                values[i] = sample.getSample();
            }
        }
        return CentralItemFactory.createProcessVariableWithSamples(item.getName(), values);
    }
}
