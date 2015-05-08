/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal.util;


import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
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
        Image string_image = drawText(display,string,gc.getForeground(),gc.getBackground(),gc.getFont().getFontData()[0],pt.x,pt.y);
        if (string_image != null) {
            // Draw the image vertically onto the original GC
            drawVerticalImage(gc, x, y, string_image, style);
            // Dispose the image
            string_image.dispose();
        }
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

    private static Image drawText(Device device, String text, Color foreground, Color background, FontData fd, int sx, int sy) {
        int style = fd.getStyle();
        int newStyle = Font.PLAIN;
        if ((style & SWT.BOLD) == SWT.BOLD) newStyle |= Font.BOLD;
        if ((style & SWT.ITALIC) == SWT.ITALIC) newStyle |= Font.ITALIC;
        Font f = new Font(fd.getName(),newStyle,fd.getHeight());
        BufferedImage awtImage = new BufferedImage(sx,sy, BufferedImage.TYPE_INT_ARGB);
        Graphics g = awtImage.getGraphics();
        g.setColor(new java.awt.Color(background.getRed(), background.getGreen(), background.getBlue()));
        g.fillRect(0, 0, sx,sy);
        g.setClip(0, 0, sx,sy);
        g.setColor(new java.awt.Color(foreground.getRed(), foreground.getGreen(), foreground.getBlue()));
        g.setFont(f);
        g.drawString(text, 0, fd.getHeight());
        try {
            return makeSWTImage(device, awtImage);
        } catch (Exception e) {
            return null;
        }
    }

    private static Image makeSWTImage(Device device, java.awt.Image ai) {
        int width = ai.getWidth(null);
        int height = ai.getHeight(null);
        BufferedImage bufferedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(ai, 0, 0, null);
        g2d.dispose();
        int[] data = ((DataBufferInt) bufferedImage.getData().getDataBuffer())
                .getData();
        ImageData imageData = new ImageData(width, height, 24, new PaletteData(
                0xFF0000, 0x00FF00, 0x0000FF));
        imageData.transparentPixel = 0;
        imageData.setPixels(0, 0, data.length, data, 0);
        return new Image(device,imageData);
    }
}
