package org.csstudio.sds.components.ui.internal.figureparts;

import java.util.ArrayList;

import org.csstudio.sds.components.ui.internal.figureparts.AbstractScale.LabelSide;
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
    public static final int MAJOR_TICK_LENGTH = 10;
    /** the minor tick length */
    public static final int MINOR_TICK_LENGTH = 5;

    /** the minor tick number */
    public static final int MINOR_TICK_NUM = 5;
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
    }

   protected void paintClientArea(Graphics graphics) {
	   graphics.translate(bounds.x, bounds.y);
	   graphics.setAntialias(SWT.ON);
	   ArrayList<Double> tickLabelPositions = scale
                .getScaleTickLabels().getTickLabelPositions(); 
       drawTickMarks(graphics, tickLabelPositions);
 
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

        // draw tick marks
        graphics.setLineStyle(SWT.LINE_SOLID);
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
                
                graphics.setLineWidth(MINOR_LINE_WIDTH);
                if(scale.isMinorTicksVisible()){
                	if(i>0) {
                		for(int j =1; j<MINOR_TICK_NUM; j++) {
                			double t =tickLabelPositions.get(i-1) +
                				(tickLabelPositions.get(i) - tickLabelPositions.get(i-1))*j/MINOR_TICK_NUM;
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

    

}
