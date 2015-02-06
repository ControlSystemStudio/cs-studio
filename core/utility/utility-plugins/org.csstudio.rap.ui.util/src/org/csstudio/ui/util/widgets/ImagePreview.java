/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.widgets;

import java.io.FileInputStream;
import java.io.InputStream;

import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
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
 *  
 *  <p>Similar to a Label with image, but image is resized to fit the widget.
 *  
 *  <p>Image can be provided as {@link InputStream}.
 *
 *  <p>As a shortcut, the widget can also read a file.
 *  
 *  <p>Setting the {@link Image} directly is not supported
 *  to avoid issues with ownership and disposal of such an image.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ImagePreview extends Canvas implements DisposeListener,
        PaintListener
{
	private static final long serialVersionUID = -5663464409023894169L;

	/** Image or <code>null</code> */
    private Image image = null;

    /** Additional short message or <code>null</code> */
    private String message = null;

    /** Initialize empty
     *  @param parent Parent widget
     */
    public ImagePreview(final Composite parent)
    {
        super(parent, 0);
        addDisposeListener(this);
        addPaintListener(this);
    }
    
    /** Set image to display
     *  @param image_filename Full path to image file or <code>null</code> for no image
     */
    public void setImage(final String image_filename)
    {
        if (image_filename == null)
            setImage((InputStream) null);
        else
        {
            try
            {
                setImage(new FileInputStream(image_filename));
            }
            catch (Exception ex)
            {
                ExceptionDetailsErrorDialog.openError(getShell(), "Error", ex);
            }
        }
    }
    
    /** Set image to display
     *  @param image_stream Image stream
     */
    public void setImage(final InputStream image_stream)
    {
        // Remove previous image, if there was one
        if (image != null)
        {
            image.dispose();
            image = null;
            setToolTipText("");
        }
        if (image_stream == null)
            return;
        image = new Image(getDisplay(), image_stream);
        redraw();
    }

    /** Set a message that is displayed on top of the image
     *  @param message Message to display or <code>null</code>
     */
    public void setMessage(final String message)
    {
        this.message = message;
        redraw();
    }
    
    // Note that this is supported by Canvas
    // public void setToolTipText(final String tooltip);
    
    /** @see Control */
    @Override
    public Point computeSize(final int wHint, final int hHint)
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
        {   // Draw image to fit widget bounds, maintaining
            // aspect ratio
            final Rectangle img = image.getBounds();
            // Start with original size
            int width = img.width;
            int height = img.height;
            int destX = 0;
            int destY = 0;
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
            destX = (bounds.width - width) / 2;
            destY = (bounds.height - height) / 2;
            gc.drawImage(image, 0, 0, img.width, img.height, destX, destY,
                    width, height);
        }
        if (message != null)
        {   // Show message
            final Point extend = gc.textExtent(message);
            final int x = (bounds.width - extend.x) / 2;
            final int y = (bounds.height - extend.y) / 2;
            gc.drawText(message, x, y, SWT.DRAW_DELIMITER
                    | SWT.DRAW_TRANSPARENT);
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
