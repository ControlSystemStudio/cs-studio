/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figureparts;


import org.csstudio.swt.xygraph.linearscale.AbstractScale;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Round scale has the tick labels and tick marks on a circle. 
 * It can be used for any round scale based widget, such meter, gauge, knob etc. <br>
 * A round scale is comprised of Scale line, tick labels and tick marks which include
 * minor ticks and major ticks. <br>
 * The endAngle is on the clockwise side of startAngle. Regardless the startAngle and endAngle,
 * the scale will always be drawn in a square. The bounds will be automatically cropped to the
 * square with the max possible size.
 * 
 * @author Xihui Chen
 *  
 */
public class RoundScale extends AbstractScale {
	
	public static final int SPACE_BTW_MARK_LABEL = 1;
    
    
    /** the scale tick labels */
    private RoundScaleTickLabels tickLabels;

    /** the scale tick marks */
    private RoundScaleTickMarks tickMarks;

    /** the length of the whole scale in pixels */
    private int lengthInPixels;
    
    /** the length of the whole scale in degrees */
    private double lengthInDegrees;
    
    /** The estimated donut width which is used calculate the radius. */
    private int estimatedDonutWidth;
    
    /** the start angle of the scale in degrees, which is the angle position of minimum */
    private double startAngle = 225;
        
    /** the end angle of the scale in degrees, which is the angle position of maximum.
     * The end angle is in the clockwise of startAngle. */
    private double endAngle = 315;
    
    /** the radius of the scale */
    private int radius;  
    
    
//    private final Font DEFAULT_FONT = CustomMediaFactory.getInstance().getFont(
//    		CustomMediaFactory.FONT_ARIAL);
    
    /**
     * Constructor.
     */
    public RoundScale() {      
    	
        tickLabels = new RoundScaleTickLabels(this);        
        tickMarks = new RoundScaleTickMarks(this);                  
        add(tickMarks);        
        add(tickLabels);    
//        setFont(DEFAULT_FONT);
 
    }
	
	private void calcEstimatedDonutWidth() {
		estimatedDonutWidth = Math.max(FigureUtilities.getTextExtents(
					format(getRange().getLower()),getFont()).width, 
					FigureUtilities.getTextExtents(format(getRange().getUpper()), getFont()).width)
					+ SPACE_BTW_MARK_LABEL + RoundScaleTickMarks.MAJOR_TICK_LENGTH;
	}
	
	/**
	 * @return the length of the whole scale in pixels
	 */
	public int getLengthInPixels() {
		return lengthInPixels;
	}
	
	/**
	 * @return the length of the whole scale in degrees
	 */
	public double getLengthInDegrees() {
		return lengthInDegrees;
	}
	
	/**@param pixels the pixels to be converted
	 * @return the corresponding length in radians
	 */
	public double convertPixelToRadians(int pixels) {
		return lengthInDegrees * (Math.PI/180) * pixels / lengthInPixels;
	}

	

    /**
     * @return the estimated donut width, which is used to calculate the radius
     */
    public int getEstimatedDonutWidth() {
		if(isDirty())
			calcEstimatedDonutWidth();
		return estimatedDonutWidth;
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
	
		wHint = Math.min(wHint, hHint);
		hHint = wHint;
		Dimension size = new Dimension(wHint, hHint);			
		return size;
		
	}

    /**
     * Gets the scale tick labels.
     * 
     * @return the scale tick labels
     */
    public RoundScaleTickLabels getScaleTickLabels() {
        return tickLabels;
    }
    
    /**
     * Gets the scale tick marks.
     * 
     * @return the scale tick marks
     */
    public RoundScaleTickMarks getScaleTickMarks() {
        return tickMarks;
    }

	
    public double getCoercedValuePosition(double value, boolean relative){
		//coerce to range
		double min = getRange().getLower();
        double max = getRange().getUpper();
        if(max>=min)
        	value = value < min ? min : (value > max ? max : value);
        else
        	value = value > min? min: (value<max? max: value);
		return getValuePosition(value, relative);
    }
    
