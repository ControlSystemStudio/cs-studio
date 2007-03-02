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
package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A simple waveform figure.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class WaveformFigure extends Panel implements IRefreshableFigure {
	/**
	 * The displayed waveform data.
	 */
	private double[] _data;

	/**
	 * Standard constructor.
	 */
	public WaveformFigure() {
		_data = new double[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public void randomNoiseRefresh() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		return null;
	}

	/**
	 * Set the waveform data that is to be displayed. Perform a repaint
	 * afterwards.
	 * 
	 * @param data
	 *            The waveform data that is to be displayed
	 */
	public void setData(final double[] data) {
		_data = data;
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintFigure(final Graphics graphics) {
		Rectangle figureBounds = getBounds();

		double i = 0;

		Point lastPoint = null;

		for (double value : _data) {
			if (lastPoint == null) {
				lastPoint = new Point(i, value).translate(figureBounds
						.getTopLeft());
			}

			Point currentPoint = new Point(i, value).translate(figureBounds
					.getTopLeft());

			graphics.drawLine(lastPoint, currentPoint);

			lastPoint = currentPoint.getCopy();
			i += 5;
		}
	}
}
