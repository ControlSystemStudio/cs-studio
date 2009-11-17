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
package org.csstudio.opibuilder.widgets.feedback;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.PointList;

/**
 * Custom feedback figure for polyglines. The figure shows a rectangle, which
 * does also include the shape of the polyline.
 * 
 * @author Sven Wende
 * 
 */
public final class PolyFeedbackFigureWithRectangle extends RectangleFigure {
	/**
	 * The "included" polyline.
	 */
	private Polyline _innerFigure;

	/**
	 * Constructor.
	 * 
	 * @param polyline
	 *            the inner figure (may be a polyline or polygon)
	 * @param points
	 *            the polygon points
	 */
	public PolyFeedbackFigureWithRectangle(final Polyline polyline,
			final PointList points) {
		assert polyline != null;
		_innerFigure = polyline;
		add(_innerFigure);
		setPoints(points);
	}

	/**
	 * Gets the point list for the polyline part of this figure.
	 * 
	 * @return a point list
	 */
	public PointList getPoints() {
		return _innerFigure.getPoints();
	}

	/**
	 * Sets the point list for the polyline part.
	 * 
	 * @param points
	 *            the point list
	 */
	public void setPoints(final PointList points) {
		_innerFigure.setPoints(points);
		setBounds(points.getBounds());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final Graphics graphics) {
		// enable tranparency
		graphics.setAlpha(120);
		super.paint(graphics);
	}
}
