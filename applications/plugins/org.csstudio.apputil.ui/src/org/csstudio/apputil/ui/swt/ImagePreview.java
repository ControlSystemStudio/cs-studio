/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.swt;

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
    /** Name of image file */
    private String filename;

    /** Image or <code>null</code> */
    private Image image = null;

    /** Additional short message or <code>null</code> */
    private String message;

    public ImagePreview(final Composite parent)
    {
        this(parent, null, null);
    }

    public ImagePreview(final Composite parent, final String message, final String image_filename)
    {
        super(parent, 0);
        this.message = message;

        addDisposeListener(this);
        setImage(image_filename);

        addPaintListener(this);
    }


    /** Set image to display
     *  @param image_filename Name of image file or <code>null</code> for no image
     */
    public void setImage(final String image_filename)
    {
        this.filename = image_filename;
        // Remove previous image, if there was one
        if (image != null)
        {
            image.dispose();
            image = null;
            setToolTipText(""); //$NON-NLS-1$
        }
        if (image_filename == null)
            return;
        try
        {
            image = new Image(getDisplay(), image_filename);
        }
        catch (Exception ex)
        {
            setToolTipText("Error: " + ex.getMessage()); //$NON-NLS-1$
            return;
        }
        setToolTipText(image_filename);
    }

    /** @see Control */
    @Override
    public Point computeSize(final int wHint, final int hHint)
    {
        if (image == null)
            return new Point(1, 1);
        return new Point(200, 200);
    }

    /** @see PaintListener */
    @Override
    public void paintControl(final PaintEvent e)
    {
        final Rectangle bounds = getBounds();
        final GC gc = e.gc;
        if (image != null)
        {   // Draw image to fit widget bounds, maintaining aspect ratio
            final Rectangle img = image.getBounds();
            // Start with original size
            int width = img.width;
            int height = img.height;
            if (width > bounds.width)
            {   // Too wide?
                width = bounds.width;
                height = width * img.height / img.width;
            }
            if (height > bounds.height)
            {   // Too high?
                height = bounds.height;
                width = height * img.width / img.height;
            }
            gc.drawImage(image, 0, 0, img.width, img.height,
                    0, 0, width, height);
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
    @Override
    public void widgetDisposed(final DisposeEvent e)
    {
        if (image != null)
            image.dispose();
    }

    /** @return Image file name */
    public String getImageFileName()
    {
        return filename;
    }
}
