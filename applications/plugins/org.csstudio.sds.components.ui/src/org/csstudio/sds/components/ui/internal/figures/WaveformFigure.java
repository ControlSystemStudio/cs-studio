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

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.BorderLayout;
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
public final class WaveformFigure extends Panel implements IAdaptable {
	
	/**
	 * Maximum difference to tolerate between actual and displayed
	 * minimum/maximum when autoscaling is enabled.
	 */
	private static final double AUTOSCALE_TRESHOLD = 0.001;

	/**
	 * Height of the text.
	 */
	private static final int TEXTHEIGHT = 14;

	/**
	 * Width of the text.
	 */
	private static final int TEXTWIDTH = 46;

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
	private static final Rectangle DEFAULT_CONSTRAINT = new Rectangle(0, 0, 0,
			0);

	/**
	 * The displayed waveform data.
	 */
	private double[] _data;

	/**
	 * A double, representing the maximum value of the data.
	 * This value can be calculated, if <code>_autoScale</code> is true
	 */
	private double _max = 0;

	/**
	 * A double, representing the minimum value of the data.
	 * This value can be calculated, if <code>_autoScale</code> is true
	 */
	private double _min = 0;
	
	/**
	 * A double, representing the maximum value of the data.
	 * This value is only setted by the properties
	 */
	private double _settedMax = 0;

	/**
	 * A double, representing the minimum value of the data.
	 * This value is only setted by the properties
	 */
	private double _settedMin = 0;

	/**
	 * The zero level of the graph.
	 */
	private int _zeroLevel = 0;
	
	/**
	 * The transparent state of the background.
	 */
	private boolean _transparent = false;

	/**
	 * The PointList based on the real data.
	 */
	private List<PrecisionPoint> _dataPoints = new LinkedList<PrecisionPoint>();

	/**
	 * The bounds of the actual graph area (where the data points are drawn).
	 * The location of the rectangle is relative to the figure bounds.
	 */
	private Rectangle _graphBounds = new Rectangle(10, 10, 10, 10);

	/**
	 * An int, representing in which way the scale should be drawn. 0 = None; 1 =
	 * Vertical; 2 = Horizontal; 3 = Both
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
	 * A boolean, which indicates, if the lines from point to point should be
	 * drawn.
	 */
	private boolean _showConnectionLines = false;
	
	/**
	 * A boolean, which indicates, if the values should be shown.
	 */
	private boolean _showValues = true;

	/**
	 * A boolean, which indicates, if the graph should be automatically scaled.
	 */
	private boolean _autoScale = false;

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
	 * The graph of this waveform.
	 */
	private GraphFigure _graphFigure;

	/**
	 * True, if this figure has to be initiated, false otherwise.
	 */
	private boolean _init = true;

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	/**
	 * The count of sections on the y-axis.
	 */
	private int _ySectionCount = 4;
	
	/**
	 * The count of sections on the x-axis.
	 */
	private int _xSectionCount = 4;

	/**
	 * Standard constructor.
	 */
	public WaveformFigure() {
		_data = new double[0];
		this.setLayoutManager(new XYLayout());
		_verticalLedgerScale = new Scale();
		_verticalLedgerScale.setHorizontalOrientation(false);
		_verticalLedgerScale.setSectionLength(20);
		_verticalLedgerScale.setShowNegativeSections(true);
		_verticalLedgerScale.setShowValues(false);
		_verticalLedgerScale.setForegroundColor(ColorConstants.lightGray);
		this.add(_verticalLedgerScale);
		_horizontalLedgerScale = new Scale();
		_horizontalLedgerScale.setHorizontalOrientation(true);
		_horizontalLedgerScale.setSectionLength(50);
		_horizontalLedgerScale.setShowNegativeSections(false);
		_horizontalLedgerScale.setReferencePositions(0);
		_horizontalLedgerScale.setShowValues(false);
		_horizontalLedgerScale.setForegroundColor(ColorConstants.lightGray);
		this.add(_horizontalLedgerScale);
		_verticalScale = new Scale();
		_verticalScale.setHorizontalOrientation(false);
		_verticalScale.setSectionLength(20);
		_verticalScale.setShowNegativeSections(true);
		_verticalScale.setShowValues(_showValues);
		_verticalScale.setAlignment(true);
		_verticalScale.setForegroundColor(this.getForegroundColor());
		this.add(_verticalScale);
		_horizontalScale = new Scale();
		_horizontalScale.setHorizontalOrientation(true);
		_horizontalScale.setSectionLength(50);
		_horizontalScale.setShowNegativeSections(false);
		_horizontalScale.setReferencePositions(0);
		_horizontalScale.setShowFirstMarker(false);
		_horizontalScale.setShowValues(_showValues);
		_horizontalScale.setAlignment(false);
		_horizontalScale.setForegroundColor(this.getForegroundColor());
		this.add(_horizontalScale);
		_graphFigure = new GraphFigure();
		this.add(_graphFigure);

		// listen to figure movement events
		addFigureListener(new FigureListener() {
			public void figureMoved(final IFigure source) {
				refreshConstraints();
			}
		});
	}

