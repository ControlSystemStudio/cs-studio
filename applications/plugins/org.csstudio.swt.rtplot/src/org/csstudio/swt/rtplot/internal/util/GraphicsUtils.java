/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal.util;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/** Utility methods for drawing graphics
 *
 *  <p>Based on
 *  http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/Utilitymethodsfordrawinggraphics.htm
 */
public class GraphicsUtils
{
    /**
     * Draws text vertically (rotates plus or minus 90 degrees). Uses the
     * current font, color, and background.
     * <dl>
     * <dt><b>Styles: </b></dt>
     * <dd>UP, DOWN</dd>
     * </dl>
     * @param gc the GC on which to draw the text
     * @param x the x coordinate of the top left corner of the drawing rectangle
     * @param y the y coordinate of the top left corner of the drawing rectangle
     * @param string the text to draw
     * @param style the style (SWT.UP or SWT.DOWN)
     *            <p>
     *            Note: Only one of the style UP or DOWN may be specified.
     *            </p>
     */
    public static void drawVerticalText(final GC gc, final int x, final int y, final String string,
            final int style)
    {
        // Get the current display
        final Device display = gc.getDevice();

        // Determine string's dimensions
        final Point pt = gc.textExtent(string);
        if (pt.x <= 0  ||  pt.y <= 0)
            return;

        // Create an image the same size as the string
        final Image string_image = new Image(display, pt.x, pt.y);

        // Create a GC so we can draw the image
        final GC string_gc = new GC(string_image);

        // Set attributes from the original GC to the new GC
        string_gc.setForeground(gc.getForeground());
        string_gc.setBackground(gc.getBackground());
        string_gc.setFont(gc.getFont());

        // Draw the text onto the image.
        // Must draw in background color, because otherwise the image
        // background remains white, not the original gc's background.
        string_gc.drawText(string, 0, 0, false);

        // Draw the image vertically onto the original GC
        drawVerticalImage(gc, x, y, string_image, style);

        // Dispose the new GC
        string_gc.dispose();

        // Dispose the image
        string_image.dispose();
    }

    /**
     * Draws an image vertically (rotates plus or minus 90 degrees)
     * <dl>
     * <dt><b>Styles: </b></dt>
     * <dd>UP, DOWN</dd>
     * </dl>
     * @param gc the GC on which to draw the image
     * @param x the x coordinate of the top left corner of the drawing rectangle
     * @param y the y coordinate of the top left corner of the drawing rectangle
     * @param image the image to draw
     * @param style the style (SWT.UP or SWT.DOWN)
     *            <p>
     *            Note: Only one of the style UP or DOWN may be specified.
     *            </p>
     */
    public static void drawVerticalImage(final GC gc, final int x, final int y, final Image image,
            final int style)
    {
        // Get the current display
        final Device display = gc.getDevice();

        // Use the image's data to create a rotated image's data
        final ImageData sd = image.getImageData();
        final ImageData dd = new ImageData(sd.height, sd.width, sd.depth, sd.palette);

        // Determine which way to rotate, depending on up or down
        final boolean up = (style & SWT.UP) == SWT.UP;

        // Run through the horizontal pixels
        for (int sx = 0; sx < sd.width; sx++)
        {
            // Run through the vertical pixels
            for (int sy = 0; sy < sd.height; sy++)
            {
                // Determine where to move pixel to in destination image data
                int dx = up ? sy : sd.height - sy - 1;
                int dy = up ? sd.width - sx - 1 : sx;

                // Swap the x, y source data to y, x in the destination
                dd.setPixel(dx, dy, sd.getPixel(sx, sy));
            }
        }

        // Create the vertical image
        final Image vertical = new Image(display, dd);

        // Draw the vertical image onto the original GC
        gc.drawImage(vertical, x, y);

        // Dispose the vertical image
        vertical.dispose();
    }
}
