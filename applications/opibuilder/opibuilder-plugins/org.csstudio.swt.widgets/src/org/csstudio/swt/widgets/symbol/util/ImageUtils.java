/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.symbol.util;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

/**
 * Utility class to change image behavior like color, shape, rotation
 * management, ...
 *
 * @author Fred Arnaud (Sopra Group)
 */
public final class ImageUtils {

    /**
     * Constructor cannot be call because of static invocation.
     */
    private ImageUtils() {
    }

    /**
     * Apply the specified {@link PermutationMatrix} to the given
     * {@link ImageData}.
     */
    public static ImageData applyMatrix(ImageData srcData, PermutationMatrix pm) {
        if (srcData == null || pm == null
                || pm.equals(PermutationMatrix.generateIdentityMatrix()))
            return srcData;
        double[][] matrix = pm.getMatrix();

        // point to rotate about => center of image
        double x0 = 0;
        double y0 = 0;

        // apply permutation to 4 corners
        double[] a = translate(0, 0, x0, y0, matrix);
        double[] b = translate(srcData.width - 1, 0, x0, y0, matrix);
        double[] c = translate(srcData.width - 1, srcData.height - 1, x0, y0, matrix);
        double[] d = translate(0, srcData.height - 1, x0, y0, matrix);

        // find new point
        double minX = findMin(a[0], b[0], c[0], d[0]);
        double minY = findMin(a[1], b[1], c[1], d[1]);
        double maxX = findMax(a[0], b[0], c[0], d[0]);
        double maxY = findMax(a[1], b[1], c[1], d[1]);
        int newWidth = (int) Math.round(maxX - minX);
        int newHeight = (int) Math.round(maxY - minY);

        ImageData newImageData = new ImageData(newWidth, newHeight,
                srcData.depth, srcData.palette);
        for (int destX = 0; destX < newImageData.width; destX++) {
            for (int destY = 0; destY < newImageData.height; destY++) {
                if (srcData.transparentPixel >= 0) {
                    newImageData.setPixel(destX, destY, srcData.transparentPixel);
                }
                newImageData.setAlpha(destX, destY, 0);
            }
        }

        for (int srcX = 0; srcX < srcData.width; srcX++) {
            for (int srcY = 0; srcY < srcData.height; srcY++) {
                int destX = 0, destY = 0;
                double[] destP = translate(srcX, srcY, x0, y0, matrix);
                destX = (int) Math.round(destP[0] - minX);
                destY = (int) Math.round(destP[1] - minY);

                if (destX >= 0 && destX < newWidth && destY >= 0
                        && destY < newHeight) {
                    newImageData.setPixel(destX, destY, srcData.getPixel(srcX, srcY));
                    newImageData.setAlpha(destX, destY, srcData.getAlpha(srcX, srcY));
                }
            }
        }
        // Re-set the lost transparency
        newImageData.transparentPixel = srcData.transparentPixel;
        newImageData.delayTime = srcData.delayTime;
        newImageData.disposalMethod = srcData.disposalMethod;
        return newImageData;
    }

    // multiply matrices
    private static double[][] multiply(double[][] m1, double[][] m2) {
        int p1 = m1.length, p2 = m2.length, q2 = m2[0].length;
        double[][] result = new double[p1][q2];
        for (int i = 0; i < p1; i++)
            for (int j = 0; j < q2; j++)
                for (int k = 0; k < p2; k++)
                    result[i][j] += m1[i][k] * m2[k][j];
        return result;
    }

    // calculate new coordinates
    private static double[] translate(int x, int y, double x0, double y0, double[][] matrix) {
        // translate coordinates
        double[][] p = new double[2][1];
        p[0][0] = x - x0;
        p[1][0] = y - y0;
        // apply permutation
        double[][] pp = multiply(matrix, p);
        // translate back
        double[] result = new double[2];
        result[0] = pp[0][0] + x0;
        result[1] = pp[1][0] + y0;
        return result;
    }

    private static double findMax(double a, double b, double c, double d) {
        double result = Math.max(a, b);
        result = Math.max(result, c);
        result = Math.max(result, d);
        return result;
    }

    private static double findMin(double a, double b, double c, double d) {
        double result = Math.min(a, b);
        result = Math.min(result, c);
        result = Math.min(result, d);
        return result;
    }

