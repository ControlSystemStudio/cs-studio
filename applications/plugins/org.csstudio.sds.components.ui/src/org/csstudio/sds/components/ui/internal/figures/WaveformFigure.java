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
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

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
	 * An int, representing in which way the scale should be drawn.
	 */
	private int _showScale = 0;
	/**
	 * An int, representing in which way the help lines should be drawn.
	 */
	private int _showHelpLines = 0;
	/**
	 * A boolean, which indicates, if the lines from point to point should be drawn.
	 */
	private boolean _showPointLines = false;

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
		super.paintFigure(graphics);
		
		Rectangle figureBounds = getBounds();

		int x = 0;
		
		for (int i=0;i<_data.length;i+=5) {
			x++;
			double y =150+  _data[i]*100;
			
			Point p = new Point(x,y).translate(figureBounds.getTopLeft());
			
			graphics.drawPoint(p.x, p.y);
		}
	
	
	}

	/**
	 * Sets in which way the scale should be drawn.
	 * @param showScale
	 * 			0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public void setShowScale(final int showScale) {
		_showScale = showScale;
	}
	
	/**
	 * Gets in which way the scale should be drawn.
	 * @return int
	 * 			0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowScale() {
		return _showScale;
	}

	/**
	 * Sets in which way the help lines should be drawn.
	 * @param showHelpLines
	 * 			0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public void setShowHelplLines(final int showHelpLines) {
		_showHelpLines = showHelpLines;
	}
	
	/**
	 * Gets in which way the help lines should be drawn.
	 * @return int
	 * 			0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowHelpLines() {
		return _showHelpLines;
	}

	/**
	 * Sets if the point lines should be drawn.
	 * @param showPointLines
	 * 			true, the point lines should be drawn, false otherwise
	 */
	public void setShowPointLines(final boolean showPointLines) {
		_showPointLines = showPointLines;
	}
	
	/**
	 * Gets if the point lines should be drawn.
	 * @return boolean
	 * 			true, the point lines should be drawn, false otherwise
	 */
	public boolean getShowPointLines() {
		return _showPointLines;
	}

	/**
	 * Sets the background color of this figure.
	 * @param backgroundRGB 
	 * 				The RGB-value for the color
	 */
	public void setBackgroundColor(final RGB backgroundRGB) {
		this.setBackgroundColor(CustomMediaFactory.getInstance().getColor(backgroundRGB));
	}

	/**
	 * Sets the foreground color of this figure.
	 * @param foregroundRGB 
	 * 				The RGB-value for the color
	 */
	public void setForegroundColor(final RGB foregroundRGB) {
		this.setForegroundColor(CustomMediaFactory.getInstance().getColor(foregroundRGB));
	}
}
