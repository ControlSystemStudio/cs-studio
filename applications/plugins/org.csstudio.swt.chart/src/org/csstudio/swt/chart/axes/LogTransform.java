package org.csstudio.swt.chart.axes;

/** Logarithmic Transformation from x1..x2 into y1..y2 range.
 *  @author Kay Kasemir
 */
public class LogTransform implements ITransform
{
    private LinearTransform linear = new LinearTransform();
    
    /* @see org.csstudio.swt.chart.axes.ITransform#transform(double)
     */
    public double transform(double x)
    {
        return linear.transform(Log10.log10(x));
    }

    /* @see org.csstudio.swt.chart.axes.ITransform#inverse(double)
     */
    public double inverse(double y)
    {
        return Log10.pow10(linear.inverse(y));
    }
    
    /* @see org.csstudio.swt.chart.axes.ITransform#config(double, double, double, double)
     */
    public void config(double x1, double x2, double y1, double y2)
    {
        linear.config(Log10.log10(x1), Log10.log10(x2), y1, y2);
    }
}
