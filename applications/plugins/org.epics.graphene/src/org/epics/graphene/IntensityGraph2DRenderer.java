/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Color;
import org.epics.util.stats.Range;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.List;
import org.epics.util.array.ListNumbers;
import org.epics.util.array.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.epics.util.stats.Ranges;

/**
 * A renderer for intensity graph (also known as heat graph), which visualizes
 * the value of a 2D field using a color map.
 *
 * @author carcassi, sjdallst, asbarber, jkfeng
 */
public class IntensityGraph2DRenderer extends Graph2DRenderer<IntensityGraph2DRendererUpdate> {
    
    /**
     * Default color map: JET.
     */
    public static NumberColorMap DEFAULT_COLOR_MAP = NumberColorMaps.JET;
    
    /**
     * Default draw legend: false.
     */
    public static boolean DEFAULT_DRAW_LEGEND = false;
    
    //Colors to be used when drawing the graph, gives a color based on a given value and the range of data.
    private NumberColorMapInstance colorMapInstance;
    private Range optimizedRange;
    private boolean optimizeColorScheme = true;
    
    /**
     *Uses constructor specified in super class (Graph2DRenderer)
     * @param imageWidth should be equal to the width of the bufferedImage.
     * @param imageHeight should be equal to the height of the bufferedImage.
     */
    public IntensityGraph2DRenderer(int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
        super.update(new IntensityGraph2DRendererUpdate());
    }

    /**
     *Default Constructor: makes an IntensityGraph2DRenderer of width 300 and height 200.
     */
    public IntensityGraph2DRenderer() {
        this(300, 200);
    }
      
    @Override
    public void update(IntensityGraph2DRendererUpdate update) {
        super.update(update);
        if(update.getDrawLegend() != null){
            drawLegend = update.getDrawLegend();
        }
        if(update.getColorMap() != null){
            colorMap = update.getColorMap();
            colorMapInstance = null;
        }
        if(update.getZLabelMargin() != null){
            zLabelMargin = update.getZLabelMargin();
        }
        if(update.getLegendWidth() != null){
            legendWidth = update.getLegendWidth();
        }
        if(update.getGraphAreaToLegendMargin() != null){
            graphAreaToLegendMargin = update.getGraphAreaToLegendMargin();
        }
        if(update.getRightMargin() != null){
            originalRightMargin = update.getRightMargin();
        }
        if(update.getXPixelSelectionRange()!= null){
            xPixelSelectionRange = update.getXPixelSelectionRange();
        }
        if(update.getYPixelSelectionRange()!= null){
            yPixelSelectionRange = update.getYPixelSelectionRange();
        }
    }
    
    /*legendWidth,legendMarginToGraph,graphAreaToLegendMargin, and zLabelMargin are all lengths, in terms of pixels.
    legendMarginToGraph corresponds to the space between the original graph and the legend.
    graphAreaToLegendMargin -> the space between the legend labels and the edge of the picture.*/
    private int legendWidth = 10,
                legendMarginToGraph = 10,
                graphAreaToLegendMargin = 3;
    protected int zLabelMargin = 3;
    private boolean drawLegend = DEFAULT_DRAW_LEGEND;
    private Range zRange;
    private Range zAggregatedRange;
    private Range zPlotRange;
    private AxisRangeInstance zAxisRange = AxisRanges.display().createInstance();
    private ValueScale zValueScale = ValueScales.linearScale();
    protected ListInt zReferenceCoords;
    protected ListDouble zReferenceValues;
    protected List<String> zReferenceLabels;
    private int zLabelMaxWidth;
    private int originalRightMargin = super.rightMargin;
    public boolean useColorArray = false; 
    
    private NumberColorMap colorMap = DEFAULT_COLOR_MAP;
    
    private Range xPixelSelectionRange;
    private Range yPixelSelectionRange;
    
    private Range xValueSelectionRange;
    private Range yValueSelectionRange;
    
    private Range xIndexSelectionRange;
    private Range yIndexSelectionRange;
    
