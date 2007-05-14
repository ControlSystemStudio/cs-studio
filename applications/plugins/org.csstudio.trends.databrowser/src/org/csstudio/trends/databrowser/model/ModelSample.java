package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.swt.chart.ChartSample;

/** One sample of a ModelItem.
 *  <p>
 *  The archive presents samples in the fullest,
 *  with timestamp, status, severity.
 *  <p>
 *  The plot library wants the samples as x/y coordinate,
 *  with info for tooltip and representation.
 *  <p>
 *  This interface allows both:
 *  It provides the archive sample, but also presents it
 *  via an interface suitable for the plot library. 
 *  
 *  @author Kay Kasemir
 */
public class ModelSample implements ChartSample
{
    /** This is the 'real' archive sample. */
    private final IValue sample;
    
    /** Create ModelSample from Archive Sample. */
    ModelSample(IValue sample)
    {
        this.sample = sample;
    }
    
    // TODO Add 'source' info so that we can display the data source
    // (live, archive XYZ, ...) in getInfo
    
    /** @return The archive sample. */
    final public IValue getSample()
    {   return sample;  }
    
    /** @see org.csstudio.swt.chart.ChartSample */
    public ChartSample.Type getType()
    {
        double y = getY();
        if (Double.isInfinite(y)  ||  Double.isNaN(y))
            return ChartSample.Type.Point;
        return ChartSample.Type.Normal;
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    final public double getX()
    {
        return sample.getTime().toDouble();
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    final public double getY()
    {
        return ValueUtil.getDouble(sample);
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public String getInfo()
    {
        return ValueUtil.getInfo(sample);
    }

    @Override
    final public boolean equals(Object obj)
    {
        if (! (obj instanceof ModelSample))
            return false;
        ModelSample o = (ModelSample) obj;
        return sample.equals(o.getSample());
    }
    
    @Override
    final public String toString()
    {   return "ModelSample: " + sample.toString();  } //$NON-NLS-1$
}
