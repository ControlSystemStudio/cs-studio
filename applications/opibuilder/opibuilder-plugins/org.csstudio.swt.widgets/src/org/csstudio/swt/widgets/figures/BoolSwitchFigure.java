/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;


import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.swt.widgets.util.SingleSourceHelper;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

/**
 * Boolean Switch figure
 * @author Xihui Chen
 *
 */
public class BoolSwitchFigure extends AbstractBoolControlFigure {


	class Bar extends Figure {
		
		private Rectangle smallEndBounds;
		private Rectangle bigEndBounds;
		private PointList echelon;
		public Bar() {
			addMouseListener(buttonPresser);
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
			Polygon polygon = new Polygon();
			polygon.setPoints(echelon);
			Ellipse smallEndEllipse = new Ellipse();
			smallEndEllipse.setBounds(smallEndBounds);
			Ellipse bigEndEllipse = new Ellipse();
			bigEndEllipse.setBounds(bigEndBounds);
			if(polygon.containsPoint(x,y) || smallEndEllipse.containsPoint(x,y) || 
					bigEndEllipse.containsPoint(x, y))
				return true;
			return false;
		}
		
		/**
		 * @return the smallEndBounds
		 */
		public Rectangle getSmallEndBounds() {
			return smallEndBounds;
		}

		@Override
		protected void paintFigure(Graphics graphics) {			
			graphics.setAntialias(SWT.ON);
			graphics.pushState();
			boolean support3D = GraphicsUtil.testPatternSupported(graphics);

			if(!horizontal){ //if vertical
				echelon = new PointList(new int[]{
							bigEndBounds.x, bigEndBounds.y + bigEndBounds.height/2,
							bigEndBounds.x + bigEndBounds.width, bigEndBounds.y + bigEndBounds.height/2,
							smallEndBounds.x + smallEndBounds.width, smallEndBounds.y + smallEndBounds.height/2,
							smallEndBounds.x, smallEndBounds.y + smallEndBounds.height/2});
				
				//paint small end
				graphics.setBackgroundColor(booleanValue? onColor : offColor);
				graphics.fillOval(smallEndBounds);	
				Pattern pattern = null;

				if(effect3D && support3D){
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bigEndBounds.x, bigEndBounds.y,
						bigEndBounds.x+bigEndBounds.width, bigEndBounds.y, 
						BLACK_COLOR, 10,
						BLACK_COLOR, booleanValue ? 210 : 160);
					graphics.setBackgroundPattern(pattern);
					graphics.fillOval(smallEndBounds);
				}
				
				
				//paint echelon
				graphics.setBackgroundColor(booleanValue? onColor : offColor);
				graphics.fillPolygon(echelon);
				if(effect3D && support3D){
					graphics.setBackgroundPattern(pattern);
					graphics.fillPolygon(echelon);
				}				
				
				//paint big end
				graphics.setBackgroundColor(booleanValue? onColor : offColor);
				graphics.fillOval(bigEndBounds);	
				if(effect3D && support3D){
					/*
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bigEndBounds.x, bigEndBounds.y,
							bigEndBounds.x+bigEndBounds.width, bigEndBounds.y + bigEndBounds.height,
							BLACK_COLOR, boolValue ? 5 : 10, BLACK_COLOR, boolValue ? 180 : 160);
					*/
					int a = bigEndBounds.width/2;
					int b = bigEndBounds.height/2;
					double w =  Math.sqrt(a*a + b*b);
					double wp = b - a;
					Point ul = new Point(bigEndBounds.x + a + (wp-w)/2 -1, bigEndBounds.y + b - (wp+w)/2 -1);
					Point br = new Point(bigEndBounds.x + a + (wp+w)/2 + 5, bigEndBounds.y + b - (wp-w)/2+5);
					
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), ul.x, ul.y,
						br.x, br.y, 
						BLACK_COLOR, 10, BLACK_COLOR, booleanValue ? 180 : 160);
					
