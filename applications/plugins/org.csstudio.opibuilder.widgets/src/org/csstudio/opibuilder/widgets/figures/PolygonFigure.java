package org.csstudio.opibuilder.widgets.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.SWT;

/**
 * A polygon figure.
 * 
 * @author Sven Wende, Stefan Hofer, Xihui chen
 * 
 */
public final class PolygonFigure extends Polygon implements HandleBounds {

	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fill = 100.0;
	
	private boolean antiAlias = true;

	private boolean horizontalFill;

	private boolean _transparent;
	
	
	/**
	 * Constructor.
	 */
	public PolygonFigure() {
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
	protected void fillShape(final Graphics graphics) {
		graphics.setAntialias(antiAlias ? SWT.ON : SWT.OFF);
		graphics.pushState();	
		Rectangle figureBounds = getBounds();
		if(!_transparent){
			graphics.setBackgroundColor(getBackgroundColor());
			graphics.fillPolygon(getPoints());
		}
		
		
		graphics.setBackgroundColor(getForegroundColor());		
		if(horizontalFill){
			int newW = (int) Math.round(figureBounds.width * (getFill() / 100));			
			graphics
				.setClip(new Rectangle(figureBounds.x, figureBounds.y, newW, figureBounds.height));
		}else{
			int newH = (int) Math.round(figureBounds.height * (getFill() / 100));			
			graphics
				.setClip(new Rectangle(figureBounds.x, figureBounds.y + figureBounds.height - newH, 
						figureBounds.width, newH));
		}
		graphics.fillPolygon(getPoints());
		graphics.popState();
	}

	/**
	 * Overridden, to ensure that the bounds rectangle gets repainted each time,
	 * the _points of the polygon change. {@inheritDoc}
	 */
	@Override
	public void setBounds(final Rectangle rect) {
		invalidate();
		fireFigureMoved();
		repaint();
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

	
}