    /**
     * Apply color change on an image.
     *
     * @param color
     * @param imageData
     */
    public static ImageData changeImageColor(Color color,
            ImageData originalImageData) {
        if (color == null || originalImageData == null
                || color.getRGB().equals(new RGB(0, 0, 0)))
            return originalImageData;
        ImageData imageData = ImageUtils.convertToGrayscale(originalImageData);

        int luminance = (int) Math.round((0.299 * color.getRed())
                + (0.587 * color.getGreen()) + (0.114 * color.getBlue()));

        // find min/max/average values ignoring white & transparent
        int sum = 0, count = 0, min = 0, max = 0;
        int[] lineData = new int[imageData.width];
        PaletteData palette = imageData.palette;
        for (int y = 0; y < imageData.height; y++) {
            imageData.getPixels(0, y, imageData.width, lineData, 0);

            // Analyze each pixel value in the line
            for (int x = 0; x < lineData.length; x++) {
                int pixelValue = lineData[x];

                // Do not set transparent pixel
                if (lineData[x] != imageData.transparentPixel) {
                    // Get pixel color value if not using direct palette
                    if (!palette.isDirect) {
                        pixelValue = palette.getPixel(palette.colors[lineData[x]]);
                    }
                    RGB current = palette.getRGB(pixelValue);
                    if (current.blue == current.green
                            && current.blue == current.red
                            && current.blue < 255) {
                        min = Math.min(current.red, min);
                        max = Math.max(current.red, max);
                        sum += current.red;
                        count++;
                    }
                }
            }
        }
        if (count == 0)
            return imageData;
        // we need to adjust the gradient depending on the luminance unless
        // bright colors will appear in white
        int gradientWidth = 512, gradientHeight = 10;
        int average = (int) sum / count;
        int start = average - 32;
        if (start < 0) start = 0;
        int end = max + luminance;
        if (end > gradientWidth - 1) end = gradientWidth - 1;

        // create the color gradient
        java.awt.Color color1 = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
        java.awt.Color color2 = new java.awt.Color(250, 250, 255);

        BufferedImage gradient = new BufferedImage(gradientWidth, gradientHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = gradient.createGraphics();
        g2.setPaint(color1);
        g2.fill(new java.awt.Rectangle(0, 0, start, gradientHeight));
        g2.setPaint(new GradientPaint(start, 0, color1, end, gradientHeight, color2, false));
        g2.fill(new java.awt.Rectangle(start, 0, end - start, gradientHeight));
        g2.setPaint(color2);
        g2.fill(new java.awt.Rectangle(end, 0, gradientWidth - end, gradientHeight));

        for (int y = 0; y < imageData.height; y++) {
            imageData.getPixels(0, y, imageData.width, lineData, 0);

            // Analyze each pixel value in the line
            for (int x = 0; x < lineData.length; x++) {
                int pixelValue = lineData[x];

                // Do not set transparent pixel
                if (lineData[x] != imageData.transparentPixel) {
                    // Get pixel color value if not using direct palette
                    if (!palette.isDirect) {
                        pixelValue = palette.getPixel(palette.colors[lineData[x]]);
                    }
                    RGB current = palette.getRGB(pixelValue);
                    if (current.blue == current.green
                            && current.blue == current.red
                            && current.blue < 255) {
                        int gradientRGB = gradient.getRGB(current.red, 0);
                        java.awt.Color gradientColor = new java.awt.Color(gradientRGB);
                        RGB degraded = new RGB(gradientColor.getRed(), gradientColor.getGreen(), gradientColor.getBlue());
                        if (palette.isDirect) {
                            int appliedColor = palette.getPixel(degraded);
                            imageData.setPixel(x, y, appliedColor);
                        } else {
                            palette.colors[lineData[x]] = degraded;
                        }
                    }
                }
            }
        }
        return imageData;
    }

    public static ImageData changeImageColor2(Color color,
            ImageData originalImageData) {
        if (color == null || originalImageData == null
                || color.getRGB().equals(new RGB(0, 0, 0)))
            return originalImageData;
        ImageData imageData = ImageUtils.convertToGrayscale(originalImageData);
        new Colorizer().doColorize(imageData);
        return imageData;
    }

    /**
     * Crop the given rectangle with the given insets.
     *
     * @param rect rectangle to crop.
     * @param insets
     */
    public static void crop(Rectangle rect, Insets insets) {
        if (insets == null)
            return;
        rect.setX(rect.x + insets.left);
        rect.setY(rect.y + insets.top);
        rect.setWidth(rect.width - insets.left - insets.right);
        rect.setHeight(rect.height - insets.top - insets.bottom);
    }

    /**
     * Convert a colored image to grayscale image using average method.
     */
    public static ImageData convertToGrayscale(ImageData originalImageData) {
        ImageData imageData = (ImageData) originalImageData.clone();
        PaletteData palette = imageData.palette;
        if (palette.isDirect) {
            int[] lineData = new int[imageData.width];
            for (int y = 0; y < imageData.height; y++) {
                imageData.getPixels(0, y, imageData.width, lineData, 0);
                // Analyze each pixel value in the line
                for (int x = 0; x < lineData.length; x++) {
                    RGB rgb = palette.getRGB(lineData[x]);
                    // int gray = (int) Math.round((rgb.red + rgb.green + rgb.blue) / 3);
                    int gray = (int) Math.round((0.21 * rgb.red) + (0.72 * rgb.green) + (0.07 * rgb.blue));
                    int newColor = palette.getPixel(new RGB(gray, gray, gray));
                    imageData.setPixel(x, y, newColor);
                }
            }
            if (imageData.transparentPixel != -1) {
                RGB rgb = palette.getRGB(imageData.transparentPixel);
                // int gray = (int) Math.round((rgb.red + rgb.green + rgb.blue) / 3);
                int gray = (int) Math.round((0.21 * rgb.red) + (0.72 * rgb.green) + (0.07 * rgb.blue));
                int newColor = palette.getPixel(new RGB(gray, gray, gray));
                imageData.transparentPixel = newColor;
            }
        } else {
            for (int i = 0; i < palette.colors.length; i++) {
                RGB rgb = palette.colors[i];
                // int gray = (int) Math.round((rgb.red + rgb.green + rgb.blue) / 3);
                int gray = (int) Math.round((0.21 * rgb.red) + (0.72 * rgb.green) + (0.07 * rgb.blue));
                palette.colors[i] = new RGB(gray, gray, gray);
            }
        }
        return imageData;
    }

    // ************************************************************
    // Old method to change image color
    // ************************************************************

    public static void oldChangeImageColor(Color color, ImageData originalImageData) {
        if (color == null || originalImageData == null
                || color.getRGB().equals(new RGB(0, 0, 0)))
            return;
        ImageData imageData = ImageUtils.convertToGrayscale(originalImageData);
        int newColor = 0;
        int[] lineData = new int[imageData.width];
        // Calculate pixel value (integer)
        if (imageData.palette.isDirect) {
            RGB rgb = color.getRGB();

            int redMask = imageData.palette.redMask;
            int blueMask = imageData.palette.blueMask;
            int greenMask = imageData.palette.greenMask;

            int redShift = imageData.palette.redShift;
            int greenShift = imageData.palette.greenShift;
            int blueShift = imageData.palette.blueShift;

            newColor |= (redShift < 0 ? rgb.red << -redShift : rgb.red >>> redShift) & redMask;
            newColor |= (greenShift < 0 ? rgb.green << -greenShift : rgb.green >>> greenShift) & greenMask;
            newColor |= (blueShift < 0 ? rgb.blue << -blueShift : rgb.blue >>> blueShift) & blueMask;
        } else {
            // Add new color in PaletteData colors
            int paletteLength = imageData.palette.colors.length;
            newColor = (imageData.transparentPixel + 1) % paletteLength;
            imageData.palette.colors[newColor] = color.getRGB();
        }
        for (int y = 0; y < imageData.height; y++) {
            imageData.getPixels(0, y, imageData.width, lineData, 0);

            // Analyze each pixel value in the line
            for (int x = 0; x < lineData.length; x++) {
                // Do not set transparent pixel && change only black pixel
                int pixelValue = lineData[x];
                if (!imageData.palette.isDirect) {
                    pixelValue = imageData.palette.getPixel(imageData.palette.colors[lineData[x]]);
                }
                if (lineData[x] != imageData.transparentPixel
                        && isShadeOfGray(pixelValue, imageData.palette)) {
                    int appliedColor = applyShade(pixelValue, newColor, imageData.palette);
                    if (imageData.alphaData == null) {
                        // appliedColor = applyShade(pixelValue, newColor, imageData.palette);
                    }
                    imageData.setPixel(x, y, appliedColor);
                }
            }
        }
    }

    private static boolean isShadeOfGray(int pixel, PaletteData palette) {
        int r = (pixel & palette.redMask) >> palette.redShift;
        int g = (pixel & palette.greenMask) >> palette.greenShift;
        int b = (pixel & palette.blueMask) >> palette.blueShift;
        return (r == g) && (g == b);
    }

    private static int applyShade(int shadedPixel, int pixelToShade, PaletteData palette) {
        int newColor = 0
        ,redMask = palette.redMask
        ,blueMask = palette.blueMask
        ,greenMask = palette.greenMask
        ,redShift = palette.redShift
        ,greenShift = palette.greenShift
        ,blueShift = palette.blueShift;

        float ratioR = ((shadedPixel & redMask) >> redShift) / 255f;
        float ratioG = ((shadedPixel & greenMask) >> greenShift) / 255f;
        float ratioB = ((shadedPixel & blueMask) >> blueShift) / 255f;

        int r = (pixelToShade & redMask) >> redShift;
        int g = (pixelToShade & greenMask) >> greenShift;
        int b = (pixelToShade & blueMask) >> blueShift;
        r = (int) Math.round(r * ratioR);
        g = (int) Math.round(g * ratioG);
        b = (int) Math.round(b * ratioB);
        if (r < 0) r = 0; else if (r > 255) r = 255;
        if (g < 0) g = 0; else if (g > 255) g = 255;
        if (b < 0) b = 0; else if (b > 255) b = 255;

        newColor |= (redShift < 0 ? r << -redShift : r >>> redShift) & redMask;
        newColor |= (greenShift < 0 ? g << -greenShift : g >>> greenShift) & greenMask;
        newColor |= (blueShift < 0 ? b << -blueShift : b >>> blueShift) & blueMask;
        return newColor;
    }

    // ************************************************************
    // Another old method to change image color
    // ************************************************************

    public static ImageData oldChangeImageColor2(Color color, ImageData originalImageData) {
        if (color == null || originalImageData == null
                || color.getRGB().equals(new RGB(0, 0, 0)))
            return originalImageData;
        ImageData imageData = ImageUtils.convertToGrayscale(originalImageData);

        float[] hsb = new float[3];
        java.awt.Color.RGBtoHSB(color.getRGB().red, color.getRGB().green, color.getRGB().blue, hsb);
        int[] lineData = new int[imageData.width];
        PaletteData palette = imageData.palette;

        for (int y = 0; y < imageData.height; y++) {
            imageData.getPixels(0, y, imageData.width, lineData, 0);

            // Analyze each pixel value in the line
            for (int x = 0; x < lineData.length; x++) {
                int pixelValue = lineData[x];

                // Do not set transparent pixel
                if (lineData[x] != imageData.transparentPixel) {
                    // Get pixel color value if not using direct palette
                    if (!palette.isDirect) {
                        pixelValue = palette.getPixel(palette.colors[lineData[x]]);
                    }
                    RGB current = palette.getRGB(pixelValue);
                    if (current.blue == current.green
                            && current.blue == current.red
                            && current.blue < 255)
                    {
                        float[] pixelHSB = new float[3];
                        java.awt.Color.RGBtoHSB(current.red, current.green, current.blue, pixelHSB);
                        int awtRGB = java.awt.Color.HSBtoRGB(hsb[0], hsb[1], 1 - pixelHSB[2]);
                        java.awt.Color awtColor = new java.awt.Color(awtRGB);
                        RGB degraded = new RGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
                        if (palette.isDirect) {
                            int appliedColor = palette.getPixel(degraded);
                            imageData.setPixel(x, y, appliedColor);
                        } else {
                            palette.colors[lineData[x]] = degraded;
                        }
                    }
                }
            }
        }
        return imageData;
    }
}
