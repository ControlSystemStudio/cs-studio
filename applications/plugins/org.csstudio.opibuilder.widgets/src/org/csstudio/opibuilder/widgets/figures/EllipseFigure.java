package org.csstudio.opibuilder.widgets.figures;

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

/**
 * An ellipse figure.
 * 
 * @author Sven Wende, Alexander Will, Xihui Chen
 * 
 */
public final class EllipseFigure extends Ellipse {

	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fill = 100.0;

	/**
	 * The orientation (horizontal==true | vertical==false).
	 */
	private boolean _orientationHorizontal = true;

	/**
	 * The transparent state of the background.
	 */
	private boolean _transparent = false;

	
	/**
	 * The antiAlias flag
	 */
	private boolean antiAlias = true;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillShape(final Graphics graphics) {
		graphics.setAntialias(antiAlias ? SWT.ON : SWT.OFF);
		
		Rectangle figureBounds = getBounds().getCopy();
		figureBounds.crop(this.getInsets());

		Rectangle backgroundRectangle;
		Rectangle fillRectangle;
		if (_orientationHorizontal) {
			int newW = (int) Math.round(figureBounds.width * (getFill() / 100));
			backgroundRectangle = new Rectangle(figureBounds.x + newW,
					figureBounds.y, figureBounds.width - newW,
					figureBounds.height);
			fillRectangle = new Rectangle(figureBounds.x, figureBounds.y, newW,
					figureBounds.height);
		} else {
			int newH = (int) Math
					.round(figureBounds.height * (getFill() / 100));
			backgroundRectangle = new Rectangle(figureBounds.x, figureBounds.y,
					figureBounds.width, figureBounds.height - newH);
			fillRectangle = new Rectangle(figureBounds.x, figureBounds.y
					+ figureBounds.height - newH, figureBounds.width, newH);
		}
		if (!_transparent) {
			graphics.pushState();
			graphics.setClip(backgroundRectangle);
			graphics.setBackgroundColor(getBackgroundColor());
			graphics.fillOval(figureBounds);
			graphics.popState();
		}
		
		graphics.pushState();
		
		graphics.clipRect(fillRectangle);
		graphics.setBackgroundColor(getForegroundColor());
		
		graphics.fillOval(figureBounds);
		graphics.popState();
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

	/**
	 * Sets the orientation (horizontal==true | vertical==false).
	 * 
	 * @param horizontal
	 *            The orientation.
	 */
	public void setOrientation(final boolean horizontal) {
		_orientationHorizontal = horizontal;
	}

	/**
	 * Gets the orientation (horizontal==true | vertical==false).
	 * 
	 * @return boolean The orientation
	 */
	public boolean getOrientation() {
		return _orientationHorizontal;
	}

	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}
	
	
}
