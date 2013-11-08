/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figureparts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
/**
 * Round Scale tick labels.
 * @author Xihui Chen
 */
public class RoundScaleTickLabels extends Figure {

    /** the array of tick label vales */
    private ArrayList<Double> tickLabelValues;

    /** the array of tick label */
    private ArrayList<String> tickLabels;

    /** the array of tick label position in radians */
    private ArrayList<Double> tickLabelPositions;

    /** the array of visibility state of tick label */
    private ArrayList<Boolean> tickLabelVisibilities;

   /** the array of tick label area*/
    private ArrayList<Rectangle> tickLabelAreas;
    
    /** the maximum height of tick labels */
    private int tickLabelMaxOutLength;

    private double gridStepInRadians;    
    
    private RoundScale scale;  
    

    /**
     * Constructor. 
     * @param scale
     *            the round scale has this tick labels.
     */
    protected RoundScaleTickLabels(RoundScale scale) {    	
    	this.scale = scale;
        tickLabelValues = new ArrayList<Double>();
        tickLabels = new ArrayList<String>();
        tickLabelPositions = new ArrayList<Double>();
        tickLabelVisibilities = new ArrayList<Boolean>();
        tickLabelAreas = new ArrayList<Rectangle>();
        
        
        setFont(scale.getFont());
        setForegroundColor(scale.getForegroundColor());
    }  

    /**
	 * @return the gridStepInPixel
	 */
	public double getGridStepInRadians() {
		return gridStepInRadians;
	}


    /**
	 * @return if the tick label is draw outside scale's bounds, this is the 
	 * max length of the outside part 
	 */
	public int getTickLabelMaxOutLength() {		
		return tickLabelMaxOutLength;
	}
    
    /**
     * Gets the tick label positions.
     * 
     * @return the tick label positions
     */
    public ArrayList<Double> getTickLabelPositions() {
        return tickLabelPositions;
    }

    
    /**
	 * @return the tickVisibilities
	 */
	public ArrayList<Boolean> getTickVisibilities() {
		return tickLabelVisibilities;
	}
    

    /**
     * Draw the tick labels.
     * 
     * @param grahics
     *            the graphics context
     */
    private void drawTickLabels(Graphics graphics) {
        // draw tick labels
        graphics.setFont(scale.getFont());
        for (int i = 0; i < tickLabelPositions.size(); i++) {
            if (tickLabelVisibilities.get(i) == true) {
                String text = tickLabels.get(i);
                graphics.drawText(text, tickLabelAreas.get(i).x, tickLabelAreas.get(i).y);
            }
        }
    }


	/**
     * Gets the grid step.
     * 
     * @param lengthInPixels
     *            scale length in pixels
     * @param min
     *            minimum value
     * @param max
     *            maximum value
     * @return rounded value.
     */
    private double getGridStep(int lengthInPixels, final double minR, final double maxR) {
    	if((int) scale.getMajorGridStep() != 0) {
    		return scale.getMajorGridStep();
    	}
    	double min = minR, max = maxR;
        if (lengthInPixels <= 0) {
            lengthInPixels = 1;
        }
        boolean minBigger = false;
        if (min >= max) {        	
        	if(max == min)
        		max ++;
        	else{
        		minBigger = true;
        		double swap = min;
        		min = max;
        		max= swap;
        	}
//        		throw new IllegalArgumentException("min must be less than max.");
        }

        double length = Math.abs(max - min);
        double markStepHint = scale.getMajorTickMarkStepHint();
        if(markStepHint > lengthInPixels)
        	markStepHint = lengthInPixels;
        double gridStepHint = length / lengthInPixels
                * markStepHint;       	
        
        if(scale.isDateEnabled()) {
        	//by default, make the least step to be minutes
        	long timeStep = 60000l;       		
        	if (scale.getTimeUnit() == Calendar.SECOND) {
        		timeStep = 1000l;
        	} else if (scale.getTimeUnit() == Calendar.MINUTE) {
        		timeStep = 60000l;
        	}else if (scale.getTimeUnit() == Calendar.HOUR_OF_DAY) {
        		timeStep = 3600000l;
        	}else if (scale.getTimeUnit() == Calendar.DATE) {
        		timeStep = 86400000l;
        	}else if (scale.getTimeUnit() == Calendar.MONTH) {
        		timeStep = 30l*86400000l;
        	}else if (scale.getTimeUnit() == Calendar.YEAR) {
        		timeStep = 365l*86400000l;  
        	}
        	double temp = gridStepHint + (timeStep - gridStepHint%timeStep);       	
        	return temp;
        }
        
        double mantissa = gridStepHint;
        int exp = 0;
        if (mantissa < 1) {
        	if(mantissa != 0){
	            while (mantissa < 1) {
	                mantissa *= 10.0;
	                exp--;
	            }
            }
        } else {
            while (mantissa >= 10) {
                mantissa /= 10.0;
                exp++;
            }
        }

        double gridStep;
		if (mantissa > 7.5) {
			// 10*10^exp
			gridStep = 10 * Math.pow(10, exp);
		} else if (mantissa > 3.5) {
			// 5*10^exp
			gridStep = 5 * Math.pow(10, exp);
		} else if (mantissa > 1.5) {
			// 2.0*10^exp
			gridStep = 2 * Math.pow(10, exp);
		} else {
			gridStep = Math.pow(10, exp); // 1*10^exponent
		}
		if (minBigger)
			gridStep = -gridStep;
		return gridStep;
    }

