/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Bool button figure
 * @author Xihui Chen
 *
 */
public class BoolButtonFigure extends AbstractBoolControlFigure {

	class EllipseButton extends Figure {
		
		public EllipseButton() {
			addMouseListener(buttonPresser);
			addMouseMotionListener(new MouseMotionListener.Stub() {
				@Override
				public void mouseEntered(MouseEvent me) {
					if (isRunMode()) {
						Color backColor = BoolButtonFigure.this
								.getBackgroundColor();
						RGB darkColor = GraphicsUtil
								.mixColors(backColor.getRGB(), new RGB(255,
										255, 255), 0.5);
						EllipseButton.this
								.setBackgroundColor(CustomMediaFactory
										.getInstance().getColor(darkColor));
					}
				}
				@Override
				public void mouseExited(MouseEvent me) {
					if (isRunMode()) {
						EllipseButton.this
								.setBackgroundColor(BoolButtonFigure.this
										.getBackgroundColor());
					}

				}
			});
		}
		
		/**
		 * Returns <code>true</code> if the given point (x,y) is contained within this ellipse.
		 * @param x the x coordinate
		 * @param y the y coordinate
		 * @return <code>true</code>if the given point is contained
		 */
		public boolean containsPoint(int x, int y) {
			if (!super.containsPoint(x, y))
				return false;
			Rectangle r = getBounds();
			long ux = x - r.x - r.width / 2;
			long uy = y - r.y - r.height / 2;
			return ((ux * ux) << 10) / (r.width * r.width) 
				 + ((uy * uy) << 10) / (r.height * r.height) <= 256;
		}
		