	/**
	 * This method is a tribute to unit tests, which need a way to test the
	 * performance of the figure implementation. Implementors should produce
	 * some random changes and refresh the figure, when this method is called.
	 * 
	 */
	public void randomNoiseRefresh() {
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if (_borderAdapter == null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
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

//		 int count = 100;
//		 int amplitude = 75;
//		 int verschiebung = 0;
//		 double[] result = new double[count];
//		 double value = (Math.PI*2)/count;
//		 for (int i=0;i<count;i++) {
//			 result[i] = (Math.sin(value*i)*amplitude)+verschiebung;
//		 }
//		 _data = result;
		 
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
		_graphBounds = this.getGraphBounds();
		_dataPoints = this.calculatePoints(_graphBounds);
		_zeroLevel = this.valueToYPos(0.0);
		int verticalScaleWidth = 0;
		if (_showScale == SHOW_VERTICAL || _showScale == SHOW_BOTH) {
			verticalScaleWidth = _scaleWideness;
			if (_showValues) {
				verticalScaleWidth = verticalScaleWidth + TEXTWIDTH;
			}
			this.setConstraint(_verticalScale, new Rectangle(0, 0,
					verticalScaleWidth, _graphBounds.height+TEXTHEIGHT));
			_verticalScale.setReferencePositions(_zeroLevel+TEXTHEIGHT/2+1);
			_verticalScale.setSectionLength(((double) _graphBounds.height) / Math.max(1, _ySectionCount));
			_verticalScale.setRegion(0, _graphBounds.y+_graphBounds.height);
			_verticalScale.setIncrement((_max-_min)/_ySectionCount);
		} else {
			this.setConstraint(_verticalScale, DEFAULT_CONSTRAINT);
		}
		if (_showScale == SHOW_HORIZONTAL || _showScale == SHOW_BOTH) {
			if (_showScale == SHOW_HORIZONTAL) {
				this.setConstraint(_horizontalScale, new Rectangle(
						verticalScaleWidth, _zeroLevel - (_scaleWideness/2)+1,
						_graphBounds.width, _scaleWideness+TEXTHEIGHT));	
			} else {
				this.setConstraint(_horizontalScale, new Rectangle(
						verticalScaleWidth, _zeroLevel - (_scaleWideness/2)+1 + TEXTHEIGHT/2,
						_graphBounds.width, _scaleWideness+TEXTHEIGHT));
			}

			double d = ((double)_data.length)/Math.max(1, _xSectionCount);
			_horizontalScale.setIncrement(d);
			_horizontalScale.setSectionLength(((double) _graphBounds.width) / Math.max(1, _xSectionCount));
			_horizontalScale.setRegion(0, _graphBounds.width-10);
		} else {
			this.setConstraint(_horizontalScale, DEFAULT_CONSTRAINT);
		}

		if (_showLedgerLines == SHOW_HORIZONTAL	|| _showLedgerLines == SHOW_BOTH) {
			this.setConstraint(_verticalLedgerScale, new Rectangle(
					verticalScaleWidth, 0, _graphBounds.width, _graphBounds.height+TEXTHEIGHT));
			_verticalLedgerScale.setReferencePositions(_zeroLevel+TEXTHEIGHT/2+1);
			_verticalLedgerScale.setSectionLength(((double) _graphBounds.height) / Math.max(1, _ySectionCount));
			_verticalLedgerScale.setRegion(0, _graphBounds.y+_graphBounds.height);
			_verticalLedgerScale.setWideness(_graphBounds.width);
			_verticalLedgerScale.setIncrement((_max-_min)/_ySectionCount);
		} else {
			this.setConstraint(_verticalLedgerScale, DEFAULT_CONSTRAINT);
		}
		if (_showLedgerLines == SHOW_VERTICAL || _showLedgerLines == SHOW_BOTH) {
			this.setConstraint(_horizontalLedgerScale, new Rectangle(
					verticalScaleWidth, TEXTHEIGHT/2, _graphBounds.width, _graphBounds.height));
			double d = ((double)_data.length)/_xSectionCount;
			_horizontalLedgerScale.setIncrement(d);
			_horizontalLedgerScale.setSectionLength(((double) _graphBounds.width) / Math.max(1, _xSectionCount));
			_horizontalLedgerScale.setWideness(_graphBounds.height);
			_horizontalLedgerScale.setRegion(0, _graphBounds.width-10);
		} else {
			this.setConstraint(_horizontalLedgerScale, DEFAULT_CONSTRAINT);
		}
		this.setConstraint(_graphFigure, _graphBounds);

		this.setToolTip(this.getToolTipFigure());
	}

	/**
	 * Gets the bounds of the graph.
	 * @return Rectangle
	 * 				The rectangle for the graph
	 */
	private Rectangle getGraphBounds() {
		Rectangle figureBounds = this.getBounds();
		if (_showScale == SHOW_VERTICAL || _showScale == SHOW_BOTH) {
			int width = _scaleWideness;
			if (_showValues) {
				width = TEXTWIDTH + _scaleWideness;
			}
			return new Rectangle(width, TEXTHEIGHT/2, figureBounds.width
					- width, figureBounds.height-TEXTHEIGHT);
		}
		return new Rectangle(0, 0, figureBounds.width, figureBounds.height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintFigure(final Graphics graphics) {
		if (!_transparent) {
			super.paintFigure(graphics);
		}
		if (_init) {
			_init = false;
			this.refreshConstraints();
		}
	}

	/**
	 * Gets the IFigure for the tooltip.
	 * 
	 * @return IFigure The IFigure for the tooltip
	 */
	private IFigure getToolTipFigure() {
		Panel panel = new Panel();
		panel.setLayoutManager(new ToolbarLayout(false));
		panel.add(new Label("Count of data points: " + _data.length));
		panel.add(new Label("Minimum value: " + _min));
		panel.add(new Label("Maximum value: " + _max));
		panel.setBackgroundColor(ColorConstants.tooltipBackground);
		return panel;
	}

	/**
	 * Calculates all Points for the curve and add them into a PointList.
	 * Caution! These points are zero-related.
	 * 
	 * @param bounds
	 *            The bounds for the Rectangle of the curve
	 * @return PoinList The PointList with all Points
	 */
	private List<PrecisionPoint> calculatePoints(final Rectangle bounds) {
		List<PrecisionPoint> pointList = new LinkedList<PrecisionPoint>();
		if (bounds.width == 0) {
			return pointList;
		}
		double min = 0;
		double max = 0;

		// stepSize: number of data points that will be averaged into
		// a single pixel
		int stepSize = Math.max(1, (int) Math.ceil((double) _data.length
				/ bounds.width));
		
		// pointCount: number of data points that must be drawn
		int pointCount;
		if (_data.length > bounds.width) {
			// XXX: this loses a huge amount of data points!
			// for example if _data.length == 100 and bounds.width == 99,
			// then stepSize will be 2 and pointCount will be only 50.
			pointCount = (int) Math.ceil((double) _data.length / stepSize);
		} else {
			pointCount = _data.length;
		}

		for (int i = 0; i < pointCount; i++) {
			double yValue = 0;
			int div = 0;
			for (int j = 0; j < stepSize; j++) {
				int index = j + i * stepSize;
				if (index < _data.length) {
					yValue = yValue + _data[index];
					div++;
					if (_data[index] < min || i == 0) {
						min = _data[index];
					}
					if (_data[index] > max || i == 0) {
						max = _data[index];
					}
				}
			}
			div = Math.max(div, 1);
			yValue = yValue / div;

			double x = 1;
			if (pointCount != 0) {
				x = 1 + ((bounds.width - 1) * i) / (pointCount);
			}
			pointList.add(new PrecisionPoint(x, yValue));
		}

		if (_autoScale) {
			if (min < _min - AUTOSCALE_TRESHOLD || min > _min + AUTOSCALE_TRESHOLD) {
				_min = min;
			}
			if (max < _max - AUTOSCALE_TRESHOLD || max > _max + AUTOSCALE_TRESHOLD) {
				_max = max;
			}
		}

		return pointList;
	}
	
	/**
	 * Sets the max value for the graph.
	 * @param max
	 * 				The max value
	 */
	public void setMax(final double max) {
		_max = max;
		_settedMax = max;
		this.refreshConstraints();
	}

	/**
	 * Sets the min value for the graph.
	 * @param min
	 * 				The min value
	 */
	public void setMin(final double min) {
		_min = min;
		_settedMin = min;
		this.refreshConstraints();
	}

	/**
	 * Sets, if the graph should be automatically scaled.
	 * @param autoScale
	 * 				True if it should be automatically scaled, false otherwise
	 */
	public void setAutoScale(final boolean autoScale) {
		_autoScale = autoScale;
		if (!_autoScale) {
			_min = _settedMin;
			_max = _settedMax;
		}
		this.refreshConstraints();
	}

	/**
	 * Sets in which way the scale should be drawn.
	 * 
	 * @param showScale
	 *            0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public void setShowScale(final int showScale) {
		_showScale = showScale;
		this.refreshConstraints();
	}

	/**
	 * Gets in which way the scale should be drawn.
	 * 
	 * @return int 0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowScale() {
		return _showScale;
	}

	/**
	 * Sets in which way the help lines should be drawn.
	 * 
	 * @param showLedgerLines
	 *            0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public void setShowLedgerlLines(final int showLedgerLines) {
		_showLedgerLines = showLedgerLines;
		this.refreshConstraints();
	}

	/**
	 * Gets in which way the help lines should be drawn.
	 * 
	 * @return int 0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowLedgerLines() {
		return _showLedgerLines;
	}

	/**
	 * Sets if the point lines should be drawn.
	 * 
	 * @param showPointLines
	 *            true, the point lines should be drawn, false otherwise
	 */
	public void setShowConnectionLines(final boolean showPointLines) {
		_showConnectionLines = showPointLines;
	}

	/**
	 * Gets if the point lines should be drawn.
	 * 
	 * @return boolean true, the point lines should be drawn, false otherwise
	 */
	public boolean getShowConnectionLines() {
		return _showConnectionLines;
	}
	
	/**
	 * Sets the width of the lines of the graph.
	 * @param lineWidth
	 * 				The width of the lines of the graph.
	 */
	public void setGraphLineWidth(final int lineWidth) {
		_graphFigure.setGraphLineWidth(lineWidth);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBackgroundColor(final Color backgroundColor) {
		super.setBackgroundColor(backgroundColor);
		_graphFigure.setBackgroundColor(backgroundColor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setForegroundColor(final Color foregroundcolor) {
		super.setForegroundColor(foregroundcolor);
		_graphFigure.setForegroundColor(foregroundcolor);
	}

	/**
	 * Sets the color for the graph.
	 * 
	 * @param graphRGB
	 *            The RGB-value for the color
	 */
	public void setGraphColor(final RGB graphRGB) {
		_graphFigure.setGraphColor(CustomMediaFactory.getInstance().getColor(
				graphRGB));
	}

	/**
	 * Sets the color for the connection lines.
	 * 
	 * @param lineRGB
	 *            The RGB-value for the color
	 */
	public void setConnectionLineColor(final RGB lineRGB) {
		_graphFigure.setConnectionLineColor(CustomMediaFactory.getInstance()
				.getColor(lineRGB));
	}

	/**
	 * Sets the color for the ledger lines.
	 * 
	 * @param lineRGB
	 *            The RGB-value for the color
	 */
	public void setLedgerLineColor(final RGB lineRGB) {
		_verticalLedgerScale.setForegroundColor(CustomMediaFactory
				.getInstance().getColor(lineRGB));
		_horizontalLedgerScale.setForegroundColor(CustomMediaFactory
				.getInstance().getColor(lineRGB));
	}
	
	/**
	 * Sets, if the values should be shown.
	 * 
	 * @param showValues
	 *            True, if the values should be shown, false otherwise
	 */
	public void setShowValues(final boolean showValues) {
		_showValues = showValues;
		_verticalScale.setShowValues(showValues);
		_horizontalScale.setShowValues(showValues);
		this.refreshConstraints();
	}

	/**
	 * Gets, if the values should be shown.
	 * 
	 * @return boolean True, if the values should be shown, false otherwise
	 */
	public boolean getShowValues() {
		return _showValues;
	}
	
	/**
	 * Sets the count of sections on the y-axis.
	 * @param ySectionCount
	 * 			The count of sections on the y-axis
	 */
	public void setYSectionCount(final int ySectionCount) {
		_ySectionCount = ySectionCount;
		this.refreshConstraints();
	}
	
	/**
	 * Sets the count of sections on the x-axis.
	 * @param xSectionCount
	 * 			The count of sections on the x-axis
	 */
	public void setXSectionCount(final int xSectionCount) {
		_xSectionCount = xSectionCount;
		this.refreshConstraints();
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
	 * Returns the y position relative to the top of the plot at which the
	 * given value should be drawn.
	 * 
	 * @param value the value.
	 * @return the y position.
	 */
	private int valueToYPos(double value) {
		double dataRange = _max - _min;
		int plotHeight = _graphBounds.height - 1;
		// the data values are mapped to [0, height-1]
		double scaling = plotHeight / dataRange;
		
		// by subtracting the mapped value from height-1, we get the actual
		// y position (subtracting because the y axis in Draw2D goes from top
		// to bottom)
		return (_graphBounds.height - 1) - ((int) Math.round((value - _min) * scaling));
	}

	/**
	 * This class represents the graph of the waveform.
	 */
	private final class GraphFigure extends RectangleFigure {

		/**
		 * The Color for the graph.
		 */
		private Color _graphColor = ColorConstants.red;

		/**
		 * The Color for the connection lines.
		 */
		private Color _connectionLineColor = ColorConstants.red;
		
		/**
		 * The width of the lines of the graph.
		 */
		private int _graphLineWidth = 1;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintFigure(final Graphics graphics) {
			Rectangle figureBounds = this.getBounds();
			graphics.setForegroundColor(this.getForegroundColor());
			graphics.drawLine(figureBounds.x, figureBounds.y, figureBounds.x,
					figureBounds.y + figureBounds.height);
			graphics.drawLine(figureBounds.x, figureBounds.y + _zeroLevel,
					figureBounds.x + figureBounds.width, figureBounds.y
							+ _zeroLevel);
			graphics.drawLine(figureBounds.x,
					figureBounds.y + figureBounds.height,
					figureBounds.x + figureBounds.width,
					figureBounds.y + figureBounds.height);
			
			PointList pointList = this.translatePointList(_dataPoints,
					figureBounds.x, figureBounds);
			graphics.setLineWidth(_graphLineWidth);
			if (_showConnectionLines) {
				graphics.setForegroundColor(_connectionLineColor);
				graphics.drawPolyline(pointList);
			}
			graphics.setForegroundColor(_graphColor);
			for (int i = 0; i < pointList.size(); i++) {
				Point p = pointList.getPoint(i);
				graphics.drawPoint(p.x, p.y);
			}
		}

		/**
		 * Translates all Points in PointList.
		 * 
		 * @param pointList
		 *            The PointList
		 * @param x
		 *            The reference x value
		 * @param figureBounds
		 *            The bounds of the waveform
		 * @return PointList The new PointList
		 */
		private PointList translatePointList(
				final List<PrecisionPoint> pointList, final int x,
				final Rectangle figureBounds) {
			int y = figureBounds.y;
			PointList result = new PointList();
			for (int i = 0; i < pointList.size(); i++) {
				PrecisionPoint p = pointList.get(i);
				Point newPoint = new Point(p.x + x, valueToYPos(p.preciseY) + y);
				result.addPoint(newPoint);
			}
			return result;
		}

		/**
		 * Sets the color for the graph.
		 * 
		 * @param graphColor
		 *            The color
		 */
		public void setGraphColor(final Color graphColor) {
			_graphColor = graphColor;
		}

		/**
		 * Sets the color for the connection lines.
		 * 
		 * @param lineColor
		 *            The color
		 */
		public void setConnectionLineColor(final Color lineColor) {
			_connectionLineColor = lineColor;
		}
		
		/**
		 * Sets the width of the lines of the graph.
		 * @param lineWidth
		 * 				The width of the lines of the graph.
		 */
		public void setGraphLineWidth(final int lineWidth) {
			_graphLineWidth = lineWidth;
		}

	}

	/**
	 * This class represents a scale.
	 * 
	 * @author Kai Meyer
	 */
	private final class Scale extends RectangleFigure {
		/**
		 * The length of this Scale.
		 */
		private double _sectionLength;
		/**
		 * The direction of this Scale.
		 */
		private boolean _isHorizontal;
		/**
		 * The Alignment for the Scalemarkers.
		 */
		private boolean _isTopLeft;
		/**
		 * The start position.
		 */
		private int _refPos = 10;
		/**
		 * The begin of the region, which surrounds the Markers.
		 */
		private int _begin;
		/**
		 * The end of the region, which surrounds the Markers.
		 */
		private int _end;
		/**
		 * True, if the negativ sections should be draan, false otherwise.
		 */
		private boolean _showNegativSections = false;
		/**
		 * The lenght of the lines.
		 */
		private int _wideness = 10;
		/**
		 * True, if the first Marker should be shown, false otherwise.
		 */
		private boolean _showFirst = true;		
		/**
		 * True, if the values of the Markers should be shown, false otherwise.
		 */
		private boolean _showValues = false;
		/**
		 * The size of one step in a Scale.
		 */
		private double _increment = 1;
		/**
		 * The start-value for the markers.
		 */
		private double _startValue = 0;
	
		/**
		 * The List of positive ScaleMarkers.
		 */
		private List<ScaleMarker> _posScaleMarkers = new LinkedList<ScaleMarker>();
		/**
		 * The List of negative ScaleMarkers.
		 */
		private List<ScaleMarker> _negScaleMarkers = new LinkedList<ScaleMarker>();
		
		/**
		 * Constructor.
		 */
		public Scale() {
			this.setLayoutManager(new XYLayout());
			this.refreshConstraints();
			// listen to figure movement events
			addFigureListener(new FigureListener() {
				public void figureMoved(final IFigure source) {
					refreshConstraints();
				}
			});
		}
		
		/**
		 * Refreshes the Constraints.
		 */
		private void refreshConstraints() {
			if (_sectionLength==0 || this.getBounds().height==0 || this.getBounds().width==0) {
				_posScaleMarkers.clear();
				_negScaleMarkers.clear();
				this.removeAll();
				return;
			}
			int index = 0;
			double pos = _refPos;
			if (_isHorizontal) {
				int height = _wideness;
				if (_showValues) {
					height = TEXTHEIGHT + _wideness;
				}
				double value = _startValue;
				while (pos < this.getBounds().width && pos <= _end) {
					if (pos>=_begin) {
						if (index>=_posScaleMarkers.size()) {
							this.addScaleMarker(index, _posScaleMarkers);
						}
						this.setConstraint(_posScaleMarkers.get(index), new Rectangle((int) Math.round(pos-TEXTWIDTH/2),0,TEXTWIDTH,height));
						this.refreshScaleMarker(_posScaleMarkers.get(index), value, ((index>0 || _showFirst) && _showValues));
						index++;
					}
					value = value + _increment;
					pos = pos + _sectionLength;
				}
				this.removeScaleMarkers(index, _posScaleMarkers);
				if (_showNegativSections) {
					pos = _refPos - _sectionLength;
					index = 0;
					value = _startValue - _increment;
					while (pos > 0 && pos >= _begin) {
						if (pos<=_end) {
							if (index>=_negScaleMarkers.size()) {
								this.addScaleMarker(index, _negScaleMarkers);
							}
							this.setConstraint(_negScaleMarkers.get(index), new Rectangle((int) Math.round(pos-TEXTWIDTH/2),0,TEXTWIDTH,height));
							this.refreshScaleMarker(_negScaleMarkers.get(index), value, _showValues);
							index++;	
						}
						value = value - _increment;
						pos = pos - _sectionLength;
					}	
					this.removeScaleMarkers(index, _negScaleMarkers);
				}
			} else {
//				pos = pos - 1;
				int width = _wideness;
				if (_showValues) {
					width = TEXTWIDTH + _wideness;
				}
//				double value = _startValue;
//				while (pos > 0 && pos >= _begin) {
//					if (pos<=_end) {
//						if (index>=_posScaleMarkers.size()) {
//							this.addScaleMarker(index, _posScaleMarkers);
//						}
//						this.setConstraint(_posScaleMarkers.get(index), new Rectangle(0,(int) Math.round(pos-TEXTHEIGHT/2),width,TEXTHEIGHT));
//						this.refreshScaleMarker(_posScaleMarkers.get(index), value, ((index>0 || _showFirst) && _showValues)); 
//						index++;
//					}
//					value = value + _increment;
//					pos = pos - _sectionLength;
//				}
				double value = 0;
				while (value <= _max) {
					if (index >= _posScaleMarkers.size()) {
						this.addScaleMarker(index, _posScaleMarkers);
					}
					int y = valueToYPos(value);
					this.setConstraint(_posScaleMarkers.get(index), new Rectangle(0, y, width, TEXTHEIGHT));
					this.refreshScaleMarker(_posScaleMarkers.get(index), value, ((index>0 || _showFirst) && _showValues));
					value += _increment;
					index++;
				}
				this.removeScaleMarkers(index, _posScaleMarkers);
				if (_showNegativSections) {
					
//					pos = _refPos + _sectionLength - 1;
					index = 0;
					value = 0 - _increment;
					while (value >= _min) {
						if (index >= _negScaleMarkers.size()) {
							this.addScaleMarker(index, _negScaleMarkers);
						}
						int y = valueToYPos(value);
						this.setConstraint(_negScaleMarkers.get(index), new Rectangle(0, y, width, TEXTHEIGHT));
						this.refreshScaleMarker(_negScaleMarkers.get(index), value, _showValues);
						value -= _increment;
						index++;
					}
//					while (pos < this.getBounds().height && pos <= _end) {
//						if (pos>=_begin) {
//							if (index>=_negScaleMarkers.size()) {
//								this.addScaleMarker(index, _negScaleMarkers);
//							}
//							this.setConstraint(_negScaleMarkers.get(index), new Rectangle(0,(int) Math.round(pos-TEXTHEIGHT/2),width,TEXTHEIGHT));
//							this.refreshScaleMarker(_negScaleMarkers.get(index), value, _showValues);
//							index++;	
//						}
//						value = value - _increment;
//						pos = pos + _sectionLength;
//					}	
					this.removeScaleMarkers(index, _negScaleMarkers);
				}
			}
		}

		/**
		 * Refreshes the given ScaleMarker.
		 * @param marker
		 * 				The ScaleMarker, which should be refreshed
		 * @param labelValue
		 * 				The new value for the displayed text
		 * @param showValue
		 * 				True, if the value should be shown, false otherwise
		 */
		private void refreshScaleMarker(final ScaleMarker marker, final double labelValue, final boolean showValue) {
			marker.setTopLeftAlignment(_isTopLeft);
			marker.setHorizontalOrientation(!_isHorizontal);
			NumberFormat format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(2);
			marker.setText(format.format(labelValue));
			marker.setShowValues(showValue);
			marker.setWideness(_wideness);
		}

		/**
		 * Adds a new ScaleMarker into the given List at the given index.
		 * @param index
		 * 				The index
		 * @param scaleMarkers
		 * 				The List of ScaleMarkers
		 */
		private void addScaleMarker(final int index, final List<ScaleMarker> scaleMarkers) {
			ScaleMarker marker = new ScaleMarker();
			scaleMarkers.add(index, marker);
			this.add(marker);
		}
		
		/**
		 * Removes all ScaleMarkers in the given List, beginning by the given index.
		 * @param index
		 * 				The index
		 * @param scaleMarkers
		 * 				The List of ScaleMarkers
		 */
		private void removeScaleMarkers(final int index, final List<ScaleMarker> scaleMarkers) {
			if (!scaleMarkers.isEmpty() && index<=scaleMarkers.size()) {
				while (index<scaleMarkers.size()) {
					this.remove(scaleMarkers.remove(index));
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintFigure(final Graphics graphics) {
//			graphics.setForegroundColor(ColorConstants.blue);
//			graphics.setBackgroundColor(ColorConstants.blue);
//			graphics.fillRectangle(this.getBounds());
		}
		
		/**
		 * Sets the length of one section of this Scale.
		 * 
		 * @param length
		 *            The lenght of one section of this Scale
		 */
		public void setSectionLength(final double length) {
			_sectionLength = Math.round(length);
		}

		/**
		 * Sets the orientation of this Scale.
		 * 
		 * @param isHorizontal
		 *            The orientation of this Scale
		 *            (true=horizontal;false=vertical)
		 */
		public void setHorizontalOrientation(final boolean isHorizontal) {
			_isHorizontal = isHorizontal;
			this.refreshConstraints();
		}
		
		/**
		 * Sets the alignment for the ScaleMarker.
		 * @param isTopLeft
		 * 			  The alignment for the ScaleMarker
		 *            (true=top/left; false=bottom/right)
		 * 				
		 */
		public void setAlignment(final boolean isTopLeft) {
			_isTopLeft = isTopLeft;
			this.refreshConstraints();
		}

		/**
		 * Sets the reference values for this figure.
		 * 
		 * @param refPos
		 *            The start value
		 */
		public void setReferencePositions(final int refPos) {
			_refPos = refPos;
			if (_refPos<0) {
				if (_isHorizontal) {
					_refPos = _refPos + 1;
				} else {
					_refPos = _refPos - 1;
				}
			}
			this.refreshConstraints();
		}
		
		/**
		 * The begin and the end of the region, which surrounds the Markers.
		 * @param begin 
		 * 			 The begin
		 * @param end
		 * 			 The end
		 */
		public void setRegion(final int begin, final int end) {
			_begin = begin;
			_end = end;
			this.refreshConstraints();
		}

		/**
		 * Sets if the negative sections should be drawn.
		 * 
		 * @param showNegativ
		 *            True, if the negativ sections should be drawn, false
		 *            otherwise.
		 */
		public void setShowNegativeSections(final boolean showNegativ) {
			_showNegativSections = showNegativ;
		}

		/**
		 * Sets the wideness of this scale.
		 * 
		 * @param wideness
		 *            The wideness of this scale
		 */
		public void setWideness(final int wideness) {
			_wideness = wideness;
			this.refreshConstraints();
		}
		
		/**
		 * Sets, if the first Marker should be shown.
		 * @param showFirst
		 * 				True if the first Marker should be shown, false otherwise
		 */
		public void setShowFirstMarker(final boolean showFirst) {
			_showFirst = showFirst;
			this.refreshConstraints();
		}
		
		/**
		 * Sets, if the values of the Markers should be shown.
		 * @param showValues
		 * 				True if the values of the Markers should be shown, false otherwise
		 */
		public void setShowValues(final boolean showValues) {
			_showValues = showValues;
			this.refreshConstraints();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setForegroundColor(final Color fg) {
			super.setForegroundColor(fg);
			for (ScaleMarker marker : _posScaleMarkers) {
				marker.setForegroundColor(fg);
			}
			for (ScaleMarker marker : _negScaleMarkers) {
				marker.setForegroundColor(fg);
			}
		}
		
		/**
		 * Sets the increment for the Scale.
		 * @param value
		 * 			The value for the increment
		 */
		public void setIncrement(final double value) {
			_increment = value;
			this.refreshConstraints();
		}
		
		/**
		 * Sets the start value for the Markers.
		 * @param startValue
		 * 			The start value
		 */
		public void setStartValue(final double startValue) {
			_startValue = startValue;
		}
		
		/**
		 * This class represents a marker for the scale.
		 * @author Kai Meyer
		 */
		private final class ScaleMarker extends RectangleFigure {
			/**
			 * The Label of this ScaleMarker.
			 */
			private Label _textLabel;
			/**
			 * The hyphen of this ScaleMarker.
			 */
			private ScaleHyphen _scaleHyphen;
			/**
			 * The orientation of this Marker.
			 */
			private boolean _isHorizontal;
			/**
			 * The alignment of this Marker.
			 */
			private boolean _topLeft;
			/**
			 * True, if the values of the Markers should be shown, false otherwise.
			 */
			private boolean _showValues = false;
			
			/**
			 * Constructor.
			 */
			public ScaleMarker() {
				this.setLayoutManager(new BorderLayout());
				_textLabel = new Label("");
				_textLabel.setForegroundColor(this.getForegroundColor());
				_scaleHyphen = new ScaleHyphen();
				_scaleHyphen.setForegroundColor(this.getForegroundColor());
				this.add(_scaleHyphen, BorderLayout.CENTER);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void paintFigure(final Graphics graphics) {
//				graphics.setForegroundColor(ColorConstants.green);
//				graphics.setBackgroundColor(ColorConstants.green);
//				graphics.fillRectangle(this.getBounds());
			}
			
			/**
			 * Sets the orientation of this figure.
			 * @param isHorizontal
			 *            The orientation of this figure
			 *            (true=horizontal;false=vertical)
			 */
			public void setHorizontalOrientation(final boolean isHorizontal) {
				_isHorizontal = !isHorizontal;
				_scaleHyphen.setHorizontalOrientation(isHorizontal);
				this.refreshLabel();
			}

			/**
			 * Sets the alignment of this figure.
			 * 
			 * @param topLeft
			 *            The alignment of this figure
			 *            (true=top/left;false=bottom/right)
			 */
			public void setTopLeftAlignment(final boolean topLeft) {
				_topLeft = topLeft;
				_scaleHyphen.setAlignment(_topLeft);
				this.refreshLabel();
			}
			
			/**
			 * Sets the displayed text.
			 * @param text
			 * 			The text to display
			 */
			public void setText(final String text) {
				_textLabel.setText(text);
				this.refreshLabel();
			}
			
			/**
			 * Sets, if the values of the Markers should be shown.
			 * @param showValues
			 * 				True if the values of the Markers should be shown, false otherwise
			 */
			public void setShowValues(final boolean showValues) {
				_showValues = showValues;
				this.refreshLabel();
			}
			
			/**
			 * Sets the wideness of the Hyphen.
			 * @param wideness
			 * 				The wideness
			 */
			public void setWideness(final int wideness) {
				_scaleHyphen.setWideness(wideness);
			}
			
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void setForegroundColor(final Color fg) {
				super.setForegroundColor(fg);
				_scaleHyphen.setForegroundColor(fg);
				_textLabel.setForegroundColor(fg);
			}
			
			/**
			 * Refreshes the Label.
			 */
			private void refreshLabel() {
				if (this.getChildren().contains(_textLabel)) {
					this.remove(_textLabel);
				}
				if (_showValues) {
					Integer place;
					if (_isHorizontal) {
						if (_topLeft) {
							place = BorderLayout.TOP;
						} else {
							place = BorderLayout.BOTTOM;
						}
					} else {
						if (_topLeft) {
							place = BorderLayout.LEFT;
						} else {
							place = BorderLayout.RIGHT;
						}
					}
					this.add(_textLabel, place);
				}
			}
			
			/**
			 * This class represents a hyphen for the scale.
			 * 
			 * @author Kai Meyer
			 */
			private final class ScaleHyphen extends RectangleFigure {
				/**
				 * The height of the line. 
				 */
				private int _height = 0;
				/**
				 * The width of the line.
				 */
				private int _width = 10;
				/**
				 * The orientation of the line.
				 */
				private boolean _isHorizontal;
				/**
				 * The wideness of this Hyphen.
				 */
				private int _wideness = 10;
				/**
				 * The Alignment of this Hyphen.
				 */
				private boolean _isTopLeft;
				
				/**
				 * {@inheritDoc}
				 */
				@Override
				public void paintFigure(final Graphics graphics) {
					graphics.setForegroundColor(this.getForegroundColor());
					//vertical
					int x = this.getBounds().x+this.getBounds().width/2;
					int y = this.getBounds().y;
					if (_isHorizontal) {
						if (_isTopLeft) {
							x = this.getBounds().x + this.getBounds().width-_width;
							y = this.getBounds().y + this.getBounds().height/2;
						} else {
							x = this.getBounds().x;
							y = this.getBounds().y + this.getBounds().height/2;
						}
					}
					graphics.drawLine(x, y,	x + _width,	y + _height);
				}
				
				/**
				 * Sets the wight and height of this Hyphen.
				 */
				private void setHeightAndWidth() {
					if (_isHorizontal) {
						_height = 0;
						_width = _wideness;
					} else {
						_height = _wideness;
						_width = 0;
					}
				}
				
				/**
				 * Sets the orientation of this Hyphen.
				 * @param isHorizontal
				 * 				The Orientation of this Hyphen
				 * 				true=horizontal; false = vertical
				 */
				public void setHorizontalOrientation(final boolean isHorizontal) {
					_isHorizontal = isHorizontal;
					this.setHeightAndWidth();
				}
				
				/**
				 * Sets the wideness of the Hyphen.
				 * @param wideness
				 * 				The wideness
				 */
				public void setWideness(final int wideness) {
					_wideness = wideness;
					this.setHeightAndWidth();
				}			
				
				/**
				 * Sets the alignment of this Hyphen.
				 * @param isTopLeft
				 * 				The alignment (true=top/left; false = bottom/right)
				 */
				public void setAlignment(final boolean isTopLeft) {
					_isTopLeft = isTopLeft;
				}
			}
		}
	}
}
