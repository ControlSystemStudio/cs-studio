/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figureparts;

import java.util.ArrayList;

import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.ui.util.SWTConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

/**
 * Round scale tick marks.
 * @author Xihui Chen
 */
public class RoundScaleTickMarks extends Figure {   



	/** the scale */
    private RoundScale scale;

    /** the line width for major ticks */
    protected static final int MAJOR_LINE_WIDTH = 2;
    
    /** the line width for minor ticks */
    protected static final int MINOR_LINE_WIDTH = 1;

    /** the major tick length */
    public static final int MAJOR_TICK_LENGTH = 8;
    /** the minor tick length */
    public static final int MINOR_TICK_LENGTH = 5;
    
    private double minorGridStepInRadians;
    
    private int minorTicksNumber;
    
    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param style
     *            the style
     * @param scale
     *            the scale
     */
    public RoundScaleTickMarks(RoundScale scale) {
        
        this.scale = scale;

        setForegroundColor(scale.getForegroundColor());
        this.scale.setMinorTickMarkStepHint(6);
    }

   protected void paintClientArea(Graphics graphics) {
	   graphics.translate(bounds.x, bounds.y);
	   graphics.setAntialias(SWT.ON);
	   ArrayList<Double> tickLabelPositions = scale
                .getScaleTickLabels().getTickLabelPositions(); 
       drawTickMarks(graphics, tickLabelPositions);
 
       }
   
	/**
	 * update the parameters for minor ticks
	 */
	public void updateMinorTickParas() {
		if(scale.isDateEnabled()) {
			minorTicksNumber = 6;
			minorGridStepInRadians = scale.getScaleTickLabels().getGridStepInRadians()/6.0;
			return;
		}
			
		if(scale.getScaleTickLabels().getGridStepInRadians()/5 >=
			scale.convertPixelToRadians(scale.getMinorTickMarkStepHint())){
			minorTicksNumber = 5;
			minorGridStepInRadians = scale.getScaleTickLabels().getGridStepInRadians()/5.0;
			return;
		}			
		if(scale.getScaleTickLabels().getGridStepInRadians()/4 >= 
			scale.convertPixelToRadians(scale.getMinorTickMarkStepHint())){
			minorTicksNumber = 4;
			minorGridStepInRadians = scale.getScaleTickLabels().getGridStepInRadians()/4.0;
			return;
		}
		
		minorTicksNumber = 2;
		minorGridStepInRadians = scale.getScaleTickLabels().getGridStepInRadians()/2.0;
		return;
	} 

   
   