		@Override
		protected void paintFigure(Graphics graphics) {
			graphics.setAntialias(SWT.ON);
			Rectangle clientArea = getClientArea().getCopy();
			 // if oval button
			boolean support3D = GraphicsUtil.testPatternSupported(graphics);
			if(effect3D && support3D){
				graphics.setBackgroundColor(WHITE_COLOR);	
				graphics.fillOval(clientArea);
//				graphics.fillRectangle(new Rectangle());
				Pattern pattern;
				int a = clientArea.width/2;
				int b = clientArea.height/2;
				double w =  Math.sqrt(a*a + b*b);
				double wp = b - a;
				Point ul = new Point(clientArea.x + a + (wp-w)/2 -1, clientArea.y + b - (wp+w)/2 -1);
				Point br = new Point(clientArea.x + a + (wp+w)/2 + 5, clientArea.y + b - (wp-w)/2+5);
				if(booleanValue){
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), ul.x, ul.y,
						br.x, br.y, 
						DARK_GRAY_COLOR, 255, WHITE_COLOR,0);
				}else{
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), ul.x, ul.y,
						br.x, br.y, 
						WHITE_COLOR, 0, DARK_GRAY_COLOR, 255);
				}
				
				graphics.setBackgroundPattern(pattern);
				graphics.fillOval(clientArea);
				pattern.dispose();
				
				
			}else {				
				graphics.setBackgroundColor(booleanValue ? WHITE_COLOR : DARK_GRAY_COLOR);
				graphics.fillOval(clientArea);
			}
			graphics.setBackgroundColor(getBackgroundColor());
			Rectangle inRect = clientArea.getCopy().shrink(ELLIPSE_BORDER_WIDTH, ELLIPSE_BORDER_WIDTH);
			graphics.fillOval(inRect);	
			
			//draw LED on Button
			if(showLED){
				int ledDiameter = (int) (0.25 * (clientArea.width + clientArea.height)/2.0);
				if(ledDiameter > Math.min(clientArea.width, clientArea.height))
					ledDiameter = Math.min(clientArea.width, clientArea.height)-8;
				Rectangle ledArea;
				if(clientArea.width >=clientArea.height){
					ledArea = new Rectangle(
							(int) (clientArea.x + clientArea.width * LED_POSITION - ledDiameter/2.0),
							(int) (clientArea.y + clientArea.height/2.0 - ledDiameter/2.0), ledDiameter, ledDiameter);
				}else{
					ledArea = new Rectangle(
							(int) (clientArea.x + clientArea.width/2.0 - ledDiameter/2.0),
							(int) (clientArea.y + (1-LED_POSITION)*clientArea.height - ledDiameter/2.0), ledDiameter, ledDiameter);
				}
				
				// Fills the circle with solid bulb color
				Color ledColor = booleanValue ? onColor : offColor;
			    graphics.setBackgroundColor(ledColor);
			    graphics.fillOval(ledArea);
				if(effect3D && support3D){					
			        //diagonal linear gradient
			        Pattern p = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), ledArea.x,ledArea.y,
			        		ledArea.x + ledArea.width, ledArea.y + ledArea.height,
			        		WHITE_COLOR, 255, ledColor, 0);
			        graphics.setBackgroundPattern(p);
			       	graphics.fillOval(ledArea);		
			       	p.dispose();
				}				
			}
		}		
	}
	class SquareButton extends Figure {
		public SquareButton() {			
			addMouseListener(buttonPresser);

			addMouseMotionListener(new MouseMotionListener.Stub() {
				@Override
				public void mouseEntered(MouseEvent me) {
					if (isRunMode()) {
						Color backColor = BoolButtonFigure.this
								.getBackgroundColor();
						RGB darkColor = GraphicsUtil
								.mixColors(backColor.getRGB(), new RGB(255,
										255, 255), 0.5);
						SquareButton.this.setBackgroundColor(CustomMediaFactory
								.getInstance().getColor(darkColor));
					}
				}

				@Override
				public void mouseExited(MouseEvent me) {

					if (isRunMode()) {
						SquareButton.this
								.setBackgroundColor(BoolButtonFigure.this
										.getBackgroundColor());
					}

				}
			});			
		}
		
		/**
		 * This must be implemented to make tool tip work. I don't know why. 
		 */
		public boolean containsPoint(int x, int y) {
			return getBounds().getCopy().shrink(2, 2).contains(x, y);
		}
		
		@Override
		protected void paintClientArea(Graphics graphics) {			
			graphics.pushState();
			graphics.setAntialias(SWT.ON);
			Rectangle clientArea = getClientArea().getCopy();
			boolean support3D = GraphicsUtil.testPatternSupported(graphics);
			if(effect3D && support3D){			
//				graphics.fillRectangle(new Rectangle());

				//draw up border			
				Pattern pattern;
				if(booleanValue)
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x, clientArea.y, 
							clientArea.x, clientArea.y+SQURE_BORDER_WIDTH, GRAY_COLOR, DARK_GRAY_COLOR);			
				else
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x, clientArea.y, 
							clientArea.x, clientArea.y+SQURE_BORDER_WIDTH, WHITE_COLOR, LIGHT_GRAY_COLOR);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x, clientArea.y, 
					clientArea.x+SQURE_BORDER_WIDTH,clientArea.y + SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, clientArea.y + SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width, clientArea.y});
				pattern.dispose();
				
				//draw left border
				if(booleanValue)
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x, clientArea.y, 
						clientArea.x + SQURE_BORDER_WIDTH, clientArea.y, GRAY_COLOR, DARK_GRAY_COLOR);			
				else
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x, clientArea.y, 
						clientArea.x + SQURE_BORDER_WIDTH, clientArea.y, WHITE_COLOR, LIGHT_GRAY_COLOR);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x, clientArea.y, 
						clientArea.x+SQURE_BORDER_WIDTH,clientArea.y + SQURE_BORDER_WIDTH,
						clientArea.x+SQURE_BORDER_WIDTH, clientArea.y + clientArea.height - SQURE_BORDER_WIDTH,
						clientArea.x, clientArea.y + clientArea.height});
				pattern.dispose();				
				
				//draw bottom border	
				if(booleanValue)						
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x, 
							clientArea.y+ clientArea.height - SQURE_BORDER_WIDTH, 
							clientArea.x, clientArea.y+clientArea.height, 
							LIGHT_GRAY_COLOR, WHITE_COLOR);
				else
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x, 
							clientArea.y+ clientArea.height - SQURE_BORDER_WIDTH, 
							clientArea.x, clientArea.y+clientArea.height, 
							LIGHT_GRAY_COLOR, DARKER_GRAY_COLOR);
				
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x, clientArea.y + clientArea.height, 
					clientArea.x+SQURE_BORDER_WIDTH,clientArea.y +clientArea.height - SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
					clientArea.y + clientArea.height - SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width, clientArea.y + clientArea.height});
				pattern.dispose();
				
				//draw right border			
				if(booleanValue)
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
							clientArea.y, 
							clientArea.x + clientArea.width, clientArea.y, 
							LIGHT_GRAY_COLOR, WHITE_COLOR);	
				else					
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
							clientArea.y, 
							clientArea.x + clientArea.width, clientArea.y, 
							LIGHT_GRAY_COLOR, DARKER_GRAY_COLOR);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x + clientArea.width, clientArea.y, 
					clientArea.x+ clientArea.width - SQURE_BORDER_WIDTH,clientArea.y + SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
					clientArea.y + clientArea.height - SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width, clientArea.y + clientArea.height});
				pattern.dispose();		
				
				//draw button
				clientArea.shrink(SQURE_BORDER_WIDTH, SQURE_BORDER_WIDTH);
		        graphics.setBackgroundColor(getBackgroundColor());
		        graphics.fillRectangle(clientArea);
		       	pattern.dispose();
				
			}else { //if not 3D
				clientArea.shrink(SQURE_BORDER_WIDTH/2, SQURE_BORDER_WIDTH/2);
				graphics.setForegroundColor(booleanValue ? WHITE_COLOR : DARK_GRAY_COLOR);
				graphics.setLineWidth(SQURE_BORDER_WIDTH);
				graphics.drawRectangle(clientArea);				
				clientArea.shrink(SQURE_BORDER_WIDTH/2, SQURE_BORDER_WIDTH/2);
		        graphics.setBackgroundColor(getBackgroundColor());
		        graphics.fillRectangle(clientArea);
			}	
			
			//draw LED on Button
			if(showLED){
				int ledDiameter = (int) (0.3 * (clientArea.width + clientArea.height)/2.0);
				if(ledDiameter > Math.min(clientArea.width, clientArea.height))
					ledDiameter = Math.min(clientArea.width, clientArea.height)-2;
				Rectangle ledArea;
				if(clientArea.width >=clientArea.height){
					ledArea = new Rectangle(
							(int) (clientArea.x + clientArea.width * LED_POSITION - ledDiameter/2.0),
							(int) (clientArea.y + clientArea.height/2.0 - ledDiameter/2.0), ledDiameter, ledDiameter);
				}else{
					ledArea = new Rectangle(
							(int) (clientArea.x + clientArea.width/2.0 - ledDiameter/2.0),
							(int) (clientArea.y + (1-LED_POSITION)*clientArea.height - ledDiameter/2.0), ledDiameter, ledDiameter);
				}
				
				// Fills the circle with solid bulb color
				Color ledColor = booleanValue ? onColor : offColor;
			    graphics.setBackgroundColor(ledColor);
			    graphics.fillOval(ledArea);
				if(effect3D && support3D){					
			        //diagonal linear gradient
			        Pattern p = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), ledArea.x,ledArea.y,
			        		ledArea.x + ledArea.width, ledArea.y + ledArea.height,
			        		WHITE_COLOR, 255, ledColor, 0);
			        graphics.setBackgroundPattern(p);
			       	graphics.fillOval(ledArea);		
			       	p.dispose();
				}				
			}
			graphics.popState();
			super.paintClientArea(graphics);
		}
	}
	private final static int ELLIPSE_BORDER_WIDTH = 3;
	private final static int SQURE_BORDER_WIDTH = 3; 
	private final static double LED_POSITION = 4.8/6.0; 
	private final static Color LIGHT_GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			new RGB(240, 240, 240));
	private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			new RGB(200, 200, 200));
	private final static Color DARK_GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			new RGB(127, 127, 127)); 
	
	private final static Color DARKER_GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			new RGB(100, 100, 100));
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	private boolean effect3D = true;
	
	private boolean squareButton = true;
	private boolean showLED = true;
	
	private EllipseButton ellipseButton;
	private SquareButton squareButtonFigure;

	Cursor cursor;

	public BoolButtonFigure() {
		super();	
		squareButtonFigure = new SquareButton();
		ellipseButton = new EllipseButton();
		setLayoutManager(new XYLayout());		
		add(squareButtonFigure);
		squareButtonFigure.add(boolLabel);
		cursor = Cursors.HAND;		
	}

	/**
	 * @return the effect3D
	 */
	public boolean isEffect3D() {
		return effect3D;
	}
	
	/**
	 * @return the showLED
	 */
	public boolean isShowLED() {
		return showLED;
	}

	/**
	 * @return the squareButton
	 */
	public boolean isSquareButton() {
		return squareButton;
	}
	
	@Override
	protected void layout() {	
		Rectangle clientArea = getClientArea().getCopy();
		if(ellipseButton.isVisible() && !squareButton)
			ellipseButton.setBounds(clientArea);	
		if(squareButtonFigure.isVisible() && squareButton)
			squareButtonFigure.setBounds(clientArea);
		if(boolLabel.isVisible()){			
			Dimension labelSize = boolLabel.getPreferredSize();				
			Rectangle labelBounds = new Rectangle(clientArea.x + clientArea.width/2 - labelSize.width/2,
					clientArea.y + clientArea.height/2 - labelSize.height/2,
					labelSize.width, labelSize.height);
			if(getBooleanValue())
				labelBounds.translate(1, 1);
			boolLabel.setBounds(labelBounds);
		}
		super.layout();
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		if(!isEnabled()) {
			graphics.setAlpha(DISABLED_ALPHA);
			graphics.setBackgroundColor(DISABLE_COLOR);
			graphics.fillRectangle(bounds);
		}	
	}
	
	@Override
	public void setBackgroundColor(Color bg) {
		super.setBackgroundColor(bg);
		for(Object child: getChildren()){
			((Figure) child).setBackgroundColor(bg);
		}
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

	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		if(runMode){
			if(value){
				if(cursor == null || cursor.isDisposed())
					cursor = Cursors.HAND;		
			}else {				
				cursor = null;
			}	
		}				
		if(squareButton)
			squareButtonFigure.setEnabled(value);
		else
			ellipseButton.setEnabled(value);
		if(ellipseButton.isVisible())
			ellipseButton.setCursor(runMode ? cursor : null);
		else if (squareButtonFigure.isVisible())
			squareButtonFigure.setCursor(runMode ? cursor : null);	
	}
	
	@Override
	public void setRunMode(boolean runMode) {
		super.setRunMode(runMode);				
		
		ellipseButton.setCursor(runMode ? cursor : null);		
		squareButtonFigure.setCursor(runMode ? cursor : null);	
	}
	
	/**
	 * @param showLED the showLED to set
	 */
	public void setShowLED(boolean showLED) {
		if(this.showLED == showLED)
			return;
		this.showLED = showLED;
		repaint();
	}	
	
	/**
	 * @param squareLED the squareLED to set
	 */
	public void setSquareButton(boolean squareButton) {
		if(this.squareButton == squareButton)
			return;
		this.squareButton = squareButton;
	
		if(squareButton){
			if(getChildren().contains(ellipseButton))
				remove(ellipseButton);
			if(!getChildren().contains(squareButtonFigure))
				add(squareButtonFigure);
			if(ellipseButton.getChildren().contains(boolLabel))
				ellipseButton.remove(boolLabel);	
			squareButtonFigure.add(boolLabel);
			squareButtonFigure.setCursor(runMode ? cursor : null);	
		}else {
			if(getChildren().contains(squareButtonFigure))
				remove(squareButtonFigure);
			if(!getChildren().contains(ellipseButton))
				add(ellipseButton);
			if(squareButtonFigure.getChildren().contains(boolLabel))
				squareButtonFigure.remove(boolLabel);	
			ellipseButton.add(boolLabel);
			ellipseButton.setCursor(runMode ? cursor : null);
		}		
		ellipseButton.setVisible(!squareButton);
		ellipseButton.setEnabled(!squareButton);
		squareButtonFigure.setVisible(squareButton);
		squareButtonFigure.setEnabled(squareButton);					
	}
}
