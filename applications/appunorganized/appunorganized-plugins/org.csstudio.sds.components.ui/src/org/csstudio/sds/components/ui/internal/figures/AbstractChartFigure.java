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

package org.csstudio.sds.components.ui.internal.figures;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Base class for widgets that implement a chart (waveform, strip chart).</p>
 *
 * <p>This class paints the plot, labels and axes. It is also responsible for
 * transforming data values to display coordinates and can autoscale the y-axis
 * if requested by the user.</p>
 *
 * <p>Subsclasses of this class are responsible for managing the actual data
 * values to be displayed in the plot. They must provide these values to this
 * base class by implementing the {@link #dataValues} method. If the data range
 * of the y-axis or the x-axis changes, subclasses must call the methods
 * {@link #dataRangeChanged} or {@link #xAxisRangeChanged}, respectively.</p>
 *
 * @author Joerg Rathlev
 * @author based on waveform by Kai Meyer and Sven Wende
 */
public abstract class AbstractChartFigure extends Figure implements IAdaptable {

    // TODO: format all comments
    // TODO: check all method names for "waveform" etc.

    /**
     * Constant value which represents that a scale or grid lines should be
     * shown for the x-axis.
     */
    private static final int SHOW_X_AXIS = 1;

    /**
     * Constant value which represents that a scale or grid lines should be
     * shown for the y-axis.
     */
    private static final int SHOW_Y_AXIS = 2;

    /**
     * Constant value which represents that a scale or grid lines should be
     * shown for both axes.
     */
    private static final int SHOW_BOTH = 3;

    /**
     * The size of an axis with ticks in pixels. For a horizontal axis, this is
     * the height of the axis; for a vertical axis, this is its width.
     */
    private static final int AXIS_SIZE = 10;

    /**
     * Height of the text.
     */
    private static final int TEXTHEIGHT = 14;

    /**
     * The width of the area that is reserved for the axis labels to the left
     * of the y-axis.
     */
    private static final int TEXTWIDTH = 46;

    /**
     * A rectangle with zero width and height.
     */
    private static final Rectangle ZERO_RECTANGLE = new Rectangle(0, 0, 0, 0);

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;

    /**
     * Whether this figure has a transparent background.
     */
    private boolean _transparent;

    /**
     * The axes for which grid lines are drawn.
     * @see #SHOW_X_AXIS
     * @see #SHOW_Y_AXIS
     * @see #SHOW_BOTH
     */
    private int _showGridLines = 0;

    /**
     * Which axes are shown.
     * @see #SHOW_X_AXIS
     * @see #SHOW_Y_AXIS
     * @see #SHOW_BOTH
     */
    private int _showAxes = 0;

    /**
     * Whether the axes ticks are labeled.
     */
    private boolean _labeledTicks = true;

    /**
     * The maximum data value set in this waveform's properties.
     */
    private double _propertyMax = 0;

    /**
     * The minimum data value set in this waveform's properties.
     */
    private double _propertyMin = 0;

    /**
     * Whether autoscaling is enabled.
     */
    private boolean _autoscale = false;

    /**
     * The plot colors for the data arrays.
     */
    private final Color[] _plotColor;

    /**
     * Whether the plot is enabled.
     */
    private final boolean[] _plotEnabled;

    /**
     * Whether this figure is drawn as a line chart.
     */
    private boolean _lineChart = false;

    /**
     * <code>true</code> until the figure is painted for the first time. This is
     * used to prevent recalculating the layout of the subfigures while the
     * properties are set initially.
     */
    private boolean _deferLayout = true;

    /**
     * The number of data series displayed by this figure.
     */
    private final int _numberOfDataSeries;

    /**
     * The bounds of the plotting area (where the data points are drawn).
     * The location of the rectangle is relative to the figure bounds.
     */
    private Rectangle _plotBounds = new Rectangle(10, 10, 10, 10);

    /**
     * The y-axis data mapping.
     */
    private IAxis _yAxis = new LinearAxis(0.0, 0.0, 0);

    /**
     * The x-axis data mapping.
     */
    private final IAxis _xAxis = new LinearAxis(0.0, 0.0, 0);

    /**
     * The scale for the x-axis.
     */
    private Scale _xAxisScale;

    /**
     * The scale for the y-axis.
     */
    private Scale _yAxisScale;

    /**
     * The scale for x-axis grid lines.
     */
    private Scale _xAxisGridLines;

    /**
     * The scale for y-axis grid lines.
     */
    private Scale _yAxisGridLines;

    /**
     * The graph of this waveform.
     */
    private PlotFigure _plotFigure;

    /**
     * The label for this waveform.
     */
    private Label _waveformLabel;

    /**
     * The x-axis label.
     */
    private Label _xAxisLabel;

    /**
     * The y-axis label.
     */
    private Label _yAxisLabel;

    /**
     * The logger used by this object.
     */
    private static final Logger _logger = LoggerFactory.getLogger(AbstractChartFigure.class);


    /**
     * The aliases of this waveform.
     */
    private Map<String, String> _aliases;

    private ICrossedFigure _crossedOutAdapter;

    private IRhombusEquippedWidget _rhombusAdapter;


    /**
     * Creates a new chart figure.
     *
     * @param numberOfDataSeries
     *            the number of data series to be displayed by this figure.
     */
    protected AbstractChartFigure(final int numberOfDataSeries) {
        if (numberOfDataSeries < 0) {
            throw new IllegalArgumentException("numberOfDataSeries must be >= 0");
        }
        _numberOfDataSeries = numberOfDataSeries;

        _plotColor = new Color[_numberOfDataSeries];
        Arrays.fill(_plotColor, ColorConstants.black);
        _plotEnabled = new boolean[_numberOfDataSeries];
        Arrays.fill(_plotEnabled, true);

        setLayoutManager(new XYLayout());
        createSubfigures();
        addRefreshLayoutListener();
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        Rectangle bound = getBounds().getCopy();
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);

    }

    /**
     * Registers a figure listener that listens for movement events and
     * refreshes the layout of the subfigures when the figure has moved.
     */
    private void addRefreshLayoutListener() {
        addFigureListener(new FigureListener() {
            public void figureMoved(final IFigure source) {
                AbstractChartFigure.this.refreshConstraints();
            }
        });
    }

    /**
     * Creates the subfigures that this figure contains.
     */
    private void createSubfigures() {
        _yAxisGridLines = new Scale();
        _yAxisGridLines.setHorizontalOrientation(false);
        _yAxisGridLines.setShowValues(false);
        _yAxisGridLines.setForegroundColor(ColorConstants.lightGray);
        this.add(_yAxisGridLines);

        _xAxisGridLines = new Scale();
        _xAxisGridLines.setHorizontalOrientation(true);
        _xAxisGridLines.setShowValues(false);
        _xAxisGridLines.setForegroundColor(ColorConstants.lightGray);
        this.add(_xAxisGridLines);

        _yAxisScale = new Scale();
        _yAxisScale.setHorizontalOrientation(false);
        _yAxisScale.setShowValues(_labeledTicks);
        _yAxisScale.setAlignment(true);
        _yAxisScale.setForegroundColor(this.getForegroundColor());
        this.add(_yAxisScale);

        _xAxisScale = new Scale();
        _xAxisScale.setHorizontalOrientation(true);
        _xAxisScale.setShowValues(_labeledTicks);
        _xAxisScale.setAlignment(false);
        _xAxisScale.setForegroundColor(this.getForegroundColor());
        this.add(_xAxisScale);

        _plotFigure = new PlotFigure();
        this.add(_plotFigure);

        _waveformLabel = new Label("");
        this.add(_waveformLabel);
        _xAxisLabel = new Label("");
        this.add(_xAxisLabel);
        _yAxisLabel = new Label("");
        this.add(_yAxisLabel);

        /* TODO: Draw y-label text vertically.
         *
         * The following way to do this is recommended in the Eclipse
         * newsgroups, but it currently causes a NullPointerException:
         *
         * _yAxisLabel = new Label("") {
         *     protected void paintFigure(...) {
         *         graphics.rotate(90);
         *         super.paintFigure(...);
         *     }
         * }
         *
         * http://dev.eclipse.org/newslists/news.eclipse.tools.gef/msg15609.html
         * http://dev.eclipse.org/mhonarc/newsLists/news.eclipse.tools.gef/msg20487.html
         */
    }

    /**
     * Returns the lowest data value.
     *
     * @return the lowest data value.
     */
    protected abstract double lowestDataValue();

    /**
     * Returns the greatest data value.
     *
     * @return the greatest data value.
     */
    protected abstract double greatestDataValue();

    /**
     * Sends the data points of the data series with the specified index to the
     * processor.
     *
     * @param index
     *            the index of the data series.
     * @param processor
     *            the processor.
     */
    protected abstract void dataValues(int index, IDataPointProcessor processor);

    /**
     * Returns the lowest x-axis value.
     *
     * @return the lowest x-axis value.
     */
    protected abstract double xAxisMinimum();

    /**
     * Returns the greatest x-axis value.
     *
     * @return the greatest x-axis value.
     */
    protected abstract double xAxisMaximum();

    /**
     * Notifies this figure that the range of the x-axis has changed. This
     * method must be called by subclasses when the range of the x-axis has
     * changed.
     */
    protected final void xAxisRangeChanged() {
        _logger.debug("xAgisRangeChanged(): " + xAxisMinimum() + ", " + xAxisMaximum());
        _xAxis.setDataRange(xAxisMinimum(), xAxisMaximum());
        refreshConstraints();
    }

    /**
     * Notifies this figure that the data range has changed. This method must
     * be called by subclasses when the lowest or greates data value has
     * changed.
     */
    protected final void dataRangeChanged() {
        if (_autoscale) {
            _yAxis.setDataRange(lowestDataValue(), greatestDataValue());
            refreshConstraints();
        }
    }

    /**
     * Returns the lower bound of the y-axis.
     *
     * @return the lower bound of the y-axis.
     */
    private double yAxisLowerBound() {
        return _autoscale ? lowestDataValue() : _propertyMin;
    }

    /**
     * Returns the upper bound of the y-axis.
     *
     * @return the upper bound of the y-axis.
     */
    private double yAxisUpperBound() {
        return _autoscale ? greatestDataValue() : _propertyMax;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public final Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        } else if(adapter == ICrossedFigure.class) {
            if(_crossedOutAdapter==null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if(adapter == IRhombusEquippedWidget.class) {
            if(_rhombusAdapter==null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void paintFigure(final Graphics graphics) {
        super.paintFigure(graphics);

        // After the background of this figure is painted, its children will
        // be painted, so if we have not layed them out yet, we must do so now.
        if (_deferLayout) {
            _deferLayout = false;
            this.refreshConstraints();
        }
    }

    /**
     * Sets the transparent state of the background.
     *
     * @param transparent
     *            the transparent state.
     */
    public final void setTransparent(final boolean transparent) {
        _transparent = transparent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isOpaque() {
        return !_transparent;
    }

    /**
     * Sets which axes should be displayed.
     *
     * @param axes
     *            a value representing which axes should be displayed.
     * @see #SHOW_X_AXIS
     * @see #SHOW_Y_AXIS
     * @see #SHOW_BOTH
     */
    public final void setShowScale(final int axes) {
        _showAxes = axes;
        refreshConstraints();
    }

    /**
     * Sets the axes for which grid lines should be displayed.
     *
     * @param axes
     *            a value representing for which axes grid lines should be
     *            displayed.
     * @see #SHOW_X_AXIS
     * @see #SHOW_Y_AXIS
     * @see #SHOW_BOTH
     */
    public final void setShowGridLines(final int axes) {
        _showGridLines = axes;
        refreshConstraints();
    }

    /**
     * Sets the width of the lines of the graph.
     * @param lineWidth
     *                 The width of the lines of the graph.
     */
    public final void setGraphLineWidth(final int lineWidth) {
        _plotFigure.setPlotLineWidth(lineWidth);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setBackgroundColor(final Color backgroundColor) {
        super.setBackgroundColor(backgroundColor);
        _plotFigure.setBackgroundColor(backgroundColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setForegroundColor(final Color foregroundcolor) {
        super.setForegroundColor(foregroundcolor);
        _plotFigure.setForegroundColor(foregroundcolor);
    }

    /**
     * Sets the color for the grid lines.
     *
     * @param color
     *            the color
     */
    public final void setGridLinesColor(final Color color) {
        _yAxisGridLines.setForegroundColor(color);
        _xAxisGridLines.setForegroundColor(color);
    }

    /**
     * Sets, if the values should be shown.
     *
     * @param showValues
     *            True, if the values should be shown, false otherwise
     */
    public final void setShowValues(final boolean showValues) {
        _labeledTicks = showValues;
        _yAxisScale.setShowValues(showValues);
        _xAxisScale.setShowValues(showValues);
        this.refreshConstraints();
    }

    /**
     * Sets the y-axis scaling of this waveform figure.
     *
     * @param scaling
     *            the new scaling. 0 = linear, 1 = logarithmic.
     */
    public final void setYAxisScaling(final int scaling) {
        switch (scaling) {
        case 0:
        default:
            _yAxis = new LinearAxis(yAxisLowerBound(), yAxisUpperBound(),
                    _plotBounds.height);
            break;
        case 1:
            _yAxis = new LogarithmicAxis(yAxisLowerBound(), yAxisUpperBound(),
                    _plotBounds.height);
            break;
        }
        refreshConstraints();
    }

    /**
     * Sets the label.
     *
     * @param label the label.
     */
    public final void setLabel(final String label) {
        try {
            _waveformLabel.setText(ChannelReferenceValidationUtil
                    .createCanonicalName(label, _aliases));
        } catch (ChannelReferenceValidationException e) {
            _waveformLabel.setText(label);
            _logger.info("Waveform label contains unresolvable aliases: \""
                    + label + "\"");
        }
        refreshConstraints();
    }

    /**
     * Sets the x-axis label.
     *
     * @param axisLabel the label.
     */
    public final void setXAxisLabel(final String axisLabel) {
        try {
            _xAxisLabel.setText(ChannelReferenceValidationUtil
                    .createCanonicalName(axisLabel, _aliases));
        } catch (ChannelReferenceValidationException e) {
            _xAxisLabel.setText(axisLabel);
            _logger.info("Waveform x-axis label contains unresolvable aliases: \""
                    + axisLabel + "\"");
        }
        refreshConstraints();
    }

    /**
     * Sets the y-axis label.
     *
     * @param axisLabel the label.
     */
    public final void setYAxisLabel(final String axisLabel) {
        try {
            _yAxisLabel.setText(ChannelReferenceValidationUtil
                    .createCanonicalName(axisLabel, _aliases));
        } catch (ChannelReferenceValidationException e) {
            _yAxisLabel.setText(axisLabel);
            _logger.info("Waveform y-axis label contains unresolvable aliases: \""
                    + axisLabel + "\"");
        }
        refreshConstraints();
    }

    /**
     * Sets the aliases of this waveform.
     *
     * @param aliases
     *            the aliases of this waveform.
     */
    public final void setAliases(final Map<String, String> aliases) {
        _aliases = aliases != null ? aliases : new HashMap<String, String>();
    }

    /**
     * Sets the max value for the graph.
     * @param max
     *                 The max value
     */
    public final void setMax(final double max) {
        _propertyMax = max;
        if (!_autoscale) {
            _yAxis.setDataRange(_propertyMin, _propertyMax);
            this.refreshConstraints();
        }
    }

    /**
     * Sets the min value for the graph.
     * @param min
     *                 The min value
     */
    public final void setMin(final double min) {
        _propertyMin = min;
        if (!_autoscale) {
            _yAxis.setDataRange(_propertyMin, _propertyMax);
            this.refreshConstraints();
        }
    }

    /**
     * Sets, if the graph should be automatically scaled.
     * @param autoscale
     *                 True if it should be automatically scaled, false otherwise
     */
    public final void setAutoScale(final boolean autoscale) {
        _autoscale = autoscale;
        if (!_autoscale) {
            _yAxis.setDataRange(_propertyMin, _propertyMax);
        } else {
            _yAxis.setDataRange(lowestDataValue(), greatestDataValue());
        }
        this.refreshConstraints();
    }

    /**
     * Sets whether the chart is a line chart.
     *
     * @param lineChart
     *            <code>true</code> if the chart should be drawn as a line
     *            chart, <code>false</code> otherwise.
     */
    public final void setLineChart(final boolean lineChart) {
        _lineChart = lineChart;
    }

    /**
     * Sets the data point drawing style.
     *
     * @param style the style.
     */
    public final void setDataPointDrawingStyle(final int style) {
        _plotFigure.setDataPointDrawingStyle(style);
        refreshConstraints();
    }

    /**
     * Sets the color to be used for the plot of the data with the specified
     * index.
     *
     * @param index
     *            the data index. This must be a positive integer or zero, and
     *            smaller than the number of data arrays specified in the
     *            constructor of this figure.
     * @param color
     *            the color.
     */
    public final void setPlotColor(final int index, final Color color) {
        if ((index < 0) || (index >= _numberOfDataSeries)) {
            throw new IndexOutOfBoundsException(
                    "invalid index: " + index);
        }
        _plotColor[index] = color;
    }

    /**
     * Enables or disables the plot with the specified index.
     *
     * @param index
     *            the data index. This must be a positive integer or zero, and
     *            smaller than the number of data arrays specified in the
     *            constructor of this figure.
     * @param enabled
     *            whether the plot should be enabled.
     */
    public final void setPlotEnabled(final int index, final boolean enabled) {
        if ((index < 0) || (index >= _numberOfDataSeries)) {
            throw new IndexOutOfBoundsException(
                    "invalid index: " + index);
        }
        _plotEnabled[index] = enabled;
    }

    /**
     * Checks whether the x-axis is displayed.
     *
     * @return <code>true</code> if the x-axis is displayed,
     *         <code>false</code> otherwise.
     */
    protected final boolean showXAxis() {
        return ((_showAxes == SHOW_X_AXIS) || (_showAxes == SHOW_BOTH));
    }

    /**
     * Checks whether the y-axis is displayed.
     *
     * @return <code>true</code> if the y-axis is displayed,
     *         <code>false</code> otherwise.
     */
    protected final boolean showYAxis() {
        return ((_showAxes == SHOW_Y_AXIS) || (_showAxes == SHOW_BOTH));
    }

    /**
     * Checks whether gridlines are displayed for the x-axis.
     *
     * @return <code>true</code> if gridlines are displayed,
     *         <code>false</code> otherwise.
     */
    protected final boolean showXAxisGrid() {
        return ((_showGridLines == SHOW_X_AXIS) || (_showGridLines == SHOW_BOTH));
    }

    /**
     * Checks whether gridlines are displayed for the y-axis.
     *
     * @return <code>true</code> if gridlines are displayed,
     *         <code>false</code> otherwise.
     */
    protected final boolean showYAxisGrid() {
        return ((_showGridLines == SHOW_Y_AXIS) || (_showGridLines == SHOW_BOTH));
    }

    /**
     * Returns the y position relative to the top of the plot at which the given
     * value should be drawn.
     *
     * @param value
     *            the y value.
     * @return the y position.
     */
    private int valueToYPos(final double value) {
        // the data values are mapped to [0, height-1]
        int plotHeight = _plotBounds.height - 1;

        // the axis calculates the distance from the lower bound of the data
        // range, but for the y coordinate, we need the distance from the top
        // of the plot, so we subtract the returned value from plotHeight.
        return plotHeight - _yAxis.valueToCoordinate(value);
    }

    /**
     * Returns the x position relative to the left of the plot at which the
     * given value should be drawn.
     *
     * @param value
     *            the x value. This is usually the index of the data point
     *            within the data array.
     * @return the x position.
     */
    private int valueToXPos(final double value) {
        return _xAxis.valueToCoordinate(value);
    }

    /**
     * Performs the layout of the subfigures of this figure.
     */
    public final void refreshConstraints() {
        if (_deferLayout) {
            return;
        }

        Rectangle figBounds = this.getBounds().getCopy();
        figBounds.crop(this.getInsets());

        // These bounds are used for the placement of the sub-figures below.
        // The bounds are cropped after the placement of each sub-figure and
        // the next sub-figure will be placed in the remaining bounds.
        Rectangle bounds = new Rectangle(0, 0, figBounds.width, figBounds.height);

        Rectangle labelBounds = calculateLabelBounds(bounds);
        setConstraint(_waveformLabel, labelBounds);

        Rectangle xAxisLabelBounds = calculateXAxisLabelBounds(bounds);
        setConstraint(_xAxisLabel, xAxisLabelBounds);

        Rectangle xAxisBounds = calculateXAxisBounds(bounds);
        setConstraint(_xAxisScale, xAxisBounds);

        Rectangle yAxisLabelBounds = calculateYAxisLabelBounds(bounds);
        setConstraint(_yAxisLabel, yAxisLabelBounds);

        Rectangle yAxisBounds = calculateYAxisBounds(bounds);
        setConstraint(_yAxisScale, yAxisBounds);

        _plotBounds = calculatePlotBounds(bounds);
        setConstraint(_plotFigure, _plotBounds);

        // Grid lines are located on top of the plot (within the same bounds,
        // but the y-axis grid needs to be adjusted for the text height at the
        // top to align with the y-axis).
        setConstraint(_yAxisGridLines,
                showYAxisGrid() ?
                        _plotBounds.getCopy().expand(
                                new Insets(TEXTHEIGHT / 2, 0, 0 ,0))
                        : ZERO_RECTANGLE);
        _yAxisGridLines.setWideness(_plotBounds.width);
        setConstraint(_xAxisGridLines,
                showXAxisGrid() ? _plotBounds.getCopy() : ZERO_RECTANGLE);
        _xAxisGridLines.setWideness(_plotBounds.height);

        setToolTip(getToolTipFigure());

        // Update the axis (for mapping the data points to display coordinates)
        _yAxis.setDisplaySize(_plotBounds.height);
        _xAxis.setDisplaySize(_plotBounds.width);
        _yAxisScale.refreshConstraints();
        _xAxisScale.refreshConstraints();
    }

    /**
     * Calculates the bounds of the y-axis label.
     *
     * @param bounds
     *            the bounds within which the label will be displayed. These
     *            bounds will be cropped to the remaining bounds.
     * @return the bounds of the label.
     */
    private Rectangle calculateYAxisLabelBounds(final Rectangle bounds) {
        if (isYAxisLabeled()) {
            int width = yAxisLabelWidth();
            Rectangle result = new Rectangle(bounds.x, bounds.y,
                    width, bounds.height);
            bounds.crop(new Insets(0, width, 0, 0));
            return result;
        } else {
            return ZERO_RECTANGLE;
        }
    }

    /**
     * Calculates the bounds of the x-axis label.
     *
     * @param bounds
     *            the bounds within which the label will be displayed. These
     *            bounds will be cropped to the remaining bounds.
     * @return the bounds of the label.
     */
    private Rectangle calculateXAxisLabelBounds(final Rectangle bounds) {
        if (isXAxisLabeled()) {
            int height = TEXTHEIGHT;
            Rectangle result = new Rectangle(bounds.x, bounds.bottom() - height,
                    bounds.width, height);
            bounds.crop(new Insets(0, 0, height, 0));
            return result;
        } else {
            return ZERO_RECTANGLE;
        }
    }

    /**
     * Calculates the bounds of the plot of this figure.
     *
     * @param bounds
     *            the bounds within which the plot will be displayed. Note:
     *            unlike the other {@code calculate...} methods, this method
     *            will not crop these bounds to the remaining bounds, because
     *            the plot fills up all the remaining space (except necessary
     *            padding).
     * @return the bounds of the plot.
     */
    private Rectangle calculatePlotBounds(final Rectangle bounds) {
        int y = bounds.y + (showYAxis() ? TEXTHEIGHT / 2 : 0);
        // height, adjusted for extra space at top and bottom for y-axis labels
        int height = bounds.height
                - (showYAxis() ? (showXAxis() ? TEXTHEIGHT / 2 : TEXTHEIGHT) : 0);
        if (height < 0) {
            height = 0;
        }
        Rectangle result = new Rectangle(bounds.x, y, bounds.width,
                height);
        return result;
    }

    /**
     * Calculates the bounds of the x-axis of this figure.
     *
     * @param bounds
     *            the bounds within which the x-axis will be displayed. These
     *            bounds will be cropped to the remaining bounds.
     * @return the bounds of the x-axis.
     */
    private Rectangle calculateXAxisBounds(final Rectangle bounds) {
        if (showXAxis()) {
            int height = xAxisHeight();
            Rectangle result = new Rectangle(
                    bounds.x + yAxisWidth() + yAxisLabelWidth(),
                    bounds.bottom() - height,
                    bounds.width - yAxisWidth() - yAxisLabelWidth(),
                    height);
            bounds.crop(new Insets(0, 0, height, 0));
            return result;
        } else {
            return ZERO_RECTANGLE;
        }
    }

    /**
     * Calculates the width of the y-axis label.
     *
     * @return the width of the y-axis label.
     */
    private int yAxisLabelWidth() {
        return isYAxisLabeled() ? TEXTWIDTH : 0;
    }

    /**
     * Calculates the bounds of the y-axis of this figure.
     *
     * @param bounds
     *            the bounds within which the y-axis will be displayed. These
     *            bounds will be cropped to the remaining bounds.
     * @return the bounds of the y-axis.
     */
    private Rectangle calculateYAxisBounds(final Rectangle bounds) {
        if (showYAxis()) {
            int width = yAxisWidth();
            // height, adjusted for extra space at the bottom if the x-axis is
            // shown (the space is then already subtracted from the figureBounds)
            int height = bounds.height + (showXAxis() ? TEXTHEIGHT / 2 : 0);
            Rectangle result = new Rectangle(bounds.x, bounds.y,
                    width, height);
            bounds.crop(new Insets(0, width, 0, 0));
            return result;
        } else {
            return ZERO_RECTANGLE;
        }
    }

    /**
     * Calculates the bounds of the label of this figure.
     *
     * @param bounds
     *            the bounds within which the label will be displayed. These
     *            bounds will be cropped to the remaining bounds.
     * @return the bounds of the label.
     */
    private Rectangle calculateLabelBounds(final Rectangle bounds) {
        if (isLabelled()) {
            int height = TEXTHEIGHT;
            Rectangle result = new Rectangle(bounds.x, bounds.y,
                    bounds.width, height);
            bounds.crop(new Insets(height, 0, 0, 0));
            return result;
        } else {
            return ZERO_RECTANGLE;
        }
    }

    /**
     * Returns whether this figure has a label.
     *
     * @return <code>true</code> if this figure has a label,
     *         <code>false</code> otherwise.
     */
    private boolean isLabelled() {
        return !"".equals(_waveformLabel.getText());
    }

    /**
     * Returns whether this figure has a label on its x-axis.
     *
     * @return <code>true</code> if the x-axis is labeled, <code>false</code>
     *         otherwise.
     */
    private boolean isXAxisLabeled() {
        return showXAxis() && !"".equals(_xAxisLabel.getText());
    }

    /**
     * Returns whether this figure has a label on its y-axis.
     *
     * @return <code>true</code> if the y-axis is labeled, <code>false</code>
     *         otherwise.
     */
    private boolean isYAxisLabeled() {
        return showYAxis() && !"".equals(_yAxisLabel.getText());
    }

    /**
     * Calculates the width of the y-axis.
     *
     * @return the width of the y-axis in pixels.
     */
    private int yAxisWidth() {
        if (showYAxis()) {
            return _labeledTicks ? AXIS_SIZE + TEXTWIDTH : AXIS_SIZE;
        } else {
            return 0;
        }
    }

    /**
     * Calculates the height of the x-axis.
     *
     * @return the height of the x-axis in pixels.
     */
    private int xAxisHeight() {
        if (showXAxis()) {
            return _labeledTicks ? AXIS_SIZE + TEXTHEIGHT : AXIS_SIZE;
        } else {
            return 0;
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
//        panel.add(new Label("Count of data points: " + _data.length));
        panel.add(new Label("Minimum value: " + lowestDataValue()));
        panel.add(new Label("Maximum value: " + greatestDataValue()));
        panel.setBackgroundColor(ColorConstants.tooltipBackground);
        return panel;
    }

    /**
     * A drawing style for drawing data points in a plot.
     *
     * @author Joerg Rathlev
     */
    private enum DataPointDrawingStyle {
        /**
         * Draws a data point as a single pixel.
         */
        PIXEL {
            /**
             * {@inheritDoc}
             */
            @Override
            protected void draw(final Graphics g, final Point p) {
                g.drawPoint(p.x, p.y);
            }
        },

        /**
         * Draws a data point as a small plus sign.
         */
        SMALL_PLUS_SIGN {
            /**
             * {@inheritDoc}
             */
            @Override
            protected void draw(final Graphics g, final Point p) {
                //    #
                //    #
                //  #####
                //    #
                //    #
                g.drawLine(p.x, p.y-2, p.x, p.y+2);
                g.drawLine(p.x-2, p.y, p.x+2, p.y);
            }
        },

        /**
         * Draws a data point as a small square (3x3 pixels).
         */
        SMALL_SQUARE {
            /**
             * {@inheritDoc}
             */
            @Override
            protected void draw(final Graphics g, final Point p) {
                g.fillRectangle(p.x-1, p.y-1, 3, 3);
            }
        },

        /**
         * Draws a diamod-shaped data point.
         */
        DIAMOND {
            /**
             * {@inheritDoc}
             */
            @Override
            protected void draw(final Graphics g, final Point p) {
                //    #
                //   ###
                //  #####
                //   ###
                //    #

                // Note: the call to drawPolygon is required because otherwise
                // for some reason the polygon drawn by fillPolygon is a bit
                // too small (the right edge is drawn one pixel to the left).
                g.drawPolygon(new int[] {
                        p.x-2, p.y, p.x, p.y-2, p.x+2, p.y, p.x, p.y+2 });
                g.fillPolygon(new int[] {
                        p.x-2, p.y, p.x, p.y-2, p.x+2, p.y, p.x, p.y+2 });
            }
        };

        /**
         * Draws a data point at the specified coordinates.
         *
         * @param g the graphics object to use for drawing.
         * @param p the coordinates of the data point.
         */
        protected abstract void draw(Graphics g, Point p);
    }

    /**
     * Receives data points from a subclass implementation and processes them.
     *
     * @author Joerg Rathlev
     */
    protected interface IDataPointProcessor {

        /**
         * Processes the specified data point. The values of the data point must
         * be given in data value units (not display units).
         *
         * @param x
         *            the x-value of the data point.
         * @param y
         *            the y-value of the data point.
         */
        void processDataPoint(double x, double y);
    }

    /**
     * Figure for the actual plot.
     */
    private final class PlotFigure extends RectangleFigure {
        /**
         * The width of the lines of the graph.
         */
        private int _plotLineWidth = 1;

        /**
         * The drawing style used for the data points.
         */
        private DataPointDrawingStyle _style = DataPointDrawingStyle.SMALL_SQUARE;

        /**
         * {@inheritDoc}
         */
        @Override
        public void paintFigure(final Graphics graphics) {
            Rectangle figureBounds = this.getBounds();
            graphics.setForegroundColor(this.getForegroundColor());
            graphics.drawLine(figureBounds.x, figureBounds.y, figureBounds.x,
                    figureBounds.y + figureBounds.height);
            graphics.drawLine(figureBounds.x, figureBounds.bottom() - 1,
                    figureBounds.x + figureBounds.width, figureBounds.bottom() - 1);

            for (int i = 0; i < _numberOfDataSeries; i++) {
                if (!_plotEnabled[i]) {
                    continue;
                }

                // TODO: the points don't actually have to be recalculated everytime the plot
                // is redrawn -- only if the data points have changed or if the size of the
                // plot has changed.
                PointList pointList = calculatePlotPoints(i);
                graphics.setForegroundColor(_plotColor[i]);
                graphics.setBackgroundColor(_plotColor[i]);
                graphics.setLineWidth(_plotLineWidth);
                if (_lineChart) {
                    graphics.drawPolyline(pointList);
                }
                for (int j = 0; j < pointList.size(); j++) {
                    Point p = pointList.getPoint(j);
                    _style.draw(graphics, p);
                }
            }
        }

        /**
         * Calculates the coordinates of the data points in the plot area.
         *
         * @param index
         *            the index of the data series
         * @return a list of points to be plotted.
         */
        private PointList calculatePlotPoints(final int index) {
            final Rectangle bounds = getBounds();
            final PointList result = new PointList();
            IDataPointProcessor proc = new IDataPointProcessor() {
                public void processDataPoint(final double x, final double y) {
                    if (_xAxis.isLegalValue(x) && _yAxis.isLegalValue(y)) {
                        int displayY = valueToYPos(y);
                        int displayX = valueToXPos(x);
                        result.addPoint(bounds.x + displayX, bounds.y + displayY);
                    }
                }
            };
            dataValues(index, proc);
            return result;
        }

        /**
         * Sets the width of the lines of the plot.
         * @param lineWidth
         *                 The width of the lines of the graph.
         */
        private void setPlotLineWidth(final int lineWidth) {
            _plotLineWidth = lineWidth;
        }

        /**
         * Sets the data point drawing style of this plot.
         *
         * @param style the style.
         */
        private void setDataPointDrawingStyle(final int style) {
            switch(style) {
            case 0:
                _style = DataPointDrawingStyle.PIXEL;
                break;
            case 1:
                _style = DataPointDrawingStyle.SMALL_PLUS_SIGN;
                break;
            case 2:
                _style = DataPointDrawingStyle.SMALL_SQUARE;
                break;
            case 3:
                _style = DataPointDrawingStyle.DIAMOND;
                break;
            default:
                _style = DataPointDrawingStyle.SMALL_SQUARE;
            }
        }
    }

    /**
     * This class represents a scale.
     *
     * @author Kai Meyer
     */
    private final class Scale extends RectangleFigure {
        /**
         * The direction of this Scale.
         */
        private boolean _isHorizontal;
        /**
         * The Alignment for the Scalemarkers.
         */
        private boolean _isTopLeft;
        /**
         * The length of the lines.
         */
        private int _wideness = 10;
        /**
         * True, if the values of the Markers should be shown, false otherwise.
         */
        private boolean _showValues = false;

        /**
         * The List of positive ScaleMarkers.
         */
        private final List<ScaleMarker> _posScaleMarkers = new LinkedList<ScaleMarker>();

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
            if ((this.getBounds().height==0) || (this.getBounds().width==0)) {
                _posScaleMarkers.clear();
                this.removeAll();
                return;
            }
            int index = 0;
            if (_isHorizontal) {
                int height = _wideness;
                if (_showValues) {
                    height = TEXTHEIGHT + _wideness;
                }

                int distance = TEXTWIDTH;
                List<Tick> ticks = _xAxis.calculateIntegerTicks(distance, 3);
                for (Tick tick : ticks) {
                    if (index >= _posScaleMarkers.size()) {
                        this.addScaleMarker(index, _posScaleMarkers);
                    }
                    int x = valueToXPos(tick.value());
                    this.setConstraint(_posScaleMarkers.get(index),
                            new Rectangle(x - (TEXTWIDTH/2), 0, TEXTWIDTH, height));
                    this.refreshScaleMarker(_posScaleMarkers.get(index), tick.value(), _showValues);
                    index++;
                }
                this.removeScaleMarkers(index, _posScaleMarkers);
            } else {
                int width = _wideness;
                if (_showValues) {
                    width = TEXTWIDTH + _wideness;
                }

                int distance = TEXTHEIGHT * 2;
                List<Tick> ticks = _yAxis.calculateTicks(distance, 3);
                for (Tick tick : ticks) {
                    if (index >= _posScaleMarkers.size()) {
                        this.addScaleMarker(index, _posScaleMarkers);
                    }
                    int y = valueToYPos(tick.value());
                    this.setConstraint(_posScaleMarkers.get(index), new Rectangle(0, y, width, TEXTHEIGHT));
                    this.refreshScaleMarker(_posScaleMarkers.get(index), tick.value(), _showValues);
                    index++;
                }
                this.removeScaleMarkers(index, _posScaleMarkers);
            }
        }

        /**
         * Refreshes the given ScaleMarker.
         * @param marker
         *                 The ScaleMarker, which should be refreshed
         * @param labelValue
         *                 The new value for the displayed text
         * @param showValue
         *                 True, if the value should be shown, false otherwise
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
         *                 The index
         * @param scaleMarkers
         *                 The List of ScaleMarkers
         */
        private void addScaleMarker(final int index, final List<ScaleMarker> scaleMarkers) {
            ScaleMarker marker = new ScaleMarker();
            scaleMarkers.add(index, marker);
            this.add(marker);
        }

        /**
         * Removes all ScaleMarkers in the given List, beginning by the given index.
         * @param index
         *                 The index
         * @param scaleMarkers
         *                 The List of ScaleMarkers
         */
        private void removeScaleMarkers(final int index, final List<ScaleMarker> scaleMarkers) {
            if (!scaleMarkers.isEmpty() && (index<=scaleMarkers.size())) {
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
//            graphics.setForegroundColor(ColorConstants.blue);
//            graphics.setBackgroundColor(ColorConstants.blue);
//            graphics.fillRectangle(this.getBounds());
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
         *               The alignment for the ScaleMarker
         *            (true=top/left; false=bottom/right)
         *
         */
        public void setAlignment(final boolean isTopLeft) {
            _isTopLeft = isTopLeft;
            this.refreshConstraints();
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
         * Sets, if the values of the Markers should be shown.
         * @param showValues
         *                 True if the values of the Markers should be shown, false otherwise
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
        }

        /**
         * This class represents a marker for the scale.
         * @author Kai Meyer
         */
        private final class ScaleMarker extends RectangleFigure {
            /**
             * The Label of this ScaleMarker.
             */
            private final Label _textLabel;
            /**
             * The hyphen of this ScaleMarker.
             */
            private final ScaleHyphen _scaleHyphen;
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
//                if (_showValues) {
                    this.add(_textLabel);
//                }
                this.refreshConstraints();
                addFigureListener(new FigureListener() {
                    public void figureMoved(final IFigure source) {
                        refreshConstraints();
                    }
                });
            }

            /**
             * Recalculates the constraints.
             */
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
//                graphics.setForegroundColor(ColorConstants.green);
//                graphics.setBackgroundColor(ColorConstants.green);
//                graphics.fillRectangle(this.getBounds());
            }

            /**
             * Sets the orientation of the scale to which this marker belongs.
             *
             * @param isHorizontal
             *            <code>true</code> if the scale is a horizontal scale
             *            (i.e. along the x-axis), <code>false</code> if it is
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
             *             The text to display
             */
            public void setText(final String text) {
                _textLabel.setText(text);
                this.refreshLabel();
            }

            /**
             * Sets, if the values of the Markers should be shown.
             * @param showValues
             *                 True if the values of the Markers should be shown, false otherwise
             */
            public void setShowValues(final boolean showValues) {
                _showValues = showValues;
                this.refreshLabel();
            }

            /**
             * Sets the wideness of the Hyphen.
             * @param wideness
             *                 The wideness
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
                    graphics.drawLine(x, y,    x + _width,    y + _height);
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
                 *                 The Orientation of this Hyphen
                 *                 true=horizontal; false = vertical
                 */
                public void setHorizontalOrientation(final boolean isHorizontal) {
                    _isHorizontal = isHorizontal;
                    this.setHeightAndWidth();
                }

                /**
                 * Sets the wideness of the Hyphen.
                 * @param wideness
                 *                 The wideness
                 */
                public void setWideness(final int wideness) {
                    _wideness = wideness;
                    this.setHeightAndWidth();
                }

                /**
                 * Sets the alignment of this Hyphen.
                 * @param isTopLeft
                 *                 The alignment (true=top/left; false = bottom/right)
                 */
                public void setAlignment(final boolean isTopLeft) {
                    _isTopLeft = isTopLeft;
                }
            }
        }
    }
}
