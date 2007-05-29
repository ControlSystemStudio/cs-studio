package org.csstudio.swt.chart.axes;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/** A marker that's displayed on a YAxis
 *  @author Kay Kasemir
 */
public class Marker
{
    /** The position and value, i.e. x/y in value space */
    final private double position, value;
    
    /** The (multiline) text to display */
    final private String text;
    
    private boolean selected = false;
    
    /** The screen coordinates.
     *  Only updated when painted !
     */
    private Rectangle screen_pos = null;
    
    public Marker(double position, double value, String text)
    {
        this.position = position;
        this.value = value;
        this.text = text;
    }

    public boolean isSelected()
    {
        return selected;
    }
    
    public void select(boolean selected)
    {
        this.selected = selected;
    }
    
    /** @return On-screen coordinates or <code>null</code> if never displayed. */
    public Rectangle getScreenCoords()
    {
        return screen_pos;
    }
    
    /** Paint the marker on given gc and axes. */
    void paint(GC gc, Axis xaxis, Axis yaxis)
    {
        // Somewhat like this:
        //
        //    __Text__
        //   /
        //  x
        final int x = xaxis.getScreenCoord(position);
        final int y = yaxis.getScreenCoord(value);
        final Point size = gc.textExtent(text);
        final int dist = gc.getAdvanceWidth('x');
        final int tx = x+dist, ty = y-dist;
        // '/'
        gc.drawLine(x, y, tx, ty);
        // '___________'
        gc.drawLine(tx, ty, tx+size.x, ty);
        // Text
        final int txt_top = ty-size.y;
        gc.drawText(text, tx, txt_top, true);
        screen_pos = new Rectangle(tx, txt_top, size.x, size.y);
        if (selected)
        {
            final int olw = gc.getLineWidth();
            gc.setLineWidth(3);
            gc.drawRectangle(screen_pos);
            gc.setLineWidth(olw);
        }
    }
}
