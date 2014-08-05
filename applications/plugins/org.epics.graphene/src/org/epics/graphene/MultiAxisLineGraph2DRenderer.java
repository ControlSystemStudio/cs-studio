/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.Range;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import static org.epics.graphene.InterpolationScheme.CUBIC;
import static org.epics.graphene.InterpolationScheme.LINEAR;
import static org.epics.graphene.InterpolationScheme.NEAREST_NEIGHBOR;
import static org.epics.graphene.ReductionScheme.FIRST_MAX_MIN_LAST;
import static org.epics.graphene.ReductionScheme.NONE;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListMath;
import org.epics.util.array.ListNumber;
import org.epics.util.array.SortedListView;
import org.epics.util.stats.Ranges;

/**
 * Renderer for a line graph with multiple y axes.
 *
 * @author carcassi, sjdallst
 */
public class MultiAxisLineGraph2DRenderer extends Graph2DRenderer<MultiAxisLineGraph2DRendererUpdate> {

    /**
     * List of supported interpolation schemes for this renderer.
     */
    public static java.util.List<InterpolationScheme> supportedInterpolationScheme = Arrays.asList(InterpolationScheme.NEAREST_NEIGHBOR, InterpolationScheme.LINEAR, InterpolationScheme.CUBIC);
    
    /**
     * List of supported data reduction schemes for this renderer.
     */
    public static java.util.List<ReductionScheme> supportedReductionScheme = Arrays.asList(ReductionScheme.FIRST_MAX_MIN_LAST, ReductionScheme.NONE);

    /**
     * Default interpolation scheme: nearest neighbor.
     */
    public static final InterpolationScheme DEFAULT_INTERPOLATION_SCHEME = InterpolationScheme.NEAREST_NEIGHBOR;
    
    /**
     * Default interpolation scheme: nearest neighbor.
     */
    public static final ReductionScheme DEFAULT_REDUCTION_SCHEME = ReductionScheme.FIRST_MAX_MIN_LAST;
    
    /**
     * Default separate area flag: false (all lines overlap in one area).
     */
    public static final boolean DEFAULT_SEPARATE_AREAS = false;
    
    @Override
    public MultiAxisLineGraph2DRendererUpdate newUpdate() {
        return new MultiAxisLineGraph2DRendererUpdate();
    }

    private InterpolationScheme interpolation = DEFAULT_INTERPOLATION_SCHEME;
    private ReductionScheme reduction = DEFAULT_REDUCTION_SCHEME;
    private List<ListDouble> yReferenceCoords;
    private List<ListDouble> yReferenceValues;
    private List<List<String>> yReferenceLabels;
    private Range emptyRange;
    private AxisRangeInstance xAxisRange = AxisRanges.auto().createInstance();
    private AxisRangeInstance yAxisRange = AxisRanges.auto().createInstance();
    private List<AxisRangeInstance> yAxisRanges;
    private ValueScale xValueScale = ValueScales.linearScale();
    private ValueScale yValueScale = ValueScales.linearScale();
    private Range xAggregatedRange;
    private List<Range> yAggregatedRange;
    private Range xPlotRange;
    private List<Range> yPlotRange;
    private HashMap<Integer, Range> indexToRangeMap = new HashMap<Integer,Range>();
    private int numGraphs = 0;
    private int spaceForYAxes;
    private int minimumGraphWidth = 200;
    private int yLabelMaxWidth = 0;
    private int xLabelMaxHeight;
    private NumberColorMap valueColorScheme = NumberColorMaps.JET;
    private NumberColorMapInstance valueColorSchemeInstance;
    
    private double xPlotValueStart;
    private List<Double> yPlotValueStart;
    private double xPlotValueEnd;
    private List<Double> yPlotValueEnd;
    
    private ArrayList<Double> graphBoundaries;
    private ArrayList<Double> graphBoundaryRatios;
    private int marginBetweenGraphs = 0;
    private int totalYMargins = 0;
    private int minimumGraphHeight = 100;
    protected List<String> xReferenceLabels;

    // The pixel coordinates for the area
    private int xAreaCoordStart;
    private List<Integer> yAreaCoordStart;
    private List<Integer> yAreaCoordEnd;
    private int xAreaCoordEnd;

    // The pixel coordinates for the ranges
    // These match the xPlotValueXxx
    private double xPlotCoordStart;
    private List<Double> yPlotCoordStart;
    private List<Double> yPlotCoordEnd;
    private double xPlotCoordEnd;

    // The pixel size of the range (not of the plot area)
    private List<Double> yPlotCoordHeight;
    private double xPlotCoordWidth;
    
    private boolean stretch = false;
    private boolean separateAreas = DEFAULT_SEPARATE_AREAS;
    /**
     * Creates a new line graph renderer.
     * 
     * @param imageWidth the graph width
     * @param imageHeight the graph height
     */
    public MultiAxisLineGraph2DRenderer(int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
    }

