package org.csstudio.sds.components.ui.internal.figureparts;

import java.util.ArrayList;

import org.csstudio.sds.components.ui.internal.figureparts.AbstractScale.LabelSide;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;

/**
 * Linear scale tick marks.
 * @author Xihui Chen
 */
public class LinearScaleTickMarks extends Figure {   

	/**
	 * @return the minor_tick_num
	 */
	public int getMinor_tick_num() {
		if(scale.isDateEnabled())
			return 6;
		return 5;
	}

	/** the scale */
    private LinearScale scale;

    /** the line width */
    protected static final int LINE_WIDTH = 1;

    /** the tick length */
    public static final int MAJOR_TICK_LENGTH = 10;
    /** the tick length */
    public static final int MINOR_TICK_LENGTH = 4;


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
    public LinearScaleTickMarks(LinearScale scale) {
        
        this.scale = scale;

        setForegroundColor(scale.getForegroundColor());
    }


    /**
     * Gets the associated scale.
     * 
     * @return the scale
     */
    public LinearScale getAxis() {
        return scale;
    }


   protected void paintClientArea(Graphics graphics) {
	   graphics.translate(bounds.x, bounds.y);
	   ArrayList<Integer> tickLabelPositions = scale
                .getScaleTickLabels().getTickLabelPositions();

        int width = getSize().width;
        int height = getSize().height;

        if (scale.isHorizontal()) {
            drawXTickMarks(graphics, tickLabelPositions, scale.getTickLablesSide(), width,
                    height);
        } else {
            drawYTickMarks(graphics, tickLabelPositions, scale.getTickLablesSide(), width,
                    height);
        }
   };
    

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
     * @param gc
     *            the graphics context
     */
    private void drawXTickMarks(Graphics gc, ArrayList<Integer> tickLabelPositions,
            LabelSide tickLabelSide, int width, int height) {

        // draw tick marks
        gc.setLineStyle(SWT.LINE_SOLID);
        
        if(scale.isLogScaleEnabled()) {
        	ArrayList<Boolean> tickLabelVisibilities = 
        		scale.getScaleTickLabels().getTickVisibilities();        	
        	for (int i = 0; i < tickLabelPositions.size(); i++) {
                int x = tickLabelPositions.get(i);
                int y = 0;
                int tickLength =0;
                if(tickLabelVisibilities.get(i))
                	tickLength = MAJOR_TICK_LENGTH;
                else
                	tickLength = MINOR_TICK_LENGTH;

                if (tickLabelSide == LabelSide.Secondary) {
                    y = height - 1 - LINE_WIDTH - tickLength;
                }
                if(tickLabelVisibilities.get(i) || scale.isMinorTicksVisible())
                	gc.drawLine(x, y, x, y + tickLength);
        	}
        } else {
        	for (int i = 0; i < tickLabelPositions.size(); i++) {
                int x = tickLabelPositions.get(i);
                int y = 0;
                if (tickLabelSide == LabelSide.Secondary) {
                    y = height - 1 - LINE_WIDTH - MAJOR_TICK_LENGTH;
                }
                gc.drawLine(x, y, x, y + MAJOR_TICK_LENGTH);
                
                if(scale.isMinorTicksVisible()){
                	if(i>0) {
                		for(int j =0; j<getMinor_tick_num(); j++) {
                			x =tickLabelPositions.get(i-1) +
                				(tickLabelPositions.get(i) - tickLabelPositions.get(i-1))*j/getMinor_tick_num();
                			if(tickLabelSide == LabelSide.Primary)
                				gc.drawLine(x, y, x, y + MINOR_TICK_LENGTH);
                			else
                				gc.drawLine(x, y + MAJOR_TICK_LENGTH - MINOR_TICK_LENGTH, 
                						x, y + MAJOR_TICK_LENGTH);
                		}                		
                	}
                }
                
            }
        }
       
            
        

        //draw scale line
        if(scale.isScaleLineVisible()) {
        	if (tickLabelSide == LabelSide.Primary) {
            gc.drawLine(scale.getMargin(), 0, width - scale.getMargin(), 0);
        } else {
            gc.drawLine(scale.getMargin(), height - 1, width - scale.getMargin(), height - 1);
        }
        }
        
    }

    /**
     * Draw the Y tick marks.
     * 
     * @param tickLabelPositions
     *            the tick label positions
     * @param tickLabelSide
     *            the side of tick label relative to tick marks
     * @param width
     *            the width to draw tick marks
     * @param height
     *            the height to draw tick marks
     * @param gc
     *            the graphics context
     */
    private void drawYTickMarks(Graphics gc, ArrayList<Integer> tickLabelPositions,
            LabelSide tickLabelSide, int width, int height) {

        // draw tick marks
        gc.setLineStyle(SWT.LINE_SOLID);
        int x = 0;
        int y = 0;
        if(scale.isLogScaleEnabled()) {
        	ArrayList<Boolean> tickLabelVisibilities = 
        		scale.getScaleTickLabels().getTickVisibilities();        	
        	for (int i = 0; i < tickLabelPositions.size(); i++) {
        		
                int tickLength =0;
                if(tickLabelVisibilities.get(i))
                	tickLength = MAJOR_TICK_LENGTH;
                else
                 	tickLength = MINOR_TICK_LENGTH;            
                
                if (tickLabelSide == LabelSide.Primary) {
                    x = width - 1 - LINE_WIDTH - tickLength;
                } else {
                    x = LINE_WIDTH;
                }
                y = height - tickLabelPositions.get(i);
                if(tickLabelVisibilities.get(i) || scale.isMinorTicksVisible())
                	gc.drawLine(x, y, x + tickLength, y);
        	}
        } else {        
            for (int i = 0; i < tickLabelPositions.size(); i++) {
                if (tickLabelSide == LabelSide.Primary) {
                    x = width - 1 - LINE_WIDTH - MAJOR_TICK_LENGTH;
                } else {
                    x = LINE_WIDTH;
                }
                y = height - tickLabelPositions.get(i);
                gc.drawLine(x, y, x + MAJOR_TICK_LENGTH, y);
                
                if(scale.isMinorTicksVisible() && !scale.isLogScaleEnabled()){
                	if(i>0) {
                		for(int j =0; j<getMinor_tick_num(); j++) {
                			y =height - tickLabelPositions.get(i-1) -
                				(tickLabelPositions.get(i) - tickLabelPositions.get(i-1))*j/getMinor_tick_num();
                			if(tickLabelSide == LabelSide.Primary)               				
                				gc.drawLine(x + MAJOR_TICK_LENGTH - MINOR_TICK_LENGTH, y,
                					x + MAJOR_TICK_LENGTH, y);
                			else
                				gc.drawLine(x, y,
                					x + MINOR_TICK_LENGTH, y);
                		}                		
                	}
                }
            }
        }

        // draw scale line
        if(scale.isScaleLineVisible()) {
        	if (tickLabelSide == LabelSide.Primary) {
            gc.drawLine(width - 1, scale.getMargin(), width - 1, height - scale.getMargin());
        } else {
            gc.drawLine(0, scale.getMargin(), 0, height - scale.getMargin());
        }
        }
        
    }
    

}
