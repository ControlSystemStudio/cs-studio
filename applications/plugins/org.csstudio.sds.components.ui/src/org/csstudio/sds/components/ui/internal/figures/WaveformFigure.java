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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

/**
 * A simple waveform figure.
 * 
 * @author Sven Wende, Kai Meyer
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
	private boolean _showConnectionLines = false;

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
		//_data = data;
		
		int count = 2000;
		int amplitude = 50;
		int verschiebung = 0;
		double[] result = new double[count];
		double value = (Math.PI*2)/count;
		for (int i=0;i<count;i++) {
			result[i] = (Math.sin(value*i)*amplitude)+verschiebung;
		}
		_data = result;
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintFigure(final Graphics graphics) {
//		super.paintFigure(graphics);
//		
//		Rectangle figureBounds = getBounds();
//
//		int x = 0;
//		
//		for (int i=0;i<_data.length;i+=5) {
//			x++;
//			double y =150+  _data[i]*100;
//			
//			Point p = new Point(x,y).translate(figureBounds.getTopLeft());
//			
//			graphics.drawPoint(p.x, p.y);
//		}
		super.paintFigure(graphics);
		graphics.setForegroundColor(ColorConstants.black);
		
		Rectangle figureBounds = this.getBounds();
		
		PointList pointList = this.calculatePoints(figureBounds);
		
		Point p = this.getMinMaxY(pointList);
		int min = p.y;
		int max = p.x;		
		System.out.println(max+"/"+min);
		
		int x = figureBounds.x;
		int y;
		if (min<0 && max<0) {
			y = figureBounds.y;
			System.out.println("<:"+y);
		} else if (min>=0 && max >=0) {
			y = figureBounds.y+figureBounds.height-1;
			System.out.println(">:"+y);
		} else {
			y = figureBounds.y + (int)(((double)figureBounds.height/(max-min))*max);
			System.out.println("<>:"+y);
		}
		graphics.drawLine(x, figureBounds.y, x, figureBounds.y+figureBounds.height);
		graphics.drawLine(x, y, x+figureBounds.width, y);
		
		//pointList.performTranslate(x, y);
		pointList = this.translatePointList(pointList, x, y);
		if (_showConnectionLines) {
			graphics.drawPolyline(pointList);
		}
		graphics.setForegroundColor(ColorConstants.red);
		for (int i=0;i<pointList.size();i++) {
			p = pointList.getPoint(i);//.translate(figureBounds.getTopLeft());
			graphics.drawPoint(p.x,p.y);
		}
	}
	
	/**
	 * Calculates all Points for the curve and add them into a PointList.
	 * Caution! These points are zero-related.
	 * @param bounds
	 * 			The bounds for the Rectangle of the curve
	 * @return PoinList
	 * 			The PointList with all Points
	 */
	private PointList calculatePoints(final Rectangle bounds) {
		PointList pointList = new PointList();
		
		int stepSize = Math.max(1, (int)Math.ceil((double)_data.length/bounds.width));
		int pointCount;
		if (_data.length>bounds.width) {
			pointCount = (int) Math.ceil((double)_data.length / stepSize);
		}  else {
			pointCount = _data.length;
		}
				
		for (int i=0;i<pointCount;i++) {
			double yValue = 0; 
			for (int j=0;j<stepSize;j++) {
				int index = Math.min(_data.length-1, j+i*stepSize);
				yValue = yValue + _data[index];
			}
			yValue = yValue / stepSize;
			pointList.addPoint( new Point( ((bounds.width-1)*i)/(pointCount-1), yValue ) );
		}
		return pointList;
	}
	
	/**
	 * Gets the minimal and the maximal value for y of all Points.
	 * @param pointList
	 * 				The PointList of the points
	 * @return Point
	 * 				The x value is the maximum, the y value is the minimum.  
	 */
	private Point getMinMaxY(final PointList pointList) {
		int min = pointList.getPoint(0).y;
		int max = pointList.getPoint(0).y;
		for (int i=1;i<pointList.size();i++) {
			Point p = pointList.getPoint(i); 
			if (p.y<min) {
				min = p.y;
			}
			if (p.y>max) {
				max = p.y;
			}
		}
		return new Point(max, min);
	}
	
	/**
	 * Translates all Points in PointList. 
	 * @param pointList 
	 * 				The PointList
	 * @param x
	 * 				The reference x value
	 * @param y
	 * 				The reference y value
	 * @return PointList
	 * 				The new PointList
	 */
	private PointList translatePointList(final PointList pointList, final int x, final int y) {
		Point start = new Point(x,y);
		PointList result = new PointList();
		for (int i=0;i<pointList.size();i++) {
			start = new Point(x,y);
			Point p = pointList.getPoint(i);
			p.y = -p.y;
			//Point newPoint = start.translate(p);
			result.addPoint(start.translate(p));
		}
		return result;
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
	public void setShowLedgerlLines(final int showHelpLines) {
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
	public void setShowConnectionLines(final boolean showPointLines) {
		_showConnectionLines = showPointLines;
	}
	
	/**
	 * Gets if the point lines should be drawn.
	 * @return boolean
	 * 			true, the point lines should be drawn, false otherwise
	 */
	public boolean getShowPointLines() {
		return _showConnectionLines;
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
