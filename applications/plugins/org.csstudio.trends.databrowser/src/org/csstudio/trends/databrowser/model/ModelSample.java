package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.Sample;
import org.csstudio.archive.util.SampleUtil;
import org.csstudio.swt.chart.ChartSample;

/** One sample of a ModelItem.
 *  <p>
 *  The archive presents samples in the fullest,
 *  with timestamp, status, severity.
 *  <p>
 *  The plot library wants the samples as x/y coordinate,
 *  with info for tooltip and representation.
 *  <p>
 *  This class interfaces between the two:
 *  It keeps the archive sample and presents it
 *  via an interface suitable for the plot library. 
 *  
 *  @author Kay Kasemir
 */
public class ModelSample implements ChartSample
{
    /** This is the 'real' archive sample. */
    private final Sample sample;
    
    /** Create ModelSample from Archive Sample. */
    ModelSample(Sample sample)
    {
        this.sample = sample;
    }
    
    /** @return The archive sample. */
    public Sample getSample()
    {   return sample;  }
    
    /** @see org.csstudio.swt.chart.ChartSample */
    public int getType()
    {
        double y = getY();
        if (Double.isInfinite(y)  ||  Double.isNaN(y))
            return ChartSample.TYPE_POINT;
        return ChartSample.TYPE_NORMAL;
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public double getX()
    {
        return sample.getTime().toDouble();
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public double getY()
    {
        return SampleUtil.getDouble(sample);
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public String getInfo()
    {
        return SampleUtil.getInfo(sample);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (! (obj instanceof ModelSample))
            return false;
        ModelSample o = (ModelSample) obj;
        return sample.equals(o.getSample());
    }
    
    @Override
    public String toString()
    {   return "ModelSample: " + sample.toString();  } //$NON-NLS-1$
}