    /**
     *Draws an intensity graph in the given graphics context, using the given data.
     * All drawing is done within the bounds specified either at initialization or at update.
     *  Different colorSchemes may be specified using the IntensityGraph2DRendererUpdate class, in combination with the update function. 
     * @param graphBuffer Contains <code>imageBuffer</code> and <code>Graphics2D</code> objects used to perform drawing functions within draw.
     * @param data can not be null
     */
    public void draw(GraphBuffer graphBuffer, Cell2DDataset data) {
        //Use super class to draw basics of graph.
        this.g = graphBuffer.getGraphicsContext();
        GraphAreaData area = new GraphAreaData();
        BufferedImage image = graphBuffer.getImage();
        calculateRanges(data.getXRange(), data.getXRange(), data.getYRange(), data.getYRange());
        area.setGraphBuffer(graphBuffer);
        graphBuffer.drawBackground(backgroundColor);
        calculateZRange(data.getStatistics(), data.getDisplayRange());
        
        // TODO: the calculation for leaving space for the legend is somewhat hacked
        // Instead of actually having a nice calculation, we increase the margin
        // to the right before using the standard calculateGraphArea
        int areaRightPixel;
        if(drawLegend){
            calculateZLabels();
            areaRightPixel = getImageWidth() - 1 - (graphAreaToLegendMargin + legendWidth + zLabelMargin + zLabelMaxWidth + rightMargin);
        } else {
            areaRightPixel = getImageWidth() - 1 - rightMargin;
        }
        area.setGraphArea(leftMargin, getImageHeight() - 1 - bottomMargin, areaRightPixel, topMargin);
        area.setGraphPadding(leftAreaMargin, bottomAreaMargin, rightAreaMargin, topAreaMargin);
        area.setLabelMargin(xLabelMargin, yLabelMargin);
        area.setRanges(getXPlotRange(), xValueScale, getYPlotRange(), yValueScale);
        area.prepareLabels(labelFont, labelColor);
        area.prepareGraphArea(true, referenceLineColor);
        area.drawGraphArea();
        
        /*Wait to calculate the coordinates of the legend labels till after yPlotCoordRange is calculated.
        Allows for the use of yPlotCoordEnd/start in calculations.*/
        if(drawLegend){
            if (zReferenceValues != null) {
                int[] zRefCoords = new int[zReferenceValues.size()];
                if(zRefCoords.length == 1){
                    zRefCoords[0] = area.areaTop;
                }
                else{
                    for (int i = 0; i < zRefCoords.length; i++) {
                        zRefCoords[i] = (int) scaledZ(zReferenceValues.getDouble(i), area.graphBottom, area.graphTop);
                    }
                }
                zReferenceCoords = new ArrayInt(zRefCoords);
            }
        }
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        
        //Set color scheme
        if (!optimizeColorScheme){
            colorMapInstance = colorMap.createInstance(zPlotRange);
        } else {
            if (colorMapInstance == null || !Ranges.equals(optimizedRange, zRange)) {
                colorMapInstance = colorMap.createInstance(zPlotRange);
                colorMapInstance = NumberColorMaps.optimize(colorMapInstance, zPlotRange);
                optimizedRange = zPlotRange;
            }
        }


        double xStartGraph = super.xPlotCoordStart;
        double yEndGraph = area.graphBottom;

        double yHeightTotal = area.graphBottom - area.graphTop + 1;
        
        int startX = area.graphLeft;//(int) Math.floor(xPlotCoordStart);
        int startY = area.graphTop;//(int) Math.floor(yPlotCoordStart);
        int endX = area.graphRight;//(int) Math.ceil(xPlotCoordEnd);
        int endY = area.graphBottom;//(int) Math.ceil(yPlotCoordEnd);
        PointToDataMap xPointToDataMap = createXPointToDataMap(startX, endX, graphBuffer, data.getXBoundaries()); //createPointToDataMap(startX, endX+1, getXPlotRange(), data.getXBoundaries(), false);
        PointToDataMap yPointToDataMap = createYPointToDataMap(startY, endY, graphBuffer, data.getYBoundaries());//getYPlotRange(), data.getYBoundaries(), true);
        graphBuffer.drawDataImage(xPointToDataMap.startPoint, yPointToDataMap.startPoint, xPointToDataMap.pointToDataMap, yPointToDataMap.pointToDataMap, data, colorMapInstance);
        
        if(drawLegend && legendWidth>0){
            /*dataList is made by splitting the aggregated range of the z(color) data into a list of the
            same length as the the height of the graph in pixels.*/
            ListNumber dataList = ListNumbers.linearListFromRange(zPlotRange.getMinimum().doubleValue(),zPlotRange.getMaximum().doubleValue(),(int)yHeightTotal);
            //legendData is a Cell2DDataset representation of dataList.
            Cell2DDataset legendData = Cell2DDatasets.linearRange(dataList, Ranges.range(0, 1), 1, Ranges.range(0, (int)yHeightTotal), (int)yHeightTotal);
            int xLegendStart = getImageWidth() - originalRightMargin - zLabelMaxWidth - zLabelMargin - legendWidth;
            drawRectanglesArray(g, legendData, xLegendStart, yEndGraph, legendWidth, yHeightTotal, 1, legendWidth, image);
            graphBuffer.drawLeftLabels(zReferenceLabels, zReferenceCoords, labelColor, labelFont, area.areaBottom, area.areaTop, getImageWidth() - originalRightMargin - 1);
        }
        
        // Calculate selection and draw rectangle
        if (xPixelSelectionRange != null && yPixelSelectionRange != null) {
            // Calculate the selection pixel box
            int selectionLeftPixel = xPixelSelectionRange.getMinimum().intValue();
            int selectionRightPixel = xPixelSelectionRange.getMaximum().intValue();
            int selectionTopPixel = yPixelSelectionRange.getMinimum().intValue();
            int selectionBottomPixel = yPixelSelectionRange.getMaximum().intValue();
            
            // Calculate the selection value range
            double selectionLeftValue = graphBuffer.xPixelLeftToValue(selectionLeftPixel);
            double selectionRightValue = graphBuffer.xPixelRightToValue(selectionRightPixel);
            double selectionTopValue = graphBuffer.yPixelTopToValue(selectionTopPixel);
            double selectionBottomValue = graphBuffer.yPixelBottomToValue(selectionBottomPixel);
            xValueSelectionRange = Ranges.range(selectionLeftValue, selectionRightValue);
            yValueSelectionRange = Ranges.range(selectionBottomValue, selectionTopValue);
            
            // Calcualte the selection data index boundaries
            int xLeftOffset = selectionLeftPixel - xPointToDataMap.startPoint;
            int xRightOffset = selectionRightPixel - xPointToDataMap.startPoint;
            if ((xLeftOffset < 0 && xRightOffset <0) 
                    || (xLeftOffset >= xPointToDataMap.pointToDataMap.length && xRightOffset >= xPointToDataMap.pointToDataMap.length)) {
                // Selection range is outside range
                xIndexSelectionRange = null;
            } else {
                xLeftOffset = Math.max(0, xLeftOffset);
                xRightOffset = Math.min(xPointToDataMap.pointToDataMap.length, xRightOffset);
                xIndexSelectionRange = Ranges.range(xPointToDataMap.pointToDataMap[xLeftOffset], xPointToDataMap.pointToDataMap[xRightOffset]);
            }
            
            int yBottomOffset = selectionBottomPixel - yPointToDataMap.startPoint;
            int yTopOffset = selectionTopPixel - yPointToDataMap.startPoint;
            if ((yBottomOffset < 0 && yTopOffset <0) 
                    || (yBottomOffset >= yPointToDataMap.pointToDataMap.length && yTopOffset >= yPointToDataMap.pointToDataMap.length)) {
                // Selection range is outside range
                yIndexSelectionRange = null;
            } else {
                yTopOffset = Math.max(0, yTopOffset);
                yBottomOffset = Math.min(xPointToDataMap.pointToDataMap.length, yBottomOffset);
                yIndexSelectionRange = Ranges.range(yPointToDataMap.pointToDataMap[yBottomOffset], yPointToDataMap.pointToDataMap[yTopOffset]);
            }
            
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g.setColor(Color.BLACK);
            graphBuffer.getGraphicsContext().drawRect(selectionLeftPixel, selectionTopPixel,
                    selectionRightPixel - selectionLeftPixel, selectionBottomPixel - selectionTopPixel);
        }
    }
    
