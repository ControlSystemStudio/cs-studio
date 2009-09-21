package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.widgets.model.PolyLineModel.ArrowType;
import org.csstudio.opibuilder.widgets.util.GraphicsUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
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

	public static final double ARROW_ANGLE = Math.PI/10;
	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fill = 100.0;
	private boolean antiAlias;
	private boolean horizontalFill;
	private boolean _transparent;

	private boolean fillArrow = true;
	
	private ArrowType arrowType;

	private int arrowLineLength = 30;
	


	/**
	 * Constructor.
	 */
	public PolylineFigure() {
		setFill(true);
		setBackgroundColor(ColorConstants.darkGreen);
	}
	
	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void outlineShape(final Graphics graphics) {

		graphics.setAntialias(antiAlias ? SWT.ON : SWT.OFF);	
		
		Rectangle figureBounds = getBounds();

		
		if(!_transparent){
			graphics.setForegroundColor(getBackgroundColor());
			drawPolyLineWithArrow(graphics);
		}
		
		//set clip by fill level
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
		drawPolyLineWithArrow(graphics);
				
	}

	private void drawPolyLineWithArrow(Graphics graphics){
		PointList points = getPoints().getCopy();

		graphics.pushState();
		
		if(points.size() >= 2){
			Point endPoint = points.getLastPoint(); 
			Point firstPoint = points.getFirstPoint();
			if(arrowType == ArrowType.To || arrowType == ArrowType.Both){
				//draw end arrow
				PointList arrowPoints = GraphicsUtil.calcArrowPoints(points.getPoint(points.size()-2),
							endPoint, arrowLineLength, ARROW_ANGLE);	
				if(fillArrow)
					points.setPoint(arrowPoints.getLastPoint(), points.size()-1);
				arrowPoints.setPoint(endPoint, 2);
				if(fillArrow){			
					graphics.setBackgroundColor(graphics.getForegroundColor());
					graphics.fillPolygon(arrowPoints);
						
				}else{
					graphics.drawLine(endPoint, arrowPoints.getFirstPoint());
					graphics.drawLine(endPoint, arrowPoints.getMidpoint());
				}
			}
			if(arrowType == ArrowType.From || arrowType == ArrowType.Both){
				//draw start arrow			
				PointList arrowPoints = GraphicsUtil.calcArrowPoints(points.getPoint(1),
						firstPoint, arrowLineLength, ARROW_ANGLE);
				if(fillArrow)
					points.setPoint(arrowPoints.getLastPoint(), 0);
				arrowPoints.setPoint(firstPoint, 2);
				if(fillArrow){			
					graphics.setBackgroundColor(graphics.getForegroundColor());
					graphics.fillPolygon(arrowPoints);
				}else{
					graphics.drawLine(firstPoint, arrowPoints.getFirstPoint());
					graphics.drawLine(firstPoint, arrowPoints.getMidpoint());
				}
			}
		}		
		graphics.drawPolyline(points);
		graphics.popState();	
	}
	
	/**
	 * Overridden, to ensure that the bounds rectangle gets repainted each time,
	 * the points of the polygon change. {@inheritDoc}
	 */
	/*@Override
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
*/


	@Override
	public Rectangle getBounds() {
		if (bounds == null) {
			bounds = GraphicsUtil.getPointsBoundsWithArrows(
					getPoints(), arrowType, arrowLineLength, ARROW_ANGLE);
			if(lineWidth <= 1){
				bounds = bounds.getExpanded(1,1); //expand 1 for container scrollbar bug
			}else
				bounds = bounds.getExpanded(lineWidth / 2, lineWidth / 2);
		}
		return bounds;
	}
	
	
	/**
	 * Translates this Figure's bounds, without firing a move.
	 * @param dx The amount to translate horizontally
	 * @param dy The amount to translate vertically
	 * @see #translate(int, int)
	 * @since 2.0
	 */
	public void primTranslate(int dx, int dy) {
		bounds.x += dx;
		bounds.y += dy;
		if (useLocalCoordinates()) {
			fireCoordinateSystemChanged();
			return;
		}
		for (int i = 0; i < getChildren().size(); i++)
			((IFigure)getChildren().get(i)).translate(dx, dy);
	}
	
	
	@Override
	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
		bounds = bounds.getExpanded(lineWidth / 2, lineWidth / 2);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Rectangle getHandleBounds() {
		return getBounds();
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
}
