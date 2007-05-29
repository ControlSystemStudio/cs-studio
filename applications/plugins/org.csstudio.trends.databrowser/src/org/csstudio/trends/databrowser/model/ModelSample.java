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
    /** The actual sample. */
    private final IValue sample;
    
    /** The source that provided the sample (archive, live, ...) */
    private final String source;
    
    /** Create ModelSample from Archive Sample. */
    ModelSample(IValue sample, String source)
    {
        this.sample = sample;
        this.source = source;
    }
    
    /** @return The archive sample. */
    final public IValue getSample()
    {   return sample;  }

    /** @return The source that provided this sample. */
    final public String getSource()
    {   return source;  }
    
    /** Plot a point or part of line?
     *  @see org.csstudio.swt.chart.ChartSample
     */
    public ChartSample.Type getType()
    {
        double y = getY();
        if (Double.isInfinite(y)  ||  Double.isNaN(y))
            return ChartSample.Type.Point;
        return ChartSample.Type.Normal;
    }

    /** X coordinate.
     *  @see org.csstudio.swt.chart.ChartSample
     */
    final public double getX()
    {
        return sample.getTime().toDouble();
    }

    /** Y coordinate
     *  @see org.csstudio.swt.chart.ChartSample
     */
    final public double getY()
    {
        return ValueUtil.getDouble(sample);
    }

    /** Info text that the plot uses.
     *  @see org.csstudio.swt.chart.ChartSample
     */
    public String getInfo()
    {
        final String val_info = ValueUtil.getInfo(sample);
        if (val_info == null)
            return source;
        return val_info + "\n" + source; //$NON-NLS-1$
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
