package org.csstudio.diag.postanalyser.model;

/** A named channel with (x,y) sample series.
 *  @author Kay Kasemir
 */
public class Channel extends XYChartSamples
{
    final private String name;
    
    public Channel(final String name, final double[] x, final double[] y)
    {
        super(x, y);
        this.name = name;
    }

    /** @return the name */
    public String getName()
    {
        return name;
    }
}
