/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * A rectangle figure.
 * 
 * @author Sven Wende (original author), Xihui Chen (since import from SDS 2009/10) 
 * 
 */
public final class OPIRectangleFigure extends RectangleFigure {
	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fillGrade = 100;
	
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
	
	private RGB lineColor = CustomMediaFactory.COLOR_PURPLE;

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void fillShape(final Graphics graphics) {
		graphics.setAntialias(antiAlias ? SWT.ON : SWT.OFF);
		Rectangle figureBounds = getBounds().getCopy();
		figureBounds.crop(this.getInsets());
		if (!_transparent) {
			graphics.setBackgroundColor(getBackgroundColor());
			graphics.fillRectangle(figureBounds);	
		}
		if(getFill() > 0){
			graphics.setBackgroundColor(getForegroundColor());
			Rectangle fillRectangle;
			if (_orientationHorizontal) {
				int newW = (int) Math.round(figureBounds.width * (getFill() / 100));
				fillRectangle = new Rectangle(figureBounds.x,figureBounds.y,newW,figureBounds.height);
			} else {
				int newH = (int) Math.round(figureBounds.height * (getFill() / 100));
				fillRectangle = new Rectangle(figureBounds.x,figureBounds.y+figureBounds.height-newH,figureBounds.width,newH);
			}
			graphics.fillRectangle(fillRectangle);
		}
	}
	
	/**
	 * @see Shape#outlineShape(Graphics)
	 */
	protected void outlineShape(Graphics graphics) {
	    float lineInset = Math.max(1.0f, getLineWidth()) / 2.0f;
	    int inset1 = (int)Math.floor(lineInset);
	    int inset2 = (int)Math.ceil(lineInset);

	    Rectangle r = Rectangle.SINGLETON.setBounds(getClientArea());
	    r.x += inset1 ; 
	    r.y += inset1; 
	    r.width -= inset1 + inset2;
	    r.height -= inset1 + inset2;
	    graphics.pushState();
	    graphics.setForegroundColor(CustomMediaFactory.getInstance().getColor(lineColor));
	    graphics.drawRectangle(r);
	    graphics.popState();
	}
	
	
	/**
	 * Sets the fill grade.
	 * 
	 * @param fill
	 *            the fill grade.
	 */
	public void setFill(final double fill) {
		_fillGrade = fill;
	}

	/**
	 * Gets the fill grade.
	 * 
	 * @return the fill grade
	 */
	public double getFill() {
		return _fillGrade;
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
	 * @return boolean
	 * 				The orientation
	 */
	public boolean getOrientation() {
		return _orientationHorizontal;
	}

	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}


	/**
	 * @param lineColor the lineColor to set
	 */
	public void setLineColor(RGB lineColor) {
		this.lineColor = lineColor;
	}



}
