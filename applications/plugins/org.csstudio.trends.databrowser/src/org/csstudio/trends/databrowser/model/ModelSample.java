package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.swt.chart.ChartSample;
import org.eclipse.osgi.util.NLS;

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
        // Display string samples as points on the x axis.
        if (sample instanceof IStringValue)
            return Double.NEGATIVE_INFINITY;
        return ValueUtil.getDouble(sample);
    }

    /** Is there Y error/range info?
     *  @see org.csstudio.swt.chart.ChartSample
     */
    final public boolean haveMinMax()
    {
        return sample instanceof IMinMaxDoubleValue;
    }

    /** Minimum Y error/range.
     *  @see org.csstudio.swt.chart.ChartSample
     */
    public double getMinY()
    {
        return ((IMinMaxDoubleValue)sample).getMinimum();
    }

    /** Minimum Y error/range.
     *  @see org.csstudio.swt.chart.ChartSample
     */
    public double getMaxY()
    {
        return ((IMinMaxDoubleValue)sample).getMaximum();
    }

    /** Info text that the plot uses.
     *  @see org.csstudio.swt.chart.ChartSample
     */
    public String getInfo()
    {
        final String val_info = ValueUtil.getInfo(sample);
        final String quality = QualityHelper.getString(sample.getQuality());
        final String src_qual =
            NLS.bind(Messages.ModelSample_SourceQuality, source, quality);
        if (val_info == null)
            return src_qual;
        return val_info + "\n" + src_qual; //$NON-NLS-1$
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
