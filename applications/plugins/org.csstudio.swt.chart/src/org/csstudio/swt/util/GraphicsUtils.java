/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.util;

// Found this at 
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/Utilitymethodsfordrawinggraphics.htm

//Send questions, comments, bug reports, etc. to the authors:
//Rob Warner (rwarner@interspatial.com)
//Robert Harris (rbrt_harris@yahoo.com)

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

/**
 * This class contains utility methods for drawing graphics
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
     * 
     * @param string the text to draw
     * @param x the x coordinate of the top left corner of the drawing rectangle
     * @param y the y coordinate of the top left corner of the drawing rectangle
     * @param gc the GC on which to draw the text
     * @param style the style (SWT.UP or SWT.DOWN)
     * @param transparent boolean to set transparent background on or off
     *            <p>
     *            Note: Only one of the style UP or DOWN may be specified.
     *            </p>
     */
    public static void drawVerticalText(String string, int x, int y, GC gc,
            int style, boolean transparent)
    {
        // Get the current display
        Display display = Display.getCurrent();
        if (display == null)
            SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);

        // Determine string's dimensions
        Point pt = gc.textExtent(string);

        // Create an image the same size as the string
        Image stringImage = new Image(display, pt.x, pt.y);

        // Create a GC so we can draw the image
        GC stringGc = new GC(stringImage);

        // Set attributes from the original GC to the new GC
        stringGc.setForeground(gc.getForeground());
        stringGc.setBackground(gc.getBackground());
        stringGc.setFont(gc.getFont());

        // Draw the text onto the image
        stringGc.drawText(string, 0, 0, transparent);

        // Draw the image vertically onto the original GC
        drawVerticalImage(stringImage, x, y, gc, style);
        

        // Dispose the new GC
        stringGc.dispose();

        // Dispose the image
        stringImage.dispose();
    }

    /**
     * Draws text vertically (rotates plus or minus 90 degrees). Uses the
     * current font, color, and background.
     * <dl>
     * <dt><b>Styles: </b></dt>
     * <dd>UP, DOWN</dd>
     * </dl>
     * 
     * @param string the text to draw
     * @param x the x coordinate of the top left corner of the drawing rectangle
     * @param y the y coordinate of the top left corner of the drawing rectangle
     * @param gc the GC on which to draw the text
     * @param style the style (SWT.UP or SWT.DOWN)
     *            <p>
     *            Note: Only one of the style UP or DOWN may be specified.
     *            </p>
     */
    public static void drawVerticalText(String string, int x, int y, GC gc,
            int style)
    {
    	drawVerticalText(string, x, y, gc, style, false);
    }
    
    /**
     * Draws an image vertically (rotates plus or minus 90 degrees)
     * <dl>
     * <dt><b>Styles: </b></dt>
     * <dd>UP, DOWN</dd>
     * </dl>
     * 
     * @param image the image to draw
     * @param x the x coordinate of the top left corner of the drawing rectangle
     * @param y the y coordinate of the top left corner of the drawing rectangle
     * @param gc the GC on which to draw the image
     * @param style the style (SWT.UP or SWT.DOWN)
     *            <p>
     *            Note: Only one of the style UP or DOWN may be specified.
     *            </p>
     */
    public static void drawVerticalImage(Image image, int x, int y, GC gc,
            int style)
    {
        // Get the current display
        Display display = Display.getCurrent();
        if (display == null)
            SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);

        // Use the image's data to create a rotated image's data
        ImageData sd = image.getImageData();
        ImageData dd = new ImageData(sd.height, sd.width, sd.depth, sd.palette);

        // Determine which way to rotate, depending on up or down
        boolean up = (style & SWT.UP) == SWT.UP;

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
        Image vertical = new Image(display, dd);

        // Draw the vertical image onto the original GC
        gc.drawImage(vertical, x, y);

        // Dispose the vertical image
        vertical.dispose();
    }

    /**
     * Creates an image containing the specified text, rotated either plus or
     * minus 90 degrees.
     * <dl>
     * <dt><b>Styles: </b></dt>
     * <dd>UP, DOWN</dd>
     * </dl>
     * 
     * @param text the text to rotate
     * @param font the font to use
     * @param foreground the color for the text
     * @param background the background color
     * @param style direction to rotate (up or down)
     * @return Image
     *         <p>
     *         Note: Only one of the style UP or DOWN may be specified.
     *         </p>
     */
    public static Image createRotatedText(String text, Font font,
            Color foreground, Color background, int style)
    {
        // Get the current display
        Display display = Display.getCurrent();
        if (display == null)
            SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);

        // Create a GC to calculate font's dimensions
        GC gc = new GC(display);
        gc.setFont(font);

        // Determine string's dimensions
        Point pt = gc.textExtent(text);

        // Dispose that gc
        gc.dispose();

        // Create an image the same size as the string
        Image stringImage = new Image(display, pt.x, pt.y);

        // Create a gc for the image
        gc = new GC(stringImage);
        gc.setFont(font);
        gc.setForeground(foreground);
        gc.setBackground(background);

        // Draw the text onto the image
        gc.drawText(text, 0, 0);

        // Draw the image vertically onto the original GC
        Image image = createRotatedImage(stringImage, style);

        // Dispose the new GC
        gc.dispose();

        // Dispose the horizontal image
        stringImage.dispose();

        // Return the rotated image
        return image;
    }

    /**
     * Creates a rotated image (plus or minus 90 degrees)
     * <dl>
     * <dt><b>Styles: </b></dt>
     * <dd>UP, DOWN</dd>
     * </dl>
     * 
     * @param image the image to rotate
     * @param style direction to rotate (up or down)
     * @return Image
     *         <p>
     *         Note: Only one of the style UP or DOWN may be specified.
     *         </p>
     */
    public static Image createRotatedImage(Image image, int style)
    {
        // Get the current display
        Display display = Display.getCurrent();
        if (display == null)
            SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);

        // Use the image's data to create a rotated image's data
        ImageData sd = image.getImageData();
        ImageData dd = new ImageData(sd.height, sd.width, sd.depth, sd.palette);

        // Determine which way to rotate, depending on up or down
        boolean up = (style & SWT.UP) == SWT.UP;

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
        return new Image(display, dd);
    }
}
