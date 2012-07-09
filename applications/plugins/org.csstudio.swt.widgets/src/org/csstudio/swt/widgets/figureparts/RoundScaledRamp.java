/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figureparts;

import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A ramp looks like a colorful donut, which is used to indicate the alarm limit, hihi, hi, lo or lolo.
 * The ramp is based on a round scale which is in the same polar coordinate system as the ramp.
 * The ramp could be used for any round scale based widgets, such as meter, gauge and knob etc.
 * @author Xihui Chen
 *
 */
public class RoundScaledRamp extends Figure {

	private static final int OVERLAP_DEGREE = 2;

	/**
	 * The alarm thereshold for a PV, includs HIHI, HI, LO or LOLO. 
	 */
	public enum Threshold{
		HIHI, 
		HI,
		LO,
		LOLO		
	}
	
	private RoundScale scale;
	private ThresholdMarker lolo = new ThresholdMarker(10, CustomMediaFactory.COLOR_RED, true);
	private ThresholdMarker lo = new ThresholdMarker(25, CustomMediaFactory.COLOR_ORANGE, true);
	private ThresholdMarker hi = new ThresholdMarker(75, CustomMediaFactory.COLOR_ORANGE, true);
	private ThresholdMarker hihi = new ThresholdMarker(90, CustomMediaFactory.COLOR_RED, true);
	
	//the middle value in the normal range, for internal use only
	private ThresholdMarker normal = new ThresholdMarker(50, CustomMediaFactory.COLOR_GREEN, true);
	private ThresholdMarker min = new ThresholdMarker(0, null, true);	
	private ThresholdMarker max = new ThresholdMarker(100, null, true);
	private int rampWidth = 20;
	private boolean gradient = true;
	
	private boolean dirty = true;
	
