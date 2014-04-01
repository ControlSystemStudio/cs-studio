/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;
import org.epics.util.array.*;
import java.util.*;
/**
 *
 * @author sjdallst
 */
public class NLineGraphs2DRendererUpdate extends Graph2DRendererUpdate<NLineGraphs2DRendererUpdate>{
    private HashMap<Integer, Range> IndexToRangeMap = new HashMap<Integer, Range>();
    private Integer marginBetweenGraphs,
            minimumGraphHeight;
    private InterpolationScheme interpolation;
    private ReductionScheme reduction;
    
    public NLineGraphs2DRendererUpdate setRanges(HashMap<Integer, Range> map){
        IndexToRangeMap.putAll(map);
        return this.self();
    }
    
    public NLineGraphs2DRendererUpdate interpolation(InterpolationScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Interpolation scheme can't be null");
        }
        if (!NLineGraphs2DRenderer.supportedInterpolationScheme.contains(scheme)) {
            throw new IllegalArgumentException("Interpolation " + scheme + " is not supported");
        }
        this.interpolation = scheme;
        return this;
    }
    
    public NLineGraphs2DRendererUpdate dataReduction(ReductionScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Data reduction scheme can't be null");
        }
        if (!NLineGraphs2DRenderer.supportedReductionScheme.contains(scheme)) {
            throw new IllegalArgumentException("Data reduction " + scheme + " is not supported");
        }
        this.reduction = scheme;
        return this;
    }
    
    public NLineGraphs2DRendererUpdate marginBetweenGraphs(Integer margin){
        marginBetweenGraphs = margin;
        return this.self();
    }
    
    public NLineGraphs2DRendererUpdate minimumGraphHeight(Integer minimumGraphHeight){
        this.minimumGraphHeight = minimumGraphHeight;
        return this.self();
    }
    
    public HashMap<Integer, Range> getIndexToRange(){
        return IndexToRangeMap;
    }
    
    public Integer getMarginBetweenGraphs(){
        return marginBetweenGraphs;
    }
    
    public Integer getMinimumGraphHeight(){
        return minimumGraphHeight;
    }
    
    /**
     *Current interpolation scheme
     * @return the current interpolation scheme.
     */
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }

    /**
     *Current reduction scheme
     * @return the current reduction scheme.
     */
    public ReductionScheme getDataReduction() {
        return reduction;
    }
}
