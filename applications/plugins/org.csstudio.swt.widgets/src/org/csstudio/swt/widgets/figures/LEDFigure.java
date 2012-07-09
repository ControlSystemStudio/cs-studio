/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import org.csstudio.swt.widgets.figureparts.Bulb;
import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

/**
 * LED figure
 * @author Xihui Chen
 *
 */
public class LEDFigure extends AbstractBoolFigure {

	Bulb bulb; 
	private final static int OUTLINE_WIDTH = 2;
	private final static int SQURE_BORDER_WIDTH = 3;
	private final static Color DARK_GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_DARK_GRAY); 
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE); 
	private final static Color BLACK_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_BLACK); 
	private boolean effect3D = true;
	private boolean squareLED = false;
	public LEDFigure() {
		super();
		bulb = new Bulb();		
		setLayoutManager(new XYLayout());
		add(bulb);
		add(boolLabel);
		bulb.setBulbColor(booleanValue ? onColor : offColor);		
	}
	
	/**
	 * @return the effect3D
	 */
	public boolean isEffect3D() {
		return effect3D;
	}
	
	/**
	 * @return the squareLED
	 */
	public boolean isSquareLED() {
		return squareLED;
	}
	
	@Override
	protected void layout() {	
		Rectangle bulbBounds = getClientArea().getCopy();
		if(bulb.isVisible() && !squareLED){			
			bulbBounds.shrink(OUTLINE_WIDTH, OUTLINE_WIDTH);
			bulb.setBounds(bulbBounds);
		}		
		if(boolLabel.isVisible()){
			Dimension labelSize = boolLabel.getPreferredSize();				
			boolLabel.setBounds(new Rectangle(bulbBounds.x + bulbBounds.width/2 - labelSize.width/2,
					bulbBounds.y + bulbBounds.height/2 - labelSize.height/2,
					labelSize.width, labelSize.height));
		}
		super.layout();
	}
	
	@Override
	protected void paintClientArea(Graphics graphics) {	
		graphics.pushState();
		graphics.setAntialias(SWT.ON);		
		Rectangle clientArea = getClientArea().getCopy();
		boolean support3D = GraphicsUtil.testPatternSupported(graphics);
		if(squareLED){
			if(effect3D && support3D){
				//draw up border			
				Pattern pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x, clientArea.y, 
					clientArea.x, clientArea.y+SQURE_BORDER_WIDTH, BLACK_COLOR, 20, BLACK_COLOR, 100);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x, clientArea.y, 
					clientArea.x+SQURE_BORDER_WIDTH,clientArea.y + SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, clientArea.y + SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width, clientArea.y});
				pattern.dispose();
				
				//draw left border
				pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x, clientArea.y, 
					clientArea.x + SQURE_BORDER_WIDTH, clientArea.y, BLACK_COLOR, 20, BLACK_COLOR, 100);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x, clientArea.y, 
						clientArea.x+SQURE_BORDER_WIDTH,clientArea.y + SQURE_BORDER_WIDTH,
						clientArea.x+SQURE_BORDER_WIDTH, clientArea.y + clientArea.height - SQURE_BORDER_WIDTH,
						clientArea.x, clientArea.y + clientArea.height});
				pattern.dispose();				
				
				//draw bottom border			
				pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x, 
					clientArea.y+ clientArea.height - SQURE_BORDER_WIDTH, 
					clientArea.x, clientArea.y+clientArea.height, 
					WHITE_COLOR, 20, WHITE_COLOR, 30);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x, clientArea.y + clientArea.height, 
					clientArea.x+SQURE_BORDER_WIDTH,clientArea.y +clientArea.height - SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
					clientArea.y + clientArea.height - SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width, clientArea.y + clientArea.height});
				pattern.dispose();
				
				//draw right border			
				pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
					clientArea.y, 
					clientArea.x + clientArea.width, clientArea.y, 
					WHITE_COLOR, 20, WHITE_COLOR, 30);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x + clientArea.width, clientArea.y, 
					clientArea.x+ clientArea.width - SQURE_BORDER_WIDTH,clientArea.y + SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
					clientArea.y + clientArea.height - SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width, clientArea.y + clientArea.height});
				pattern.dispose();		
				
				//draw light
				clientArea.shrink(SQURE_BORDER_WIDTH, SQURE_BORDER_WIDTH);
				Color fillColor = booleanValue?onColor:offColor;
		        graphics.setBackgroundColor(fillColor);
		        graphics.fillRectangle(clientArea);
				pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x,	clientArea.y,
		        		clientArea.x + clientArea.width, clientArea.y + clientArea.height,
		        		WHITE_COLOR, 200, fillColor, 0);
		        graphics.setBackgroundPattern(pattern);
		       	graphics.fillRectangle(clientArea);		
		       	pattern.dispose();
				
			}else { //if not 3D
				clientArea.shrink(SQURE_BORDER_WIDTH/2, SQURE_BORDER_WIDTH/2);
				graphics.setForegroundColor(DARK_GRAY_COLOR);
				graphics.setLineWidth(SQURE_BORDER_WIDTH);
				graphics.drawRectangle(clientArea);
				
				clientArea.shrink(SQURE_BORDER_WIDTH/2, SQURE_BORDER_WIDTH/2);
				Color fillColor = booleanValue?onColor:offColor;
		        graphics.setBackgroundColor(fillColor);
		        graphics.fillRectangle(clientArea);
			}
			
		}else { // if round LED
			int width = Math.min(clientArea.width, clientArea.height);
			Rectangle outRect = new Rectangle(getClientArea().x, getClientArea().y, 
				width, width);
			if(effect3D && support3D){
				graphics.setBackgroundColor(WHITE_COLOR);
				graphics.fillOval(outRect);
				Pattern pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), outRect.x, outRect.y,
					outRect.x+width, outRect.y+width, DARK_GRAY_COLOR, 255, DARK_GRAY_COLOR, 0);
				graphics.setBackgroundPattern(pattern);
				graphics.fillOval(outRect);
				pattern.dispose();
			}else {
				graphics.setBackgroundColor(DARK_GRAY_COLOR);
				graphics.fillOval(outRect);
			}
		}
		
		graphics.popState();
		super.paintClientArea(graphics);
	}
	
	/**
	 * @param effect3D the effect3D to set
	 */
	public void setEffect3D(boolean effect3D) {
		if(this.effect3D == effect3D)
			return;
		this.effect3D = effect3D;
		bulb.setEffect3D(effect3D);
	}
	
	@Override
	public void setOffColor(Color offColor) {
		super.setOffColor(offColor);
		if(!booleanValue  && bulb.isVisible())
			bulb.setBulbColor(offColor);
	}

	@Override
	public void setOnColor(Color onColor) {
		super.setOnColor(onColor);
		if(booleanValue && bulb.isVisible())
			bulb.setBulbColor(onColor);
	}

	/**
	 * @param squareLED the squareLED to set
	 */
	public void setSquareLED(boolean squareLED) {
		if(this.squareLED == squareLED)
			return;
		this.squareLED = squareLED;
		bulb.setVisible(!squareLED);
	}

	@Override
	protected void updateBoolValue() {
		super.updateBoolValue();
		bulb.setBulbColor(booleanValue ? onColor : offColor);
		
	}
}