    /**
     * Returns the state indicating if there is a space to draw tick label.
     * 
     * @param previousLabelArea
     *            the previously drawn tick label area.
     * @param labelArea
     * 			  the tick label's area
     * @return true if there is a space to draw tick label
     */
    private boolean hasSpaceToDraw(Rectangle previousLabelArea, Rectangle labelArea) {
    	 return labelArea.getIntersection(previousLabelArea).isEmpty(); 
    }

    /**
	 * Checks if the tick label is major tick. For example: 0.001, 0.01, 0.1, 1,
	 * 10, 100...
	 */
	private boolean isMajorTick(double tickValue) {
		if (!scale.isLogScaleEnabled()) {
			return true;
		}

		double log10 = Math.log10(tickValue);
		if (log10 == Math.rint(log10)) {
			return true;
		}

		return false;
	}

    /**
     * Calculates the value of the first argument raised to the power of the
     * second argument.
     * 
     * @param base
     *            the base
     * @param expornent
     *            the exponent
     * @return the value <tt>a<sup>b</sup></tt> in <tt>BigDecimal</tt>
     */
    private BigDecimal pow(double base, int expornent) {
        BigDecimal value;
        if (expornent > 0) {
            value = new BigDecimal(new Double(base).toString()).pow(expornent);
        } else {
            value = BigDecimal.ONE.divide(new BigDecimal(new Double(base)
                    .toString()).pow(-expornent));
        }
        return value;
    }

    
    
    /**
     * Updates the the draw area of each label.
     */
    private void updateTickLabelAreas() {
    	int  lableRadius;
    	tickLabelAreas.clear();
    	for(int i=0; i<tickLabelPositions.size(); i++) {
    		Dimension ls = FigureUtilities.getTextExtents(tickLabels.get(i), scale.getFont());
    		if(scale.getTickLablesSide() == LabelSide.Primary)	
    			lableRadius = (int) (scale.getRadius() + 
    					RoundScaleTickMarks.MAJOR_TICK_LENGTH + RoundScale.SPACE_BTW_MARK_LABEL +
    						ls.width/2.0 * Math.abs(Math.cos(tickLabelPositions.get(i))) + 
    						ls.height/2.0 * Math.abs(Math.sin(tickLabelPositions.get(i))));
    		else 		
    			lableRadius = (int) (scale.getRadius() - 
    					RoundScaleTickMarks.MAJOR_TICK_LENGTH - RoundScale.SPACE_BTW_MARK_LABEL -
    						ls.width/2.0 * Math.abs(Math.cos(tickLabelPositions.get(i))) - 
    						ls.height/2.0 * Math.abs(Math.sin(tickLabelPositions.get(i))));
    	
    		Point lp = new PolarPoint(lableRadius, tickLabelPositions.get(i)).toRelativePoint(
    				scale.getBounds());
    		tickLabelAreas.add(new Rectangle(lp.x - ls.width/2, lp.y - ls.height/2, 
    				ls.width, ls.height));   		
    	}	
    }