	/**
	 * Constructor
	 * @param scale the round scale
	 */
	public RoundScaledRamp(RoundScale scale) {
		this.scale = scale;
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

    @Override
    public Dimension getPreferredSize(int wHint, int hHint) {
    	wHint = Math.min(wHint, hHint);
		hHint = wHint;
		Dimension size = new Dimension(wHint, hHint);			
		return size;
    }
    
    /**
     * update the the position for each threshold, and other parameters related to the positions.  
     */
    private void updateThresholdPosition(){
    	if(dirty){   
    		//get normal value
    		double lowLimit;
    		double upLimit;
    		if(lo.visible)
    			lowLimit = lo.value;
    		else if(lolo.visible)
    			lowLimit = lolo.value;
    		else
    			lowLimit = scale.getRange().getLower();
    		
    		if(hi.visible)
    			upLimit = hi.value;
    		else if(hihi.visible)
    			upLimit = hihi.value;
    		else
    			upLimit = scale.getRange().getUpper();
    		
    		//update normal
    		normal.value = (lowLimit + upLimit)/2;   	
    		normal.absolutePosition = (int) scale.getCoercedValuePosition(normal.value, false);
    		normal.relativePosition = (int) scale.getCoercedValuePosition(normal.value, true);
    		normal.rightPoint = new PolarPoint(
    					bounds.width/2, (normal.absolutePosition - OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);
    		normal.leftPoint = new PolarPoint(
    					bounds.width/2, (normal.absolutePosition + OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);    	
    		
    		//update min, max
    		if(scale.getRange().isMinBigger()){
    			min.value = scale.getRange().getUpper();
    			max.value = scale.getRange().getLower();
    		}
    		else{
    			min.value = scale.getRange().getLower();
    			max.value = scale.getRange().getUpper();
    		}
    		min.absolutePosition = (int) scale.getCoercedValuePosition(min.value, false);
    		min.relativePosition = (int) scale.getCoercedValuePosition(min.value, true);
    		max.absolutePosition = (int) scale.getCoercedValuePosition(max.value, false);
    		max.relativePosition = (int) scale.getCoercedValuePosition(max.value, true);
    		
    		//update lolo, lo, hi, hihi
    		if(lolo.visible){
    			lolo.absolutePosition = (int) scale.getCoercedValuePosition(lolo.value, false);
    			lolo.relativePosition = (int) scale.getCoercedValuePosition(lolo.value, true);
    			lolo.rightPoint = new PolarPoint(
    					bounds.width/2, (lolo.absolutePosition - OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);
    			lolo.leftPoint = new PolarPoint(
    					bounds.width/2, (lolo.absolutePosition + OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);
    		}    			
    		if(lo.visible) {
    			lo.absolutePosition = (int) scale.getCoercedValuePosition(lo.value, false);
    			lo.relativePosition = (int) scale.getCoercedValuePosition(lo.value, true);
    			lo.rightPoint = new PolarPoint(
    					bounds.width/2, (lo.absolutePosition - OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);
    			lo.leftPoint = new PolarPoint(
    					bounds.width/2, (lo.absolutePosition + OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);
    		}    			
    		if(hi.visible){    			
    			hi.absolutePosition = (int) scale.getCoercedValuePosition(hi.value, false);
    			hi.relativePosition = (int) scale.getCoercedValuePosition(hi.value, true);
    			hi.rightPoint = new PolarPoint(
    					bounds.width/2, (hi.absolutePosition - OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);
    			hi.leftPoint = new PolarPoint(
    					bounds.width/2, (hi.absolutePosition + OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);
    		}    		
    		if(hihi.visible){
    			hihi.absolutePosition = (int) scale.getCoercedValuePosition(hihi.value, false);
    			hihi.relativePosition = (int) scale.getCoercedValuePosition(hihi.value, true);
    			hihi.rightPoint = new PolarPoint(
    					bounds.width/2, (hihi.absolutePosition - OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);
    			hihi.leftPoint = new PolarPoint(
    					bounds.width/2, (hihi.absolutePosition + OVERLAP_DEGREE)*Math.PI/180).toAbsolutePoint(bounds);
    		}    	
    		setDirty(false);
    	}
    	
    }

    @Override
    protected void paintClientArea(Graphics graphics) {
    	updateThresholdPosition();
    	graphics.setAntialias(SWT.ON);
    	graphics.setLineWidth(rampWidth);
    	graphics.pushState();
    	int overlap = 0;
    	Pattern pattern = null;
    	boolean support3D = GraphicsUtil.testPatternSupported(graphics);    	
    	//draw lolo part
    	if(lolo.visible){    		
  			graphics.setBackgroundColor(lolo.color);  			
  			graphics.fillArc(bounds, lolo.absolutePosition, min.relativePosition - lolo.relativePosition);
    		
    	}
    	//draw lo part
    	if(lo.visible){
    		if(support3D && gradient && lolo.visible){
    				try {
						pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), lolo.leftPoint.x, lolo.leftPoint.y, 
								lo.rightPoint.x, lo.rightPoint.y, lolo.color, lo.color);
						graphics.setBackgroundPattern(pattern);    		
						overlap = OVERLAP_DEGREE/2;
					} catch (Exception e) {
						support3D = false;
						pattern.dispose();
						graphics.setBackgroundColor(lo.color);
						overlap = 0;
					}
    		} else {
    			graphics.setBackgroundColor(lo.color);
    			overlap = 0;
    		}
    		
    		if(lolo.visible)
    			graphics.fillArc(bounds, lo.absolutePosition, 
    					lolo.relativePosition - lo.relativePosition + overlap);
    		else
    			graphics.fillArc(bounds, lo.absolutePosition, min.relativePosition - lo.relativePosition);
    		if(gradient && lolo.visible && support3D)
    			pattern.dispose();    		
    	}
    	
    	//draw left normal part
    	//get the left marker
    	boolean leftMarkerVisible = false;
    	ThresholdMarker leftMarker = null;
    	if(lo.visible){
    		leftMarkerVisible = true;
    		leftMarker = lo;
    	} else if (lolo.visible){
    		leftMarkerVisible =true;
    		leftMarker = lolo;    		
    	} else 
    		leftMarkerVisible = false;
    	
    	if(gradient && leftMarkerVisible && support3D){
    		pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), leftMarker.leftPoint.x, leftMarker.leftPoint.y, 
    				normal.rightPoint.x, normal.rightPoint.y, leftMarker.color, normal.color);
    		graphics.setBackgroundPattern(pattern);    		
    		overlap = OVERLAP_DEGREE/2;
    	} else {
    		graphics.setBackgroundColor(normal.color);
    		overlap = 0;
    	}
    		
    	if(leftMarkerVisible)
    		graphics.fillArc(bounds, normal.absolutePosition, 
    				leftMarker.relativePosition - normal.relativePosition + overlap);
    	else
    		graphics.fillArc(bounds, normal.absolutePosition, min.relativePosition - normal.relativePosition);
    	
    	if(gradient && leftMarkerVisible && support3D)
    		pattern.dispose();   		
    	
    	//draw right normal part
    	//get the right marker
    	boolean rightMarkerVisible = false;
    	ThresholdMarker rightMarker = null;
    	if(hi.visible){
    		rightMarkerVisible = true;
    		rightMarker = hi;
    	} else if (hihi.visible){
    		rightMarkerVisible =true;
    		rightMarker = hihi;    		
    	} else 
    		rightMarkerVisible = false;
    	
    	if(gradient && rightMarkerVisible && support3D){
    		pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), rightMarker.rightPoint.x, rightMarker.rightPoint.y, 
    				normal.leftPoint.x, normal.leftPoint.y, rightMarker.color, normal.color);
    		graphics.setBackgroundPattern(pattern);    		
    		overlap = OVERLAP_DEGREE/2;
    	} else {
    		graphics.setBackgroundColor(normal.color);
    		overlap = 0;
    	}
    		
    	if(rightMarkerVisible)
    		graphics.fillArc(bounds, rightMarker.absolutePosition, 
    				normal.relativePosition - rightMarker.relativePosition + overlap + 1);
    	else
    		graphics.fillArc(bounds, max.absolutePosition, 
    				normal.relativePosition - max.relativePosition +1);
    	
    	if(gradient && rightMarkerVisible && support3D)
    		pattern.dispose();
    	
    	
    	//draw hi part
    	if(hi.visible){
    		if(hihi.visible){
	    		rightMarkerVisible = true;
	    		rightMarker = hihi;   	
	    	} else 
	    		rightMarkerVisible = false;
	    	
	    	if(gradient && rightMarkerVisible && support3D){
	    		pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), rightMarker.rightPoint.x, rightMarker.rightPoint.y, 
	    				hi.leftPoint.x, hi.leftPoint.y, rightMarker.color, hi.color);
	    		graphics.setBackgroundPattern(pattern);    		
	    		overlap = OVERLAP_DEGREE/2;
	    	} else {
	    		graphics.setBackgroundColor(hi.color);
	    		overlap = 0;
	    	}
	    		
	    	if(rightMarkerVisible)
	    		graphics.fillArc(bounds, rightMarker.absolutePosition, 
	    				hi.relativePosition - rightMarker.relativePosition + overlap);
	    	else
	    		graphics.fillArc(bounds, max.absolutePosition, 
	    				hi.relativePosition - max.relativePosition);
	    	
	    	if(gradient && rightMarkerVisible && support3D)
	    		pattern.dispose();
    	}
    	
    	
    	//draw hihi part
    	if(hihi.visible){
    		if(gradient && support3D)
    			overlap = OVERLAP_DEGREE/2;
    		else
    			overlap = 0;
    		graphics.setBackgroundColor(hihi.color);
    		graphics.fillArc(bounds, max.absolutePosition, 
    				hihi.relativePosition - max.relativePosition + overlap);
    	}
    	
    	graphics.popState();    	
    	graphics.fillOval(bounds.x + rampWidth, bounds.y + rampWidth, 
    			bounds.width-2*rampWidth,bounds.height - 2*rampWidth);
    	
    	super.paintClientArea(graphics);    	
    }
    
	
	
	/**
	 * @return the round scale for this ramp
	 */
	public RoundScale getScale() {
		return scale;
	}
	/**
	 * @param scale the round scale to set
	 */
	public void setScale(RoundScale scale) {
		this.scale = scale;
		setDirty(true);
	}
	/**
	 * @return the rampWidth
	 */
	public int getRampWidth() {
		return rampWidth;
	}
	/**
	 * @param rampWidth the rampWidth to set
	 */
	public void setRampWidth(int rampWidth) {
		this.rampWidth = rampWidth;
		setDirty(true);
	}
	
	
	/**
	 * If gradient is true, the color will be displayed in gradient style
	 * @param gradient the gradient to set
	 */
	public void setGradient(boolean gradient) {
		this.gradient = gradient;
		setDirty(true);
	}
	
	/**
	 * Set value of the threshold.
	 * @param thresholdName the threshold name which should be one of {@link Threshold}
	 * @param value the value to set
	 */
	public void setThresholdValue(Threshold thresholdName, double value){
		switch (thresholdName) {
		case HIHI:
			hihi.value = value;
			break;
		case HI:
			hi.value =value;
			break;
		case LO:
			lo.value = value;
			break;
		case LOLO:
			lolo.value = value;			
		default:
			break;
		}
		setDirty(true);
	}


	/**
	 * Set color of the threshold.
	 * @param thresholdName the threshold name which should be one of {@link Threshold}
	 * @param color the RGB color to set
	 */
	public void setThresholdColor(Threshold thresholdName, RGB color){
		switch (thresholdName) {
		case HIHI:
			hihi.setColor(color);
			break;
		case HI:
			hi.setColor(color);
			break;
		case LO:
			lo.setColor(color);
			break;
		case LOLO:
			lolo.setColor(color);			
		default:
			break;
		}
	}
	
	
	/**
	 * Set visibility of the threshold.
	 * @param thresholdName the threshold name which should be one of {@link Threshold}
	 * @param visible true if this threshold should be visible
	 */
	public void setThresholdVisibility(Threshold thresholdName, boolean visible){
		switch (thresholdName) {
		case HIHI:
			hihi.visible = visible;
			break;
		case HI:
			hi.visible =visible;
			break;
		case LO:
			lo.visible = visible;
			break;
		case LOLO:
			lolo.visible = visible;			
		default:
			break;
		}
		setDirty(true);
	}
	
	
	
	/**
	 * @param dirty the dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Hold the properties for each threshold.
	 * @author Xihui Chen
	 */
	static class ThresholdMarker {	
		
		private double value;		
		private Color color;		
		private boolean visible;
		
		/** Its  absolute degree position on the scale */
		private int absolutePosition;
		
		/** Its relative degree position on the scale */
		private int relativePosition;
		
		/** The right overlap point. Only used for gradient */
		private Point rightPoint;
		
		/** The left overlap point. Only used for gradient */
		private Point leftPoint;
		
		/**
		 * @param value
		 * @param color
		 * @param visible
		 */
		public ThresholdMarker(double value, RGB color, boolean visible) {
			this.value = value;
			if(color != null)
				this.color = CustomMediaFactory.getInstance().getColor(color);
			this.visible = visible;
		}

		/**
		 * @param color the RGB color to set
		 */
		public void setColor(RGB color) {
			this.color = CustomMediaFactory.getInstance().getColor(color);
		}

	
}

}
