package org.csstudio.swt.chart.axes;

/** A transformation.
 *  @see LinearTransform
 *  @see LogTransform
 *  @author Kay Kasemir
 */
public interface ITransform
{
    /** @return Returns x transformed into the y range. */
    public abstract double transform(double x);

    /** @return Returns x transformed into the y range. */
    public abstract double inverse(double y);

    /** Configure a transformation from x1..x2 into y1..y2
     *  <p>
     *  If the transformation is undefined (x1 == x2),
     *  or results in a==0 so that the 'inverse' won't work,
     *  the result is a 1:1 transformation. No error message.
     *  
     *  @param x1 Start of 'source'
     *  @param x2 End of 'source'
     *  @param y1 Start of 'destination'
     *  @param y2 End of 'destination'
     */
    public abstract void config(double x1, double x2, double y1, double y2);

}