    /**
     * Updates tick label for normal scale.
     * 
     * @param lengthInDegrees
     *            scale length in degrees
     * @param lengthInPixels 
     * 			  scale length in pixels      
     */
    private void updateTickLabelForLinearScale(double lengthInDegrees, int lengthInPixels) {
        double min = scale.getRange().getLower();
        double max = scale.getRange().getUpper();
        boolean minBigger = max < min;
        double tickStep = getGridStep(lengthInPixels, min, max);
        gridStepInRadians = lengthInDegrees*(Math.PI/180)*tickStep/(max-min);

        double firstPosition;

        //make firstPosition as the right most of min based on tickStep 
        if (min%tickStep <= 0) {
            firstPosition = min - min % tickStep;
        } else {
            /* firstPosition = min - min % tickStep + tickStep */
            firstPosition =  min - min % tickStep + tickStep;
        }

        //add min
        if((min > firstPosition) == minBigger ) {
        	tickLabelValues.add(min);
        	if (scale.isDateEnabled()) {
                Date date = new Date((long) min);
                tickLabels.add(scale.format(date));
            } else {
                tickLabels.add(scale.format(min));
            }
        	tickLabelPositions.add(scale.getStartAngle()*Math.PI/180);        	
        }
        
        for (double b = firstPosition; max >= min ? b <= max : b>= max;
        		b = b+tickStep) {
            if (scale.isDateEnabled()) {
                Date date = new Date((long) b);
                tickLabels.add(scale.format(date));
            } else {
                tickLabels.add(scale.format(b));
            }
            tickLabelValues.add(b);

            double tickLabelPosition =  scale.getStartAngle() - ((b - min)
                    / (max - min) * lengthInDegrees);
            tickLabelPosition = tickLabelPosition * Math.PI/180;
            tickLabelPositions.add(tickLabelPosition);
        }
        
        //add max
        if((minBigger ? max < tickLabelValues.get(tickLabelValues.size()-1) :
        	max > tickLabelValues.get(tickLabelValues.size()-1) )) {
        	tickLabelValues.add(max);
        	if (scale.isDateEnabled()) {
                Date date = new Date((long) max);
                tickLabels.add(scale.format(date));
            } else {
                tickLabels.add(scale.format(max));
            }
        	tickLabelPositions.add((scale.getStartAngle()- lengthInDegrees)*Math.PI/180);
        }
    }

    /**
     * Updates tick label for log scale.
     * 
     * @param lengthInDegrees
     *            scale length in degrees 
     */
    private void updateTickLabelForLogScale(double lengthInDegrees) {
        double min = scale.getRange().getLower();
        double max = scale.getRange().getUpper();
        if(min <= 0 || max <= 0)
        	throw new IllegalArgumentException(
        			"the range for log scale must be in positive range");
        boolean minBigger = max < min;
        
        double minLog = Math.log10(min);
		int minLogDigit = (int) Math.ceil(minLog);
        int maxLogDigit = (int) Math.ceil(Math.log10(max));

        final BigDecimal minDec = new BigDecimal(new Double(min).toString());
        BigDecimal tickStep = pow(10, minLogDigit-1);        
        BigDecimal firstPosition;

        if (minDec.remainder(tickStep).doubleValue() <= 0) {
            firstPosition = minDec.subtract(minDec.remainder(tickStep));
        } else {
        	if(minBigger)
        		firstPosition = minDec.subtract(minDec.remainder(tickStep));
        	else
            firstPosition = minDec.subtract(minDec.remainder(tickStep)).add(tickStep);
        }

        //add min
        if(minDec.compareTo(firstPosition) == (minBigger? 1:-1) ) {
        	tickLabelValues.add(min);
        	if (scale.isDateEnabled()) {
                Date date = new Date((long) minDec.doubleValue());
                tickLabels.add(scale.format(date));
            } else {
                tickLabels.add(scale.format(minDec.doubleValue()));
            }
        	tickLabelPositions.add(scale.getStartAngle()*Math.PI/180);        	
        }
        
        for (int i = minLogDigit; minBigger? i>=maxLogDigit : i <= maxLogDigit; i+=minBigger?-1:1) {        	
        	if(Math.abs(maxLogDigit - minLogDigit) > 20){//if the range is too big, skip minor ticks.
	       		 BigDecimal v = pow(10,i);
	       		 if(v.doubleValue() > max)
	       			 break;
	       		 if (scale.isDateEnabled()) {
	                 Date date = new Date((long) v.doubleValue());
	                 tickLabels.add(scale.format(date));
	             } else {
	                 tickLabels.add(scale.format(v.doubleValue()));
	             }
	             tickLabelValues.add(v.doubleValue());
	
	             double tickLabelPosition = scale.getStartAngle() - ((Math.log10(v.doubleValue()) - minLog)
	                     / (Math.log10(max) - minLog) * lengthInDegrees);
	             tickLabelPosition = tickLabelPosition * Math.PI/180;
	             tickLabelPositions.add(tickLabelPosition);
       	 }else{
       		for (BigDecimal j = firstPosition; minBigger? j.doubleValue() >= pow(10, i-1)
                    .doubleValue() : j.doubleValue() <= pow(10, i).doubleValue(); j = minBigger? j.subtract(tickStep) : j.add(tickStep)) {
                if (minBigger? j.doubleValue() < max : j.doubleValue() > max) {
                    break;
                }

                if (scale.isDateEnabled()) {
                    Date date = new Date(j.longValue());
                    tickLabels.add(scale.format(date));
                } else {
                    tickLabels.add(scale.format(j.doubleValue()));
                }
                tickLabelValues.add(j.doubleValue());

                double tickLabelPosition = scale.getStartAngle() - ((Math.log10(j.doubleValue()) - minLog)
                        / (Math.log10(max) - minLog) * lengthInDegrees);
                tickLabelPosition = tickLabelPosition * Math.PI/180;
                tickLabelPositions.add(tickLabelPosition);
            }
            tickStep = minBigger? tickStep.divide(pow(10,1)) : tickStep.multiply(pow(10, 1));
            firstPosition = minBigger? pow(10,i-1) : tickStep.add(pow(10, i));
       	 }
            
        }
        
        //add max
        if(max > tickLabelValues.get(tickLabelValues.size()-1)) {
        	tickLabelValues.add(max);
        	if (scale.isDateEnabled()) {
                Date date = new Date((long) max);
                tickLabels.add(scale.format(date));
            } else {
                tickLabels.add(scale.format(max));
            }
        	tickLabelPositions.add(scale.getEndAngle()*Math.PI/180);
        }
    };

