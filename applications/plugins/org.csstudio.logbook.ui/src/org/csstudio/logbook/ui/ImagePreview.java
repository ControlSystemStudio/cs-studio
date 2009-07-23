package org.csstudio.logbook.ui;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/** Widget that displays an image.
 *  <p>
 *  Similar to a Label with image, but image is resized to fit the widget.
 *  @author Kay Kasemir
 */
public class ImagePreview extends Canvas implements DisposeListener, PaintListener
{
    private String filename;
    private Image image = null;

    public ImagePreview(final Composite parent)
    {
        super(parent, 0);
        addDisposeListener(this);
        
        addPaintListener(this);
    }

    /** Set image to display
     *  @param filename Name of image file or <code>null</code> for no image
     */
    public void setImage(final String filename)
    {
        if (image != null)
        {
            image.dispose();
            image = null;
            this.filename = null;
        }
        setToolTipText(""); //$NON-NLS-1$
        if (filename == null)
            return;
        try
        {
            image = new Image(getDisplay(), filename);
        }
        catch (Exception ex)
        {
            setToolTipText("Error: " + ex.getMessage()); //$NON-NLS-1$
            return;
        }
        setToolTipText(filename);
        this.filename = filename;
    }

    /** @return Name of image file or <code>null</code> */
    public String getImage()
    {
        return filename;
    }
    
    @Override
    public Point computeSize(int wHint, int hHint)
    {
        if (image == null)
            return new Point(1, 1);
        return new Point(200, 200);
    }

    /** @see PaintListener */
    public void paintControl(final PaintEvent e)
    {
        final Rectangle bounds = getBounds();
        final GC gc = e.gc;
        if (image != null)
        {   // Draw image to fit widget bounds
            final Rectangle img_rect = image.getBounds();
            gc.drawImage(image, 0, 0, img_rect.width, img_rect.height,
                    0, 0, bounds.width, bounds.height);
        }
    }

    /** @see DisposeListener */
    public void widgetDisposed(final DisposeEvent e)
    {
        if (image != null)
            image.dispose();
    }
}
