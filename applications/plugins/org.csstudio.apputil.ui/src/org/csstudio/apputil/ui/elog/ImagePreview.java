package org.csstudio.apputil.ui.elog;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
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
    private Image image;
    private String message;

    public ImagePreview(final Composite parent, final String message, String image_filename)
    {
        super(parent, 0);
        addDisposeListener(this);
        setToolTipText(image_filename);
        
        try
        {
            image = new Image(parent.getDisplay(), image_filename);
            this.message = message;
        }
        catch (Exception ex)
        {
            this.message =
                NLS.bind(Messages.ImagePreview_ImageError, image_filename, ex.getMessage());
        }
        addPaintListener(this);
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
        if (message != null)
        {   // Show message
            final Point extend = gc.textExtent(message, SWT.DRAW_DELIMITER);
            final int x = (bounds.width - extend.x)/2;
            final int y = (bounds.height - extend.y)/2;
            gc.drawText(message, x, y, SWT.DRAW_DELIMITER | SWT.DRAW_TRANSPARENT);
            return;
        }
    }

    /** @see DisposeListener */
    public void widgetDisposed(final DisposeEvent e)
    {
        if (image != null)
            image.dispose();
    }
}
