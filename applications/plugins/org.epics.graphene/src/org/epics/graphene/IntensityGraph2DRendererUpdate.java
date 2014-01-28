/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 * A set of parameters that can be applied to a <code>IntensityGraph2DRenderer</code>
 * to update its settings.
 * 
 * <p>
 * Only the parameters that are set in the update get applied to the renderer.
 * Parameters unique to the intensity graph that can be changed are:
 * <ul>
 *          <li>zLabel margin size</li>
 *          <li>Legend pixel width</li>
 *          <li>Legend margin size between the legend and graph</li>
 *          <li>Legend margin size between the legend and the image edge</li>
 * </ul>
 * 
 * @author carcassi
 * @author sjdallst
 */
public class IntensityGraph2DRendererUpdate extends Graph2DRendererUpdate<IntensityGraph2DRendererUpdate> {

    private Integer zLabelMargin,
                    legendWidth,
                    legendMarginToGraph,
                    legendMarginToEdge;
    
      private Boolean drawLegend;
    
    private ColorScheme valueColorScheme;
    
    /**
     * Sets this object's drawLegend to the given boolean value.
     * To be used in conjunction with IntensityGraph2DRenderer's update function.
     * @param drawLegend boolean that will tell IntensityGraph2DRenderer whether or not it should draw a legend
     * @return this
     */
    public IntensityGraph2DRendererUpdate drawLegend(boolean drawLegend) {
        this.drawLegend = drawLegend;
        return self();
    }

    /**
     * Sets this object's valueColorScheme to the given ColorScheme.
     * @param scheme supported schemes: any <code>ColorScheme</code> supported by the <code>ValueColorSchemes</code>
     * @return this
     */
    public IntensityGraph2DRendererUpdate valueColorScheme(ColorScheme scheme) {
        this.valueColorScheme = scheme;
        return self();
    }
    
    /**
     * Sets this object's zLabelMargin to the given margin size.
     * @param margin integer distance(pixels) from the beginning of the z labels to the legend. 
     * @return this
     */
    public IntensityGraph2DRendererUpdate zLabelMargin(int margin) {
        this.zLabelMargin = margin;
        return self();
    }
    
    /**
     * Sets this object's legendWidth to the given margin size.
     * @param width corresponds to the x-axis
     * @return this
     */
    public IntensityGraph2DRendererUpdate legendWidth(int width) {
        this.legendWidth = width;
        return self();
    }
    
    /**
     * Sets this object's legendMarginToGraph to the given margin size.
     * @param margin distance(pixels) from the intensity graph to the legend.
     * @return this
     */
    public IntensityGraph2DRendererUpdate legendMarginToGraph(int margin) {
        this.legendMarginToGraph = margin;
        return self();
    }
    
    /**
     * Sets this object's legendMarginToEdge to the given margin size.
     * @param margin distance(pixels) from the end of the legend(including labels and other margins) to the end of the graphics component.
     * @return this
     */
    public IntensityGraph2DRendererUpdate legendMarginToEdge(int margin) {
        this.legendMarginToEdge = margin;
        return self();
    }
    
    /**
     *
     * @return Boolean drawLegend, used to determine whether an IntensityGraph2DRenderer object will add a legend to the right of the intensity graph. Can be null.
     */
    public Boolean getDrawLegend() {
        return drawLegend;
    }
    
    /**
     *
     * @return ColorScheme valueColorScheme, used to determine which color scheme will be used when drawing an intensity graph. 
     * Possible values include: GRAY_SCALE, JET, HOT, COOL, SPRING, BONE, COPPER, PINK
     */
    public ColorScheme getValueColorScheme() {
        return valueColorScheme;
    }
    
    /**
     *
     * @return Integer zLabelMargin, distance(pixels) from the beginning of the z labels to the legend.
     */
    public Integer getZLabelMargin(){
        return zLabelMargin;
    }
    
    /**
     *
     * @return Integer legendWidth, corresponds to the x-axis length.
     */
    public Integer getLegendWidth(){
        return legendWidth;
    }
    
    /**
     *
     * @return Integer legendMarginToGraph, distance(pixels) from the intensity graph to the legend.
     */
    public Integer getLegendMarginToGraph(){
        return legendMarginToGraph;
    }
    
    /**
     *
     * @return Integer legendMarginToEdge, distance(pixels) from the end of the legend(including labels and other margins) to the end of the graphics component.
     */
    public Integer getLegendMarginToEdge(){
        return legendMarginToEdge;
    }
}
