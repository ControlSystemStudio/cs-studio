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

import java.util.LinkedList;
import java.util.List;

import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
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
	 * Show vertical.
	 */
	private static final int SHOW_VERTICAL = 1;  
	/**
	 * Show vertical.
	 */
	private static final int SHOW_HORIZONTAL = 2;
	/**
	 * Show both.
	 */
	private static final int SHOW_BOTH = 3;
	/**
	 * Default constraint for all scales.
	 */
	private static final Rectangle DEFAULT_CONSTRAINT = new Rectangle(0,0,0,0);
	
	/**
	 * The Color for the graph.
	 */
	private Color _graphColor = ColorConstants.red;
	
	/**
	 * The Color for the connection lines.
	 */
	private Color _connectionLineColor = ColorConstants.red;
	
	/**
	 * The displayed waveform data.
	 */
	private double[] _data;
	/**
	 * A double, representing the maximum value of the data.
	 */
	private double _max = 0;
	/**
	 * A double, representing the minimum value of the data.
	 */
	private double _min = 0;
	/**
	 * The zero level of the graph.
	 */
	private int _zeroLevel = 0;
	/**
	 * The PointList based on the real data.
	 */
	//private PointList _dataPoints = new PointList();
	private List<PrecisionPoint> _dataPoints = new LinkedList<PrecisionPoint>();
	/**
	 * The bounds of the graph.
	 */
	private Rectangle _graphBounds = new Rectangle(10,10,10,10);
	
	/**
	 * An int, representing in which way the scale should be drawn.
	 * 0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	private int _showScale = 0;
	/**
	 * The wideness of the Scale.
	 */
	private int _scaleWideness = 10;
	/**
	 * An int, representing in which way the ledger lines should be drawn.
	 */
	private int _showLedgerLines = 0;
	/**
	 * A boolean, which indicates, if the lines from point to point should be drawn.
	 */
	private boolean _showConnectionLines = false;
	
	/**
	 * The vertical scale of this waveform.
	 */
	private Scale _verticalScale;
	/**
	 * The horizontal scale of this waveform.
	 */
	private Scale _horizontalScale;
	/**
	 * The vertical ledger scale.
	 */
	private Scale _verticalLedgerScale;
	/**
	 * The horizontal ledger scale.
	 */
	private Scale _horizontalLedgerScale;
	
	/**
	 * True, if this figure has to be initiated, false otherwise.
	 */
	private boolean _init = true;

	/**
	 * Standard constructor.
	 */
	public WaveformFigure() {
		_data = new double[0];
		this.setLayoutManager(new XYLayout());
		_verticalLedgerScale = new Scale();
		_verticalLedgerScale.setHorizontalOrientation(false);
		_verticalLedgerScale.setLength(20);
		_verticalLedgerScale.setShowNegativeSections(true);
		_verticalLedgerScale.setForegroundColor(ColorConstants.lightGray);
		this.add(_verticalLedgerScale);
		_horizontalLedgerScale = new Scale();
		_horizontalLedgerScale.setHorizontalOrientation(true);
		_horizontalLedgerScale.setLength(20);
		_horizontalLedgerScale.setShowNegativeSections(false);
		_horizontalLedgerScale.setReferencePositions(0);
		_horizontalLedgerScale.setForegroundColor(ColorConstants.lightGray);
		this.add(_horizontalLedgerScale);
		_verticalScale = new Scale();
		_verticalScale.setHorizontalOrientation(false);
		_verticalScale.setLength(20);
		_verticalScale.setShowNegativeSections(true);
		_verticalScale.setForegroundColor(this.getForegroundColor());
		this.add(_verticalScale);
		_horizontalScale = new Scale();
		_horizontalScale.setHorizontalOrientation(true);
		_horizontalScale.setLength(20);
		_horizontalScale.setShowNegativeSections(false);
		_horizontalScale.setReferencePositions(0);
		_horizontalScale.setForegroundColor(this.getForegroundColor());
		this.add(_horizontalScale);
		
//		 listen to figure movement events
		addFigureListener(new FigureListener() {
			public void figureMoved(final IFigure source) {
				refreshConstraints();
			}
		});
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
		
//		int count = 2000;
//		int amplitude = 50;
//		int verschiebung = 00;
//		double[] result = new double[count];
//		double value = (Math.PI*2)/count;
//		for (int i=0;i<count;i++) {
//			result[i] = (Math.sin(value*i)*amplitude)+verschiebung;
//		} 
//		_data = result;
		this.refreshConstraints();
		repaint();
	}
	
	/**
	 * Refreshes the constraints for the scales.
	 */
	private void refreshConstraints() {
		if (_init) {
			return;
		}
		this.setGraphBounds();
		_dataPoints = this.calculatePoints(_graphBounds);
		this.setZeroLevel();
		Rectangle figureBounds = this.getBounds();
		int verticalScaleWidth = 0;
		if (_showScale==SHOW_VERTICAL || _showScale==SHOW_BOTH) {
			verticalScaleWidth = _scaleWideness;
			this.setConstraint(_verticalScale, new Rectangle(0,0,_scaleWideness,figureBounds.height));
			_verticalScale.setReferencePositions(_zeroLevel);
		} else {
			this.setConstraint(_verticalScale, DEFAULT_CONSTRAINT);
		}
		if (_showScale==SHOW_HORIZONTAL || _showScale==SHOW_BOTH) {
			this.setConstraint(_horizontalScale, new Rectangle(verticalScaleWidth,_zeroLevel-(_scaleWideness/2)+1,figureBounds.width-verticalScaleWidth,_scaleWideness));
		} else {
			this.setConstraint(_horizontalScale, DEFAULT_CONSTRAINT);
		}
		
		if (_showLedgerLines==SHOW_HORIZONTAL || _showLedgerLines==SHOW_BOTH) {
			this.setConstraint(_verticalLedgerScale, new Rectangle(verticalScaleWidth,0,figureBounds.width-verticalScaleWidth,figureBounds.height));
			_verticalLedgerScale.setReferencePositions(_zeroLevel);
			_verticalLedgerScale.setWideness(figureBounds.width-verticalScaleWidth);
		} else {
			this.setConstraint(_verticalLedgerScale, DEFAULT_CONSTRAINT);
		}
		if (_showLedgerLines==SHOW_VERTICAL || _showLedgerLines==SHOW_BOTH) {
			this.setConstraint(_horizontalLedgerScale, new Rectangle(verticalScaleWidth,0,figureBounds.width-verticalScaleWidth,figureBounds.height));
			_horizontalLedgerScale.setWideness(figureBounds.height);
		} else {
			this.setConstraint(_horizontalLedgerScale, DEFAULT_CONSTRAINT);
		}
		
		this.setToolTip(this.getToolTipFigure());
	}
	
	/**
	 * Sets the bounds of the graph.
	 */
	private void setGraphBounds() {
		Rectangle figureBounds = this.getBounds();	
		if (_showScale==SHOW_VERTICAL || _showScale==SHOW_BOTH) {
			_graphBounds = new Rectangle(figureBounds.x+_scaleWideness,figureBounds.y,figureBounds.width-_scaleWideness,figureBounds.height);
		} else {
			_graphBounds = new Rectangle(figureBounds.x,figureBounds.y,figureBounds.width,figureBounds.height);
		}
	}
	
	/**
	 * Sets the zero level.
	 */
	private void setZeroLevel() {
		if (_min<0 && _max<0) {
			_zeroLevel = 1;
		} else if (_min>=0 && _max >=0) {
			_zeroLevel = _graphBounds.height-1;
		} else {
			_zeroLevel = (int) (((double)_graphBounds.height/(_max-_min))*_max);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintFigure(final Graphics graphics) {
		super.paintFigure(graphics);
		if (_init) {
			_init = false;
			this.refreshConstraints();
		}					
		int x = _graphBounds.x;
		graphics.setForegroundColor(this.getForegroundColor());
		graphics.drawLine(x, _graphBounds.y, x, _graphBounds.y+_graphBounds.height);
		graphics.drawLine(x, _graphBounds.y+_zeroLevel, x+_graphBounds.width, _graphBounds.y+_zeroLevel);
		PointList pointList = this.translatePointList(_dataPoints, x, _graphBounds);
		if (_showConnectionLines) {
			graphics.setForegroundColor(_connectionLineColor);
			graphics.drawPolyline(pointList);
		}
		graphics.setForegroundColor(_graphColor);
		for (int i=0;i<pointList.size();i++) {
			Point p = pointList.getPoint(i);
			graphics.drawPoint(p.x,p.y);
		}
	}
	
	/**
	 * Gets the IFigure for the tooltip.
	 * @return IFigure
	 * 			The IFigure for the tooltip
	 */
	private IFigure getToolTipFigure() {
		Panel panel = new Panel();
		panel.setLayoutManager(new ToolbarLayout(false));
		panel.add(new Label("Count of data points: "+_data.length));
		panel.add(new Label("Minimum value: "+_min));
		panel.add(new Label("Maximum value: "+_max));
		panel.setBackgroundColor(ColorConstants.tooltipBackground);
		return panel;
	}
	
	/**
	 * Calculates all Points for the curve and add them into a PointList.
	 * Caution! These points are zero-related.
	 * @param bounds
	 * 			The bounds for the Rectangle of the curve
	 * @return PoinList
	 * 			The PointList with all Points
	 */
	private List<PrecisionPoint> calculatePoints(final Rectangle bounds) {
		//PointList pointList = new PointList();
		List<PrecisionPoint> pointList = new LinkedList<PrecisionPoint>();
		
		double min = 0;
		double max = 0;
		
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
			if (yValue<min || i==0) {
				min = yValue;
			}
			if (yValue>max || i==0) {
				max = yValue;
			}
			pointList.add( new PrecisionPoint(  ((bounds.width-1)*i)/(pointCount-1), yValue ) );
		}
		if (min<_min-0.001 || min>_min+0.001) {
			_min = min;
		}
		if (max<_max-0.001 || max>_max+0.001) {
			_max = max;
		}
		return pointList;
	}
	
	/**
	 * Translates all Points in PointList. 
	 * @param pointList 
	 * 				The PointList
	 * @param x
	 * 				The reference x value
	 * @param figureBounds
	 * 				The bounds of the waveform
	 * @return PointList
	 * 				The new PointList
	 */
	private PointList translatePointList(final List<PrecisionPoint> pointList, final int x, final Rectangle figureBounds) {
		PointList result = new PointList();
		double posWeight = (double)(_zeroLevel)/_max;
		double negWeight = (double)(figureBounds.height-_zeroLevel)/Math.abs(_min);
		for (int i=0;i<pointList.size();i++) {
			PrecisionPoint p = pointList.get(i);
			double newY; 
			if (p.y>=0) {
				 newY = p.preciseY * posWeight;
			} else {
				newY = p.preciseY * negWeight;
			}
			Point newPoint = new Point(p.x+x, (double)(figureBounds.y+_zeroLevel) - newY);
			result.addPoint(newPoint);
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
		this.refreshConstraints();
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
	 * @param showLedgerLines
	 * 			0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public void setShowLedgerlLines(final int showLedgerLines) {
		_showLedgerLines = showLedgerLines;
		this.refreshConstraints();
	}
	
	/**
	 * Gets in which way the help lines should be drawn.
	 * @return int
	 * 			0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowLedgerLines() {
		return _showLedgerLines;
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
	
	/**
	 * Sets the color for the graph.
	 * @param graphRGB 
	 * 				The RGB-value for the color
	 */
	public void setGraphColor(final RGB graphRGB) {
		_graphColor = CustomMediaFactory.getInstance().getColor(graphRGB);
	}
	
	/**
	 * Sets the color for the connection lines.
	 * @param lineRGB 
	 * 				The RGB-value for the color
	 */
	public void setConnectionLineColor(final RGB lineRGB) {
		_connectionLineColor = CustomMediaFactory.getInstance().getColor(lineRGB);
	}
	
	/**
	 * This class represents a scale.
	 * 
	 * @author Kai Meyer
	 *
	 */
	private final class Scale extends RectangleFigure {	
		/**
		 * The length of this Scale.
		 */
		private int _length;
		/**
		 * The count of sections in this Scale.
		 */
		private int _sectionCount = -1;
		/**
		 * The direction of this Scale.
		 */
		private boolean _isHorizontal;
		/**
		 * The start position.
		 */
		private int _start = 10;
		/**
		 * True, if the negativ sections should be draan, false otherwise.
		 */
		private boolean _showNegativSections = false;
		/**
		 * The lenght of the lines.
		 */
		private int _wideness = 10;
		
		/**
		 * Sets the length of this Scale.
		 * @param length
		 * 					The lenght of this Scale
		 */
		public void setLength(final int length) {
			_length = length;
		}
		
		/**
		 * Sets the orientation of this Scale.
		 * @param isHorizontal
		 * 					The orientation of this Scale (true=horizontal;false=vertical)
		 */
		public void setHorizontalOrientation(final boolean isHorizontal) {
			_isHorizontal = isHorizontal;
		}
		
		/**
		 * Sets the count of setcion in this Scale.
		 * @param sectionCount
		 * 					The count of setcion in this Scale
		 */
		public void setSectionCount(final int sectionCount) {
			_sectionCount = sectionCount;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintFigure(final Graphics graphics) {
			int sectionWidth = 0;
			int sectionHeight = 0;
			int height = 0;
			int width = 0;
			graphics.setForegroundColor(this.getForegroundColor());
			graphics.setBackgroundColor(this.getBackgroundColor());
			if (_isHorizontal) {
				height = _wideness;
				if (_sectionCount>0) {
					sectionWidth = _length/_sectionCount;
					for (int i=0;i<_sectionCount+1;i++) {
						graphics.drawLine(this.getBounds().x+_start+i*sectionWidth, this.getBounds().y, this.getBounds().x+_start+i*sectionWidth+width , this.getBounds().y+height);
					}
				} else {
					int pos = _start;
					while (pos<this.getBounds().width) {
						graphics.drawLine(this.getBounds().x+pos, this.getBounds().y, this.getBounds().x+pos , this.getBounds().y+height);
						pos = pos +_length;
					}
					if (_showNegativSections) {
						pos = _start;
						while (pos>0) {
							graphics.drawLine(this.getBounds().x, this.getBounds().y+pos, this.getBounds().x+width , this.getBounds().y+pos);
							pos = pos - _length;
						}	
					}
				}
			} else {
				width = _wideness;
				if (_sectionCount>0) {
					sectionHeight = _length/_sectionCount;
					for (int i=0;i<_sectionCount+1;i++) {
						graphics.drawLine(this.getBounds().x, this.getBounds().y+_start+i*sectionHeight, this.getBounds().x+width , this.getBounds().y+_start+i*sectionHeight);
					}	
				} else {
					int pos = _start;
					while (pos<this.getBounds().height) {
						graphics.drawLine(this.getBounds().x, this.getBounds().y+pos, this.getBounds().x+width , this.getBounds().y+pos);
						pos = pos +_length;
					}
					if (_showNegativSections) {
						pos = _start;
						while (pos>0) {
							graphics.drawLine(this.getBounds().x, this.getBounds().y+pos, this.getBounds().x+width , this.getBounds().y+pos);
							pos = pos - _length;
						}	
					}
				}
			}
		}		
		
		/**
		 * Sets the reference values for this figure.
		 * @param start
		 * 				The start value
		 */
		public void setReferencePositions(final int start) {
			_start = start;
		}
		
		/**
		 * Sets if the negative sections should be drawn.
		 * @param showNegativ
		 * 				True, if the negativ sections should be drawn, false otherwise.
		 */
		public void setShowNegativeSections(final boolean showNegativ) {
			_showNegativSections = showNegativ;
		}
		
		/**
		 * Sets the wideness of this scale.
		 * @param wideness
		 * 				The wideness of this scale
		 */
		public void setWideness(final int wideness) {
			_wideness = wideness;
		}
	}
}
