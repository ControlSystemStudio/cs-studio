/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sjdallst
 */
public abstract class ValueColorSchemeInstanceGeneral implements ValueColorSchemeInstance{
    protected List<Color> colors = new ArrayList<Color>();
    protected List<Double> percentages = new ArrayList<Double>();
    protected int nanColor;
    protected Range range;
    
    
    /*public ValueColorSchemeInstanceGeneral(List<Color> colors, Range range, int nanColor){
        this.range = range;
        this.colors = colors;
        this.nanColor = nanColor;
        percentages = percentageRange(colors.size());
    }*/
    
    @Override
    public int colorFor(double value){
        if (Double.isNaN(value)) {
                    return nanColor;
                }
                if(range == null){
                    throw new NullPointerException("range can not be null.");
                }
                double fullRange = range.getMaximum().doubleValue() - range.getMinimum().doubleValue();
                int alpha = 0, red = 0, green = 0, blue = 0;
                if(fullRange>0){
                    for(int i = 0; i < percentages.size()-1;i++){
                        if(range.getMinimum().doubleValue()+percentages.get(i)*fullRange <= value && value <= range.getMinimum().doubleValue()+percentages.get(i+1)*fullRange){
                            double normalValue = NumberUtil.normalize(value, range.getMinimum().doubleValue()+percentages.get(i)*fullRange, range.getMinimum().doubleValue()+percentages.get(i+1)*fullRange);
                            normalValue = Math.min(normalValue, 1.0);
                            normalValue = Math.max(normalValue, 0.0);
                            alpha = 255;
                            red = (int) (colors.get(i).getRed() + (colors.get(i+1).getRed() - colors.get(i).getRed()) * normalValue);
                            green = (int) (colors.get(i).getGreen() + (colors.get(i+1).getGreen() - colors.get(i).getGreen()) * normalValue);
                            blue = (int) (colors.get(i).getBlue() + (colors.get(i+1).getBlue() - colors.get(i).getBlue()) * normalValue);
                        }
                    }
                }
                else{
                    for(int i = 0; i < percentages.size()-1;i++){
                        if(percentages.get(i) <= .5 && .5 <= percentages.get(i+1)){
                            double normalValue =0;
                            normalValue = Math.min(normalValue, 1.0);
                            normalValue = Math.max(normalValue, 0.0);
                            alpha = 255;
                            red = (int) (colors.get(i).getRed() + (colors.get(i+1).getRed() - colors.get(i).getRed()) * normalValue);
                            green = (int) (colors.get(i).getGreen() + (colors.get(i+1).getGreen() - colors.get(i).getGreen()) * normalValue);
                            blue = (int) (colors.get(i).getBlue() + (colors.get(i+1).getBlue() - colors.get(i).getBlue()) * normalValue);
                        }
                    }
                }
                return (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
    
        protected static ArrayList<Double> percentageRange(int size){
        ArrayList<Double> percentages = new ArrayList<>();
        
        percentages.add(0.0);
        
        for (int i = 1; i <= size; i++){
            percentages.add((double) i / size);
        }
        
        return percentages;
    }
    }

