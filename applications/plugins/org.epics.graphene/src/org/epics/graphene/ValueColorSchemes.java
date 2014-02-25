/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;
import java.util.ArrayList;
import java.awt.Color;
import static org.epics.graphene.ColorScheme.BONE;
import static org.epics.graphene.ColorScheme.COOL;
import static org.epics.graphene.ColorScheme.COPPER;
import static org.epics.graphene.ColorScheme.GRAY_SCALE;
import static org.epics.graphene.ColorScheme.HOT;
import static org.epics.graphene.ColorScheme.JET;
import static org.epics.graphene.ColorScheme.PINK;
import static org.epics.graphene.ColorScheme.SPRING;

/**
 * The <code>ValueColorSchemes</code> class contains several useful methods
 * to associate a <code>ValueColorScheme</code> with a <code>ColorScheme</code>.
 * It cannot be instantiated.
 * 
 * <p>The <code>ValueColorSchemes</code> class has preset colors that represent
 * a color scheme that can be associated with any range to create a value range.
 * There are various predefined scales that can be found in <code>ColorScheme</code>.
 * 
 * @author carcassi
 * @author sjdallst
 * @author asbarber
 * @author jkfeng
 */
public class ValueColorSchemes {

    /**
     * Cannot be instantiated.
     */
    private ValueColorSchemes(){
    }
    
    /**
     * Creates a Value scheme associated with the Color scheme and range.
     * An unrecognized or null <code>ColorScheme</code> is handled as gray scale.
     * @param color the color scheme to create a value scheme for
     * @param range associates colors from a color scheme for different values within the range, can not be null
     * @return a Value scheme for the specified Color scheme
     */
    public static ValueColorScheme schemeFor(ColorScheme color, final Range range){
        if (range == null){
            throw new NullPointerException("The range cannot be null.");
        }

        switch(color){
            case GRAY_SCALE:
                return ValueColorSchemes.grayScale(range);
            case JET:
                return ValueColorSchemes.jetScale(range);
            case HOT:
                return ValueColorSchemes.hotScale(range);
            case COOL:
                return ValueColorSchemes.coolScale(range);
            case SPRING:
                return ValueColorSchemes.springScale(range);
            case BONE:
                return ValueColorSchemes.boneScale(range);
            case COPPER:
                return ValueColorSchemes.copperScale(range);
            case PINK:
                return ValueColorSchemes.pinkScale(range);
            default:
                return ValueColorSchemes.grayScale(range);
        }        
    }
            
    /**
     * Returns a color scheme that varies linearly (black:white) for values within range.
     *  NaN = red:
     *  Single value case = black
     * @param range can not be null.
     * @return gray color range for the value range
     */
    public static ValueColorScheme grayScale(final Range range) {
        return singleRangeGradient(range, Color.BLACK, Color.WHITE, Color.RED);
    }
    
    /**
     * Returns a color scheme that varies linearly (dark blue:blue:cyan:yellow:red:dark red) for values within range.
     *  NaN = black:
     *  Single value case = cyan
     * @param range can not be null
     * @return jet (blue to yellow to red) color range for the value range
     */
    public static ValueColorScheme jetScale(final Range range) {
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(new Color(0,0,138)); //Dark Blue
        colors.add(Color.BLUE);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        colors.add(new Color(138,0,0)); //Dark Red
        colors.add(Color.BLACK);    //NaN
        
        return RangeGradient(range, colors, percentageRange(colors.size() - 2));
    }
    
    /**
     * Returns a color scheme that varies linearly (dark red:red:yellow:white) for values within range.
     *  NaN = blue:
     *  Single value case = red
     * @param range can not be null
     * @return hot (red to white) color range for the value range
     */
    public static ValueColorScheme hotScale(final Range range) {
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(new Color(30,0,0)); //Very Dark Red
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.WHITE);
        colors.add(Color.BLUE); //NaN
        