    @Override
    public IntensityGraph2DRendererUpdate newUpdate() {
        return new IntensityGraph2DRendererUpdate();
    }
    
    private class PointToDataMap {
        public int[] pointToDataMap;
        public int startPoint;
    }
    
    PointToDataMap createXPointToDataMap(int leftPixel, int rightPixel, GraphBuffer buffer, ListNumber xBoundaries) {
        // Check any data in range
        int minValuePixel = buffer.xValueToPixel(xBoundaries.getDouble(0));
        int maxValuePixel = buffer.xValueToPixel(xBoundaries.getDouble(xBoundaries.size() - 1));
        if ( minValuePixel > rightPixel ||
                maxValuePixel < leftPixel) {
            PointToDataMap result = new PointToDataMap();
            result.pointToDataMap = new int[0];
            result.startPoint = leftPixel;
            return result;
        }

        // Find the subset that fits
        int startPixel = Math.max(minValuePixel, leftPixel);
        int endPixel = Math.min(maxValuePixel, rightPixel);
        int nPoints = endPixel - startPixel + 1;
        int[] pointToDataMap = new int[nPoints];
        
        int currentValueIndex = 0;
        int currentLeftBoundaryPixel = minValuePixel;
        int currentRightBoundaryPixel = buffer.xValueToPixel(xBoundaries.getDouble(1));
        
        for (int currentOffset = 0; currentOffset < pointToDataMap.length; currentOffset++) {
            // Advance if the pixel is past the right boundary, or if it's the same
            // but the left boundary is passed (to "encourage" the change of value
            // from large cells)
            int currentPixel = startPixel + currentOffset;
            while ((currentRightBoundaryPixel < currentPixel || (currentRightBoundaryPixel == currentPixel && currentLeftBoundaryPixel != currentPixel))
                    && currentValueIndex < xBoundaries.size() - 2) {
                currentValueIndex++;
                currentLeftBoundaryPixel = currentRightBoundaryPixel;
                currentRightBoundaryPixel = buffer.xValueToPixel(xBoundaries.getDouble(currentValueIndex + 1));
            }
            
            pointToDataMap[currentOffset] = currentValueIndex;
        }
        
        PointToDataMap result = new PointToDataMap();
        result.pointToDataMap = pointToDataMap;
        result.startPoint = startPixel;
        return result;
    }
    