    /**
	 * Get the position of the value in degrees. Which is the angular coordinate in the polar 
	 * coordinate system, whose pole(the origin) is the center of the bounds, whose polar axis 
	 * is from left to right on horizontal if relative is false.    
	 * @param value the value to find its position. It can be value out of range.
	 * @param relative the polar axs would be counterclockwisely rotated to the endAngle if true.
	 * @return position in degrees
	 */
	public double getValuePosition(double value, boolean relative) {
		updateTick();

		double valuePosition;
		if(isLogScaleEnabled()) {
			if(value <=0)
				throw new IllegalArgumentException(
						"Invalid value: value must be greater than 0");
			valuePosition = startAngle - ((Math.log10(value) - Math
                    .log10(min))
                    / (Math.log10(max) - Math.log10(min)) * lengthInDegrees);
		}			
		else			
			valuePosition = startAngle - ((value - min)/(max-min)*lengthInDegrees);
		
		//rotate the axis to endAngle
		if(relative)
			valuePosition  -= endAngle;
		
		if(valuePosition < 0)
			valuePosition += 360;
		
		return valuePosition;
	}

    @Override
    protected void layout() {
    	super.layout();
    	updateTick();
      	Rectangle area = getClientArea();
      	tickLabels.setBounds(area);
      	tickMarks.setBounds(area);
      	
    } 

    @Override
    public void setBounds(Rectangle rect) {
    	if(!bounds.equals(rect))
    		setDirty(true);    	
    	//get the square in the rect
    	rect.width = Math.min(rect.width, rect.height);
    	rect.height = rect.width;
    	super.setBounds(rect);  	
    } 

   
    public void setFont(Font font) {
        if (font != null && font.isDisposed()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        tickLabels.setFont(font);
        super.setFont(font);
        
    }

    public void setForegroundColor(Color color) {
        tickMarks.setForegroundColor(color);
        tickLabels.setForegroundColor(color);
    }
	
	
	

	/**
     * Updates the tick, recalculate all parameters, such as margin, length...
     */
    public void updateTick() {
    	if(isDirty()){      		
    		//set radius
        	if(getTickLablesSide() == LabelSide.Primary) {
        		//set an estimated radius first
        		radius = bounds.width/2 - getEstimatedDonutWidth();        		
        		if(endAngle - startAngle > 0) {
        			lengthInDegrees = 360 - (endAngle - startAngle);
        			lengthInPixels =(int) (2*Math.PI*radius*( 1 - (endAngle-startAngle)/360));
        		}    			
        		else {
        			lengthInDegrees = startAngle - endAngle;
        			lengthInPixels =(int) (2*Math.PI*radius*((startAngle-endAngle)/360));
        		}    	
        		tickLabels.update(lengthInDegrees, lengthInPixels);  
        		
        		//adjust the radius so the tick labels have enough space to 
        		//be drawn inside the bounds  
        		radius -= tickLabels.getTickLabelMaxOutLength();
        	}    	
        	else
        		radius = bounds.width/2 - 1;
        	
    		
    		if(endAngle - startAngle > 0) {
    			lengthInDegrees = 360 - (endAngle - startAngle);
    			lengthInPixels =(int) (2*Math.PI*radius*( 1 - (endAngle-startAngle)/360));
    		}    			
    		else {
    			lengthInDegrees = startAngle - endAngle;
    			lengthInPixels =(int) (2*Math.PI*radius*((startAngle-endAngle)/360));
    		}    	
	    	tickLabels.update(lengthInDegrees, lengthInPixels); 
	    	
	    	setDirty(false);
    	}
    	
    }
	
	/**
     * Updates the tick layout.
     
    protected void updateLayoutData() {
        axisTickLabels.updateLayoutData();
        axisTickMarks.updateLayoutData();
    }
     */
	
	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	/**
	 * @param startAngle the startAngle to set
	 */
	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
	}

	/**
	 * @return the startAngle
	 */
	public double getStartAngle() {
		return startAngle;
	}

	/**
	 * @param endAngle the endAngle to set
	 */
	public void setEndAngle(double endAngle) {
		this.endAngle = endAngle;
	}

	/**
	 * @return the endAngle
	 */
	public double getEndAngle() {
		return endAngle;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * @return the radius
	 */
	public int getRadius() {			
		return radius;
	}
	
	/**
	 * @return the inner radius for a primary tick label side scale.  
	 */
	public int getInnerRadius() {		
		updateTick();
		return radius;
	}
}
