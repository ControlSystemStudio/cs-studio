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
    
    /** Constructor */
    public Marker(double position, double value, String text)
    {
        this.position = position;
        this.value = value;
        this.text = text;
    }

    /** @return Position (x-axis value, time) of this marker */
    final public double getPosition()
    {
        return position;
    }
    
    /** @return Value (on the Y axis) of this marker */
    final public double getValue()
    {
        return value;
    }
    
    /** @return Text (label) of this marker */
    final public String getText()
    {
        return text;
    }
    
    /** @return <code>true</code> if currently selected */
    final boolean isSelected()
    {
        return selected;
    }
    
    /** Set the selection state */
    final void select(boolean selected)
    {
        this.selected = selected;
    }
    
    /** @return On-screen coordinates or <code>null</code> if never displayed. */
    final Rectangle getScreenCoords()
    {
        return screen_pos;
    }

    /** 'X' marks the spot, and this is it's radius. */
    final private static int X_RADIUS = 2;

    /** Paint the marker on given gc and axes. */
    void paint(GC gc, Axis xaxis, Axis yaxis)
    {
        // Somewhat like this:
        //
        //    Text
        //    Blabla
        //    Yaddi yaddi
        //    ___________
        //   /
        //  O
        final int x = xaxis.getScreenCoord(position);
        final int y = yaxis.getScreenCoord(value);
        final Point text_size = gc.textExtent(text);
        final int label_dist = gc.getAdvanceWidth('x');
        final int tx = x+label_dist, ty = y-label_dist;
        // Marker 'O' around the actual x/y point
        gc.drawOval(x-X_RADIUS, y-X_RADIUS, 2*X_RADIUS, 2*X_RADIUS);
        // '/'
        gc.drawLine(x+X_RADIUS, y-X_RADIUS, tx, ty);
        // Text
        final int txt_top = ty-text_size.y;
        gc.drawText(text, tx, txt_top, true);
        // Update the screen position so that we can later 'select' this marker.
        screen_pos = new Rectangle(tx, txt_top, text_size.x, text_size.y);
        if (selected)
        {
            final int olw = gc.getLineWidth();
            gc.setLineWidth(3);
            gc.drawRectangle(screen_pos);
            gc.setLineWidth(olw);
        }
        else // '___________'
            gc.drawLine(tx, ty, tx+text_size.x, ty);
    }
}
