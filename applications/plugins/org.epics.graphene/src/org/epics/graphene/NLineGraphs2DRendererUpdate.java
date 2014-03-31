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
    
    // TODO: should take a Map<Integer, Range>
    public NLineGraphs2DRendererUpdate setRanges(List<Integer> indices, List<Range> ranges){
        if(indices.size() != ranges.size()){
            throw new IllegalArgumentException("Index list is not as long as range list");
        }
        for(int i = 0; i < indices.size(); i++){
            IndexToRangeMap.put(indices.get(i),ranges.get(i));
        }
        return this.self();
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
}
