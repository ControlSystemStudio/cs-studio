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

package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.ArrayOptionProperty;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.graphics.RGB;

/**
 * Abstract base class for widgets that draw a chart.
 * 
 * @author Joerg Rathlev
 */
public abstract class AbstractChartModel extends AbstractWidgetModel {
	
	/**
	 * The ID of the show axes property.
	 */
	public static final String PROP_SHOW_AXES = "show_scale";

	/**
	 * The ID of the show grid lines property.
	 */
	public static final String PROP_SHOW_GRID_LINES = "show_ledger_lines";

	/**
	 * The ID of the line chart property.
	 */
	public static final String PROP_LINE_CHART = "show_connection_lines";

	/**
	 * The ID of the grid line color property.
	 */
	public static final String PROP_GRID_LINE_COLOR = "ledger_lines_color";

	/**
	 * The ID of the minimum property.
	 */
	public static final String PROP_MIN = "min";

	/**
	 * The ID of the maximum property.
	 */
	public static final String PROP_MAX = "max";

	/**
	 * The ID of the autoscale property.
	 */
	public static final String PROP_AUTOSCALE = "autoscale";

	/**
	 * The ID of the labeled ticks property.
	 */
	public static final String PROP_LABELED_TICKS = "show_values";

	/**
	 * The ID of the plot line width property.
	 */
	public static final String PROP_PLOT_LINE_WIDTH = "connection_line_width";

	/**
	 * The ID of the transparent property.
	 */
	public static final String PROP_TRANSPARENT = "transparency";

	/**
	 * The ID of the data point drawing style property.
	 */
	public static final String PROP_DATA_POINT_DRAWING_STYLE = "data_point_drawing_style";

	/**
	 * The ID of the y-axis scaling property.
	 */
	public static final String PROP_Y_AXIS_SCALING = "y_axis_scaling";

	/**
	 * The ID of the label property.
	 */
	public static final String PROP_LABEL = "label";

	/**
	 * The ID of the x-axis label property.
	 */
	public static final String PROP_X_AXIS_LABEL = "x_axis_label";

	/**
	 * The ID of the y-axis label property.
	 */
	public static final String PROP_Y_AXIS_LABEL = "y_axis_label";

	/**
	 * The show axes options.
	 */
	private static final String[] AXES_OPTIONS = new String[] {
		"None", "X-Axis", "Y-Axis", "Both"
	};
	
	/**
	 * The options for the data point drawing style property.
	 */
	private static final String[] DRAWING_STYLE_OPTIONS = new String[] {
		"Single pixel", "Small plus sign", "Small square", "Diamond"
	};
	
	/**
	 * The options for the axis scaling property.
	 */
	private static final String[] AXIS_SCALING_OPTIONS = new String[] {
		"Linear", "Logarithmic"
	};

	/**
	 * The base property ID for the data color properties. Use the
	 * {@link #plotColorPropertyId(int)} method to get the property ID for the
	 * color of a specific data series. 
	 */
	private static final String INTERNAL_PROP_PLOT_COLOR = "plot_color";

	/**
	 * {@inheritDoc}
	 */
	// CheckStyle won't believe it, but there is nothing wrong here.
	@Override
	protected void configureProperties() {
		addProperty(PROP_SHOW_AXES, new ArrayOptionProperty("Show axes",
				WidgetPropertyCategory.Display, AXES_OPTIONS, 3));
		addProperty(PROP_SHOW_GRID_LINES, new ArrayOptionProperty(
				"Grid lines", WidgetPropertyCategory.Display, AXES_OPTIONS, 0));
		addProperty(PROP_LINE_CHART, new BooleanProperty("Line chart",
				WidgetPropertyCategory.Display, false));
		for (int i = 0; i < numberOfDataSeries(); i++) {
			addProperty(plotColorPropertyId(i), new ColorProperty(
					"Plot color #" + (i+1), WidgetPropertyCategory.Display,
					new RGB(0, 0, 0)));
		}
		addProperty(PROP_GRID_LINE_COLOR, new ColorProperty("Grid line color",
				WidgetPropertyCategory.Display, new RGB(210, 210, 210)));
		addProperty(PROP_MIN, new DoubleProperty(
				"Minimum", WidgetPropertyCategory.Display, -100.0));
		addProperty(PROP_MAX, new DoubleProperty(
				"Maximum", WidgetPropertyCategory.Display, 100.0));
		addProperty(PROP_AUTOSCALE, new BooleanProperty(
				"Automatic scaling", WidgetPropertyCategory.Display, false));
		addProperty(PROP_LABELED_TICKS, new BooleanProperty(
				"Labeled ticks", WidgetPropertyCategory.Display, true));
		addProperty(PROP_PLOT_LINE_WIDTH, new IntegerProperty(
				"Graph line width",WidgetPropertyCategory.Display, 1, 1, 100));
		addProperty(PROP_TRANSPARENT, new BooleanProperty(
				"Transparent background", WidgetPropertyCategory.Display,
				false));
		addProperty(PROP_DATA_POINT_DRAWING_STYLE, new ArrayOptionProperty(
				"Data point drawing style", WidgetPropertyCategory.Display, 
				DRAWING_STYLE_OPTIONS, 2));
		addProperty(PROP_Y_AXIS_SCALING, new ArrayOptionProperty(
				"Y-axis scaling", WidgetPropertyCategory.Display,
				AXIS_SCALING_OPTIONS, 0));
		addProperty(PROP_LABEL, new StringProperty("Label",
				WidgetPropertyCategory.Display, ""));
		addProperty(PROP_X_AXIS_LABEL, new StringProperty("X-axis label",
				WidgetPropertyCategory.Display, ""));
		addProperty(PROP_Y_AXIS_LABEL, new StringProperty("Y-axis label",
				WidgetPropertyCategory.Display, ""));
	}