    PointToDataMap createYPointToDataMap(int topPixel, int bottomPixel, GraphBuffer buffer, ListNumber yBoundaries) {
        // Check any data in range
        int minValuePixel = buffer.yValueToPixel(yBoundaries.getDouble(0));
        int maxValuePixel = buffer.yValueToPixel(yBoundaries.getDouble(yBoundaries.size() - 1));
        if (minValuePixel < topPixel ||
                maxValuePixel > bottomPixel) {
            PointToDataMap result = new PointToDataMap();
            result.pointToDataMap = new int[0];
            result.startPoint = topPixel;
            return result;
        }

        // Find the subset that fits
        int startPixel = Math.max(maxValuePixel, topPixel);
        int endPixel = Math.min(minValuePixel, bottomPixel);
        int nPoints = endPixel - startPixel + 1;
        int[] pointToDataMap = new int[nPoints];
        
        int currentValueIndex = 0;
        int currentBottomBoundaryPixel = minValuePixel;
        int currentTopBoundaryPixel = buffer.yValueToPixel(yBoundaries.getDouble(1));
        
        for (int currentOffset = 0; currentOffset < pointToDataMap.length; currentOffset++) {
            // Advance if the pixel is past the top boundary, or if it's the same
            // but the bottom boundary is passed (to "encourage" the change of value
            // from large cells)
            int currentPixel = endPixel - currentOffset;
            while ((currentTopBoundaryPixel > currentPixel || (currentTopBoundaryPixel == currentPixel && currentBottomBoundaryPixel != currentPixel))
                && currentValueIndex < yBoundaries.size() - 2) {
                currentValueIndex++;
                currentBottomBoundaryPixel = currentTopBoundaryPixel;
                currentTopBoundaryPixel = buffer.yValueToPixel(yBoundaries.getDouble(currentValueIndex + 1));
            }
            
            pointToDataMap[nPoints - currentOffset - 1] = currentValueIndex;
        }
        
        PointToDataMap result = new PointToDataMap();
        result.pointToDataMap = pointToDataMap;
        result.startPoint = startPixel;
        return result;
    }
    
    private void drawRectanglesArray(Graphics2D g, Cell2DDataset data, double xStartGraph, double yEndGraph,
            double xWidthTotal, double yHeightTotal, double cellHeight, double cellWidth, BufferedImage image){
        
        byte pixels[] = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        boolean hasAlphaChannel = image.getAlphaRaster() != null;
        int countY = 0;
        int countX;
        double yPosition = yEndGraph-yHeightTotal;
        int yPositionInt = (int)(yEndGraph-yHeightTotal);
        while (countY < data.getYCount()){
                countX = 0;
                double xPosition = xStartGraph;
                int xPositionInt = (int)xStartGraph;
                while (countX < data.getXCount()){
                    int rgb = colorMapInstance.colorFor(data.getValue((int)countX, data.getYCount()-1-(int)countY));
                    for(int w = 0; w < (int)cellWidth + 1; w++){
                        for(int h = 0; h < (int)cellHeight + 1; h++){
                            if(hasAlphaChannel){
                            pixels[(yPositionInt+h)*getImageWidth()*4 + 4*xPositionInt + 0] = (byte)(rgb >> 24 & 0xFF);
                            pixels[(yPositionInt+h)*getImageWidth()*4 + 4*xPositionInt + 1] = (byte)(rgb & 0xFF);
                            pixels[(yPositionInt+h)*getImageWidth()*4 + 4*xPositionInt + 2] = (byte)(rgb >> 8 & 0xFF);
                            pixels[(yPositionInt+h)*getImageWidth()*4 + 4*xPositionInt + 3] = (byte)(rgb >> 16 & 0xFF);
                            }
                            else{
                            pixels[(yPositionInt+h)*getImageWidth()*3 + 3*(xPositionInt+w) + 0] = (byte)(rgb & 0xFF);
                            pixels[(yPositionInt+h)*getImageWidth()*3 + 3*(xPositionInt+w) + 1] = (byte)((rgb >> 8 & 0xFF) );
                            pixels[(yPositionInt+h)*getImageWidth()*3 + 3*(xPositionInt+w) + 2] = (byte)((rgb >> 16 & 0xFF));
                            }
                        }
                    }
                    xPosition = xPosition + cellWidth;
                    xPositionInt = (int)xPosition;
                    countX++;
                }
                yPosition = yPosition + cellHeight;
                yPositionInt = (int)yPosition;
                countY++;
            }
    }