    /**
     * Gets max out length of tick label.
     */
    private void updateTickLabelMaxOutLength() {
       	int minLeft = 0;
    	int maxRight =0;
    	int minUp = 0;
    	int maxDown =0;
    	for(Rectangle rect : tickLabelAreas) {
    		if (rect.x < minLeft)
    			minLeft = rect.x;
    		if(rect.x + rect.width > maxRight)
    			maxRight = rect.x + rect.width;
    		if(rect.y < minUp )
    			minUp = rect.y;
    		if(rect.y + rect.height > maxDown)
    			maxDown = rect.y + rect.height;    		
    	}
    	
    	tickLabelMaxOutLength = Math.max(
    			Math.max(maxRight - scale.getBounds().width, -minLeft),
    			Math.max(maxDown - scale.getBounds().height, -minUp));
    	
    }



	private void updateTickLabelVisibility() {

        tickLabelVisibilities.clear();
        if (tickLabelPositions.isEmpty()) {
            return;
        }
        for (int i = 0; i < tickLabelPositions.size(); i++) {
            tickLabelVisibilities.add(Boolean.TRUE);
        }      

        // set the tick label visibility
        Rectangle previousArea = tickLabelAreas.get(0);
        String previousLabel = null;
        for (int i = 0; i < tickLabelPositions.size(); i++) {

            // check if it has space to draw
            boolean hasSpaceToDraw = true;
            if (i != 0) {
            	if(i != (tickLabelPositions.size()-1))
            		hasSpaceToDraw = hasSpaceToDraw(previousArea, tickLabelAreas.get(i)) &&
            			hasSpaceToDraw(tickLabelAreas.get(i), tickLabelAreas.get(tickLabelPositions.size()-1));
            	else
            		hasSpaceToDraw = hasSpaceToDraw(previousArea, tickLabelAreas.get(i)) &&
            					hasSpaceToDraw(tickLabelAreas.get(0), tickLabelAreas.get(i));
            }
       

            // check if the same tick label is repeated
            String currentLabel = tickLabels.get(i);
            boolean isRepeatSameTickAndNotEnd = currentLabel.equals(previousLabel)&&
        		(i!=0 && i!=tickLabelPositions.size()-1);
            previousLabel = currentLabel;

            // check if the tick label value is major
            boolean isMajorTickOrEnd = true;
            if (scale.isLogScaleEnabled()) {
                isMajorTickOrEnd = isMajorTick(tickLabelValues.get(i))
                	|| i==0 || i==tickLabelPositions.size()-1;
            }

            if (!hasSpaceToDraw || isRepeatSameTickAndNotEnd || !isMajorTickOrEnd) {
                tickLabelVisibilities.set(i, Boolean.FALSE);
            } else {
                previousArea = tickLabelAreas.get(i);
            }
        }
    }

	 @Override
    protected void paintClientArea(Graphics graphics) {
    	
    	graphics.translate(bounds.x, bounds.y);
    	drawTickLabels(graphics);
    	super.paintClientArea(graphics);
    }

	/**
     * Updates the tick labels.
     * 
     * @param lengthInDegrees
     *            scale length in degrees
     */
    protected void update(double lengthInDegrees, int lengthInPixels) {
        tickLabelValues.clear();
        tickLabels.clear();
        tickLabelPositions.clear();
        
        if (scale.isLogScaleEnabled()) {
            updateTickLabelForLogScale(lengthInDegrees);
        }else {
        	updateTickLabelForLinearScale(lengthInDegrees, lengthInPixels);
        }
        updateTickLabelAreas();
        updateTickLabelMaxOutLength();       
        updateTickLabelVisibility();
       
    }

}
