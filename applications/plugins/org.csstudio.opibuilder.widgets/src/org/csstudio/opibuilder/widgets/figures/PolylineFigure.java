package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.widgets.figures.ScaledSliderFigure.Thumb;
import org.csstudio.opibuilder.widgets.model.PolyLineModel.ArrowType;
import org.csstudio.opibuilder.widgets.util.RotationUtil;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.SWT;

/**
 * A polyline figure.
 * 
 * @author Xihui Chen
 * 
 */
public final class PolylineFigure extends Polyline implements HandleBounds {

	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fill = 100.0;
	private boolean antiAlias;
	private boolean horizontalFill;
	private boolean _transparent;

	private boolean fillArrow;
	
	private ArrowType arrowType;

	private int arrowLineLength = 30;
	
	private Arrow startArrow, endArrow;

	/**
	 * Constructor.
	 */
	public PolylineFigure() {
		setFill(true);
		setBackgroundColor(ColorConstants.darkGreen);
		/*startArrow = new Arrow();
		endArrow = new Arrow();
		setLayoutManager(new PolyLineLayout());
		add(startArrow, PolyLineLayout.START_ARROW);
		add(endArrow, PolyLineLayout.END_ARROW);*/
	}
	
	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void outlineShape(final Graphics graphics) {
		PointList points = getPoints();
		
		//if(points.size() >= 2){
		//	drawArrow(graphics, points.getPoint(points.size()-2),
		//			points.getLastPoint(), arrowLineLength, fillArrow);
		//}
		graphics.setAntialias(antiAlias ? SWT.ON : SWT.OFF);
		graphics.pushState();
		Rectangle figureBounds = getBounds();
		if(!_transparent){
			graphics.setForegroundColor(getBackgroundColor());
			graphics.drawPolyline(points);	
		}
				
		if(horizontalFill){
			
			int newW = (int) Math.round(figureBounds.width * (getFill() / 100));
			
			graphics
				.clipRect(new Rectangle(figureBounds.x, figureBounds.y, newW, figureBounds.height));
		}else{
			int newH = (int) Math.round(figureBounds.height * (getFill() / 100));			
			graphics
				.clipRect(new Rectangle(figureBounds.x, figureBounds.y + figureBounds.height - newH, 
						figureBounds.width, newH));
		}
		graphics.setForegroundColor(getForegroundColor());		
		graphics.drawPolyline(points);
		graphics.popState();
		
		
					
	}
	
	private void drawArrow(Graphics graphics, Point startPoint, Point endPoint, int arrow_line_length, boolean fillArrow){
		int dy = startPoint.y - endPoint.y;
		int dx = startPoint.x - endPoint.x;		
		if(dx ==0 || dy == 0)
			return;
		//draw Arrow
		int x1 = (int) (arrow_line_length*Math.cos(Math.atan(-dy/dx)-Math.PI/9));
		int y1 = (int) (arrow_line_length*Math.sin(Math.atan(-dy/dx)-Math.PI/9));
		if(dx <0){
			x1 = -x1;
			y1 = -y1;
		}			
		graphics.drawPolyline(new int[]{endPoint.x + x1, endPoint.y - y1, endPoint.x, endPoint.y});
		x1 = (int) (arrow_line_length*Math.cos(Math.atan(-dy/dx)+Math.PI/9));
		y1 = (int) (arrow_line_length*Math.sin(Math.atan(-dy/dx)+Math.PI/9));
		if(dx <0){
			x1 = -x1;
			y1 = -y1;
		}	
		graphics.drawPolyline(new int[]{endPoint.x + x1, endPoint.y - y1, endPoint.x, endPoint.y});

	}
	
	

	/**
	 * Overridden, to ensure that the bounds rectangle gets repainted each time,
	 * the points of the polygon change. {@inheritDoc}
	 */
	@Override
	public void setBounds(final Rectangle rect) {
		invalidate();
		fireFigureMoved();
		repaint();
		int correctedWidth = rect.width + getLineWidth();
		int correctedHeight = rect.height + getLineWidth();
		Rectangle correctedRectangle = new Rectangle(rect.x, rect.y, correctedWidth, correctedHeight);
		super.setBounds(correctedRectangle);
	}
	