    /*Calculates the range of the z values to be graphed, based on the previous z range (if there is one)
     If there is a previous range, the minimum value can only be lowered and the maximum value can only
     be raised to match the current range.*/
    /**
     *Calculates the range of the z values to be graphed based on the previous z range (if there is one).
     *  If there is a previous range, the minimum value can only be lowered and the maximum value can only
     *be raised to match the current range.
     * @param zDataRange current data range.
     */
    protected void calculateZRange(Range zDataRange, Range displayRange) {
        zPlotRange = zAxisRange.axisRange(zDataRange, displayRange);
    }
    /**
     *Sets private variables to account for the space required to draw in labels for the legend.
     * Only called if drawLegend is true.
     */
    protected void calculateZLabels() {
        labelFontMetrics = g.getFontMetrics(labelFont);
        // Calculate z axis references. If range is zero, use special logic
        if (!zPlotRange.getMinimum().equals(zPlotRange.getMaximum())) {
            ValueAxis zAxis = zValueScale.references(zPlotRange, 2, Math.max(2, getImageHeight() / 60));
            zReferenceLabels = Arrays.asList(zAxis.getTickLabels());
            zReferenceValues = new ArrayDouble(zAxis.getTickValues());            
        } else {
            // TODO: use something better to format the number
            ValueAxis zAxis = zValueScale.references(zPlotRange, 1, 1);
            zReferenceLabels = Arrays.asList(zAxis.getTickLabels());
            zReferenceValues = new ArrayDouble(zPlotRange.getMinimum().doubleValue());            
        }
        
        // Compute z axis spacing
        int[] zLabelWidths = new int[zReferenceLabels.size()];
        zLabelMaxWidth = 0;
        for (int i = 0; i < zLabelWidths.length; i++) {
            zLabelWidths[i] = labelFontMetrics.stringWidth(zReferenceLabels.get(i));
            zLabelMaxWidth = Math.max(zLabelMaxWidth, zLabelWidths[i]);
        }
    }

    /**
     *Translates a value's position in the aggregated range to a position on the legend.
     * @param value raw value
     * @return double(scaled value)
     */
    protected final double scaledZ(double value, int bottomPixel, int topPixel) {
        return Math.ceil(zValueScale.scaleValue(value, zPlotRange.getMinimum().doubleValue(), zPlotRange.getMaximum().doubleValue(), bottomPixel, topPixel));
    }

    /**
     * Whether or not the legend for the value to color mapping should be drawn.
     * Default is {@link #DEFAULT_DRAW_LEGEND}.
     * 
     * @return if true legend is drawn
     */
    public boolean isDrawLegend() {
        return drawLegend;
    }

    /**
     * Return the color scheme used for the value. Default is {@link #DEFAULT_COLOR_MAP}.
     * 
     * @return the color schemed used for the value; can't be null
     */
    public NumberColorMap getColorMap() {
        return colorMap;
    }

    public Range getXIndexSelectionRange() {
        return xIndexSelectionRange;
    }

    public Range getYIndexSelectionRange() {
        return yIndexSelectionRange;
    }

    public Range getXValueSelectionRange() {
        return xValueSelectionRange;
    }

    public Range getYValueSelectionRange() {
        return yValueSelectionRange;
    }

    public Range getXPixelSelectionRange() {
        return xPixelSelectionRange;
    }

    public Range getYPixelSelectionRange() {
        return yPixelSelectionRange;
    }
    

}