					graphics.setBackgroundPattern(pattern);
					graphics.fillOval(bigEndBounds);				
					pattern.dispose();
				}
								
			}else {	//if horizontal	
				echelon = new PointList(new int[]{
							bigEndBounds.x + bigEndBounds.width/2,bigEndBounds.y, 
							bigEndBounds.x + bigEndBounds.width/2,bigEndBounds.y + bigEndBounds.height, 
							smallEndBounds.x + smallEndBounds.width/2, smallEndBounds.y + smallEndBounds.height, 
							smallEndBounds.x + smallEndBounds.width/2, smallEndBounds.y});
				
				//paint small end
				graphics.setBackgroundColor(booleanValue? onColor : offColor);
				graphics.fillOval(smallEndBounds);	
				Pattern pattern = null;
				if(effect3D && support3D){
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bigEndBounds.x, bigEndBounds.y,
						bigEndBounds.x, bigEndBounds.y+bigEndBounds.height, 
						BLACK_COLOR, booleanValue ? 0 : 10,
						BLACK_COLOR, booleanValue ? 150 : 220);
					graphics.setBackgroundPattern(pattern);
					graphics.fillOval(smallEndBounds);
				}			
				
				//paint echelon
				graphics.setBackgroundColor(booleanValue? onColor : offColor);
				graphics.fillPolygon(echelon);
				if(effect3D && support3D){
					graphics.setBackgroundPattern(pattern);
					graphics.fillPolygon(echelon);
				}				
				
				//paint big end
				graphics.setBackgroundColor(booleanValue? onColor : offColor);
				graphics.fillOval(bigEndBounds);	
				if(effect3D && support3D){
					/*
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bigEndBounds.x, bigEndBounds.y,
							bigEndBounds.x+bigEndBounds.width, bigEndBounds.y + bigEndBounds.height,
							BLACK_COLOR, boolValue ? 5 : 10, BLACK_COLOR, boolValue ? 180 : 160);
					*/
					int a = bigEndBounds.width/2;
					int b = bigEndBounds.height/2;
					double w =  Math.sqrt(a*a + b*b);
					double wp = b - a;
					Point ul = new Point(bigEndBounds.x + a + (wp-w)/2 -1, bigEndBounds.y + b - (wp+w)/2 -1);
					Point br = new Point(bigEndBounds.x + a + (wp+w)/2 + 5, bigEndBounds.y + b - (wp-w)/2+5);
					
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), ul.x, ul.y,
						br.x, br.y, 
						BLACK_COLOR, booleanValue ? 10 : 0, BLACK_COLOR, booleanValue ? 180 : 150);
					
					graphics.setBackgroundPattern(pattern);
					graphics.fillOval(bigEndBounds);				
					pattern.dispose();
				}
			}
			
			graphics.popState();
		}

		/**
		 * @param bigEndBounds the bigEndBounds to set
		 */
		public void setBigEndBounds(Rectangle bigEndBounds) {
			this.bigEndBounds = bigEndBounds;
		}

		/**
		 * @param echelon the echelon to set
		 */
		public void setEchelon(PointList echelon) {
			this.echelon = echelon;
		}

		/**
		 * @param smallEndBounds the smallEndBounds to set
		 */
		public void setSmallEndBounds(Rectangle smallEndBounds) {
			this.smallEndBounds = smallEndBounds;
		}

		
	} 
	class BoolSwitchLayout extends AbstractLayout {
			
			/** Used as a constraint for the bulb. */
			public static final String PEDESTAL = "pedestal";   //$NON-NLS-1$
			/** Used as a constraint for the bar. */
			public static final String BAR = "bar"; //$NON-NLS-1$
			/** Used as a constraint for the shadow */
			public static final String SHADOW = "shadow";      //$NON-NLS-1$
			
			/** Used as a constraint for the boolean label */
			public static final String BOOL_LABEL = "boolLabel";      //$NON-NLS-1$
			
			private Pedestal pedestal;
			private Bar bar;
			private Shadow shadow;
			
			@Override
			protected Dimension calculatePreferredSize(IFigure container, int w,
					int h) {
				Insets insets = container.getInsets();
				Dimension d = new Dimension(64, 2*64);
				d.expand(insets.getWidth(), insets.getHeight());
				return d;
			}
			
			private void horizontalLayout(IFigure container) {
				Rectangle area = container.getClientArea().getCopy();
				if(area.height >  area.width/2)
					area.height = (int) (area.width/2);
				else 
					area.width = (int) (2*area.height);
				int W = area.width;
				int H = area.height;
				Rectangle pedBounds = null;
				Rectangle smallBounds = null;
				Rectangle bigBounds;
				if(pedestal != null && pedestal.isVisible()) {
					pedBounds = new Rectangle((int) ((63.0/218.0)*W), 0, H/2, H/2);
					pedestal.setBounds(pedBounds);
				}
				if(bar != null && bar.isVisible()){
					Dimension bigEndD = new Dimension((int) ((35.0/218.0)*W), (int) ((45.0/105.0)*H));
					Dimension smallEndD = new Dimension((int) ((43.0/218.0)*W), (int) ((35.0/105.0)*H));
					
					int smallMove = (int) ((1.0/7.0)*pedBounds.width);
					smallMove = booleanValue ? -smallMove : smallMove;
					if(!booleanValue){			
						bigBounds = new Rectangle(
								0, pedBounds.height/2 - bigEndD.height/2, bigEndD.width, bigEndD.height);					
					}else {					
						bigBounds = new Rectangle(
								2*pedBounds.x + pedBounds.width  - bigEndD.width,
								pedBounds.height/2 - bigEndD.height/2,
								bigEndD.width,bigEndD.height);					
					}
					smallBounds = new Rectangle(
							pedBounds.x + pedBounds.width/2 - smallEndD.width/2,
							pedBounds.y + pedBounds.height/2 - smallEndD.height/2,						
							smallEndD.width, smallEndD.height);				
					smallBounds.x -= smallMove;
					
					bar.setBounds(area);
					bar.setBigEndBounds(bigBounds);
					bar.setSmallEndBounds(smallBounds);				
				}
				if(shadow != null && shadow.isVisible()){
					if(booleanValue)
						shadow.setBounds(new Rectangle(
								smallBounds.x + smallBounds.width/2, smallBounds.y,
								W - smallBounds.x- smallBounds.width/2, H - smallBounds.y ));
					else
						shadow.setBounds(new Rectangle(
								(int) ((34.0/218.0)*W), smallBounds.y, 
								smallBounds.x + smallBounds.width - (int) ((34.0/218.0)*W),
								H - smallBounds.y));
				}
				if(boolLabel != null && boolLabel.isVisible()){
					Dimension labelSize = boolLabel.getPreferredSize();						
					boolLabel.setBounds(new Rectangle(getLabelLocation(
							pedBounds.x + pedBounds.width/2 - labelSize.width/2,
							pedBounds.y + pedBounds.height/2 - labelSize.height/2),
							new Dimension(labelSize.width, labelSize.height)));
				}	
				
			}
		
			public void layout(IFigure container) {
				if(horizontal)
					horizontalLayout(container);
				else
					verticalLayout(container);
			}
			
			
			@Override
			public void setConstraint(IFigure child, Object constraint) {
				if(constraint.equals(PEDESTAL))
					pedestal = (Pedestal)child;
				else if (constraint.equals(BAR))
					bar = (Bar) child;
				else if (constraint.equals(SHADOW))
					shadow = (Shadow) child;
			}
	
			private void verticalLayout(IFigure container) {
				Rectangle area = container.getClientArea().getCopy();
				if(area.width >  area.height/2)
					area.width = (int) (area.height/2);
				else 
					area.height = (int) (2*area.width);
				int W = area.width;
				int H = area.height;
				Rectangle pedBounds = null;
				Rectangle barBounds;
				Rectangle smallBounds = null;
				Rectangle bigBounds;
				if(pedestal != null && pedestal.isVisible()) {
					pedBounds = new Rectangle(0, (int) ((63.0/218.0)*H), W/2, W/2);
					pedestal.setBounds(pedBounds);
				}
				if(bar != null && bar.isVisible()){
					Dimension bigEndD = new Dimension((int) ((45.0/105.0)*W), (int) ((35.0/218.0)*H));
					Dimension smallEndD = new Dimension((int) ((35.0/105.0)*W), (int) ((43.0/218.0)*H));
					
					int smallMove = (int) ((1.0/7.0)*pedBounds.height);
					smallMove = booleanValue ? -smallMove : smallMove;
					if(booleanValue){					
						barBounds = new Rectangle(pedBounds.width/2 - bigEndD.width/2,
								0, bigEndD.width,
								pedBounds.y + pedBounds.height/2 + smallEndD.height/2 +2);
						bigBounds = new Rectangle(
								barBounds.x, 0, bigEndD.width, bigEndD.height);					
					}else {
						barBounds = new Rectangle(pedBounds.width/2 - bigEndD.width/2,
								pedBounds.y + pedBounds.height/2 - smallEndD.height/2, bigEndD.width,
								pedBounds.y + pedBounds.height/2 + smallEndD.height/2 + 2);
						bigBounds = new Rectangle(
								barBounds.x, barBounds.y + barBounds.height - bigEndD.height,
								bigEndD.width,bigEndD.height);
						
					}
					smallBounds = new Rectangle(
							pedBounds.x + pedBounds.width/2 - smallEndD.width/2,
							pedBounds.y + pedBounds.height/2 - smallEndD.height/2,
							smallEndD.width, smallEndD.height);				
					smallBounds.y += smallMove;
					//barBounds.x +=1;
					//smallBounds.x += 1;
					//bigBounds.x += 1;
					bar.setBounds(area);
					bar.setBigEndBounds(bigBounds);
					bar.setSmallEndBounds(smallBounds);				
				}
				if(shadow != null && shadow.isVisible()){
					if(!booleanValue)
						shadow.setBounds(new Rectangle(
								smallBounds.x, smallBounds.y + smallBounds.height/2,
								W - smallBounds.x, H - smallBounds.y - smallBounds.height/2));
					else
						shadow.setBounds(new Rectangle(
								smallBounds.x, (int) ((34.0/218.0)*H),
								W - smallBounds.x,
								smallBounds.y + smallBounds.height - (int) ((34.0/218.0)*H)));
				}
				if(boolLabel != null && boolLabel.isVisible()){
					Dimension labelSize = boolLabel.getPreferredSize();	
					boolLabel.setBounds(new Rectangle(getLabelLocation(
							pedBounds.x + pedBounds.width/2 - labelSize.width/2,
							pedBounds.y + pedBounds.height/2 - labelSize.height/2),
							new Dimension(labelSize.width, labelSize.height)));
				}
			}
		
		} 
	class Pedestal extends Figure {
		@Override
		protected void paintFigure(Graphics graphics) {
			boolean support3D = GraphicsUtil.testPatternSupported(graphics);
			graphics.setAntialias(SWT.ON);
			graphics.setBackgroundColor(effect3D && support3D? WHITE_COLOR : GRAY_COLOR);
			graphics.fillOval(bounds);
			
			if(effect3D && support3D) {
				Pattern pattern;
				if(booleanValue)
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bounds.x, bounds.y,
						bounds.x+bounds.width, bounds.y + bounds.height, WHITE_COLOR, 10, 
						BLACK_COLOR, 100);
				else
					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bounds.x, bounds.y,
						bounds.x+bounds.width, bounds.y + bounds.height, BLACK_COLOR, 0, 
						BLACK_COLOR, 150);
				graphics.setBackgroundPattern(pattern);
				graphics.fillOval(bounds);
				
				if(booleanValue){
					if(horizontal)
						pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bounds.x, bounds.y,
								bounds.x, bounds.y+bounds.height, BLACK_COLOR, 5, 
								BLACK_COLOR, 50);
					else	
						pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bounds.x, bounds.y,
								bounds.x+bounds.width, bounds.y, BLACK_COLOR, 5, 
								BLACK_COLOR, 100);
				graphics.setBackgroundPattern(pattern);
				graphics.fillOval(bounds);
				pattern.dispose();
				}
			}
			
		}
	}
	class Shadow extends Figure {
		private final static int ALPHA = 80;
		@Override
		protected void paintClientArea(Graphics graphics) {
			if(SWT.getPlatform().startsWith("rap")) {//$NON-NLS-1$
				super.paintClientArea(graphics);
				return;
			}
			graphics.pushState();
			graphics.setAlpha(ALPHA);
			
			if(horizontal){
				if(!booleanValue){
					Image image = new Image(Display.getCurrent(), bounds.width, bounds.height);
					GC gc = SingleSourceHelper.getImageGC(image);		
					
					Point p1 = new Point( bounds.x/0.8, (int) ((55.0/95.0)*bounds.height));
					
					Point p2 = new Point(p1.x + (int) ((23.0* 1.5/95.0)*bounds.height),
							p1.y + (int) ((23.0/95.0)*bounds.height));
					Rectangle smallEndBounds = bar.getSmallEndBounds();
					int[] echelonShadow = new int[]{
							p1.x, p1.y, p2.x, p2.y,
							smallEndBounds.x + smallEndBounds.width, smallEndBounds.y + smallEndBounds.height/2,
							smallEndBounds.x+ smallEndBounds.width/2, smallEndBounds.y };
					gc.setBackground(GRAY_COLOR);
					
					gc.fillRectangle(image.getBounds());
					Transform tr = new Transform(Display.getCurrent());
					tr.translate(-bounds.x, -bounds.y/1.05f);
					//gc.setTransform(tr);
					SingleSourceHelper.setGCTransform(gc, tr);
					gc.setBackground(BLACK_COLOR);
					gc.fillPolygon(echelonShadow);
				
					//draw cap
					int a = (int) Math.sqrt((p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y));
					int b = (int) (a);				
					
					tr.translate(p1.x, p1.y);
					tr.rotate((float) (Math.atan((double)(p2.y-p1.y)/(p2.x-p1.x))*180.0/Math.PI));
					//gc.setTransform(tr);
					SingleSourceHelper.setGCTransform(gc, tr);
					gc.fillOval(0, -b/2 ,a,b);
					tr.dispose();
					gc.dispose();
					ImageData imageData = image.getImageData();
					image.dispose();
					
					//draw image
					imageData.transparentPixel = imageData.palette.getPixel(GRAY_COLOR.getRGB());
					image = new Image(Display.getCurrent(), imageData);
					graphics.drawImage(image, bounds.x, bounds.y);
					image.dispose();
					
				}else{
					Image image = new Image(Display.getCurrent(), bounds.width, bounds.height);
					GC gc = SingleSourceHelper.getImageGC(image);	
	
					Point p1 = new Point(bounds.x+ bounds.width*0.8, (int) ((40.0/95.0)*bounds.height));
					
					Point p2 = new Point(p1.x - (int) ((10.0* Math.sqrt(3.0)/95.0)*bounds.width),
							p1.y + (int) ((40.0/95.0)*bounds.height));
					Rectangle smallEndBounds = bar.getSmallEndBounds();
					int[] echelonShadow = new int[]{
							p1.x, p1.y, p2.x, p2.y,
							smallEndBounds.x + smallEndBounds.width/2, smallEndBounds.y + smallEndBounds.height,
							smallEndBounds.x+ smallEndBounds.width/2, smallEndBounds.y};
					gc.setBackground(GRAY_COLOR);				
					gc.fillRectangle(image.getBounds());
					
					Transform tr = new Transform(Display.getCurrent());
					tr.translate(-bounds.x, -bounds.y);
					//gc.setTransform(tr);
					SingleSourceHelper.setGCTransform(gc, tr);
					
					gc.setBackground(BLACK_COLOR);
					gc.fillPolygon(echelonShadow);
					
					//draw cap
					int a = (int) Math.sqrt((p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y));
					int b = (int) (a/1);		
					
					tr.translate(p1.x, p1.y);
					tr.rotate((float) (Math.atan((double)(p1.x-p2.x)/(p2.y-p1.y))*180.0/Math.PI));
					//gc.setTransform(tr);
					SingleSourceHelper.setGCTransform(gc, tr);			
					gc.fillOval(-b/2,0, a,b);
					tr.dispose();
					gc.dispose();
					ImageData imageData = image.getImageData();
					image.dispose();
				
					//draw image
					imageData.transparentPixel = imageData.palette.getPixel(GRAY_COLOR.getRGB());
					image = new Image(Display.getCurrent(), imageData);				
					graphics.drawImage(image, bounds.x, bounds.y);
					image.dispose();							
				}
			}else {
				if(booleanValue){
					Image image = new Image(Display.getCurrent(), bounds.width, bounds.height);
					GC gc = SingleSourceHelper.getImageGC(image);		
					
					Point p1 = new Point((int) ((60.0/95.0)*bounds.width), bounds.y+3);
					
					Point p2 = new Point(p1.x + (int) ((20.0/95.0)*bounds.width), 
							p1.y + (int) ((20.0* Math.sqrt(3.0)/95.0)*bounds.width));
					Rectangle smallEndBounds = bar.getSmallEndBounds();
					int[] echelonShadow = new int[]{
							p1.x, p1.y, p2.x, p2.y,
							smallEndBounds.x + smallEndBounds.width/2, smallEndBounds.y + smallEndBounds.height,
							smallEndBounds.x, smallEndBounds.y + smallEndBounds.height/2};
					gc.setBackground(GRAY_COLOR);
					
					gc.fillRectangle(image.getBounds());
					Transform tr = new Transform(Display.getCurrent());
					tr.translate(-bounds.x, -bounds.y/1.05f);
					//gc.setTransform(tr);
					SingleSourceHelper.setGCTransform(gc, tr);
					gc.setBackground(BLACK_COLOR);
					gc.fillPolygon(echelonShadow);
				
					//draw cap
					int a = (int) Math.sqrt((p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y));
					int b = (int) (a/1.3);				
					
					tr.translate(p1.x, p1.y);
					tr.rotate((float) (Math.atan((double)(p2.y-p1.y)/(p2.x-p1.x))*180.0/Math.PI));
					//gc.setTransform(tr);
					SingleSourceHelper.setGCTransform(gc, tr);
					gc.fillOval(0, -b/2 ,a,b);
					tr.dispose();
					gc.dispose();
					ImageData imageData = image.getImageData();
					image.dispose();
					
					//draw image
					imageData.transparentPixel = imageData.palette.getPixel(GRAY_COLOR.getRGB());
					image = new Image(Display.getCurrent(), imageData);
					graphics.drawImage(image, bounds.x, bounds.y);
					image.dispose();
					
				}else{
					Image image = new Image(Display.getCurrent(), bounds.width, bounds.height);
					GC gc = SingleSourceHelper.getImageGC(image);	
	
					Point p1 = new Point((int) ((40.0/95.0)*bounds.width), bounds.y+ bounds.height*0.8);
					
					Point p2 = new Point(p1.x + (int) ((40.0/95.0)*bounds.width), 
							p1.y - (int) ((10.0* Math.sqrt(3.0)/95.0)*bounds.width));
					Rectangle smallEndBounds = bar.getSmallEndBounds();
					int[] echelonShadow = new int[]{
							p1.x, p1.y, p2.x, p2.y,
							smallEndBounds.x + smallEndBounds.width, smallEndBounds.y + smallEndBounds.height/2,
							smallEndBounds.x, smallEndBounds.y + smallEndBounds.height/2};
					gc.setBackground(GRAY_COLOR);				
					gc.fillRectangle(image.getBounds());
					
					Transform tr = new Transform(Display.getCurrent());
					tr.translate(-bounds.x, -bounds.y);
					//gc.setTransform(tr);
					SingleSourceHelper.setGCTransform(gc, tr);;
					
					gc.setBackground(BLACK_COLOR);
					gc.fillPolygon(echelonShadow);
					
					//draw cap
					int a = (int) Math.sqrt((p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y));
					int b = (int) (a/1);		
					
					tr.translate(p1.x, p1.y);
					tr.rotate(-(float) (Math.atan((double)(p1.y-p2.y)/(p2.x-p1.x))*180.0/Math.PI));
					//gc.setTransform(tr);
					SingleSourceHelper.setGCTransform(gc, tr);			
					gc.fillOval(0, -b/2,a,b);
					tr.dispose();
					gc.dispose();
					ImageData imageData = image.getImageData();
					image.dispose();
				
					//draw image
					imageData.transparentPixel = imageData.palette.getPixel(GRAY_COLOR.getRGB());
					image = new Image(Display.getCurrent(), imageData);				
					graphics.drawImage(image, bounds.x, bounds.y);
					image.dispose();							
				}
			}
			
			graphics.popState();
			super.paintClientArea(graphics);
		}
	}
	private final Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			new RGB(200, 200, 200));
	
	
	private final Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	private final Color BLACK_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_BLACK);
	private boolean effect3D = true;
	
	private boolean horizontal = false;
	private Pedestal pedestal;
	
	private Bar bar;

	private Shadow shadow;

	Cursor cursor;

	public BoolSwitchFigure() {
		super();
//		if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
//			Activator.getLogger().log(Level.SEVERE, 
//					"BoolSwitchFigure is not implemented for RAP yet!");
		pedestal = new Pedestal();		
		shadow = new Shadow();
		bar = new Bar();
		setLayoutManager(new BoolSwitchLayout());	
		
		add(pedestal, BoolSwitchLayout.PEDESTAL);	
		add(shadow, BoolSwitchLayout.SHADOW);
		add(bar, BoolSwitchLayout.BAR);	
		add(boolLabel, BoolSwitchLayout.BOOL_LABEL);
		cursor = Cursors.HAND;		
		
	}
	
	/**
	 * @return the effect3D
	 */
	public boolean isEffect3D() {
		return effect3D;
	}
	
	/**
	 * @return the horizontal
	 */
	public boolean isHorizontal() {
		return horizontal;
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
	public void setBit(int bit) {
		super.setBit(bit);
		revalidate();
	}
	
	@Override
	public void setBounds(Rectangle rect) {
		super.setBounds(rect);		
		horizontal = (bounds.width > bounds.height);
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
		if(bar.isVisible())
			bar.setCursor(runMode ? cursor : null);
		else if (shadow.isVisible())
			shadow.setCursor(runMode ? cursor : null);	
	}
	
	@Override
	public void setRunMode(boolean runMode) {
		super.setRunMode(runMode);					
		if(bar.isVisible())
			bar.setCursor(runMode ? cursor : null);
		else if (shadow.isVisible())
			shadow.setCursor(runMode ? cursor : null);	
	}	
	
	@Override
	public void setValue(double value) {
		super.setValue(value);
		revalidate();
	}
	
@Override
protected boolean useLocalCoordinates() {
	return true;
}
}
