/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.List;
import static org.epics.graphene.Graph2DRenderer.aggregateRange;
import org.epics.util.array.ListNumbers;
import org.epics.util.array.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

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
    public boolean optimizeColorScheme = true;
    /**
     *Uses constructor specified in super class (Graph2DRenderer)
     * @param imageWidth should be equal to the width of the bufferedImage.
     * @param imageHeight should be equal to the height of the bufferedImage.
     */
    public IntensityGraph2DRenderer(int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight); 
    }

    /**
     *Default Constructor: makes an IntensityGraph2DRenderer of width 300 and height 200.
     */
    public IntensityGraph2DRenderer() {
        this(300, 200);
    }
      
    /**
     *Updates private data by getting new values from update.
     * Uses update from super class (Graph2DRenderer) for updates that are not specific to IntensityGraph2DRenderer. 
     * @param update IntensityGraph2DRendererUpdate
     */
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
    private AxisRange zAxisRange = AxisRanges.integrated();
    private ValueScale zValueScale = ValueScales.linearScale();
    protected ListInt zReferenceCoords;
    protected ListDouble zReferenceValues;
    protected List<String> zReferenceLabels;
    private int zLabelMaxWidth;
    private int originalRightMargin = super.rightMargin;
    public boolean useColorArray = false; 
    
    private NumberColorMap colorMap = DEFAULT_COLOR_MAP;
    
    
    /**
     *Draws an intensity graph in the given graphics context, using the given data.
     * All drawing is done within the bounds specified either at initialization or at update.
     *  Different colorSchemes may be specified using the IntensityGraph2DRendererUpdate class, in combination with the update function. 
     * @param graphBuffer Contains <code>imageBuffer</code> and <code>Graphics2D</code> objects used to perform drawing functions within draw.
     * @param data can not be null
     */
    public void draw(GraphBuffer graphBuffer, Cell2DDataset data) {
        drawArray(graphBuffer, data);
    }
    
    public void drawArray(GraphBuffer graphBuffer, Cell2DDataset data) {
        //Use super class to draw basics of graph.
        this.g = graphBuffer.getGraphicsContext();
        GraphAreaData area = new GraphAreaData();
        BufferedImage image = graphBuffer.getImage();
        calculateRanges(data.getXRange(), data.getYRange());
        area.setGraphBuffer(graphBuffer);
        graphBuffer.drawBackground(backgroundColor);
        zRange = RangeUtil.range(data.getStatistics().getMinimum().doubleValue(),data.getStatistics().getMaximum().doubleValue());
        calculateZRange(zRange);
        
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
        area.setGraphAreaMargins(leftAreaMargin, bottomAreaMargin, rightAreaMargin, topAreaMargin);
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
                    zRefCoords[0] = area.yAreaTop;
                }
                else{
                    for (int i = 0; i < zRefCoords.length; i++) {
                        zRefCoords[i] = (int) scaledZ(zReferenceValues.getDouble(i), area.yGraphBottom, area.yGraphTop);
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
            if (colorMapInstance == null || !RangeUtil.equals(optimizedRange, zRange)) {
                colorMapInstance = colorMap.createInstance(zPlotRange);
                colorMapInstance = NumberColorMaps.optimize(colorMapInstance, zPlotRange);
                optimizedRange = zPlotRange;
            }
        }


        double xStartGraph = super.xPlotCoordStart;
        double yEndGraph = area.yGraphBottom;

        double yHeightTotal = area.yGraphBottom - area.yGraphTop + 1;
        
        int startX = area.xGraphLeft;//(int) Math.floor(xPlotCoordStart);
        int startY = area.yGraphTop;//(int) Math.floor(yPlotCoordStart);
        int endX = area.xGraphRight;//(int) Math.ceil(xPlotCoordEnd);
        int endY = area.yGraphBottom;//(int) Math.ceil(yPlotCoordEnd);
        PointToDataMap xPointToDataMap = createXPointToDataMap(startX, endX, graphBuffer, data.getXBoundaries()); //createPointToDataMap(startX, endX+1, getXPlotRange(), data.getXBoundaries(), false);
        PointToDataMap yPointToDataMap = createYPointToDataMap(startY, endY, graphBuffer, data.getYBoundaries());//getYPlotRange(), data.getYBoundaries(), true);
        graphBuffer.drawDataImage(xPointToDataMap.startPoint, yPointToDataMap.startPoint, xPointToDataMap.pointToDataMap, yPointToDataMap.pointToDataMap, data, colorMapInstance);
        
        if(drawLegend && legendWidth>0){
            /*dataList is made by splitting the aggregated range of the z(color) data into a list of the
            same length as the the height of the graph in pixels.*/
            ListNumber dataList = ListNumbers.linearListFromRange(zPlotRange.getMinimum().doubleValue(),zPlotRange.getMaximum().doubleValue(),(int)yHeightTotal);
            //legendData is a Cell2DDataset representation of dataList.
            Cell2DDataset legendData = Cell2DDatasets.linearRange(dataList, RangeUtil.range(0, 1), 1, RangeUtil.range(0, (int)yHeightTotal), (int)yHeightTotal);
            int xLegendStart = getImageWidth() - originalRightMargin - zLabelMaxWidth - zLabelMargin - legendWidth;
            drawRectanglesArray(g, legendData, xLegendStart, yEndGraph, legendWidth, yHeightTotal, 1, legendWidth, image);
            graphBuffer.drawLeftLabels(zReferenceLabels, zReferenceCoords, labelColor, labelFont, area.yAreaBottom, area.yAreaTop, getImageWidth() - originalRightMargin - 1);
            drawZLabels();
        }
        
    }
    @Override
    public IntensityGraph2DRendererUpdate newUpdate() {
        return new IntensityGraph2DRendererUpdate();
    }
    
    private void drawRectangles(Graphics2D g, Cell2DDataset data, double xStartGraph, double yEndGraph,
            double xWidthTotal, double yHeightTotal, double cellHeight, double cellWidth){
        
        int countY = 0;
        int countX;
        double yPosition = yEndGraph-yHeightTotal;
        int yPositionInt = (int)(yEndGraph-yHeightTotal);
        while (countY < data.getYCount()){
                countX = 0;
                double xPosition = xStartGraph;
                int xPositionInt = (int)xStartGraph;
                while (countX < data.getXCount()){
                    g.setColor(new Color(colorMapInstance.colorFor(data.getValue(countX, data.getYCount()-1-countY))));
                    Rectangle2D.Double currentRectangle = new Rectangle2D.Double(xPositionInt, yPositionInt, (int)cellWidth+1, (int)cellHeight+1);
                    g.fill(currentRectangle);
                    xPosition = xPosition + cellWidth;
                    xPositionInt = (int)xPosition;
                    countX++;
                }
                yPosition = yPosition + cellHeight;
                yPositionInt = (int)yPosition;
                countY++;
            }
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
    
    PointToDataMap createPointToDataMap(int startPoint, int endPoint, Range xRange, ListNumber xBoundaries, boolean mirror) {
        ListNumber pointBoundaries = ListNumbers.linearListFromRange(xRange.getMinimum().doubleValue(), xRange.getMaximum().doubleValue(), endPoint - startPoint + 1);
        // Check any data in range
        if (xBoundaries.getDouble(0) >= xRange.getMaximum().doubleValue() ||
                xBoundaries.getDouble(xBoundaries.size() - 1) <= xRange.getMinimum().doubleValue()) {
            PointToDataMap result = new PointToDataMap();
            result.pointToDataMap = new int[0];
            result.startPoint = startPoint;
            return result;
        }
        
        int startOffset = 0;
        int endOffset = 0;
        while (xBoundaries.getDouble(0) > pointBoundaries.getDouble(startOffset + 1)) {
            startOffset++;
        }
        while (xBoundaries.getDouble(xBoundaries.size() - 1) < pointBoundaries.getDouble(pointBoundaries.size() - 1 - endOffset)) {
            endOffset++;
        }
        int nPoints = endPoint - startPoint - endOffset - startOffset;
        int[] pointToDataMap = new int[nPoints];
        
        int currentOffset = 0;
        int dataPosition = 0;
        while (currentOffset < nPoints) {
            // Look for the first cell where the end value is after the 
            // point start value
            while (xBoundaries.getDouble(dataPosition + 1) <= pointBoundaries.getDouble(startOffset + currentOffset)) {
                dataPosition++;
            }

            if (!mirror) {
                pointToDataMap[currentOffset] = dataPosition;
            } else {
                pointToDataMap[nPoints - currentOffset - 1] = dataPosition;
            }
            currentOffset++;
        }
        
        PointToDataMap result = new PointToDataMap();
        result.pointToDataMap = pointToDataMap;
        result.startPoint = startPoint + startOffset;
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
    
    //Draws rectangles for the case when there are more x values than pixels, but no more y values than pixels.
    //Uses the first value within each pixel to choose a color.
    private void drawRectanglesSmallX(Graphics2D g, Cell2DDataset data, double xStartGraph, double yEndGraph,
            double xWidthTotal, double yHeightTotal, double cellHeight, double cellWidth){
        
        int countY = 0;
        double countX;
        /*DataPerBox is a measure of how many data points fit into each pixel.
         Because of checks in the draw method, DataPerBox is guaranteed to be greater 
         than 1. When drawing each box, xDataPerBox is the amount added to countX, which is then
         rounded down to the nearest integer to be used as an index for a data point.
         countX itself is never rounded.*/
        double xDataPerBox = (data.getXCount()-1)/xWidthTotal;
        //yPosition is never rounded, and keeps track of exactly where each box should start.
        double yPosition = yEndGraph-yHeightTotal;
        /*yPositionInt is used for the actual drawing of boxes, since every box needs to be drawn
        starting from the top left of a pixel. It is based off of yPosition.*/ 
        int yPositionInt = (int)(yEndGraph-yHeightTotal);
        while (countY < data.getYCount()){
                countX = 0;
                int xPositionInt = (int)xStartGraph;
                while (xPositionInt < (int)(xStartGraph+xWidthTotal)+1){
                    g.setColor(new Color(colorMapInstance.colorFor(data.getValue((int)countX, data.getYCount()-1-countY))));
                    Rectangle2D.Double rect;
                    //check to see how far the end of the drawn box is from the end of the actual data box (due to truncation)
                    if((yPositionInt+((int)cellHeight)+1)-(yPosition+cellHeight) < 1)
                        rect = new Rectangle2D.Double(xPositionInt,yPositionInt ,1,((int)cellHeight)+1);
                    else
                        rect = new Rectangle2D.Double(xPositionInt,yPositionInt ,1,((int)cellHeight)+2);
                    g.fill(rect);
                    countX+=xDataPerBox;
                    xPositionInt+=1;   
                }
                yPosition = yPosition + cellHeight;
                yPositionInt = (int)(yPosition);
                countY++;
            }
    }
    
    private void drawRectanglesSmallXArray(Graphics2D g, Cell2DDataset data, double xStartGraph, double yEndGraph,
            double xWidthTotal, double yHeightTotal, double cellHeight, double cellWidth, BufferedImage image){
        byte pixels[] = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        boolean hasAlphaChannel = image.getAlphaRaster() != null;
        int countY = 0;
        double countX;
        /*DataPerBox is a measure of how many data points fit into each pixel.
         Because of checks in the draw method, DataPerBox is guaranteed to be greater 
         than 1. When drawing each box, xDataPerBox is the amount added to countX, which is then
         rounded down to the nearest integer to be used as an index for a data point.
         countX itself is never rounded.*/
        double xDataPerBox = (data.getXCount()-1)/xWidthTotal;
        //yPosition is never rounded, and keeps track of exactly where each box should start.
        double yPosition = yEndGraph-yHeightTotal;
        /*yPositionInt is used for the actual drawing of boxes, since every box needs to be drawn
        starting from the top left of a pixel. It is based off of yPosition.*/ 
        int yPositionInt = (int)(yEndGraph-yHeightTotal);
        while (countY < data.getYCount()){
                countX = 0;
                int xPositionInt = (int)xStartGraph;
                while (xPositionInt < (int)(xStartGraph+xWidthTotal)+1){
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
                    countX+=xDataPerBox;
                    xPositionInt+=1;   
                }
                yPosition = yPosition + cellHeight;
                yPositionInt = (int)(yPosition);
                countY++;
            }
    }
//Same logic as drawRectanglesSmallX, but for when there are more y values than pixels.
    private void drawRectanglesSmallY(Graphics2D g, Cell2DDataset data, double xStartGraph, double yEndGraph,
            double xWidthTotal, double yHeightTotal, double cellHeight, double cellWidth){
        //Exact y index of data value to be used.
        double countY;
        int countX = 0;
        //Exact number of y data points per pixel. 
        double yDataPerBox = (data.getYCount()-1)/yHeightTotal;
        double xPosition = xStartGraph;
        int xPositionInt = (int)xStartGraph;
        while (countX < data.getXCount()){
                countY = 0;
                int yPositionInt = (int)(yEndGraph-yHeightTotal);
                while (yPositionInt < (int)yEndGraph+1){
                    g.setColor(new Color(colorMapInstance.colorFor(data.getValue(countX, data.getYCount()-1-((int)countY)))));
                    Rectangle2D.Double rect;
                    if((xPositionInt+(int)cellWidth+1)-(xPosition+cellWidth) < 1)
                        rect = new Rectangle2D.Double(xPositionInt,yPositionInt,(int)cellWidth+1,1);
                    else
                        rect = new Rectangle2D.Double(xPositionInt,yPositionInt,(int)cellWidth+2,1);
                    g.fill(rect);
                    countY+=yDataPerBox;
                    yPositionInt+=1;
                }
                xPosition = xPosition + cellWidth;
                xPositionInt = (int)xPosition;
                countX++;
            }
    }
    
    private void drawRectanglesSmallYArray(Graphics2D g, Cell2DDataset data, double xStartGraph, double yEndGraph,
            double xWidthTotal, double yHeightTotal, double cellHeight, double cellWidth, BufferedImage image){
        byte pixels[] = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        boolean hasAlphaChannel = image.getAlphaRaster() != null;
        //Exact y index of data value to be used.
        double countY;
        int countX = 0;
        //Exact number of y data points per pixel. 
        double yDataPerBox = (data.getYCount()-1)/yHeightTotal;
        double xPosition = xStartGraph;
        int xPositionInt = (int)xStartGraph;
        while (countX < data.getXCount()){
                countY = 0;
                int yPositionInt = (int)(yEndGraph-yHeightTotal);
                while (yPositionInt < (int)yEndGraph+1){
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
                    countY+=yDataPerBox;
                    yPositionInt+=1;
                }
                xPosition = xPosition + cellWidth;
                xPositionInt = (int)xPosition;
                countX++;
            }
    }
/*Draws for the case when there are both more x values and y values than pixels.
Picks the value at approximately the top left of each pixel to set color. Skips other values within the pixel. 
Draws boxes only 1 pixel wide and 1 pixel tall.*/ 
    private void drawRectanglesSmallXAndY(Graphics2D g, Cell2DDataset data, double xStartGraph, double yEndGraph,
            double xWidthTotal, double yHeightTotal, double cellHeight, double cellWidth){
        /*countY and countX are used in the same way as in drawRectanglesSmallX and drawRectanglesSmallY
         each is used to calculate exactly what index of data value should be used to get the color
         for the drawn square.*/
        double countY = 0;
        double countX;
        int yPositionInt = (int)(yEndGraph-yHeightTotal);
        double yDataPerBox = (data.getYCount()-1)/yHeightTotal;
        double xDataPerBox = (data.getXCount()-1)/xWidthTotal;
        int xPositionInt;
        while (yPositionInt < (int)yEndGraph+1){
            countX = 0;
            xPositionInt = (int) xStartGraph;
            while (xPositionInt < (int)(xStartGraph+xWidthTotal)+1){
                g.setColor(new Color(colorMapInstance.colorFor(data.getValue((int)countX, data.getYCount()-1-(int)countY))));
                Rectangle2D.Double rect;
                rect = new Rectangle2D.Double(xPositionInt,yPositionInt,1,1);
                g.fill(rect);
                countX+=xDataPerBox;
                xPositionInt+=1;
            }
            countY+=yDataPerBox;
            yPositionInt+=1;
        }
    }
    
    private void drawRectanglesSmallXAndYArray(Graphics2D g, Cell2DDataset data, double xStartGraph, double yEndGraph,
            double xWidthTotal, double yHeightTotal, double cellHeight, double cellWidth, BufferedImage image){
        /*countY and countX are used in the same way as in drawRectanglesSmallX and drawRectanglesSmallY
         each is used to calculate exactly what index of data value should be used to get the color
         for the drawn square.*/
        byte pixels[] = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        boolean hasAlphaChannel = image.getAlphaRaster() != null;
        double countY = 0;
        double countX;
        int yPositionInt = (int)(yEndGraph-yHeightTotal);
        double yDataPerBox = (data.getYCount()-1)/yHeightTotal;
        double xDataPerBox = (data.getXCount()-1)/xWidthTotal;
        int xPositionInt;
        while (yPositionInt < (int)yEndGraph+1){
            countX = 0;
            xPositionInt = (int) xStartGraph;
            while (xPositionInt < (int)(xStartGraph+xWidthTotal)+1){
                int rgb = colorMapInstance.colorFor(data.getValue((int)countX, data.getYCount()-1-(int)countY));
                if(hasAlphaChannel){
                    pixels[(yPositionInt)*getImageWidth()*4 + 4*xPositionInt + 0] = (byte)(rgb >> 24 & 0xFF);
                    pixels[(yPositionInt)*getImageWidth()*4 + 4*xPositionInt + 1] = (byte)(rgb & 0xFF);
                    pixels[(yPositionInt)*getImageWidth()*4 + 4*xPositionInt + 2] = (byte)(rgb >> 8 & 0xFF);
                    pixels[(yPositionInt)*getImageWidth()*4 + 4*xPositionInt + 3] = (byte)(rgb >> 16 & 0xFF);
                }
                else{
                    pixels[(yPositionInt)*getImageWidth()*3 + 3*(xPositionInt) + 0] = (byte)(rgb & 0xFF);
                    pixels[(yPositionInt)*getImageWidth()*3 + 3*(xPositionInt) + 1] = (byte)((rgb >> 8 & 0xFF) );
                    pixels[(yPositionInt)*getImageWidth()*3 + 3*(xPositionInt) + 2] = (byte)((rgb >> 16 & 0xFF));
                }
                countX+=xDataPerBox;
                xPositionInt+=1;
            }
            countY+=yDataPerBox;
            yPositionInt+=1;
        }
    }
    
    private void drawRectanglesSmallXAndYBoundaries(Graphics2D g, Cell2DDataset data){
        ListNumber cellBoundariesX = data.getXBoundaries();
        List<Integer> newBoundariesX = new ArrayList<Integer>();
        List<Integer> valueIndicesX = new ArrayList<Integer>();
        int countX = 0;
        xPlotCoordEnd+=1;
        //Go through the cellBoundaries for X and make a new array that fits the graph.
        for(int i = 0; i < cellBoundariesX.size(); i++){
            //Get rid of values outside of the plot range.
            if(cellBoundariesX.getDouble(i) >= xPlotValueStart && cellBoundariesX.getDouble(i) <= xPlotValueEnd){
                if(newBoundariesX.size() == 0){
                    if(i > 0 && (int)scaledX(cellBoundariesX.getDouble(i)) > xPlotCoordStart){
                        newBoundariesX.add((int)xPlotCoordStart);
                        valueIndicesX.add(i-1);
                        countX++;
                    }
                    newBoundariesX.add((int)scaledX(cellBoundariesX.getDouble(i)));
                    valueIndicesX.add(i);
                }
                //Add values only if they increment the cellBoundary by 1. (Make the width between boundaries at least one)
                else if((int)scaledX(cellBoundariesX.getDouble(i)) > newBoundariesX.get(countX)){
                    newBoundariesX.add((int)scaledX(cellBoundariesX.getDouble(i)));
                    valueIndicesX.add(i);
                    countX++;
                }
            }
            else{
                if(i>0){
                    if(cellBoundariesX.getDouble(i-1) >= xPlotValueStart && cellBoundariesX.getDouble(i-1) < xPlotValueEnd){
                        newBoundariesX.add((int) xPlotCoordEnd);
                        valueIndicesX.add(i);
                    }
                }
            }
        }
        xPlotCoordEnd-=1;
        ListNumber cellBoundariesY = data.getYBoundaries();
        List<Integer> newBoundariesY = new ArrayList<Integer>();
        List<Integer> valueIndicesY = new ArrayList<Integer>();
        int countY = 0;
        yPlotCoordEnd += 1;
        for(int i = 0; i < cellBoundariesY.size(); i++){
            //Make sure values are in range.
            if(cellBoundariesY.getDouble(i) >= yPlotValueStart && cellBoundariesY.getDouble(i) <= yPlotValueEnd){
                //Always add first value in range
                if(newBoundariesY.size() == 0){
                    if(i > 0 && (int)scaledY(cellBoundariesY.getDouble(i)) < yPlotCoordEnd){
                        newBoundariesY.add((int)yPlotCoordEnd);
                        valueIndicesY.add(i-1);
                        countY++;
                    }
                    newBoundariesY.add(((int)scaledY(cellBoundariesY.getDouble(i))));
                    valueIndicesY.add(i);
                }
                //Only add values if they increment the boundary by 1 or more.
                else if((int)scaledY(cellBoundariesY.getDouble(i)) <= newBoundariesY.get(countY)-1){
                    newBoundariesY.add(((int)scaledY(cellBoundariesY.getDouble(i))));
                    valueIndicesY.add(i);
                    countY++;
                }
            }
            else{
                if(i>0){
                    if(cellBoundariesY.getDouble(i-1) >= yPlotValueStart && cellBoundariesY.getDouble(i-1) > yPlotValueStart){
                        newBoundariesY.add((int) yPlotCoordStart);
                        valueIndicesY.add(i);
                    }
                }
            }
        }
        yPlotCoordEnd -=1;
        countY = 0;
        //Fill in boxes using the boundaries and values we previously generated.
        while (countY < newBoundariesY.size()-1){
                countX = 0;
                while (countX < newBoundariesX.size()-1){
                    g.setColor(new Color(colorMapInstance.colorFor(data.getValue(valueIndicesX.get(countX), valueIndicesY.get(valueIndicesY.size()-2-countY)))));
                    //make and fill the rectangle.
                    Rectangle2D.Double currentRectangle;
                    currentRectangle = new Rectangle2D.Double(newBoundariesX.get(countX), newBoundariesY.get(newBoundariesY.size()-1-countY)
                            , newBoundariesX.get(countX+1)-newBoundariesX.get(countX),  newBoundariesY.get(newBoundariesY.size()-1-countY-1)-newBoundariesY.get(newBoundariesY.size()-1-countY));
                    g.fill(currentRectangle);
                    countX++;
                }
                countY++;
            }
    }
    
    private void drawRectanglesSmallXAndYBoundariesArray(Graphics2D g, Cell2DDataset data, BufferedImage image){
        
        byte[] pixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        boolean hasAlphaChannel = image.getAlphaRaster() != null;
        ListNumber cellBoundariesX = data.getXBoundaries();
        List<Integer> newBoundariesX = new ArrayList<Integer>();
        List<Integer> valueIndicesX = new ArrayList<Integer>();
        int countX = 0;
        xPlotCoordEnd+=1;
        //Go through the cellBoundaries for X and make a new array that fits the graph.
        for(int i = 0; i < cellBoundariesX.size(); i++){
            //Get rid of values outside of the plot range.
            if(cellBoundariesX.getDouble(i) >= xPlotValueStart && cellBoundariesX.getDouble(i) <= xPlotValueEnd){
                if(newBoundariesX.size() == 0){
                    if(i > 0 && (int)scaledX(cellBoundariesX.getDouble(i)) > xPlotCoordStart){
                        newBoundariesX.add((int)xPlotCoordStart);
                        valueIndicesX.add(i-1);
                        countX++;
                    }
                    newBoundariesX.add((int)scaledX(cellBoundariesX.getDouble(i)));
                    valueIndicesX.add(i);
                }
                //Add values only if they increment the cellBoundary by 1. (Make the width between boundaries at least one)
                else if((int)scaledX(cellBoundariesX.getDouble(i)) > newBoundariesX.get(countX)){
                    newBoundariesX.add((int)scaledX(cellBoundariesX.getDouble(i)));
                    valueIndicesX.add(i);
                    countX++;
                }
            }
            else{
                if(i>0){
                    if(cellBoundariesX.getDouble(i-1) >= xPlotValueStart && cellBoundariesX.getDouble(i-1) < xPlotValueEnd){
                        newBoundariesX.add((int) xPlotCoordEnd);
                        valueIndicesX.add(i);
                    }
                }
            }
        }
        xPlotCoordEnd-=1;
        ListNumber cellBoundariesY = data.getYBoundaries();
        List<Integer> newBoundariesY = new ArrayList<Integer>();
        List<Integer> valueIndicesY = new ArrayList<Integer>();
        int countY = 0;
        yPlotCoordEnd += 1;
        for(int i = 0; i < cellBoundariesY.size(); i++){
            //Make sure values are in range.
            if(cellBoundariesY.getDouble(i) >= yPlotValueStart && cellBoundariesY.getDouble(i) <= yPlotValueEnd){
                //Always add first value in range
                if(newBoundariesY.size() == 0){
                    if(i > 0 && (int)scaledY(cellBoundariesY.getDouble(i)) < yPlotCoordEnd){
                        newBoundariesY.add((int)yPlotCoordEnd);
                        valueIndicesY.add(i-1);
                        countY++;
                    }
                    newBoundariesY.add(((int)scaledY(cellBoundariesY.getDouble(i))));
                    valueIndicesY.add(i);
                }
                //Only add values if they increment the boundary by 1 or more.
                else if((int)scaledY(cellBoundariesY.getDouble(i)) <= newBoundariesY.get(countY)-1){
                    newBoundariesY.add(((int)scaledY(cellBoundariesY.getDouble(i))));
                    valueIndicesY.add(i);
                    countY++;
                }
            }
            else{
                if(i>0){
                    if(cellBoundariesY.getDouble(i-1) >= yPlotValueStart && cellBoundariesY.getDouble(i-1) > yPlotValueStart){
                        newBoundariesY.add((int) yPlotCoordStart);
                        valueIndicesY.add(i);
                    }
                }
            }
        }
        yPlotCoordEnd -=1;
        countY = 0;
        //Fill in boxes using the boundaries and values we previously generated.
        while (countY < newBoundariesY.size()-1){
                countX = 0;
                while (countX < newBoundariesX.size()-1){
                    //make and fill the rectangle.
                    int rgb = colorMapInstance.colorFor(data.getValue(valueIndicesX.get(countX), valueIndicesY.get(valueIndicesY.size()-2-countY)));
                    for(int w = 0; w < newBoundariesX.get(countX+1)-newBoundariesX.get(countX); w++){
                        for(int h = 0; h < newBoundariesY.get(newBoundariesY.size()-1-countY-1)-newBoundariesY.get(newBoundariesY.size()-1-countY); h++){
                            if(hasAlphaChannel){
                            pixels[(newBoundariesY.get(newBoundariesY.size()-1-countY)+h)*getImageWidth()*4 + 4*(newBoundariesX.get(countX)+w) + 0] = (byte)(rgb >> 24 & 0xFF);
                            pixels[(newBoundariesY.get(newBoundariesY.size()-1-countY)+h)*getImageWidth()*4 + 4*(newBoundariesX.get(countX)+w) + 1] = (byte)(rgb & 0xFF);
                            pixels[(newBoundariesY.get(newBoundariesY.size()-1-countY)+h)*getImageWidth()*4 + 4*(newBoundariesX.get(countX)+w) + 2] = (byte)(rgb >> 8 & 0xFF);
                            pixels[(newBoundariesY.get(newBoundariesY.size()-1-countY)+h)*getImageWidth()*4 + 4*(newBoundariesX.get(countX)+w) + 3] = (byte)(rgb >> 16 & 0xFF);
                            }
                            else{
                            pixels[(newBoundariesY.get(newBoundariesY.size()-1-countY)+h)*getImageWidth()*3 + 3*(newBoundariesX.get(countX)+w) + 0] = (byte)(rgb & 0xFF);
                            pixels[(newBoundariesY.get(newBoundariesY.size()-1-countY)+h)*getImageWidth()*3 + 3*(newBoundariesX.get(countX)+w) + 1] = (byte)((rgb >> 8 & 0xFF) );
                            pixels[(newBoundariesY.get(newBoundariesY.size()-1-countY)+h)*getImageWidth()*3 + 3*(newBoundariesX.get(countX)+w) + 2] = (byte)((rgb >> 16 & 0xFF));
                            }
                        }
                    }
                    countX++;
                }
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
    protected void calculateZRange(Range zDataRange) {
       
        zAggregatedRange = aggregateRange(zDataRange, zAggregatedRange);
        zPlotRange = zAxisRange.axisRange(zDataRange, zAggregatedRange);
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
     *Draws evenly spaced labels along the legend.
     */
    protected void drawZLabels() {
        // Draw Z labels
        ListNumber zTicks = zReferenceCoords;
        if (zReferenceLabels != null && !zReferenceLabels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {yAreaCoordStart, yAreaCoordEnd - 1};
            int xRightLabel = (int) (getImageWidth() - originalRightMargin - 1);
            drawHorizontalReferencesLabel(g, metrics, zReferenceLabels.get(0), (int) Math.floor(zTicks.getDouble(0)),
                drawRange, xRightLabel, true, false);
            drawHorizontalReferencesLabel(g, metrics, zReferenceLabels.get(zReferenceLabels.size() - 1), (int) Math.floor(zTicks.getDouble(zReferenceLabels.size() - 1)),
                drawRange, xRightLabel, false, false);
            
            for (int i = 1; i < zReferenceLabels.size() - 1; i++) {
                drawHorizontalReferencesLabel(g, metrics, zReferenceLabels.get(i), (int) Math.floor(zTicks.getDouble(i)),
                    drawRange, xRightLabel, true, false);
            }
        }
    }
    private static int MIN = 0;
    private static int MAX = 1;
    private static void drawHorizontalReferencesLabel(Graphics2D graphics, FontMetrics metrics, String text, int yCenter, int[] drawRange, int xRight, boolean updateMin, boolean centeredOnly) {
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

}