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

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
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
	 * The width of the area that is reserved for the axis labels to the left
	 * of the Y axis.
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
	 * The maximum data value set in this waveform's properties.
	 */
	private double _propertyMax = 0;

	/**
	 * The minimum data value set in this waveform's properties.
	 */
	private double _propertyMin = 0;

	/**
	 * The zero level (the Y position of the value zero).
	 */
	private int _zeroLevel = 0;
	
	/**
	 * The transparent state of the background.
	 */
	private boolean _transparent = false;

	/**
	 * The bounds of the plotting area (where the data points are drawn).
	 * The location of the rectangle is relative to the figure bounds.
	 */
	private Rectangle _plotBounds = new Rectangle(10, 10, 10, 10);

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
	private PlotFigure _plotFigure;

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
		_horizontalScale.setShowFirstMarker(false);
		_horizontalScale.setShowValues(_showValues);
		_horizontalScale.setAlignment(false);
		_horizontalScale.setForegroundColor(this.getForegroundColor());
		this.add(_horizontalScale);
		_plotFigure = new PlotFigure();
		this.add(_plotFigure);

		// listen to figure movement events
		addFigureListener(new FigureListener() {
			public void figureMoved(final IFigure source) {
				refreshConstraints();
			}
		});
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
	public void refreshConstraints() {
		if (_init) {
			return;
		}
		
		_plotBounds = this.calculatePlotBounds();
		this.adjustAutoscale();
		_zeroLevel = this.valueToYPos(0.0);
		int verticalScaleWidth = 0;
		
		if (_showScale == SHOW_VERTICAL || _showScale == SHOW_BOTH) {
			verticalScaleWidth = _scaleWideness;
			if (_showValues) {
				verticalScaleWidth = verticalScaleWidth + TEXTWIDTH;
			}
			this.setConstraint(_verticalScale, new Rectangle(0, 0,
					verticalScaleWidth, _plotBounds.height+TEXTHEIGHT));
			_verticalScale.setIncrement((_max-_min)/_ySectionCount);
		} else {
			this.setConstraint(_verticalScale, DEFAULT_CONSTRAINT);
		}
		
		if (_showScale == SHOW_HORIZONTAL || _showScale == SHOW_BOTH) {
			if (_showScale == SHOW_HORIZONTAL) {
				this.setConstraint(_horizontalScale, new Rectangle(
						verticalScaleWidth, _zeroLevel - (_scaleWideness/2)+1,
						_plotBounds.width, _scaleWideness+TEXTHEIGHT));	
			} else {
				this.setConstraint(_horizontalScale, new Rectangle(
						verticalScaleWidth, _zeroLevel - (_scaleWideness/2)+1 + TEXTHEIGHT/2,
						_plotBounds.width, _scaleWideness+TEXTHEIGHT));
			}

			double d = ((double)_data.length)/Math.max(1, _xSectionCount);
			_horizontalScale.setIncrement(d);
		} else {
			this.setConstraint(_horizontalScale, DEFAULT_CONSTRAINT);
		}

		if (_showLedgerLines == SHOW_HORIZONTAL	|| _showLedgerLines == SHOW_BOTH) {
			this.setConstraint(_verticalLedgerScale, new Rectangle(
					verticalScaleWidth, 0, _plotBounds.width, _plotBounds.height+TEXTHEIGHT));
			_verticalLedgerScale.setWideness(_plotBounds.width);
			_verticalLedgerScale.setIncrement((_max-_min)/_ySectionCount);
		} else {
			this.setConstraint(_verticalLedgerScale, DEFAULT_CONSTRAINT);
		}
		
		if (_showLedgerLines == SHOW_VERTICAL || _showLedgerLines == SHOW_BOTH) {
			this.setConstraint(_horizontalLedgerScale, new Rectangle(
					verticalScaleWidth, TEXTHEIGHT/2, _plotBounds.width, _plotBounds.height));
			double d = ((double)_data.length)/_xSectionCount;
			_horizontalLedgerScale.setIncrement(d);
			_horizontalLedgerScale.setWideness(_plotBounds.height);
		} else {
			this.setConstraint(_horizontalLedgerScale, DEFAULT_CONSTRAINT);
		}
		
		this.setConstraint(_plotFigure, _plotBounds);

		this.setToolTip(this.getToolTipFigure());
	}

	/**
	 * Calculates and returns the bounds of the plot area.
	 * @return Rectangle
	 * 				The rectangle describing the bounds of the plot area.
	 */
	private Rectangle calculatePlotBounds() {
		Rectangle figureBounds = this.getBounds().getCopy();
		figureBounds.crop(this.getInsets());
		if (_showScale == SHOW_VERTICAL || _showScale == SHOW_BOTH) {
			int width = _scaleWideness;
			if (_showValues) {
				width += TEXTWIDTH;
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
	 * Adjusts the plot to the maximum and minimum values in the dataset
	 * if autoscaling is activated.
	 */
	private void adjustAutoscale() {
		if (_autoScale && _data.length > 0) {
			double min = _data[0];
			double max = _data[0];

			for (double value : _data) {
				if (value < min) {
					min = value;
				}
				else if (value > max) {
					max = value;
				}
			}

			if (min < _min - AUTOSCALE_TRESHOLD || min > _min + AUTOSCALE_TRESHOLD) {
				_min = min;
			}
			if (max < _max - AUTOSCALE_TRESHOLD || max > _max + AUTOSCALE_TRESHOLD) {
				_max = max;
			}
		}
	}
	
	/**
	 * Sets the max value for the graph.
	 * @param max
	 * 				The max value
	 */
	public void setMax(final double max) {
		_max = max;
		_propertyMax = max;
		this.refreshConstraints();
	}

	/**
	 * Sets the min value for the graph.
	 * @param min
	 * 				The min value
	 */
	public void setMin(final double min) {
		_min = min;
		_propertyMin = min;
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
			_min = _propertyMin;
			_max = _propertyMax;
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
	 * Sets if the point lines should be drawn.
	 * 
	 * @param showPointLines
	 *            true, the point lines should be drawn, false otherwise
	 */
	public void setShowConnectionLines(final boolean showPointLines) {
		_showConnectionLines = showPointLines;
	}
	
	/**
	 * Sets the width of the lines of the graph.
	 * @param lineWidth
	 * 				The width of the lines of the graph.
	 */
	public void setGraphLineWidth(final int lineWidth) {
		_plotFigure.setPlotLineWidth(lineWidth);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBackgroundColor(final Color backgroundColor) {
		super.setBackgroundColor(backgroundColor);
		_plotFigure.setBackgroundColor(backgroundColor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setForegroundColor(final Color foregroundcolor) {
		super.setForegroundColor(foregroundcolor);
		_plotFigure.setForegroundColor(foregroundcolor);
	}

	/**
	 * Sets the color for the graph.
	 * 
	 * @param graphRGB
	 *            The RGB-value for the color
	 */
	public void setGraphColor(final RGB graphRGB) {
		_plotFigure.setDataPointColor(CustomMediaFactory.getInstance().getColor(
				graphRGB));
	}

	/**
	 * Sets the color for the connection lines.
	 * 
	 * @param lineRGB
	 *            The RGB-value for the color
	 */
	public void setConnectionLineColor(final RGB lineRGB) {
		_plotFigure.setConnectionLineColor(CustomMediaFactory.getInstance()
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
	 * Returns the y position relative to the top of the plot at which the
	 * given value should be drawn.
	 * 
	 * @param value the value.
	 * @return the y position.
	 */
	private int valueToYPos(double value) {
		double dataRange = _max - _min;
		int plotHeight = _plotBounds.height - 1;
		// the data values are mapped to [0, height-1]
		double scaling = plotHeight / dataRange;
		
		// _max - value calculates the distance of the value to the plot's
		// upper edge. Since the Y axis goes from top to bottom, this is
		// exactly what we need.
		return (int) Math.round((_max - value) * scaling);
	}

	/**
	 * Figure for the actual plot.
	 */
	private final class PlotFigure extends RectangleFigure {

		/**
		 * The Color for the graph.
		 */
		private Color _dataPointColor = ColorConstants.red;

		/**
		 * The Color for the connection lines.
		 */
		private Color _connectionLineColor = ColorConstants.red;
		
		/**
		 * The width of the lines of the graph.
		 */
		private int _plotLineWidth = 1;

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
			
			// TODO: the points don't actually have to be recalculated everytime the plot
			// is redrawn -- only if the data points have changed or if the size of the
			// plot has changed.
			PointList pointList = calculatePlotPoints();
			graphics.setLineWidth(_plotLineWidth);
			if (_showConnectionLines) {
				graphics.setForegroundColor(_connectionLineColor);
				graphics.drawPolyline(pointList);
			}
			graphics.setForegroundColor(_dataPointColor);
			for (int i = 0; i < pointList.size(); i++) {
				Point p = pointList.getPoint(i);
				graphics.drawPoint(p.x, p.y);
			}
		}

		/**
		 * Calculates the coordinates of the data points in the plot area.
		 * @return a list of points to be plotted.
		 */
		private PointList calculatePlotPoints() {
			Rectangle bounds = getBounds();
			
			// This algorithm always draws all data points, even if there are
			// fewer pixels in width than data points. In that case, several
			// data points will be displayed at the same X position.
			// Note: subtracting 1 from data.length because the distance is
			// only needed between the points, not before the first or after
			// the last one.
			double xDist = ((double) (bounds.width - 1)) / (_data.length - 1);
			PointList result = new PointList();
			for (int i = 0; i < _data.length ; i++) {
				int x = (int) Math.round(xDist * i);
				int y = valueToYPos(_data[i]);
				result.addPoint(new Point(bounds.x + x, bounds.y + y));
			}
			return result;
		}

		/**
		 * Sets the color for the data points.
		 * 
		 * @param color
		 *            The color
		 */
		private void setDataPointColor(final Color color) {
			_dataPointColor = color;
		}

		/**
		 * Sets the color for the connection lines.
		 * 
		 * @param lineColor
		 *            The color
		 */
		private void setConnectionLineColor(final Color lineColor) {
			_connectionLineColor = lineColor;
		}
		
		/**
		 * Sets the width of the lines of the plot.
		 * @param lineWidth
		 * 				The width of the lines of the graph.
		 */
		private void setPlotLineWidth(final int lineWidth) {
			_plotLineWidth = lineWidth;
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
			if (_isHorizontal) {
				int x = 0;
				int height = _wideness;
				if (_showValues) {
					height = TEXTHEIGHT + _wideness;
				}
				double value = 0;
				// This calculation of dataPointDistance MUST match the
				// calculation in PlotFigure#calculatePlotPoints, otherwise
				// rounding errors may occur!
				double dataPointDistance = ((double) this.getBounds().width - 1) / (_data.length - 1);
				while (x < this.getBounds().width) {
					if (index>=_posScaleMarkers.size()) {
						this.addScaleMarker(index, _posScaleMarkers);
					}
					this.setConstraint(_posScaleMarkers.get(index), new Rectangle((int) Math.round(x-TEXTWIDTH/2),0,TEXTWIDTH,height));
					this.refreshScaleMarker(_posScaleMarkers.get(index), value, ((index>0 || _showFirst) && _showValues));
					index++;
					value += _increment;
					x = (int) Math.round(value * dataPointDistance);
				}
				this.removeScaleMarkers(index, _posScaleMarkers);
				
				// Note: negative scale markers are not supported by this
				// figure for the X axis because the axis always starts from
				// zero.
				
			} else {
				int width = _wideness;
				if (_showValues) {
					width = TEXTWIDTH + _wideness;
				}
				double value = (_min > 0) ? _min : 0;
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
			marker.setHorizontalOrientation(_isHorizontal);
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
			 * The needed space of a {@link ScaleHyphen}.
			 */
			private final int _tickMarkSpace = 9;
			/**
			 * The orientation of the scale to which this marker belongs.
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
				this.setLayoutManager(new XYLayout());
				_textLabel = new Label("");
				_textLabel.setForegroundColor(this.getForegroundColor());
				_scaleHyphen = new ScaleHyphen();
				_scaleHyphen.setForegroundColor(this.getForegroundColor());
				this.add(_scaleHyphen);
//				if (_showValues) {
					this.add(_textLabel);
//				}
				this.refreshConstraints();
				addFigureListener(new FigureListener() {
					public void figureMoved(IFigure source) {
						refreshConstraints();
					}
				});
			}

			private void refreshConstraints() {
				Rectangle bounds = this.getBounds();
				if (_isHorizontal) {
					// The tickmark height is the full height of this marker
					// figure if only the tickmark is shown, if the text label
					// is also shown, the height is the _tickMarkSpace.
					int tickmarkHeight = _showValues ? _tickMarkSpace : bounds.height;
					
					if (_topLeft) {
						this.setConstraint(_scaleHyphen, new Rectangle(0, bounds.height - tickmarkHeight, bounds.width, tickmarkHeight));
						this.setConstraint(_textLabel, new Rectangle(0, 0, bounds.width, bounds.height-_tickMarkSpace));
					} else {
						this.setConstraint(_scaleHyphen, new Rectangle(0, 0, bounds.width, tickmarkHeight));
						this.setConstraint(_textLabel, new Rectangle(0, _tickMarkSpace, bounds.width, bounds.height-_tickMarkSpace));
					}
				} else {
					int tickmarkWidth = _showValues ? _tickMarkSpace : bounds.width;
					if (_topLeft) {
						this.setConstraint(_scaleHyphen, new Rectangle(bounds.width - tickmarkWidth, 0, tickmarkWidth, bounds.height));
						this.setConstraint(_textLabel, new Rectangle(0, 0, bounds.width-_tickMarkSpace, bounds.height));
					} else {
						this.setConstraint(_scaleHyphen, new Rectangle(0, 0, tickmarkWidth, bounds.height));
						this.setConstraint(_textLabel, new Rectangle(_tickMarkSpace, 0, bounds.width-_tickMarkSpace, bounds.height));
					}
				}
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
			 * Sets the orientation of the scale to which this marker belongs.
			 * 
			 * @param isHorizontal
			 *            <code>true</code> if the scale is a horizontal scale
			 *            (i.e. along the x axis), <code>false</code> if it is
			 *            a vertical scale.
			 */
			public void setHorizontalOrientation(final boolean isHorizontal) {
				_isHorizontal = isHorizontal;
				_scaleHyphen.setHorizontalOrientation(!isHorizontal);
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
				if (_showValues) {
					_textLabel.setVisible(true);
					if (_isHorizontal) {
						_textLabel.setTextPlacement(PositionConstants.WEST);
						if (_topLeft) {
							_textLabel.setTextAlignment(PositionConstants.BOTTOM);
						} else {
							_textLabel.setTextAlignment(PositionConstants.TOP);
						}
					} else {
						_textLabel.setTextPlacement(PositionConstants.NORTH);
						if (_topLeft) {
							_textLabel.setTextAlignment(PositionConstants.RIGHT);
						} else {
							_textLabel.setTextAlignment(PositionConstants.LEFT);
						}
					}
				} else {
					_textLabel.setVisible(false);
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
				 * The orientation of the line. Note that this will be
				 * <code>true</code> for a <em>vertical</em> axis, which
				 * gets horizontal lines as its tickmarks, and vice versa.
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
					int x = this.getBounds().x+((int)(Math.round(((double)this.getBounds().width)/2)));
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
				 * Sets the orientation of this Hyphen. Note, this is the
				 * orientation of the actual line that will be drawn,
				 * <em>not</em> the orientation of the scale/axis! For a
				 * vertical axis, which gets horizontal lines for its
				 * tickmarks, this must be set to <code>true</code>, and
				 * vice versa.
				 * 
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
