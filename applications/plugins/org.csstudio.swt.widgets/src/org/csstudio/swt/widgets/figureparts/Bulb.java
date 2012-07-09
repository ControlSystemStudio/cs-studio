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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A bulb figure, which could be used for knob, LED etc.
 * @author Xihui Chen
 *
 */
public class Bulb extends Figure{
	
	private Color bulbColor;

	private static final Color COLOR_WHITE = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	private boolean effect3D;
	
	public Bulb() {
		setBulbColor(CustomMediaFactory.getInstance().getColor(
				new RGB(150,150,150)));
		setEffect3D(true);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public void setBounds(Rectangle rect) {
    	//get the square in the rect
    	rect.width = Math.min(rect.width, rect.height);
    	if(rect.width < 3)
    		rect.width =3;
    	rect.height = rect.width;
    	super.setBounds(rect);  

	}
	@Override
	protected void paintClientArea(Graphics graphics) {		
		graphics.setAntialias(SWT.ON);
		
		if(effect3D && GraphicsUtil.testPatternSupported(graphics)) {			
			// Fills the circle with solid bulb color
	        graphics.setBackgroundColor(bulbColor);
	        graphics.fillOval(bounds);
	        
			//diagonal linear gradient
				Pattern p = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bounds.x,	bounds.y,
						bounds.x + getWidth(), bounds.y + getHeight(),
						COLOR_WHITE, 255, bulbColor, 0);
	        try {				
				graphics.setBackgroundPattern(p);
				graphics.fillOval(bounds);		
				p.dispose();
			} catch (Exception e) {
				p.dispose();				
			}
			
		} else {			
			graphics.setBackgroundColor(bulbColor);
			graphics.fillOval(bounds);
		}		
		
		super.paintClientArea(graphics);
	}
	
	/**
	 * @param bulbColor the bulbColor to set
	 */
	public void setBulbColor(Color color) {
		if(this.bulbColor != null && this.bulbColor.equals(color))
			return;
		this.bulbColor = color;
		repaint();
	}
	
	private int getHeight() {
		return bounds.height;
	}

	private int getWidth() {
		return bounds.width;
	}
	
	/**
	 * @param effect3D the effect3D to set
	 */
	public void setEffect3D(boolean effect3D) {
		if(this.effect3D == effect3D)
			return;
		this.effect3D = effect3D;
		repaint();
	}




	
	
}
