/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;
import org.epics.util.stats.Range;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;
/**
 *
 * @author carcassi, sjdallst, asbarber, jkfeng
 */
public class GraphBuffer {
    
    private final BufferedImage image;
    private final Graphics2D g;
    private final byte[] pixels;
    private final boolean hasAlphaChannel;
    private final int width, height;
    
    private GraphBuffer(BufferedImage image){
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        pixels = ((DataBufferByte)this.image.getRaster().getDataBuffer()).getData();
        hasAlphaChannel = image.getAlphaRaster() != null;
        g = image.createGraphics();
    }
    
    public GraphBuffer(int width, int height) {
        this(new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR));
    }
    
    public GraphBuffer(Graph2DRenderer<?> renderer) {
        this(renderer.getImageWidth(), renderer.getImageHeight());
    }
    
    public void setPixel(int x, int y, int color){
        if(hasAlphaChannel){
            pixels[y*image.getWidth()*4 + x*4 + 3] = (byte)(color >> 24 & 0xFF);
            pixels[y*image.getWidth()*4 + x*4 + 0] = (byte)(color >> 0 & 0xFF);
            pixels[y*image.getWidth()*4 + x*4 + 1] = (byte)(color >> 8 & 0xFF);
            pixels[y*image.getWidth()*4 + x*4 + 2] = (byte)(color >> 16 & 0xFF);
        }
        else{
            pixels[y*image.getWidth()*4 + x*4 + 0] = (byte)(color >> 0 & 0xFF);
            pixels[y*image.getWidth()*4 + x*4 + 1] = (byte)(color >> 8 & 0xFF);
            pixels[y*image.getWidth()*4 + x*4 + 2] = (byte)(color >> 16 & 0xFF);
        }
    }
    
    public BufferedImage getImage(){
        return image;
    }
    
    public Graphics2D getGraphicsContext(){
        return g;
    }
    
    public void drawDataImage(int xStartPoint, int yStartPoint,
            int[] xPointToDataMap, int[] yPointToDataMap,
            Cell2DDataset data, NumberColorMapInstance colorMap) {
        int previousYData = -1;
        for (int yOffset = 0; yOffset < yPointToDataMap.length; yOffset++) {
            int yData = yPointToDataMap[yOffset];
            if (yData != previousYData) {
                for (int xOffset = 0; xOffset < xPointToDataMap.length; xOffset++) {
                    int xData = xPointToDataMap[xOffset];
                    int rgb = colorMap.colorFor(data.getValue(xData, yData));
                    if(hasAlphaChannel){
                        pixels[(yStartPoint + yOffset)*width*4 + 4*(xStartPoint + xOffset) + 0] = (byte)(rgb >> 24 & 0xFF);
                        pixels[(yStartPoint + yOffset)*width*4 + 4*(xStartPoint + xOffset) + 1] = (byte)(rgb & 0xFF);
                        pixels[(yStartPoint + yOffset)*width*4 + 4*(xStartPoint + xOffset) + 2] = (byte)(rgb >> 8 & 0xFF);
                        pixels[(yStartPoint + yOffset)*width*4 + 4*(xStartPoint + xOffset) + 3] = (byte)(rgb >> 16 & 0xFF);
                    } else {
                        pixels[(yStartPoint + yOffset)*width*3 + 3*(xStartPoint + xOffset) + 0] = (byte)(rgb & 0xFF);
                        pixels[(yStartPoint + yOffset)*width*3 + 3*(xStartPoint + xOffset) + 1] = (byte)((rgb >> 8 & 0xFF) );
                        pixels[(yStartPoint + yOffset)*width*3 + 3*(xStartPoint + xOffset) + 2] = (byte)((rgb >> 16 & 0xFF));
                    }
                }
            } else {
                if (hasAlphaChannel) {
                    System.arraycopy(pixels, (yStartPoint + yOffset - 1)*width*4 + 4*xStartPoint,
                            pixels, (yStartPoint + yOffset)*width*4 + 4*xStartPoint, xPointToDataMap.length*4);
                } else {
                    System.arraycopy(pixels, (yStartPoint + yOffset - 1)*width*3 + 3*xStartPoint,
                            pixels, (yStartPoint + yOffset)*width*3 + 3*xStartPoint, xPointToDataMap.length*3);
                }
            }
            previousYData = yData;
        }
    }
    
    private double xLeftValue;
    private double xRightValue;
    private double xLeftPixel;
    private double xRightPixel;
    private ValueScale xValueScale;

    /**
     * Sets the scaling data for the x axis assuming values are going
     * to represent cells. The minimum value is going to be positioned at the
     * left of the xMinPixel while the maximum value is going to be position
     * at the right of the xMaxPixel.
     * 
     * @param range the range to be displayed
     * @param xMinPixel the pixel corresponding to the minimum
     * @param xMaxPixel the pixel corresponding to the maximum
     * @param xValueScale the scale used to transform values to pixel
     */
    public void setXScaleAsCell(Range range, int xMinPixel, int xMaxPixel, ValueScale xValueScale) {
        xLeftValue = range.getMinimum().doubleValue();
        xRightValue = range.getMaximum().doubleValue();
        xLeftPixel = xMinPixel;
        xRightPixel = xMaxPixel + 1;
        this.xValueScale = xValueScale;
    }

    /**
     * Sets the scaling data for the x axis assuming values are going
     * to represent points. The minimum value is going to be positioned in the
     * center of the xMinPixel while the maximum value is going to be position
     * in the middle of the xMaxPixel.
     * 
     * @param range the range to be displayed
     * @param xMinPixel the pixel corresponding to the minimum
     * @param xMaxPixel the pixel corresponding to the maximum
     * @param xValueScale the scale used to transform values to pixel
     */
    public void setXScaleAsPoint(Range range, int xMinPixel, int xMaxPixel, ValueScale xValueScale) {
        xLeftValue = range.getMinimum().doubleValue();
        xRightValue = range.getMaximum().doubleValue();
        xLeftPixel = xMinPixel + 0.5;
        xRightPixel = xMaxPixel + 0.5;
        this.xValueScale = xValueScale;
    }

    /**
     * Converts the given value to the pixel position.
     * 
     * @param value the value
     * @return the pixel where the value should be mapped
     */
    public int xValueToPixel(double value) {
        return (int) xValueScale.scaleValue(value, xLeftValue, xRightValue, xLeftPixel, xRightPixel);
    }

    /**
     * Converts the left side of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double xPixelLeftToValue(int pixelValue) {
        return xValueScale.invScaleValue(pixelValue, xLeftValue, xRightValue, xLeftPixel, xRightPixel);
    }

    /**
     * Converts the right side of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double xPixelRightToValue(int pixelValue) {
        return xValueScale.invScaleValue(pixelValue + 1, xLeftValue, xRightValue, xLeftPixel, xRightPixel);
    }

    /**
     * Converts the center of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double xPixelCenterToValue(int pixelValue) {
        return xValueScale.invScaleValue(pixelValue + 0.5, xLeftValue, xRightValue, xLeftPixel, xRightPixel);
    }
    
    private double yTopValue;
    private double yBottomValue;
    private double yTopPixel;
    private double yBottomPixel;
    private ValueScale yValueScale;


    /**
     * Sets the scaling data for the y axis assuming values are going
     * to represent cells. The minimum value is going to be positioned at the
     * bottom of the yMinPixel while the maximum value is going to be position
     * at the top of the yMaxPixel.
     * 
     * @param range the range to be displayed
     * @param yMinPixel the pixel corresponding to the minimum
     * @param yMaxPixel the pixel corresponding to the maximum
     * @param yValueScale the scale used to transform values to pixel
     */
    public void setYScaleAsCell(Range range, int yMinPixel, int yMaxPixel, ValueScale yValueScale) {
        yTopValue = range.getMaximum().doubleValue();
        yBottomValue = range.getMinimum().doubleValue();
        yTopPixel = yMaxPixel - 1;
        yBottomPixel = yMinPixel;
        this.yValueScale = yValueScale;
    }

    /**
     * Sets the scaling data for the y axis assuming values are going
     * to represent points. The minimum value is going to be positioned in the
     * center of the yMinPixel while the maximum value is going to be position
     * in the center of the yMaxPixel.
     * 
     * @param range the range to be displayed
     * @param yMinPixel the pixel corresponding to the minimum
     * @param yMaxPixel the pixel corresponding to the maximum
     * @param yValueScale the scale used to transform values to pixel
     */
    public void setYScaleAsPoint(Range range, int yMinPixel, int yMaxPixel, ValueScale yValueScale) {
        yTopValue = range.getMaximum().doubleValue();
        yBottomValue = range.getMinimum().doubleValue();
        yTopPixel = yMaxPixel - 0.5;
        yBottomPixel = yMinPixel - 0.5;
        this.yValueScale = yValueScale;
    }

    /**
     * Converts the given value to the pixel position.
     * 
     * @param value the value
     * @return the pixel where the value should be mapped
     */
    public int yValueToPixel(double value) {
        return (int) Math.ceil(yValueScale.scaleValue(value, yBottomValue, yTopValue, yBottomPixel, yTopPixel));
    }
    
    /**
     * Converts the top side of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double yPixelTopToValue(int pixelValue) {
        return yValueScale.invScaleValue(pixelValue - 1, yBottomValue, yTopValue, yBottomPixel, yTopPixel);
    }
    
    /**
     * Converts the center of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double yPixelCenterToValue(int pixelValue) {
        return yValueScale.invScaleValue(pixelValue - 0.5, yBottomValue, yTopValue, yBottomPixel, yTopPixel);
    }
    
    /**
     * Converts the bottom side of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double yPixelBottomToValue(int pixelValue) {
        return yValueScale.invScaleValue(pixelValue, yBottomValue, yTopValue, yBottomPixel, yTopPixel);
    }

    void drawBackground(Color color) {
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }
    
    private static final int MIN = 0;
    private static final int MAX = 1;

    void drawBottomLabels(List<String> labels, ListInt valuePixelPositions, Color labelColor, Font labelFont, int leftPixel, int rightPixel, int topPixel) {
        // Draw X labels
        if (labels != null && !labels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {leftPixel, rightPixel};
            drawLineLabel(g, metrics, labels.get(0), valuePixelPositions.getInt(0),
                drawRange, topPixel, true, false);
            drawLineLabel(g, metrics, labels.get(labels.size() - 1),
                    valuePixelPositions.getInt(labels.size() - 1),
                drawRange, topPixel, false, false);
            
            for (int i = 1; i < labels.size() - 1; i++) {
                drawLineLabel(g, metrics, labels.get(i), valuePixelPositions.getInt(i),
                    drawRange, topPixel, true, false);
            }
        }
    }
    
    private static void drawLineLabel(Graphics2D graphics, FontMetrics metrics, String text, int xCenter, int[] drawRange, int yTop, boolean updateMin, boolean centeredOnly) {
        // If the center is not in the range, don't draw anything
        if (drawRange[MAX] < xCenter || drawRange[MIN] > xCenter)
            return;
        
        // If there is no space, don't draw anything
        if (drawRange[MAX] - drawRange[MIN] < metrics.getHeight())
            return;
        
        Java2DStringUtilities.Alignment alignment = Java2DStringUtilities.Alignment.TOP;
        int targetX = xCenter;
        int halfWidth = metrics.stringWidth(text) / 2;
        if (xCenter < drawRange[MIN] + halfWidth) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.TOP_LEFT;
            targetX = drawRange[MIN];
        } else if (xCenter > drawRange[MAX] - halfWidth) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.TOP_RIGHT;
            targetX = drawRange[MAX];
        }

        Java2DStringUtilities.drawString(graphics, alignment, targetX, yTop, text);
        
        if (updateMin) {
            drawRange[MIN] = targetX + metrics.getHeight();
        } else {
            drawRange[MAX] = targetX - metrics.getHeight();
        }
    }

    void drawLeftLabels(List<String> labels, ListInt valuePixelPositions, Color labelColor, Font labelFont, int bottomPixel, int topPixel, int leftPixel) {
        // Draw Y labels
        if (labels != null && !labels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {topPixel, bottomPixel};
            drawColumnLabel(g, metrics, labels.get(0), valuePixelPositions.getInt(0),
                drawRange, leftPixel, true, false);
            drawColumnLabel(g, metrics, labels.get(labels.size() - 1), valuePixelPositions.getInt(labels.size() - 1),
                drawRange, leftPixel, false, false);
            
            for (int i = 1; i < labels.size() - 1; i++) {
                drawColumnLabel(g, metrics, labels.get(i), valuePixelPositions.getInt(i),
                    drawRange, leftPixel, true, false);
            }
        }
    }
    
    private static void drawColumnLabel(Graphics2D graphics, FontMetrics metrics, String text, int yCenter, int[] drawRange, int xRight, boolean updateMin, boolean centeredOnly) {
        // If the center is not in the range, don't draw anything
        if (drawRange[MAX] < yCenter || drawRange[MIN] > yCenter)
            return;
        
        // If there is no space, don't draw anything
        if (drawRange[MAX] - drawRange[MIN] < metrics.getHeight())
            return;
        
        Java2DStringUtilities.Alignment alignment = Java2DStringUtilities.Alignment.RIGHT;
        int targetY = yCenter;
        int halfHeight = metrics.getAscent() / 2;
        if (yCenter < drawRange[MIN] + halfHeight) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.TOP_RIGHT;
            targetY = drawRange[MIN];
        } else if (yCenter > drawRange[MAX] - halfHeight) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.BOTTOM_RIGHT;
            targetY = drawRange[MAX];
        }

        Java2DStringUtilities.drawString(graphics, alignment, xRight, targetY, text);
        
        if (updateMin) {
            drawRange[MAX] = targetY - metrics.getHeight();
        } else {
            drawRange[MIN] = targetY + metrics.getHeight();
        }
    }
    
    void drawHorizontalReferenceLines(ListInt referencePixels, Color lineColor, int graphLeftPixel, int graphRightPixel) {
        g.setColor(lineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        for (int i = 0; i < referencePixels.size(); i++) {
            g.drawLine(graphLeftPixel, referencePixels.getInt(i), graphRightPixel, referencePixels.getInt(i));
        }
    }
    
    void drawVerticalReferenceLines(ListInt referencePixels, Color lineColor, int graphBottomPixel, int graphTopPixel) {
        g.setColor(lineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        for (int i = 0; i < referencePixels.size(); i++) {
            g.drawLine(referencePixels.getInt(i), graphTopPixel, referencePixels.getInt(i), graphBottomPixel);
        }
    }
}
