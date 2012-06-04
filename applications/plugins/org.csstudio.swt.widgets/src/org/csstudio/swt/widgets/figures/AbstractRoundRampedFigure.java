/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;


import org.csstudio.swt.widgets.figureparts.RoundScale;
import org.csstudio.swt.widgets.figureparts.RoundScaledRamp;
import org.csstudio.swt.widgets.figureparts.RoundScaledRamp.Threshold;
import org.eclipse.swt.graphics.Color;


/**
 * Abstract figure with a round ramp and a round scale. 
 * @author Xihui Chen
 *
 */
public class AbstractRoundRampedFigure extends AbstractMarkedWidgetFigure {	


	protected boolean gradient = true;
	protected RoundScaledRamp ramp;

	
	public AbstractRoundRampedFigure() {
		scale = new RoundScale();
		ramp = new RoundScaledRamp((RoundScale) scale);	
//		if(SWT.getPlatform().startsWith("rap"))//$NON-NLS-1$
//			ramp.setVisible(false);
	}
	
	@Override
	public void setShowMarkers(boolean showMarkers) {		
		super.setShowMarkers(showMarkers);
//		if(SWT.getPlatform().startsWith("rap"))//$NON-NLS-1$
//			ramp.setVisible(false);
//		else
			ramp.setVisible(showMarkers);	
	}
	
	@Override
	public void setLoloLevel(double loloLevel) {
		super.setLoloLevel(loloLevel);
		ramp.setThresholdValue(Threshold.LOLO, loloLevel);
	}
	
	@Override
	public void setLoLevel(double loLevel) {
		super.setLoLevel(loLevel);
		ramp.setThresholdValue(Threshold.LO, loLevel);
	}
	
	@Override
	public void setHiLevel(double hiLevel) {		
		super.setHiLevel(hiLevel);
		ramp.setThresholdValue(Threshold.HI, hiLevel);
	}
	
	@Override
	public void setHihiLevel(double hihiLevel) {
		super.setHihiLevel(hihiLevel);
		ramp.setThresholdValue(Threshold.HIHI, hihiLevel);
	}
	
	@Override
	public void setShowLolo(boolean showLolo) {
		super.setShowLolo(showLolo);	
		ramp.setThresholdVisibility(Threshold.LOLO, showLolo);
	}
	
	@Override
	public void setShowLo(boolean showLo) {
		super.setShowLo(showLo);		
		ramp.setThresholdVisibility(Threshold.LO, showLo);
	}
	
	@Override
	public void setShowHi(boolean showHi) {
		super.setShowHi(showHi);		
		ramp.setThresholdVisibility(Threshold.HI, showHi);
	}
	
	@Override
	public void setShowHihi(boolean showHihi) {
		super.setShowHihi(showHihi);		
		ramp.setThresholdVisibility(Threshold.HIHI, showHihi);
	}	
	
	@Override
	public void setLoloColor(Color color) {
		super.setLoloColor(color);	
		ramp.setThresholdColor(Threshold.LOLO, color.getRGB());
		repaint();
	}
	
	@Override
	public void setLoColor(Color color) {
		super.setLoColor(color);	
		ramp.setThresholdColor(Threshold.LO, color.getRGB());
		repaint();
	}
	
	@Override
	public void setHiColor(Color color) {
		super.setHiColor(color);	
		ramp.setThresholdColor(Threshold.HI, color.getRGB());
		repaint();
	}
	
	@Override
	public void setHihiColor(Color color) {
		super.setHihiColor(color);	
		ramp.setThresholdColor(Threshold.HIHI, color.getRGB());
		repaint();
	}

	/**
	 * @param gradient the gradient to set
	 */
	public void setGradient(boolean gradient) {
		if(this.gradient == gradient)
			return;
		this.gradient = gradient;
		ramp.setGradient(gradient);
		repaint();
	}
	
	@Override
	public void setRange(double min, double max) {
		super.setRange(min, max);
		ramp.setDirty(true);
	}
	
	@Override
	public void setLogScale(boolean logScale) {
		super.setLogScale(logScale);
		ramp.setDirty(true);
	}

	/**
	 * @return the gradient
	 */
	public boolean isGradient() {
		return gradient;
	}

	
	
}