        return RangeGradient(range, colors, percentageRange(colors.size() - 2));
    }

    /**
     * Returns a color scheme that varies linearly (cyan:magenta) for values within range.
     *  NaN = red:
     *  Single value case = cyan
     * @param range can not be null
     * @return cool (cyan to magenta) color range for the value range
     */
    public static ValueColorScheme coolScale(final Range range) {
        ArrayList<Color> colors = new ArrayList<>();
        
        colors.add(Color.CYAN);
        colors.add(Color.MAGENTA);
        
        colors.add(Color.RED);  //NaN
        
        return RangeGradient(range, colors, percentageRange(colors.size() - 2));        
    }
    
    /**
     * Returns a color scheme that varies linearly (magenta:yellow) for values within range.
     *  NaN = red:
     *  Single value case = magenta
     * @param range can not be null
     * @return spring (magenta to yellow) color range for the value range
     */
    public static ValueColorScheme springScale(final Range range) {
        ArrayList<Color> colors = new ArrayList<>();
        
        colors.add(Color.MAGENTA);
        colors.add(Color.YELLOW);

        colors.add(Color.RED);  //NaN
        
        return RangeGradient(range, colors, percentageRange(colors.size() - 2));        
    }    
    
    /**
     * Returns a color scheme that varies linearly (black:dark blue:blue:light blue:white) for values within range.
     *  NaN = red:
     *  Single value case = blue
     * @param range can not be null
     * @return bone (black to blue to white) color range for the value range
     */
    public static ValueColorScheme boneScale(final Range range) {
        ArrayList<Color> colors = new ArrayList<>();
        
        colors.add(Color.BLACK);
        colors.add(new Color(44, 37, 101));     //Dark Blue
        colors.add(new Color(107, 115, 140));   //Blue
        colors.add(new Color(158, 203, 205));   //Pale Blue
        colors.add(Color.WHITE);

        
        colors.add(Color.RED);  //NaN
        
        return RangeGradient(range, colors, percentageRange(colors.size() - 2));        
    }    
    
    /**
     * Returns a color scheme that varies linearly (black:dark brown:brown:light brown:tan) for values within range.
     *  NaN = red:
     *  Single value case = brown
     * @param range can not be null
     * @return copper (black to brown to tan) color range for the value range
     */
    public static ValueColorScheme copperScale(final Range range) {
        ArrayList<Color> colors = new ArrayList<>();
        
        colors.add(Color.BLACK);
        colors.add(new Color(66, 41, 24));      //Dark Brown
        colors.add(new Color(173, 107, 68));    //Brown
        colors.add(new Color(239, 148, 90));    //Light Brown
        colors.add(new Color(255, 198, 123));   //Tan

        
        colors.add(Color.RED);  //NaN
        
        return RangeGradient(range, colors, percentageRange(colors.size() - 2));        
    }    
    
    /**
     * Returns a color scheme that varies linearly (dark red:dark pink:light pink) for values within range.
     *  NaN = red:
     *  Single value case = dark pink
     * @param range can not be null
     * @return pink (red to pink) color range for the value range
     */
    public static ValueColorScheme pinkScale(final Range range) {
        ArrayList<Color> colors = new ArrayList<>();
        
        colors.add(new Color(57, 0, 0));        //Dark Red
        colors.add(new Color(189, 123, 123));   //Dark Pink
        colors.add(new Color(214, 189, 156));   //Pale Pink
        colors.add(Color.WHITE);

        
        colors.add(Color.RED);  //NaN
        
        return RangeGradient(range, colors, percentageRange(colors.size() - 2));        
    }    
    
    
    /**
     * Returns a ValueColorScheme that varies linearly from one color to the next, based on range.
     * @param range can not be null
     * @param minValueColor color that will be used for the lowest value in range. Can not be null.
     * @param maxValueColor color that will be used for the highest value in range. Can not be null. 
     * @param nanColor color to be returned when value is NaN.
     * @return linear value to color transformation scheme between two colors
     */
    public static ValueColorScheme singleRangeGradient(final Range range, final Color minValueColor, final Color maxValueColor, final Color nanColor) {
        if (range == null) {
            throw new NullPointerException("Range should not be null");
        }
        if (minValueColor == null){
            throw new NullPointerException("The minimum color value should not be null");
        }
        if (maxValueColor == null){
            throw new NullPointerException("The maximum color value should not be null");
        }
        if (nanColor == null){
            throw new NullPointerException("The 'not a number' default color value should not be null");
        }
        
        return new ValueColorScheme() {

            @Override
            public int colorFor(double value) {
                if (Double.isNaN(value)) {
                    return nanColor.getRGB();
                }
                double normalValue = NumberUtil.normalize(value, range.getMinimum().doubleValue(), range.getMaximum().doubleValue());
                normalValue = Math.min(normalValue, 1.0);
                normalValue = Math.max(normalValue, 0.0);
                int alpha = 255;
                int red = (int) (minValueColor.getRed() + (maxValueColor.getRed() - minValueColor.getRed()) * normalValue);
                int green = (int) (minValueColor.getGreen() + (maxValueColor.getGreen() - minValueColor.getGreen()) * normalValue);
                int blue = (int) (minValueColor.getBlue() + (maxValueColor.getBlue() - minValueColor.getBlue()) * normalValue);
                return (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        };
    }

    /**
     * Returns a ValueColorScheme that varies linearly from color to color based on percentage.
     * @param range can not be null
     * @param colors an ArrayList<Color> that specifies what colors ValueColorScheme will use. The last value should correspond to the color to be used when value is NaN.
     * @param percentages an ArrayList<Double> that corresponds to colors, specifying what color corresponds to what percentage of range.
     * @return linear value to color transformation scheme between multiple colors
     */
    public static ValueColorScheme RangeGradient(final Range range, final ArrayList<Color> colors, final ArrayList<Double> percentages){
        return new ValueColorScheme() {
            
            private Color nanColor = colors.get(colors.size()-1); 
            @Override
            public int colorFor(double value) {
                if (Double.isNaN(value)) {
                    return nanColor.getRGB();
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
        };
    }
    
    /**
     * Creates a list for the cumulative percentages for n equally sized bins.
     * For any color, the bin size is 1 / n.
     * For color i, the cumulative percentage would be i / n.
     * 
     * Based on the number of colors, each color gets an equal bin of 100%.
     * 
     * Example:
     *      size = 3
     *      colors = {red, blue, yellow}
     *      percentages = {0.33, 0.67, 1.00}
     * @param size number of 'bins' to divide 100% into
     * @return list of cumulative percentages 
     */
    private static ArrayList<Double> percentageRange(int size){
        ArrayList<Double> percentages = new ArrayList<>();
        
        percentages.add(0.0);
        
        for (int i = 1; i <= size; i++){
            percentages.add((double) i / size);
        }
        
        return percentages;
    }
}
