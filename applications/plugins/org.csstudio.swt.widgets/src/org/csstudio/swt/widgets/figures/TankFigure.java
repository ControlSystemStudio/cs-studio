/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;


import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.swt.xygraph.linearscale.LinearScale;
import org.csstudio.swt.xygraph.linearscale.LinearScale.Orientation;
import org.csstudio.swt.xygraph.linearscale.LinearScaledMarker;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A tank figure
 * @author Xihui Chen
 *
 */
public class TankFigure extends AbstractLinearMarkedFigure {	

	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GRAY);
	
	private final static Color BLUE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_BLUE);
	
	private boolean effect3D = true;
	private Tank tank;
	private Color fillColor= BLUE_COLOR;
	
	private Color fillBackgroundColor= GRAY_COLOR;	
	
	public TankFigure() {		
		super();		
		((LinearScale) scale).setOrientation(Orientation.VERTICAL);
		scale.setScaleLineVisible(false);

		tank = new Tank();		
		setLayoutManager(new TankLayout());
		
		add(scale, TankLayout.SCALE);
		add(marker, TankLayout.MARKERS);
		add(tank, TankLayout.TANK);
	  
	}	

	/**
	 * @return the fillBackgroundColor
	 */
	public Color getFillBackgroundColor() {
		return fillBackgroundColor;
	}

	/**
	 * @return the fillColor
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * @return the effect3D
	 */
	public boolean isEffect3D() {
		return effect3D;
	}

	@Override
	public boolean isOpaque() {
		return false;
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

	/**
	 * @param fillBackgroundColor the fillBackgroundColor to set
	 */
	public void setFillBackgroundColor(Color fillBackgroundColor) {
		if(this.fillBackgroundColor != null && this.fillBackgroundColor.equals(fillBackgroundColor))
			return;
		this.fillBackgroundColor = fillBackgroundColor;
		repaint();
	}

	/**
	 * @param fillColor the fillColor to set
	 */
	public void setFillColor(Color fillColor) {
		if(this.fillColor != null && this.fillColor.equals(fillColor))
			return;
		this.fillColor = fillColor;
		repaint();
	}
	
		
	@Override
	public void setForegroundColor(Color fg) {
		super.setForegroundColor(fg);
	}


	class Tank extends RoundedRectangle {			
			private final Color EFFECT3D_OUTLINE_COLOR = CustomMediaFactory.getInstance().getColor(
					new RGB(160, 160, 160));
			private static final int DEFAULT_CORNER = 15;
			private Boolean support3d;
			public Tank() {
				super();
				setOutline(true);
			}
			@Override
			protected void fillShape(Graphics graphics) {
				
				int fill_corner = DEFAULT_CORNER;
				//If this is more close to 1/2, more light the tank will be.
				double intersectFactor = 11d/20d;
				if(bounds.width < 2*DEFAULT_CORNER)
					intersectFactor = 12d/20d;
				int rectWidth = (int) (bounds.width * intersectFactor);
				if(fill_corner > (2*rectWidth - (bounds.width - 2*getLineWidth())))
					fill_corner = 2*rectWidth - bounds.width;
				
				corner.height = fill_corner;
				corner.width = fill_corner;
				graphics.setAntialias(SWT.ON);			
				int valuePosition = ((LinearScale) scale).getValuePosition(getCoercedValue(), false);
				if(support3d == null)
					support3d = GraphicsUtil.testPatternSupported(graphics);
				
				if(effect3D && support3d) {				
					graphics.setBackgroundColor(WHITE_COLOR);
					super.fillShape(graphics);
					//fill background
					Rectangle leftRectangle = new Rectangle(
							bounds.x, bounds.y, rectWidth, bounds.height);
					Pattern leftGradientPattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(),
							leftRectangle.x, leftRectangle.y,
							leftRectangle.x + leftRectangle.width+2, leftRectangle.y,
							fillBackgroundColor, 255, WHITE_COLOR, 0);
					graphics.setBackgroundPattern(leftGradientPattern);
					graphics.fillRoundRectangle(leftRectangle, corner.width, corner.height);
					Rectangle rightRectangle = new Rectangle(
							bounds.x + bounds.width - rectWidth, bounds.y, rectWidth, bounds.height);
					Pattern rightGradientPattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(),
							rightRectangle.x-2, rightRectangle.y, 
							rightRectangle.x + rightRectangle.width, rightRectangle.y,
							WHITE_COLOR, 0, fillBackgroundColor, 255);
					graphics.setBackgroundPattern(rightGradientPattern);
					graphics.fillRoundRectangle(rightRectangle, corner.width, corner.height);
					leftGradientPattern.dispose();
					rightGradientPattern.dispose();
					
					//fill value
					graphics.setBackgroundColor(WHITE_COLOR);
					int fillHeight = bounds.height - (valuePosition - bounds.y) - getLineWidth();
					if (fillHeight > 0) {
						graphics.fillRoundRectangle(new Rectangle(bounds.x,
								valuePosition, bounds.width, fillHeight),
								fill_corner, fill_corner);
						leftRectangle = new Rectangle(bounds.x, valuePosition,
								rectWidth, fillHeight);
						leftGradientPattern = GraphicsUtil.createScaledPattern(
								graphics, Display.getCurrent(), leftRectangle.x,
								leftRectangle.y, leftRectangle.x
										+ leftRectangle.width+2, leftRectangle.y,
								fillColor, 255, WHITE_COLOR, 0);
						graphics.setBackgroundPattern(leftGradientPattern);
						graphics.fillRoundRectangle(leftRectangle, fill_corner,
								fill_corner);
	
						rightRectangle = new Rectangle(bounds.x + bounds.width
								- rectWidth, valuePosition, rectWidth, fillHeight);
						rightGradientPattern = GraphicsUtil.createScaledPattern(
								graphics, Display.getCurrent(), rightRectangle.x-2,
								rightRectangle.y, rightRectangle.x
										+ rightRectangle.width, rightRectangle.y,
								WHITE_COLOR, 0, fillColor, 255);
						graphics.setBackgroundPattern(rightGradientPattern);
						graphics.fillRoundRectangle(rightRectangle, fill_corner,
								fill_corner);
	
						leftGradientPattern.dispose();
						rightGradientPattern.dispose();
					}
					graphics.setForegroundColor(EFFECT3D_OUTLINE_COLOR);
					
				}else {
					graphics.setBackgroundColor(fillBackgroundColor);
					super.fillShape(graphics);				
					graphics.setBackgroundColor(fillColor);
					graphics.fillRoundRectangle(new Rectangle(bounds.x + lineWidth, 
						valuePosition,
						bounds.width - 2* lineWidth, 
						bounds.height - (valuePosition - bounds.y)),
						fill_corner, fill_corner);
			//		graphics.setForegroundColor(outlineColor);
				}			
			}		
		}


	static class TankLayout extends AbstractLayout {
		
		/** Used as a constraint for the scale. */
		public static final String SCALE = "scale";   //$NON-NLS-1$
		/** Used as a constraint for the pipe indicator. */
		public static final String TANK = "tank"; //$NON-NLS-1$
		/** Used as a constraint for the alarm ticks */
		public static final String MARKERS = "markers";      //$NON-NLS-1$
		private LinearScale scale;
		private LinearScaledMarker marker;
		private Tank tank;
	
		
		@Override
		protected Dimension calculatePreferredSize(IFigure container, int w,
				int h) {
			Insets insets = container.getInsets();
			Dimension d = new Dimension(64, 4*64);
			d.expand(insets.getWidth(), insets.getHeight());
			return d;
		}
		
		public void layout(IFigure container) {
			Rectangle area = container.getClientArea();		
			area.height-=1;
			Dimension scaleSize = new Dimension(0, 0);
			Dimension markerSize = new Dimension(0, 0);
			
			if(scale != null) {
				if(scale.isVisible()){
					scaleSize = scale.getPreferredSize(-1, area.height);
					scale.setBounds(new Rectangle(area.x, area.y, 
						scaleSize.width, scaleSize.height));	
				}else{
					scaleSize = scale.getPreferredSize(-1, area.height+2*scale.getMargin());
					scale.setBounds(new Rectangle(area.x, area.y-scale.getMargin(), 
							scaleSize.width, scaleSize.height));
					scaleSize.height=0;
					scaleSize.width=0;
				}
								
			}
			
			if(marker != null && marker.isVisible()) {
				markerSize = marker.getPreferredSize();
				marker.setBounds(new Rectangle(area.x + area.width - markerSize.width,
						marker.getScale().getBounds().y, markerSize.width, markerSize.height));			
			}
			
			if(tank != null) {
				tank.setBounds(new Rectangle(
						area.x + scaleSize.width,
						scale.getValuePosition(scale.getRange().getUpper(), false),
						area.width - scaleSize.width - markerSize.width,
						scale.getTickLength()+ tank.getLineWidth()));
			}	
		}
	
		@Override
		public void setConstraint(IFigure child, Object constraint) {
			if(constraint.equals(SCALE))
				scale = (LinearScale)child;
			else if (constraint.equals(MARKERS))
				marker = (LinearScaledMarker) child;
			else if (constraint.equals(TANK))
				tank = (Tank) child;
		}
	
	}
}