    /**
     * Draw the X tick marks.
     * 
     * @param tickLabelPositions
     *            the tick label positions
     * @param tickLabelSide
     *            the side of tick label relative to tick marks
     * @param width
     *            the width to draw tick marks
     * @param height
     *            the height to draw tick marks
     * @param graphics
     *            the graphics context
     */
    private void drawTickMarks(Graphics graphics, ArrayList<Double> tickLabelPositions) {

    	updateMinorTickParas();
    	//add gap to avoid overlap
    	double minRadians = scale.convertPixelToRadians(2);
        // draw tick marks
        graphics.setLineStyle(SWTConstants.LINE_SOLID);
        int r = scale.getRadius();
        
        if(scale.isLogScaleEnabled()) {
        	ArrayList<Boolean> tickLabelVisibilities = 
        		scale.getScaleTickLabels().getTickVisibilities();        	
        	for (int i = 0; i < tickLabelPositions.size(); i++) {        	
                int tickLength;
                int lineWidth;
                if(tickLabelVisibilities.get(i)) {
                	lineWidth = MAJOR_LINE_WIDTH;
                	tickLength = MAJOR_TICK_LENGTH;
                }                	
                else {
                	lineWidth = MINOR_LINE_WIDTH;
                	tickLength = MINOR_TICK_LENGTH;
                }
                	
                
                double theta = tickLabelPositions.get(i);            
                Point startP = new PolarPoint(r, theta).toRelativePoint(scale.getBounds());
                Point endP; 
                if (scale.getTickLablesSide() == LabelSide.Primary)
                	endP = new PolarPoint(
                			r + tickLength, theta).toRelativePoint(scale.getBounds());
                else
                	endP = new PolarPoint(
                			r - tickLength, theta).toRelativePoint(scale.getBounds());
                graphics.setLineWidth(lineWidth);    
                graphics.drawLine(startP, endP);
        	}
        } else {
        	 for (int i = 0; i < tickLabelPositions.size(); i++) {        		
                double theta = tickLabelPositions.get(i);            
                Point startP = new PolarPoint(r, theta).toRelativePoint(scale.getBounds());
                Point endP; 
                if (scale.getTickLablesSide() == LabelSide.Primary)
                	endP = new PolarPoint(
                			r + MAJOR_TICK_LENGTH, theta).toRelativePoint(scale.getBounds());
                else
                	endP = new PolarPoint(
                			r - MAJOR_TICK_LENGTH, theta).toRelativePoint(scale.getBounds());
                graphics.setLineWidth(MAJOR_LINE_WIDTH);     
                graphics.drawLine(startP, endP);
                
                //draw minor ticks for linear scale
                graphics.setLineWidth(MINOR_LINE_WIDTH);
                if(scale.isMinorTicksVisible()){
                	if(i>0) {
                		
                		//draw the first grid step which is start from min value
                		//draw the first grid step which is start from min value
                		if(i == 1 && (tickLabelPositions.get(0) - tickLabelPositions.get(1))
                				< scale.getScaleTickLabels().getGridStepInRadians()){
                			double t = tickLabelPositions.get(1);
                			while((tickLabelPositions.get(0) - t) > minorGridStepInRadians + minRadians) {
                				t = t + minorGridStepInRadians;
                				drawMinorTick(graphics, r, t);	
                			}
                		} //draw the last grid step which is end to max value
                		else if(i == tickLabelPositions.size()-1 && 
                				(tickLabelPositions.get(i-1) - tickLabelPositions.get(i))
                				< scale.getScaleTickLabels().getGridStepInRadians()){
                			double t = tickLabelPositions.get(i-1);                			
                			while((t - tickLabelPositions.get(i)) > minorGridStepInRadians + minRadians) {
                				t = t - minorGridStepInRadians;
                				drawMinorTick(graphics, r, t);	
                			}
                		}else{ // draw regular steps
	                		for(int j =1; j<minorTicksNumber; j++) {
	                			double t =tickLabelPositions.get(i-1) +
	                				(tickLabelPositions.get(i) - tickLabelPositions.get(i-1))*j/minorTicksNumber;
	                			drawMinorTick(graphics, r, t);	                		
	                		}
                		}
                	}
                }                
        	 }        
        }
        
       
        //draw scale line
        if(scale.isScaleLineVisible()) {
        	graphics.drawArc(new Rectangle(bounds.x + bounds.width/2 - scale.getRadius(),
        			bounds.y + bounds.height/2 - scale.getRadius(),
        			scale.getRadius() *2, scale.getRadius() * 2), (int) scale.getEndAngle(), 
        			(int) scale.getLengthInDegrees());
        }
        
    }

	private void drawMinorTick(Graphics graphics, int r, double t) {
		Point minorStartP = new PolarPoint(
					r, t).toRelativePoint(scale.getBounds());
		Point minorEndP;
		if(scale.getTickLablesSide() == LabelSide.Primary)	
			 minorEndP =  new PolarPoint(
					r + MINOR_TICK_LENGTH, t).toRelativePoint(scale.getBounds());
		else
			 minorEndP =  new PolarPoint(
					r - MINOR_TICK_LENGTH, t).toRelativePoint(scale.getBounds());     			
		graphics.drawLine(minorStartP, minorEndP);
	}

    

}
