package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.util.SampleUtil;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.swt.chart.ChartSample;

/** One sample of a ModelItem.
 *  <p>
 *  The archive presents samples in one way,
 *  for example with numeric status codes
 *  that get decoded via meta info from the data server.
 *  <p>
 *  The plot library wants the samples in another format.
 *  <p>
 *  This class interfaces between the two:
 *  It keeps the decoded info from the archive sample and presents it
 *  via an interface suitable for the plot library. 
 *  
 *  @author Kay Kasemir
 */
public class ModelSample implements ChartSample
{
    private double x, y;
    private String info;
    
    /** Create ModelSample from Archive Sample. */
    static ModelSample
                     fromArchiveSample(org.csstudio.archive.Sample arch_sample)
    {
        ITimestamp time = arch_sample.getTime();
        double value = SampleUtil.getDouble(arch_sample);
        String info = null;
        String sevr = arch_sample.getSeverity().toString();
        String stat = arch_sample.getStatus();
        if (sevr.length() > 0  ||  stat.length() > 0)
            info = sevr + " " + stat; //$NON-NLS-1$
        if (! arch_sample.getSeverity().hasValue())
            value = Double.NEGATIVE_INFINITY;
        return new ModelSample(time, value, info); 
    }
    
    /** Construct one model sample.
     *  @param time The sample's time stamp.
     *  @param value Ask 'W'.
     *  @param info Added info, may be used as a tool-tip.
     *              May be <code>null</code>
     */
    ModelSample(ITimestamp time, double value, String info)
    {
        this.x = time.toDouble();
        this.y = value;
        this.info = info;
    }
    
    /** @see org.csstudio.swt.chart.ChartSample */
    public int getType()
    {
        if (Double.isInfinite(y)  ||  Double.isNaN(y))
            return ChartSample.TYPE_POINT;
        return ChartSample.TYPE_NORMAL;
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public double getX()
    {
        return x;
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public double getY()
    {
        return y;
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public String getInfo()
    {
        return info;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (! (obj instanceof ModelSample))
            return false;
        ModelSample o = (ModelSample) obj;
        return x == o.x &&
               y == o.y &&
               (info == null ? o.info == null : info.equals(o.info));
    }
}
