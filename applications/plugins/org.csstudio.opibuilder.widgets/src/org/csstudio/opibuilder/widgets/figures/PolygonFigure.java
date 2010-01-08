/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * A polygon figure.
 * 
 * @author Sven Wende, Stefan Hofer, Xihui chen (since import from SDS 2009/10) 
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

	private RGB lineColor = CustomMediaFactory.COLOR_BLUE;
	
	
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
	
	@Override
	protected void outlineShape(Graphics g) {
		g.pushState();
		g.setForegroundColor(CustomMediaFactory.getInstance().getColor(lineColor));
		super.outlineShape(g);
		g.popState();
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

	public void setLineColor(RGB lineColor) {
		this.lineColor = lineColor;
	}

	
}