    /**
     * The current interpolation used for the line.
     * 
     * @return the current interpolation
     */
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }
    
    @Override
    public void update(MultiAxisLineGraph2DRendererUpdate update) {
        super.update(update);
        if (update.getInterpolation() != null) {
            interpolation = update.getInterpolation();
        }
        if (update.getDataReduction() != null) {
            reduction = update.getDataReduction();
        }
        if (update.getMinimumGraphWidth() != null){
            minimumGraphWidth = update.getMinimumGraphWidth();
        }
        if(update.getImageHeight() != null){
            if(stretch){
                for(int i = 0; i < graphBoundaries.size(); i++){
                    graphBoundaries.set(i, getImageHeight() * graphBoundaryRatios.get(i));
                }
            }
            else if((double)getImageHeight()/numGraphs - totalYMargins >= (minimumGraphHeight*2)){
                numGraphs+=1;
            }
            if((double)getImageHeight()/numGraphs - totalYMargins <= minimumGraphHeight){
                numGraphs = 0;
            }
        }
        if(update.getIndexToRange() != null){
            indexToRangeMap = update.getIndexToRange();
        }
        if(update.getMarginBetweenGraphs() != null){
            marginBetweenGraphs = update.getMarginBetweenGraphs();
        }
        if(update.getMinimumGraphHeight() != null){
            minimumGraphHeight = update.getMinimumGraphHeight();
        }
        if(update.isSeparateAreas() != null){
            separateAreas = update.isSeparateAreas();
        }
    }

    /**
     * Draws the graph on the given graphics context.
     * 
     * @param g the graphics on which to display the data
     * @param data the data to display
     */
    public void draw(Graphics2D g, List<Point2DDataset> data) {
        this.g = g;
        
        //Make a list containing the x range of each data set (each one should be the same).
        List<Range> dataRangesX = new ArrayList<Range>();
        for(int i = 0; i < data.size(); i++){
            dataRangesX.add(data.get(i).getXStatistics());
        }
        //Make a list containing the y range of each data set (each one should be different).
        List<Range> dataRangesY = new ArrayList<Range>();
        for(int i = 0; i < data.size(); i++){
            dataRangesY.add(data.get(i).getYStatistics());
        }

        labelFontMetrics = g.getFontMetrics(labelFont);
        
        if(separateAreas){
            xLabelMaxHeight = labelFontMetrics.getHeight() - labelFontMetrics.getLeading();
            totalYMargins = xLabelMaxHeight + marginBetweenGraphs + topMargin + bottomMargin + topAreaMargin + bottomAreaMargin + xLabelMargin + 1;
            getNumGraphsSplit(data);
            Range datasetRange = Ranges.range(0,numGraphs-1);
            valueColorSchemeInstance = valueColorScheme.createInstance(datasetRange);
            calculateRanges(dataRangesX, dataRangesY, numGraphs);
            setGraphBoundaries(data);
            calculateLabels();
            calculateGraphAreaSplit();        
            drawBackground();
            drawGraphArea();
        }else{
            getNumGraphs(data);
            Range datasetRange = Ranges.range(0,numGraphs-1);
            valueColorSchemeInstance = valueColorScheme.createInstance(datasetRange);
            calculateRanges(dataRangesX, dataRangesY, numGraphs);
            calculateLabels();
            calculateGraphArea();        
            drawBackground();
            drawGraphArea();
        }
        
        
        List<SortedListView> xValues = new ArrayList<SortedListView>();
        for(int i = 0; i < numGraphs; i++){
            xValues.add(org.epics.util.array.ListNumbers.sortedView(data.get(i).getXValues()));
        }
        
        List<ListNumber> yValues = new ArrayList<ListNumber>();
        for(int i = 0; i < numGraphs; i++){
            yValues.add(org.epics.util.array.ListNumbers.sortedView(data.get(i).getYValues(), xValues.get(i).getIndexes()));
        }
        if(separateAreas){
            g.setColor(Color.BLACK);
            for(int i = 0; i < numGraphs; i++){
                drawValueExplicitLine(xValues.get(i), yValues.get(i), interpolation, reduction,i);
            }
        }
        else{
            for(int i = 0; i < numGraphs; i++){
                g.setColor(new Color(valueColorSchemeInstance.colorFor(i)));
                drawValueExplicitLine(xValues.get(i), yValues.get(i), interpolation, reduction,i);
            }
        }

    }
    
    //method to get the number of graphs to draw when simply drawing mutiple y axes.
    private void getNumGraphs(List<Point2DDataset> data){
            numGraphs = data.size();
            //if yLabelMaxWidth has not been calculated, guess that it will be 15
            if(yLabelMaxWidth == 0){
                yLabelMaxWidth = 15;
            }
            spaceForYAxes = leftMargin + (yLabelMaxWidth + yLabelMargin*2 + 1)*(numGraphs-(numGraphs/2)) - 1;
            if(numGraphs > 1){
                spaceForYAxes += rightMargin + (yLabelMaxWidth + yLabelMargin*2 + 1)*(numGraphs/2) + 1;
            }
            else{
                spaceForYAxes += rightMargin;
            }
            while((double)getImageWidth() - spaceForYAxes < minimumGraphWidth){
                numGraphs-=1;
                spaceForYAxes = leftMargin + (yLabelMaxWidth + yLabelMargin*2 + 1)*(numGraphs-(numGraphs/2)) - 1;
                if(numGraphs > 1){
                    spaceForYAxes += rightMargin + (yLabelMaxWidth + yLabelMargin*2 + 1)*(numGraphs/2) + 1;
                }
                else{
                    spaceForYAxes += rightMargin;
                }
            }
    }
    
    //method to get number of graphs when the graphs are separateAreas
    private void getNumGraphsSplit(List<Point2DDataset> data){
        if(this.graphBoundaries == null || this.graphBoundaries.size() != numGraphs+1){
            numGraphs = data.size();
            while((double)getImageHeight()/numGraphs - totalYMargins < minimumGraphHeight){
                numGraphs-=1;
            }
        }
        
        //if all the graphs are being drawn, they can be stretched along with the image.
        stretch = numGraphs == data.size();
    }
    
    private void setGraphBoundaries(List<Point2DDataset> data){
        if(this.graphBoundaries == null || this.graphBoundaries.size() != numGraphs+1){
            graphBoundaries = new ArrayList<Double>();
            for(double i = 0; i <= numGraphs; i++){
                if(stretch){
                    if(i > 0){
                        graphBoundaries.add(i/numGraphs*(getImageHeight()) + marginBetweenGraphs);
                    }
                    else{
                        graphBoundaries.add(i/numGraphs*(getImageHeight()));
                    }
                }
                else{
                    if(i > 0){
                        graphBoundaries.add(i* (minimumGraphHeight+totalYMargins));
                    }
                    else{
                        graphBoundaries.add(i*minimumGraphHeight);
                    }
                }
            }
            graphBoundaryRatios = new ArrayList<Double>();
            for(double i = 0; i <= numGraphs; i++){
                if(stretch){
                    graphBoundaryRatios.add(i/numGraphs);
                }
                else{
                    graphBoundaryRatios.add((i*minimumGraphHeight)/getImageHeight());
                }
            }
        } 
    }
    
    protected void calculateRanges(List<Range> xDataRange, List<Range> yDataRange, int length) {
        for(int i = 0; i < length; i++){
            xAggregatedRange = aggregateRange(xDataRange.get(i), xAggregatedRange);
            // TODO: should be update to use display range
            xPlotRange = xAxisRange.axisRange(xDataRange.get(i), xDataRange.get(i));
        }  
        if(yAggregatedRange == null || yDataRange.size() != yAggregatedRange.size() || yDataRange.size() != length){
            yAggregatedRange = new ArrayList<Range>();
            yPlotRange = new ArrayList<Range>();
            yAxisRanges = new ArrayList<>();
            for(int i = 0; i < length; i++){
                if(indexToRangeMap.isEmpty() || !indexToRangeMap.containsKey(i)){
                    yAggregatedRange.add(aggregateRange(yDataRange.get(i), emptyRange));
                    AxisRangeInstance instance = yAxisRange.getAxisRange().createInstance();
                    yAxisRanges.add(instance);
                    // TODO: should be update to use display range
                    yPlotRange.add(instance.axisRange(yDataRange.get(i), yDataRange.get(i)));
                }
                else{
                    if(indexToRangeMap.containsKey(i)){
                        yAggregatedRange.add(aggregateRange(yDataRange.get(i), emptyRange));
                        yPlotRange.add(indexToRangeMap.get(i));
                    }
                }
            }
        }
        else{
            for(int i = 0; i < length; i++){
                if(indexToRangeMap.isEmpty() || !indexToRangeMap.containsKey(i)){
                    yAggregatedRange.set(i,aggregateRange(yDataRange.get(i), yAggregatedRange.get(i)));
                    // TODO: should be update to use display range
                    yPlotRange.set(i,yAxisRanges.get(i).axisRange(yDataRange.get(i), yDataRange.get(i)));
                }
                else{
                    if(indexToRangeMap.containsKey(i)){
                        yPlotRange.set(i,indexToRangeMap.get(i));
                    }
                }
            }
        }
    }
    
    @Override
    protected void calculateLabels() {
        // Calculate horizontal axis references. If range is zero, use special logic
        if (!xPlotRange.getMinimum().equals(xPlotRange.getMaximum())) {
            ValueAxis xAxis = xValueScale.references(xPlotRange, 2, Math.max(2, getImageWidth() / 60));
            xReferenceLabels = Arrays.asList(xAxis.getTickLabels());
            xReferenceValues = new ArrayDouble(xAxis.getTickValues());            
        } else {
            // TODO: use something better to format the number
            xReferenceLabels = Collections.singletonList(xPlotRange.getMinimum().toString());
            xReferenceValues = new ArrayDouble(xPlotRange.getMinimum().doubleValue());            
        }      
        
        // Calculate vertical axis references. If range is zero, use special logic
        if(yReferenceLabels == null || yReferenceLabels.size() != numGraphs){
            yReferenceLabels = new ArrayList<List<String>>();
            yReferenceValues = new ArrayList<ListDouble>();
            for(int i = 0; i < yPlotRange.size(); i++){
                if (!yPlotRange.get(i).getMinimum().equals(yPlotRange.get(i).getMaximum())) {
                    ValueAxis yAxis;
                    if(separateAreas){
                        yAxis = yValueScale.references(yPlotRange.get(i), 2, Math.max(2, (graphBoundaries.get(i+1).intValue() - graphBoundaries.get(i).intValue()) / 60));
                    }else{
                        yAxis = yValueScale.references(yPlotRange.get(i), 2, Math.max(2, getImageHeight() / 60));
                    }
                    yReferenceLabels.add(Arrays.asList(yAxis.getTickLabels()));
                    yReferenceValues.add(new ArrayDouble(yAxis.getTickValues()));            
                } else {
                    // TODO: use something better to format the number
                    yReferenceLabels.add(Collections.singletonList(yPlotRange.get(i).getMinimum().toString()));
                    yReferenceValues.add(new ArrayDouble(yPlotRange.get(i).getMinimum().doubleValue()));            
                }
            }
        }
        else{
            for(int i = 0; i < yPlotRange.size(); i++){
                if (!yPlotRange.get(i).getMinimum().equals(yPlotRange.get(i).getMaximum())) {
                    ValueAxis yAxis;
                    if(separateAreas){
                        yAxis = yValueScale.references(yPlotRange.get(i), 2, Math.max(2, (graphBoundaries.get(i+1).intValue() - graphBoundaries.get(i).intValue()) / 60));
                    }else{
                        yAxis = yValueScale.references(yPlotRange.get(i), 2, Math.max(2, getImageHeight() / 60));
                    }
                    yReferenceLabels.set(i,Arrays.asList(yAxis.getTickLabels()));
                    yReferenceValues.set(i,new ArrayDouble(yAxis.getTickValues()));            
                } else {
                    // TODO: use something better to format the number
                    yReferenceLabels.set(i,Collections.singletonList(yPlotRange.get(i).getMinimum().toString()));
                    yReferenceValues.set(i,new ArrayDouble(yPlotRange.get(i).getMinimum().doubleValue()));            
                }
            }
        }
        
        labelFontMetrics = g.getFontMetrics(labelFont);
        
        xLabelMaxHeight = labelFontMetrics.getHeight() - labelFontMetrics.getLeading();
        
        // Compute y axis spacing
        int yLabelWidth = 0;
        yLabelMaxWidth = 0;
        for (int a = 0; a < yReferenceLabels.size(); a++) {
            for(int b = 0; b < yReferenceLabels.get(a).size(); b++){
                yLabelWidth = labelFontMetrics.stringWidth(yReferenceLabels.get(a).get(b));
                yLabelMaxWidth = Math.max(yLabelMaxWidth, yLabelWidth);
            }
        }
    }

    @Override
    protected void calculateGraphArea() {
        int areaFromBottom = bottomMargin + xLabelMaxHeight + xLabelMargin;
        int areaFromLeft = leftMargin + (yLabelMaxWidth + yLabelMargin*2 + 1)*(numGraphs-(numGraphs/2)) - 1;
        int areaFromRight;
        if(numGraphs > 1){
            areaFromRight = rightMargin + (yLabelMaxWidth + yLabelMargin*2 + 1)*(numGraphs/2) + 1;
        }
        else{
            areaFromRight = rightMargin;
        }
        
        xPlotValueStart = xPlotRange.getMinimum().doubleValue();
        xPlotValueEnd = xPlotRange.getMaximum().doubleValue();
        if (xPlotValueStart == xPlotValueEnd) {
            // If range is zero, fake a range
            xPlotValueStart -= 1.0;
            xPlotValueEnd += 1.0;
        }
        xAreaCoordStart = areaFromLeft;
        xAreaCoordEnd = getImageWidth() - areaFromRight;
        xPlotCoordStart = xAreaCoordStart + leftAreaMargin + xPointMargin;
        xPlotCoordEnd = xAreaCoordEnd - rightAreaMargin - xPointMargin;
        xPlotCoordWidth = xPlotCoordEnd - xPlotCoordStart;
        
       //set the start and end of each plot in terms of values.
        if(yPlotValueStart == null || yPlotValueStart.size() != yPlotRange.size()){
            yPlotValueStart = new ArrayList<Double>();
            yPlotValueEnd = new ArrayList<Double>();
            for(int i = 0; i < yPlotRange.size(); i++){
                yPlotValueStart.add(yPlotRange.get(i).getMinimum().doubleValue());
                yPlotValueEnd.add(yPlotRange.get(i).getMaximum().doubleValue());
            }
        }
        else{
            for(int i = 0; i < yPlotRange.size(); i++){
                yPlotValueStart.set(i, yPlotRange.get(i).getMinimum().doubleValue());
                yPlotValueEnd.set(i, yPlotRange.get(i).getMaximum().doubleValue());
            }
        }
        
        for(int i = 0; i < yPlotRange.size(); i++){
            if (yPlotValueStart.get(i).doubleValue() == yPlotValueEnd.get(i).doubleValue()) {
                // If range is zero, fake a range
                yPlotValueStart.set(i, yPlotValueStart.get(i)-1.0);
                yPlotValueEnd.set(i, yPlotValueEnd.get(i)+1.0);
            }
        }
        
        super.yAreaCoordStart = topMargin;
        super.yAreaCoordEnd = getImageHeight() - areaFromBottom;
        super.yPlotCoordStart = super.yAreaCoordStart + topAreaMargin + yPointMargin;
        super.yPlotCoordEnd = super.yAreaCoordEnd - bottomAreaMargin - yPointMargin;
        super.yPlotCoordHeight = super.yPlotCoordEnd - super.yPlotCoordStart;
        
        //Only calculates reference coordinates if calculateLabels() was called
        if (xReferenceValues != null) {
            double[] xRefCoords = new double[xReferenceValues.size()];
            for (int i = 0; i < xRefCoords.length; i++) {
                xRefCoords[i] = scaledX1(xReferenceValues.getDouble(i));
            }
            xReferenceCoords = new ArrayDouble(xRefCoords);
        }
        
        yReferenceCoords = new ArrayList<ListDouble>();
        if (yReferenceValues != null) {
            for(int a = 0; a < yReferenceValues.size(); a++){
                double[] yRefCoords = new double[yReferenceValues.get(a).size()];
                for (int b = 0; b < yRefCoords.length; b++) {
                    if(separateAreas){
                        yRefCoords[b] = scaledYSplit(yReferenceValues.get(a).getDouble(b),a);
                    }else{
                        yRefCoords[b] = scaledY(yReferenceValues.get(a).getDouble(b),a);
                    }
                }
                yReferenceCoords.add(new ArrayDouble(yRefCoords));
            }
        }
    }
    
    protected void calculateGraphAreaSplit() {
        int areaFromBottom = bottomMargin + xLabelMaxHeight + xLabelMargin;
        int areaFromLeft = leftMargin + yLabelMaxWidth + yLabelMargin;

        xPlotValueStart = xPlotRange.getMinimum().doubleValue();
        xPlotValueEnd = xPlotRange.getMaximum().doubleValue();
        if (xPlotValueStart == xPlotValueEnd) {
            // If range is zero, fake a range
            xPlotValueStart -= 1.0;
            xPlotValueEnd += 1.0;
        }
        xAreaCoordStart = areaFromLeft;
        xAreaCoordEnd = getImageWidth() - rightMargin;
        xPlotCoordStart = xAreaCoordStart + leftAreaMargin + xPointMargin;
        xPlotCoordEnd = xAreaCoordEnd - rightAreaMargin - xPointMargin;
        xPlotCoordWidth = xPlotCoordEnd - xPlotCoordStart;
        
        //set the start and end of each plot in terms of values.
        if(yPlotValueStart == null || yPlotValueStart.size() != yPlotRange.size()){
            yPlotValueStart = new ArrayList<Double>();
            yPlotValueEnd = new ArrayList<Double>();
            for(int i = 0; i < yPlotRange.size(); i++){
                yPlotValueStart.add(yPlotRange.get(i).getMinimum().doubleValue());
                yPlotValueEnd.add(yPlotRange.get(i).getMaximum().doubleValue());
            }
        }
        else{
            for(int i = 0; i < yPlotRange.size(); i++){
                yPlotValueStart.set(i, yPlotRange.get(i).getMinimum().doubleValue());
                yPlotValueEnd.set(i, yPlotRange.get(i).getMaximum().doubleValue());
            }
        }
        
        //range faking
        for(int i = 0; i < yPlotRange.size(); i++){
            if (yPlotValueStart.get(i).doubleValue() == yPlotValueEnd.get(i).doubleValue()) {
                // If range is zero, fake a range
                yPlotValueStart.set(i, yPlotValueStart.get(i)-1.0);
                yPlotValueEnd.set(i, yPlotValueEnd.get(i)+1.0);
            }
        }
       
        if(yAreaCoordStart == null || yAreaCoordStart.size() != numGraphs){
            yAreaCoordStart = new ArrayList<Integer>();
            yAreaCoordEnd = new ArrayList<Integer>();
            yPlotCoordStart = new ArrayList<Double>();
            yPlotCoordEnd = new ArrayList<Double>();
            yPlotCoordHeight = new ArrayList<Double>();

            yAreaCoordStart.add(topMargin + graphBoundaries.get(0).intValue());
            yAreaCoordEnd.add(graphBoundaries.get(1).intValue()-areaFromBottom - marginBetweenGraphs);
            yPlotCoordStart.add(yAreaCoordStart.get(0) + topAreaMargin + yPointMargin);
            yPlotCoordEnd.add(yAreaCoordEnd.get(0) - bottomAreaMargin - yPointMargin);
            yPlotCoordHeight.add(yPlotCoordEnd.get(0)-yPlotCoordStart.get(0));
            for(int i = 1; i < numGraphs-1; i++){
                yAreaCoordStart.add(topMargin + graphBoundaries.get(i).intValue());
                yAreaCoordEnd.add(graphBoundaries.get(i+1).intValue()-areaFromBottom - marginBetweenGraphs);
                yPlotCoordStart.add(yAreaCoordStart.get(i) + topAreaMargin + yPointMargin);
                yPlotCoordEnd.add(yAreaCoordEnd.get(i) - bottomAreaMargin - yPointMargin);
                yPlotCoordHeight.add(yPlotCoordEnd.get(i)-yPlotCoordStart.get(i));
            }
            yAreaCoordStart.add(topMargin + graphBoundaries.get(numGraphs-1).intValue());
            yAreaCoordEnd.add(graphBoundaries.get(numGraphs).intValue()-areaFromBottom - marginBetweenGraphs);
            yPlotCoordStart.add(yAreaCoordStart.get(numGraphs-1) + topAreaMargin + yPointMargin);
            yPlotCoordEnd.add(yAreaCoordEnd.get(numGraphs-1) - bottomAreaMargin - yPointMargin);
            yPlotCoordHeight.add(yPlotCoordEnd.get(numGraphs-1)-yPlotCoordStart.get(numGraphs-1));
        }
        else{
            yAreaCoordStart.set(0,topMargin + graphBoundaries.get(0).intValue());
            yAreaCoordEnd.set(0,graphBoundaries.get(1).intValue()-areaFromBottom - marginBetweenGraphs);
            yPlotCoordStart.set(0,yAreaCoordStart.get(0) + topAreaMargin + yPointMargin);
            yPlotCoordEnd.set(0,yAreaCoordEnd.get(0) - bottomAreaMargin - yPointMargin);
            yPlotCoordHeight.set(0,yPlotCoordEnd.get(0)-yPlotCoordStart.get(0));
            for(int i = 1; i < numGraphs-1; i++){
                yAreaCoordStart.set(i,topMargin + graphBoundaries.get(i).intValue() + marginBetweenGraphs);
                yAreaCoordEnd.set(i,graphBoundaries.get(i+1).intValue()-areaFromBottom - marginBetweenGraphs);
                yPlotCoordStart.set(i,yAreaCoordStart.get(i) + topAreaMargin + yPointMargin);
                yPlotCoordEnd.set(i,yAreaCoordEnd.get(i) - bottomAreaMargin - yPointMargin);
                yPlotCoordHeight.set(i,yPlotCoordEnd.get(i)-yPlotCoordStart.get(i));
            }
            yAreaCoordStart.set(numGraphs-1,topMargin + graphBoundaries.get(numGraphs-1).intValue());
            yAreaCoordEnd.set(numGraphs-1,graphBoundaries.get(numGraphs).intValue()-areaFromBottom - marginBetweenGraphs);
            yPlotCoordStart.set(numGraphs-1,yAreaCoordStart.get(numGraphs-1) + topAreaMargin + yPointMargin);
            yPlotCoordEnd.set(numGraphs-1,yAreaCoordEnd.get(numGraphs-1) - bottomAreaMargin - yPointMargin);
            yPlotCoordHeight.set(numGraphs-1,yPlotCoordEnd.get(numGraphs-1)-yPlotCoordStart.get(numGraphs-1));
        }
        
        //Only calculates reference coordinates if calculateLabels() was called
        if (xReferenceValues != null) {
            double[] xRefCoords = new double[xReferenceValues.size()];
            for (int i = 0; i < xRefCoords.length; i++) {
                xRefCoords[i] = scaledX1(xReferenceValues.getDouble(i));
            }
            xReferenceCoords = new ArrayDouble(xRefCoords);
        }
        yReferenceCoords = new ArrayList<ListDouble>();
        if (yReferenceValues != null) {
            for(int a = 0; a < yReferenceValues.size(); a++){
                double[] yRefCoords = new double[yReferenceValues.get(a).size()];
                for (int b = 0; b < yRefCoords.length; b++) {
                    if(separateAreas){
                        yRefCoords[b] = scaledYSplit(yReferenceValues.get(a).getDouble(b),a);
                    }else{
                        yRefCoords[b] = scaledY(yReferenceValues.get(a).getDouble(b),a);
                    }
                }
                yReferenceCoords.add(new ArrayDouble(yRefCoords));
            }
        }
    }
    
    private final double scaledY(double value,  int index) {
        return yValueScale.scaleValue(value, yPlotValueStart.get(index), yPlotValueEnd.get(index), super.yPlotCoordEnd, super.yPlotCoordStart);
    }
    
    private final double scaledYSplit(double value,  int index) {
        return yValueScale.scaleValue(value, yPlotValueStart.get(index), yPlotValueEnd.get(index), yPlotCoordEnd.get(index), yPlotCoordStart.get(index));
    }
    
    @Override
    protected void drawGraphArea() {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // When drawing the reference line, align them to the pixel
        if(separateAreas){
            drawVerticalReferenceLinesSplit();
            drawHorizontalReferenceLinesSplit();
        
            drawYLabelsSplit();
            drawXLabelsSplit();
        }else{
            drawVerticalReferenceLines();
            drawHorizontalReferenceLines();
        
            drawYLabels();
            drawXLabels();
        }
    }
    
    
    @Override
    protected void drawVerticalReferenceLines() {
        g.setColor(referenceLineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        ListNumber xTicks = xReferenceCoords;
        for (int i = 0; i < xTicks.size(); i++) {
            Shape line = new Line2D.Double(xTicks.getDouble(i), super.yAreaCoordStart, xTicks.getDouble(i), super.yAreaCoordEnd - 1);
            g.draw(line);
        }
        int count = 0;
        for(int i = 0; i < numGraphs; i+=2){
            g.setColor(new Color(valueColorSchemeInstance.colorFor(i)));
            Shape line = new Line2D.Double((xAreaCoordStart - (count+1)*(yLabelMargin + 1) - count*(yLabelMaxWidth + yLabelMargin)), super.yAreaCoordStart, (xAreaCoordStart - (count+1)*(yLabelMargin + 1) - count*(yLabelMaxWidth + yLabelMargin)), super.yAreaCoordEnd - 1);
            g.draw(line);
            count++;
        }
        count = 0;
        for(int i = 1; i < numGraphs; i+=2){
            g.setColor(new Color(valueColorSchemeInstance.colorFor(i)));
            Shape line = new Line2D.Double((xAreaCoordEnd + (count+1)*(yLabelMargin + 1) + count*(yLabelMaxWidth + yLabelMargin)), super.yAreaCoordStart, (xAreaCoordEnd + (count+1)*(yLabelMargin + 1) + count*(yLabelMaxWidth + yLabelMargin)), super.yAreaCoordEnd - 1);
            g.draw(line);
            count++;
        }
    }
    
    @Override
    protected void drawHorizontalReferenceLines() {
        g.setColor(referenceLineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        ListNumber yTicks = yReferenceCoords.get(0);
        for (int b = 0; b < yTicks.size(); b++) {
            Shape line = new Line2D.Double(xAreaCoordStart, yTicks.getDouble(b), xAreaCoordEnd - 1, yTicks.getDouble(b));
            g.draw(line);
        }
    }
    
    protected void drawVerticalReferenceLinesSplit() {
        g.setColor(referenceLineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        ListNumber xTicks = xReferenceCoords;
        for(int a = 0; a < numGraphs; a++){
            for (int b = 0; b < xTicks.size(); b++) {
                Shape line = new Line2D.Double(xTicks.getDouble(b), yAreaCoordStart.get(a), xTicks.getDouble(b), yAreaCoordEnd.get(a) - 1);
                g.draw(line);
            }
        }
    }
    
    protected void drawHorizontalReferenceLinesSplit() {
        g.setColor(referenceLineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        for(int a = 0; a < numGraphs; a++){
            ListNumber yTicks = yReferenceCoords.get(a);
            for (int b = 0; b < yTicks.size(); b++) {
                Shape line = new Line2D.Double(xAreaCoordStart, yTicks.getDouble(b), xAreaCoordEnd - 1, yTicks.getDouble(b));
                g.draw(line);
            }
        }
    }
    
    @Override
    protected void drawYLabels() {
        // Draw Y labels
        int evenCount = 0;
        int oddCount = 0;
        for(int a = 0; a < numGraphs; a++){
            ListNumber yTicks = yReferenceCoords.get(a);
            if (yReferenceLabels.get(a) != null && !yReferenceLabels.get(a).isEmpty()) {
                //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g.setColor(labelColor);
                g.setFont(labelFont);
                FontMetrics metrics = g.getFontMetrics();

                // Draw first and last label
                int verticalLinePos;
                int[] drawRange = new int[] {super.yAreaCoordStart, super.yAreaCoordEnd - 1};
                int xRightLabel;
                if(a % 2 == 0){
                    xRightLabel = (int) (xAreaCoordStart - (evenCount+1)*(yLabelMargin + 1)*2 - evenCount*(yLabelMaxWidth - 1));
                    verticalLinePos = (xAreaCoordStart - (evenCount+1)*(yLabelMargin + 1) - evenCount*(yLabelMaxWidth + yLabelMargin));
                    evenCount++;
                }
                else{
                    xRightLabel = (int) (xAreaCoordEnd + (oddCount+1)*(yLabelMargin + 1)*2 + (oddCount+1)*(yLabelMaxWidth) - oddCount - 1);
                    verticalLinePos = (xAreaCoordEnd + (oddCount+1)*(yLabelMargin + 1) + oddCount*(yLabelMaxWidth + yLabelMargin));
                    oddCount++;
                }
                drawHorizontalReferencesLabel(g, metrics, yReferenceLabels.get(a).get(0), (int) Math.floor(yTicks.getDouble(0)),
                    drawRange, xRightLabel, true, false, verticalLinePos);
                drawHorizontalReferencesLabel(g, metrics, yReferenceLabels.get(a).get(yReferenceLabels.get(a).size() - 1), (int) Math.floor(yTicks.getDouble(yReferenceLabels.get(a).size() - 1)),
                    drawRange, xRightLabel, false, false, verticalLinePos);

                for (int b = 1; b < yReferenceLabels.get(a).size() - 1; b++) {
                    drawHorizontalReferencesLabel(g, metrics, yReferenceLabels.get(a).get(b), (int) Math.floor(yTicks.getDouble(b)),
                        drawRange, xRightLabel, true, false, verticalLinePos);
                }
            }
        }
    }
    
    protected void drawYLabelsSplit() {
        // Draw Y labels
        for(int a = 0; a < numGraphs; a++){
            ListNumber yTicks = yReferenceCoords.get(a);
            if (yReferenceLabels.get(a) != null && !yReferenceLabels.get(a).isEmpty()) {
                //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g.setColor(labelColor);
                g.setFont(labelFont);
                FontMetrics metrics = g.getFontMetrics();

                // Draw first and last label
                int[] drawRange = new int[] {yAreaCoordStart.get(a), yAreaCoordEnd.get(a) - 1};
                int xRightLabel = (int) (xAreaCoordStart - yLabelMargin - 1);
                drawHorizontalReferencesLabelSplit(g, metrics, yReferenceLabels.get(a).get(0), (int) Math.floor(yTicks.getDouble(0)),
                    drawRange, xRightLabel, true, false);
                drawHorizontalReferencesLabelSplit(g, metrics, yReferenceLabels.get(a).get(yReferenceLabels.get(a).size() - 1), (int) Math.floor(yTicks.getDouble(yReferenceLabels.get(a).size() - 1)),
                    drawRange, xRightLabel, false, false);

                for (int b = 1; b < yReferenceLabels.get(a).size() - 1; b++) {
                    drawHorizontalReferencesLabelSplit(g, metrics, yReferenceLabels.get(a).get(b), (int) Math.floor(yTicks.getDouble(b)),
                        drawRange, xRightLabel, true, false);
                }
            }
        }
    }
    
    @Override
    protected void drawXLabels() {
        // Draw X labels
        ListNumber xTicks = xReferenceCoords;
        if (xReferenceLabels != null && !xReferenceLabels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {xAreaCoordStart, xAreaCoordEnd - 1};
            int yTop = (int) (super.yAreaCoordEnd + xLabelMargin);
            drawVerticalReferenceLabel(g, metrics, xReferenceLabels.get(0), (int) Math.floor(xTicks.getDouble(0)),
                drawRange, yTop, true, false);
            drawVerticalReferenceLabel(g, metrics, xReferenceLabels.get(xReferenceLabels.size() - 1), (int) Math.floor(xTicks.getDouble(xReferenceLabels.size() - 1)),
                drawRange, yTop, false, false);
            
            for (int i = 1; i < xReferenceLabels.size() - 1; i++) {
                drawVerticalReferenceLabel(g, metrics, xReferenceLabels.get(i), (int) Math.floor(xTicks.getDouble(i)),
                    drawRange, yTop, true, false);
            }
        }
    }
    
    private static void drawVerticalReferenceLabel(Graphics2D graphics, FontMetrics metrics, String text, int xCenter, int[] drawRange, int yTop, boolean updateMin, boolean centeredOnly) {
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
    
    protected void drawXLabelsSplit() {
        // Draw X labels
        ListNumber xTicks = xReferenceCoords;
        if (xReferenceLabels != null && !xReferenceLabels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {xAreaCoordStart, xAreaCoordEnd - 1};
            int yTop = (int) (yAreaCoordEnd.get(numGraphs-1) + xLabelMargin);
            drawVerticalReferenceLabelSplit(g, metrics, xReferenceLabels.get(0), (int) Math.floor(xTicks.getDouble(0)),
                drawRange, yTop, true, false);
            drawVerticalReferenceLabelSplit(g, metrics, xReferenceLabels.get(xReferenceLabels.size() - 1), (int) Math.floor(xTicks.getDouble(xReferenceLabels.size() - 1)),
                drawRange, yTop, false, false);
            
            for (int i = 1; i < xReferenceLabels.size() - 1; i++) {
                drawVerticalReferenceLabelSplit(g, metrics, xReferenceLabels.get(i), (int) Math.floor(xTicks.getDouble(i)),
                    drawRange, yTop, true, false);
            }
        }
    }
    
    private static final int MIN = 0;
    private static final int MAX = 1;
    private static void drawHorizontalReferencesLabel(Graphics2D graphics, FontMetrics metrics, String text, int yCenter, int[] drawRange, int xRight, boolean updateMin, boolean centeredOnly, int verticalLinePos) {
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
        graphics.drawLine(verticalLinePos - 1 , targetY, verticalLinePos + 1, targetY);
        if (updateMin) {
            drawRange[MAX] = targetY - metrics.getHeight();
        } else {
            drawRange[MIN] = targetY + metrics.getHeight();
        }
    }
    
    private static void drawHorizontalReferencesLabelSplit(Graphics2D graphics, FontMetrics metrics, String text, int yCenter, int[] drawRange, int xRight, boolean updateMin, boolean centeredOnly) {
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
    
    private static void drawVerticalReferenceLabelSplit(Graphics2D graphics, FontMetrics metrics, String text, int xCenter, int[] drawRange, int yTop, boolean updateMin, boolean centeredOnly) {
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
    
    private final double scaledX1(double value) {
        return xValueScale.scaleValue(value, xPlotValueStart, xPlotValueEnd, xPlotCoordStart, xPlotCoordEnd);
    }
    
    protected void drawValueExplicitLine(ListNumber xValues, ListNumber yValues, InterpolationScheme interpolation, ReductionScheme reduction, int index) {
        MultiAxisLineGraph2DRenderer.ScaledData scaledData;
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // Narrow the data
        int start = org.epics.util.array.ListNumbers.binarySearchValueOrLower(xValues, xPlotValueStart);
        int end = org.epics.util.array.ListNumbers.binarySearchValueOrHigher(xValues, xPlotValueEnd);
        
        xValues = ListMath.limit(xValues, start, end + 1);
        yValues = ListMath.limit(yValues, start, end + 1);
        
        switch (reduction) {
            default:
                throw new IllegalArgumentException("Reduction scheme " + reduction + " not supported");
            case NONE:
                scaledData = scaleNoReduction(xValues, yValues, start,index);
                break;
            case FIRST_MAX_MIN_LAST:
                scaledData = scaleFirstMaxMinLastReduction(xValues, yValues, start, index);
                break;
        }
        
        // create path
        Path2D path;
        switch (interpolation) {
            default:
            case NEAREST_NEIGHBOR:
                path = nearestNeighbour(scaledData);
                break;
            case LINEAR:
                path = linearInterpolation(scaledData);
                break;
            case CUBIC:
                path = cubicInterpolation(scaledData);
                break;
        }

        // Draw the line
        g.draw(path);
    }
    
    private static class ScaledData {
        private double[] scaledX;
        private double[] scaledY;
        private int start;
        private int end;
    }
    
    private MultiAxisLineGraph2DRenderer.ScaledData scaleNoReduction(ListNumber xValues, ListNumber yValues, int dataStart, int index) {
        MultiAxisLineGraph2DRenderer.ScaledData scaledData = new MultiAxisLineGraph2DRenderer.ScaledData();
        int dataCount = xValues.size();
        scaledData.scaledX = new double[dataCount];
        scaledData.scaledY = new double[dataCount];
        for (int i = 0; i < scaledData.scaledY.length; i++) {
            scaledData.scaledX[i] = scaledX1(xValues.getDouble(i));
            if(separateAreas){
                scaledData.scaledY[i] = scaledYSplit(yValues.getDouble(i), index);
            }else{
                scaledData.scaledY[i] = scaledY(yValues.getDouble(i), index);
            }
            processScaledValue(dataStart + i, xValues.getDouble(i), yValues.getDouble(i), scaledData.scaledX[i], scaledData.scaledY[i]);
        }
        scaledData.end = dataCount;
        return scaledData;
    }
    
    private MultiAxisLineGraph2DRenderer.ScaledData scaleFirstMaxMinLastReduction(ListNumber xValues, ListNumber yValues, int dataStart, int index) {
        // The number of points generated by this is about 4 times the 
        // number of points on the x axis. If the number of points is less
        // than that, it's not worth it. Don't do the data reduction.
        if (xValues.size() < xPlotCoordWidth * 4) {
            return scaleNoReduction(xValues, yValues, dataStart, index);
        }

        MultiAxisLineGraph2DRenderer.ScaledData scaledData = new MultiAxisLineGraph2DRenderer.ScaledData();
        scaledData.scaledX = new double[((int) xPlotCoordWidth + 1)*4 ];
        scaledData.scaledY = new double[((int) xPlotCoordWidth + 1)*4];
        int cursor = 0;
        int previousPixel = (int) scaledX1(xValues.getDouble(0));
        double last;
        if(separateAreas){
            last = scaledYSplit(yValues.getDouble(0),index);
        }else{
            last = scaledY(yValues.getDouble(0),index);
        }
        double min = last;
        double max = last;
        scaledData.scaledX[0] = previousPixel;
        scaledData.scaledY[0] = min;
        processScaledValue(dataStart, xValues.getDouble(0), yValues.getDouble(0), scaledX1(xValues.getDouble(0)), last);
        cursor++;
        for (int i = 1; i < xValues.size(); i++) {
            double currentScaledX = scaledX1(xValues.getDouble(i));
            int currentPixel = (int) currentScaledX;
            if (currentPixel == previousPixel) {
                if(separateAreas){
                    last = scaledYSplit(yValues.getDouble(i),index);
                }else{
                    last = scaledY(yValues.getDouble(i),index);
                }
                min = MathIgnoreNaN.min(min, last);
                max = MathIgnoreNaN.max(max, last);
                processScaledValue(dataStart + i, xValues.getDouble(i), yValues.getDouble(i), currentScaledX, last);
            } else {
                scaledData.scaledX[cursor] = previousPixel;
                scaledData.scaledY[cursor] = max;
                cursor++;
                scaledData.scaledX[cursor] = previousPixel;
                scaledData.scaledY[cursor] = min;
                cursor++;
                scaledData.scaledX[cursor] = previousPixel;
                scaledData.scaledY[cursor] = last;
                cursor++;
                previousPixel = currentPixel;
                if(separateAreas){
                    last = scaledYSplit(yValues.getDouble(i),index);
                }else{
                    last = scaledY(yValues.getDouble(i),index);
                }
                min = last;
                max = last;
                scaledData.scaledX[cursor] = currentPixel;
                scaledData.scaledY[cursor] = last;
                cursor++;
            }
        }
        scaledData.scaledX[cursor] = previousPixel;
        scaledData.scaledY[cursor] = max;
        cursor++;
        scaledData.scaledX[cursor] = previousPixel;
        scaledData.scaledY[cursor] = min;
        cursor++;
        scaledData.end = cursor;
        return scaledData;
    }
    
    private static Path2D.Double nearestNeighbour(MultiAxisLineGraph2DRenderer.ScaledData scaledData) {
        double[] scaledX = scaledData.scaledX;
        double[] scaledY = scaledData.scaledY;
        int start = scaledData.start;
        int end = scaledData.end;
        Path2D.Double line = new Path2D.Double();
        line.moveTo(scaledX[start], scaledY[start]);
        for (int i = 1; i < end; i++) {
            double halfX = scaledX[i - 1] + (scaledX[i] - scaledX[i - 1]) / 2;
            if (!java.lang.Double.isNaN(scaledY[i-1])) {
                line.lineTo(halfX, scaledY[i - 1]);
                if (!java.lang.Double.isNaN(scaledY[i]))
                    line.lineTo(halfX, scaledY[i]);
            } else {
                line.moveTo(halfX, scaledY[i]);
            }
        }
        line.lineTo(scaledX[end - 1], scaledY[end - 1]);
        return line;
    }
    
    private static Path2D.Double linearInterpolation(MultiAxisLineGraph2DRenderer.ScaledData scaledData){
        double[] scaledX = scaledData.scaledX;
        double[] scaledY = scaledData.scaledY;
        int start = scaledData.start;
        int end = scaledData.end;
        Path2D.Double line = new Path2D.Double();
        
        for (int i = start; i < end; i++) {
            // Do I have a current value?
            if (!java.lang.Double.isNaN(scaledY[i])) {
                // Do I have a previous value?
                if (i != start && !java.lang.Double.isNaN(scaledY[i - 1])) {
                    // Here I have both the previous value and the current value
                    line.lineTo(scaledX[i], scaledY[i]);
                } else {
                    // Don't have a previous value
                    // Do I have a next value?
                    if (i != end - 1 && !java.lang.Double.isNaN(scaledY[i + 1])) {
                        // There is no value before, but there is a value after
                        line.moveTo(scaledX[i], scaledY[i]);
                    } else {
                        // There is no value either before or after
                        line.moveTo(scaledX[i] - 1, scaledY[i]);
                        line.lineTo(scaledX[i] + 1, scaledY[i]);
                    }
                }
            } 
        }
        return line;
    }
    
    private static Path2D.Double cubicInterpolation(MultiAxisLineGraph2DRenderer.ScaledData scaledData){
        double[] scaledX = scaledData.scaledX;
        double[] scaledY = scaledData.scaledY;
        int start = scaledData.start;
        int end = scaledData.end;
        Path2D.Double path = new Path2D.Double();
        for (int i = start; i < end; i++) {
            
            double y1;
            double y2;
            double x1;
            double x2;
            double y0;
            double x0;
            double y3;
            double x3;
            
            double bx0;
            double by0;
            double bx3;
            double by3;
            double bdy0;
            double bdy3;
            double bx1;
            double by1;
            double bx2;
            double by2;
            //1. start at i = start
            //2. convert to bezier in the same place as you assign in the three normal cases
            //3. if statements - start with most general to most specific in the middle
            //4. can make function that converts to bezier and then you call it
            //5 check location and if you have nan in most if's
            
            //Do I have current value?
            if (!java.lang.Double.isNaN(scaledY[i])){
                //Do I have previous value?
                if (i > start && !java.lang.Double.isNaN(scaledY[i - 1])) {
                    //Do I have value two before?
                    if (i > start + 1 && !java.lang.Double.isNaN(scaledY[i - 2])) {
                        //Do I have next value?
                        if (i != end - 1 && !java.lang.Double.isNaN(scaledY[i + 1])) {
                            y2 = scaledY[i];
                            x2 = scaledX[i];
                            y0 = scaledY[i - 2];
                            x0 = scaledX[i - 2];
                            y3 = scaledY[i + 1];
                            x3 = scaledX[i + 1];
                            y1 = scaledY[i - 1];
                            x1 = scaledX[i - 1];
                            bx0 = x1;
                            by0 = y1;
                            bx3 = x2;
                            by3 = y2;
                            bdy0 = (y2 - y0) / (x2 - x0);
                            bdy3 = (y3 - y1) / (x3 - x1);
                            bx1 = bx0 + (x2 - x0) / 6.0;
                            by1 = (bx1 - bx0) * bdy0 + by0;
                            bx2 = bx3 - (x3 - x1) / 6.0;
                            by2 = (bx2 - bx3) * bdy3 + by3;
                            path.curveTo(bx1, by1, bx2, by2, bx3, by3);
                        } 
                        else{//Have current, previous, two before, but not value after
                            y2 = scaledY[i];
                            x2 = scaledX[i];
                            y1 = scaledY[i - 1];
                            x1 = scaledX[i - 1];
                            y0 = scaledY[i - 2];
                            x0 = scaledX[i - 2];
                            y3 = y2 + (y2 - y1) / 2;
                            x3 = x2 + (x2 - x1) / 2;
                            bx0 = x1;
                            by0 = y1;
                            bx3 = x2;
                            by3 = y2;
                            bdy0 = (y2 - y0) / (x2 - x0);
                            bdy3 = (y3 - y1) / (x3 - x1);
                            bx1 = bx0 + (x2 - x0) / 6.0;
                            by1 = (bx1 - bx0) * bdy0 + by0;
                            bx2 = bx3 - (x3 - x1) / 6.0;
                            by2 = (bx2 - bx3) * bdy3 + by3;
                            path.curveTo(bx1, by1, bx2, by2, bx3, by3);
                        } 
                    } else if (i != end - 1 && !java.lang.Double.isNaN(scaledY[i + 1])) {
                        //Have current , previous, and next, but not two before
                        path.moveTo(scaledX[i - 1], scaledY[i - 1]);
                        y2 = scaledY[i];
                        x2 = scaledX[i];
                        y1 = scaledY[i - 1];
                        x1 = scaledX[i - 1];
                        y0 = y1 - (y2 - y1) / 2;
                        x0 = x1 - (x2 - x1) / 2;
                        y3 = scaledY[i + 1];
                        x3 = scaledX[i + 1];
                        bx0 = x1;
                        by0 = y1;
                        bx3 = x2;
                        by3 = y2;
                        bdy0 = (y2 - y0) / (x2 - x0);
                        bdy3 = (y3 - y1) / (x3 - x1);
                        bx1 = bx0 + (x2 - x0) / 6.0;
                        by1 = (bx1 - bx0) * bdy0 + by0;
                        bx2 = bx3 - (x3 - x1) / 6.0;
                        by2 = (bx2 - bx3) * bdy3 + by3;
                        path.curveTo(bx1, by1, bx2, by2, bx3, by3);
                    }else{//have current, previous, but not two before or next
                        path.lineTo(scaledX[i], scaledY[i]);
                    }
                //have current, but not previous
                }else{
                    // No previous value
                    if (i != end - 1 && !java.lang.Double.isNaN(scaledY[i + 1])) {
                        // If we have the next value, just move, we'll draw later
                        path.moveTo(scaledX[i], scaledY[i]);
                    } else {
                        // If not, write a small horizontal line
                        path.moveTo(scaledX[i] - 1, scaledY[i]);
                        path.lineTo(scaledX[i] + 1, scaledY[i]);
                    }
                }
            }else{ //do not have current
               // Do nothing
             }
        }
        return path;
    }
}