	/**
	 * Returns the number of data series that this model supports. Subclasses
	 * must implement this method so that it returns a constant value.
	 * 
	 * @return the number of data series that this model supports.
	 */
	protected abstract int numberOfDataSeries();

	/**
	 * Returns whether automatic scaling for the y-axis is enabled.
	 * 
	 * @return boolean <code>true</code> if automatic scaling is enabled,
	 *         <code>false</code> otherwise.
	 */
	public final boolean getAutoscale() {
		return (Boolean) getProperty(PROP_AUTOSCALE).getPropertyValue();
	}

	/**
	 * Returns the minimum value.
	 * @return	double
	 * 				The minimum value
	 */
	public final double getMin() {
		return (Double) getProperty(PROP_MIN).getPropertyValue();
	}

	/**
	 * Returns the maximum value.
	 * @return	double
	 * 				The maximum value
	 */
	public final double getMax() {
		return (Double) getProperty(PROP_MAX).getPropertyValue();
	}

	/**
	 * Returns whether the ticks should be labeled.
	 * 
	 * @return boolean <code>true</code> if the ticks should be labeled,
	 *         <code>false</code> otherwise.
	 */
	public final boolean isLabeledTicksEnabled() {
		return (Boolean) getProperty(PROP_LABELED_TICKS).getPropertyValue();
	}

	/**
	 * Returns which axes should be shown.
	 * 
	 * @return 0 for none, 1 for x-axis, 2 for y-axis, 3 for both.
	 */
	public final int getShowAxes() {
		return (Integer) getProperty(PROP_SHOW_AXES).getPropertyValue();
	}

	/**
	 * Returns for which axes grid lines should be shown.
	 * 
	 * @return 0 for none, 1 for x-axis, 2 for y-axis, 3 for both.
	 */
	public final int getShowGridLines() {
		return (Integer) getProperty(PROP_SHOW_GRID_LINES).getPropertyValue();
	}

	/**
	 * Returns whether the chart should be drawn as a line chart.
	 * 
	 * @return <code>true</code> if the chart should be drawn as a line chart,
	 *         <code>false</code> if only the data points should be drawn.
	 */
	public final boolean isLineChart() {
		return (Boolean) getProperty(PROP_LINE_CHART).getPropertyValue();
	}

	/**
	 * Returns the width of the lines of the chart.
	 * 
	 * @return the width of the lines in pixels.
	 */
	public final int getPlotLineWidth() {
		return (Integer) getProperty(PROP_PLOT_LINE_WIDTH).getPropertyValue();
	}

	/**
	 * Returns the color of the grid lines.
	 * 
	 * @return the color of the grid lines.
	 */
	public final RGB getGridLineColor() {
		return (RGB) getProperty(PROP_GRID_LINE_COLOR).getPropertyValue();
	}

	/**
	 * Returns whether the background of the chart is transparent.
	 * 
	 * @return <code>true</code> if the background is transparent,
	 *         <code>false</code> otherwise.
	 */
	public final boolean isTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}

	/**
	 * Returns the data point drawing style. 0 = single pixel, 1 = small plus
	 * sign, 2 = small square, 3 = diamond.
	 * 
	 * @return the data point drawing style.
	 */
	public final int getDataPointDrawingStyle() {
		return (Integer) getProperty(PROP_DATA_POINT_DRAWING_STYLE).getPropertyValue();
	}

	/**
	 * Returns the setting of the y-axis scaling property.
	 * 
	 * @return the y-axis scaling. 0 = linear, 1 = logarithmic.
	 */
	public final int getYAxisScaling() {
		return getProperty(PROP_Y_AXIS_SCALING).getPropertyValue();
	}

	/**
	 * Returns the label.
	 * 
	 * @return the label.
	 */
	public final String getLabel() {
		return getProperty(PROP_LABEL).getPropertyValue();
	}

	/**
	 * Returns the x-axis label.
	 * 
	 * @return the x-axis label.
	 */
	public final String getXAxisLabel() {
		return getProperty(PROP_X_AXIS_LABEL).getPropertyValue();
	}

	/**
	 * Returns the y-axis label.
	 * 
	 * @return the y-axis label.
	 */
	public final String getYAxisLabel() {
		return getProperty(PROP_Y_AXIS_LABEL).getPropertyValue();
	}

	/**
	 * Returns the color for plotting the specified data series.
	 * 
	 * @param index
	 *            the index of the data series.
	 * @return the color for the plot.
	 */
	public final RGB getPlotColor(final int index) {
		return (RGB) getProperty(plotColorPropertyId(index)).getPropertyValue();
	}

	/**
	 * Returns the property ID of the data color property for the data with the
	 * specified index.
	 * 
	 * @param index
	 *            the data index.
	 * @return the property ID.
	 */
	public static String plotColorPropertyId(final int index) {
		return INTERNAL_PROP_PLOT_COLOR + Integer.toString(index + 1);
	}

}
