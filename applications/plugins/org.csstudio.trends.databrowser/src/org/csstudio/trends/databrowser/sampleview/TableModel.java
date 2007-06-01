package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariableWithSample;
import org.csstudio.swt.chart.ChartSampleSearch;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IModelSamples;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelSample;

public class TableModel
{
    private IModelSamples samples;
    private IProcessVariableWithSample ipv_with_samples;
    
    TableModel(Model model, IModelItem item)
    {
        System.out.println("SampleView TableModel");
        samples = item.getSamples();
        synchronized (samples)
        {
            final ITimestamp start = model.getStartTime();
            // Determine which samples are actually visible
            // TODO move this into Model.getFirstVisibleSample() ...
            int start_index =
                ChartSampleSearch.findClosestSample(samples, start.toDouble());
            final ITimestamp end = model.getEndTime();
            int end_index =
                ChartSampleSearch.findClosestSample(samples, end.toDouble());
            final int N = samples.size();
            
            System.out.format("Model: %d samples from %s to %s\n",
                              N, start, end);
            System.out.format("Visible: %d .. %d\n",
                              start_index, end_index);
            
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
    private IProcessVariableWithSample createIPVwithSamples(
                    final IModelItem item, final int start, final int end)
    {
        final int num = end - start + 1;
        if (num <= 0)
            return null;
    
        // TODO Wait for IProcessVariableWithSample that doesn't need a copy
        // of double scalars, but instead handles all IValue types via a get(i)
        // interface.
        final double times[] = new double[num];
        final double values[] = new double[num];
        final String severities[] = new String[num];
        final String stati[] = new String[num];
        // Each sample might have a different precision.
        // We keep track of the last valid precision we find.
        int precision = 0;
        synchronized (samples)
        {
            for (int i=0; i<num; ++i)
            {
                final ModelSample sample = samples.get(start+i);
                times[i] = sample.getX();
                values[i] = sample.getY();
                severities[i] = sample.getSample().getSeverity().toString();
                stati[i] = sample.getSample().getStatus();
                final IMetaData meta = sample.getSample().getMetaData();
                if (meta instanceof INumericMetaData)
                    precision = ((INumericMetaData)meta).getPrecision();
            }
        }
        return CentralItemFactory.createProcessVariableWithSample(
                item.getName(),
                0, // nonsense dbrType
                item.getUnits(),
                item.getAxisLow(),
                item.getAxisHigh(),
                precision,
                values, times, stati, severities);
    }
}