	@Override
	public void setSize(final int w, final int h) {
		int correctedWidth = w + getLineWidth();
		int correctedHeight = h + getLineWidth();
		super.setSize(correctedWidth, correctedHeight);
	}
	
	@Override
	public void setLocation(Point p) {
		super.setLocation(p);
	}


	/**
	 * {@inheritDoc}
	 */
	public Rectangle getHandleBounds() {
		return getPoints().getBounds();
	}

	/**
	 * Sets the fill grade.
	 * 
	 * @param fill
	 *            the fill grade.
	 */
	public void setFill(final double fill) {
		_fill = fill;
	}

	/**
	 * Gets the fill grade.
	 * 
	 * @return the fill grade
	 */
	public double getFill() {
		return _fill;
	}
	
	/**
	 * Sets the orientation (horizontal==true | vertical==false).
	 * 
	 * @param horizontal
	 *            The orientation.
	 */
	public void setOrientation(final boolean horizontal) {
		horizontalFill = horizontal;
	}

	/**
	 * Gets the orientation (horizontal==true | vertical==false).
	 * 
	 * @return boolean The orientation
	 */
	public boolean getOrientation() {
		return horizontalFill;
	}

	
	/**
	 * Sets the transparent state of the background.
	 * 
	 * @param transparent
	 *            the transparent state.
	 */
	public void setTransparent(final boolean transparent) {
		_transparent = transparent;
	}

	/**
	 * Gets the transparent state of the background.
	 * 
	 * @return the transparent state of the background
	 */
	public boolean getTransparent() {
		return _transparent;
	}
	
	
	public void setFillArrow(boolean fillArrow) {
		this.fillArrow = fillArrow;
	}
	
	public void setArrowType(ArrowType arrowType) {
		this.arrowType = arrowType;
	}
	
	public void setArrowLineLength(int arrowLineLength) {
		this.arrowLineLength = arrowLineLength;
	}
	
	class Arrow extends Polygon {
		public static final  int LENGTH = 20;
		public static final  int BREADTH = 20;
		public final PointList  arrowPointList = new PointList(new int[] {
				0,0,  LENGTH, BREADTH/2,  0, BREADTH}) ;
		
		public Arrow() {
			setFill(true);
		}
		
	}
	
	
	class PolyLineLayout extends AbstractLayout{
		
		
		/** Used as a constraint for the start arrow */
		public static final String START_ARROW = "startArrow";      //$NON-NLS-1$

		/** Used as a constraint for the end arrow */
		public static final String END_ARROW = "endArrow";      //$NON-NLS-1$
		
		
		private Arrow startArrow;
		private Arrow endArrow;
		
		@Override
		public void setConstraint(IFigure child, Object constraint) {
			if(constraint.equals(START_ARROW))
				startArrow = (Arrow)child;
			else if (constraint.equals(END_ARROW))
				endArrow = (Arrow)child;
		}
		
		@Override
		protected Dimension calculatePreferredSize(IFigure container, int hint,
				int hint2) {
			return new Dimension(hint, hint2);
		}

		public void layout(IFigure container) {
			Rectangle area = container.getClientArea().getCopy();
			
			Point startPoint = getPoints().getFirstPoint();
			Point endPoint = getPoints().getLastPoint();
			
			if(startArrow != null && startArrow.isVisible()){
				Point nextPoint = getPoints().getPoint(1);
				int dy = nextPoint.y - startPoint.y;
				int dx = nextPoint.x - startPoint.x;
				PointList pl = startArrow.arrowPointList.getCopy();
				pl.translate(getPoints().getFirstPoint());
				RotationUtil.rotatePoints(pl, 90 - Math.atan(-dy/dx));
				startArrow.setPoints(pl);
			}
			
		}
		
	}
	
}
