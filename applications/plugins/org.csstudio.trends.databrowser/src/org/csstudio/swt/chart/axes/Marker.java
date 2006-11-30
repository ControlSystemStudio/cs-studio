package org.csstudio.swt.chart.axes;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class Marker
{
    private double position, value;
    private String text;
    
    public Marker(double position, double value, String text)
    {
        this.position = position;
        this.value = value;
        this.text = text;
    }
    
    /** Paint the marker on given gc and axes. */
    void paint(GC gc, Axis xaxis, Axis yaxis)
    {
        // Somewhat like this:
        //
        //    __Text__
        //   /
        //  x
        int x = xaxis.getScreenCoord(position);
        int y = yaxis.getScreenCoord(value);
        Point size = gc.textExtent(text);
        int dist = gc.getAdvanceWidth('x');
        int x1 = x+dist, y1 = y-dist;
        gc.drawLine(x, y, x1, y1);
        gc.drawLine(x1, y1, x1+size.x, y1);
        gc.drawText(text, x1, y1-size.y, true);
    }
}
