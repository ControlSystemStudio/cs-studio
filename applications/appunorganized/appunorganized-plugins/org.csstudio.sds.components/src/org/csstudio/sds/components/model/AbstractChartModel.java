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

/**
 * Abstract base class for widgets that draw a chart.
 *
 * @author Joerg Rathlev
 */
public abstract class AbstractChartModel extends AbstractWidgetModel {

    /**
     * The ID of the show axes property.
     */
    public static final String PROP_SHOW_AXES = "show_axes";

    /**
     * The ID of the show grid lines property.
     */
    public static final String PROP_SHOW_GRID_LINES = "show_grid_lines";

    /**
     * The ID of the line chart property.
     */
    public static final String PROP_LINE_CHART = "line_chart";

    /**
     * The ID of the grid line color property.
     */
    public static final String PROP_GRID_LINE_COLOR = "grid_line_color";

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
    public static final String PROP_LABELED_TICKS = "labeled_ticks";

    /**
     * The ID of the plot line width property.
     */
    public static final String PROP_PLOT_LINE_WIDTH = "plot_line_width";

    /**
     * The ID of the transparent property.
     */
    public static final String PROP_TRANSPARENT = "transparent";

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
    private static final String[] AXES_OPTIONS = new String[] { "None", "X-Axis", "Y-Axis", "Both" };

    /**
     * The options for the data point drawing style property.
     */
    private static final String[] DRAWING_STYLE_OPTIONS = new String[] { "Single pixel", "Small plus sign", "Small square", "Diamond" };

    /**
     * The options for the axis scaling property.
     */
    private static final String[] AXIS_SCALING_OPTIONS = new String[] { "Linear", "Logarithmic" };

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
        // Display
        addStringProperty(PROP_LABEL, "Label", WidgetPropertyCategory.DISPLAY, "", true, PROP_TOOLTIP);
        String prop = PROP_LABEL;
        for (int i = 0; i < numberOfDataSeries(); i++) {
            addColorProperty(plotColorPropertyId(i), "Plot color #" + (i + 1), WidgetPropertyCategory.DISPLAY, getDefaultColor(i), false,prop);
            prop = plotColorPropertyId(i);
        }
        addBooleanProperty(PROP_LABELED_TICKS, "Labeled ticks", WidgetPropertyCategory.DISPLAY, true, true, PROP_TOOLTIP);
        addColorProperty(PROP_GRID_LINE_COLOR, "Grid line color", WidgetPropertyCategory.DISPLAY, "#D2D2D2", false, PROP_LABELED_TICKS);
        addIntegerProperty(PROP_PLOT_LINE_WIDTH, "Graph line width", WidgetPropertyCategory.DISPLAY, 1, 1, 100, false, PROP_GRID_LINE_COLOR);
        addArrayOptionProperty(PROP_DATA_POINT_DRAWING_STYLE, "Data point drawing style", WidgetPropertyCategory.DISPLAY, DRAWING_STYLE_OPTIONS,
                2, false, PROP_PLOT_LINE_WIDTH);
        // Format
        addBooleanProperty(PROP_TRANSPARENT, "Transparent background", WidgetPropertyCategory.FORMAT, false, true, PROP_COLOR_BACKGROUND );
        //Scale
        addArrayOptionProperty(PROP_SHOW_AXES, "Show axes", WidgetPropertyCategory.SCALE, AXES_OPTIONS, 3, false);
        addArrayOptionProperty(PROP_SHOW_GRID_LINES, "Grid lines", WidgetPropertyCategory.SCALE, AXES_OPTIONS, 0, false);
        addBooleanProperty(PROP_LINE_CHART, "Line chart", WidgetPropertyCategory.SCALE, false, false);
        addDoubleProperty(PROP_MIN, "Minimum", WidgetPropertyCategory.SCALE, -100.0, false);
        addDoubleProperty(PROP_MAX, "Maximum", WidgetPropertyCategory.SCALE, 100.0, false);
        addBooleanProperty(PROP_AUTOSCALE, "Automatic scaling", WidgetPropertyCategory.SCALE, false, false);
        addArrayOptionProperty(PROP_Y_AXIS_SCALING, "Y-axis scaling", WidgetPropertyCategory.SCALE, AXIS_SCALING_OPTIONS, 0, false);
        addStringProperty(PROP_X_AXIS_LABEL, "X-axis label", WidgetPropertyCategory.SCALE, "", false);
        addStringProperty(PROP_Y_AXIS_LABEL, "Y-axis label", WidgetPropertyCategory.SCALE, "", false);
    }

    /**
     * @param i
     * @return
     */
    private static String getDefaultColor(final int colorChooser) {
        final String[] colors = new String[] {"#0000FF","#008000","#ff0000","#ffff00","#ff8800","#8000ff","#00E0FF","#00ff00"} ;
        int i = colorChooser%colors.length;
        return colors[i];
    }

    /**
     * Returns the number of data series that this model supports. Subclasses
     * must implement this method so that it returns a constant value.
     *
     * @return the number of data series that this model supports.
     */
    public abstract int numberOfDataSeries();

    /**
     * Returns whether automatic scaling for the y-axis is enabled.
     *
     * @return boolean <code>true</code> if automatic scaling is enabled,
     *         <code>false</code> otherwise.
     */
    public final boolean getAutoscale() {
        return getBooleanProperty(PROP_AUTOSCALE);
    }

    /**
     * Returns the minimum value.
     *
     * @return double The minimum value
     */
    public final double getMin() {
        return getDoubleProperty(PROP_MIN);
    }

    /**
     * Returns the maximum value.
     *
     * @return double The maximum value
     */
    public final double getMax() {
        return getDoubleProperty(PROP_MAX);
    }

    /**
     * Returns whether the ticks should be labeled.
     *
     * @return boolean <code>true</code> if the ticks should be labeled,
     *         <code>false</code> otherwise.
     */
    public final boolean isLabeledTicksEnabled() {
        return getBooleanProperty(PROP_LABELED_TICKS);
    }

    /**
     * Returns which axes should be shown.
     *
     * @return 0 for none, 1 for x-axis, 2 for y-axis, 3 for both.
     */
    public final int getShowAxes() {
        return getArrayOptionProperty(PROP_SHOW_AXES);
    }

    /**
     * Returns for which axes grid lines should be shown.
     *
     * @return 0 for none, 1 for x-axis, 2 for y-axis, 3 for both.
     */
    public final int getShowGridLines() {
        return getArrayOptionProperty(PROP_SHOW_GRID_LINES);
    }

    /**
     * Returns whether the chart should be drawn as a line chart.
     *
     * @return <code>true</code> if the chart should be drawn as a line chart,
     *         <code>false</code> if only the data points should be drawn.
     */
    public final boolean isLineChart() {
        return getBooleanProperty(PROP_LINE_CHART);
    }

    /**
     * Returns the width of the lines of the chart.
     *
     * @return the width of the lines in pixels.
     */
    public final int getPlotLineWidth() {
        return getIntegerProperty(PROP_PLOT_LINE_WIDTH);
    }

    /**
     * Returns whether the background of the chart is transparent.
     *
     * @return <code>true</code> if the background is transparent,
     *         <code>false</code> otherwise.
     */
    public final boolean isTransparent() {
        return getBooleanProperty(PROP_TRANSPARENT);
    }

    /**
     * Returns the data point drawing style. 0 = single pixel, 1 = small plus
     * sign, 2 = small square, 3 = diamond.
     *
     * @return the data point drawing style.
     */
    public final int getDataPointDrawingStyle() {
        return getArrayOptionProperty(PROP_DATA_POINT_DRAWING_STYLE);
    }

    /**
     * Returns the setting of the y-axis scaling property.
     *
     * @return the y-axis scaling. 0 = linear, 1 = logarithmic.
     */
    public final int getYAxisScaling() {
        return getArrayOptionProperty(PROP_Y_AXIS_SCALING);
    }

    /**
     * Returns the label.
     *
     * @return the label.
     */
    public final String getLabel() {
        return getStringProperty(PROP_LABEL);
    }

    /**
     * Returns the x-axis label.
     *
     * @return the x-axis label.
     */
    public final String getXAxisLabel() {
        return getStringProperty(PROP_X_AXIS_LABEL);
    }

    /**
     * Returns the y-axis label.
     *
     * @return the y-axis label.
     */
    public final String getYAxisLabel() {
        return getStringProperty(PROP_Y_AXIS_LABEL);
    }

    /**
     * <p>
     * Returns the property ID of the data color property for the data with the
     * specified index.
     * </p>
     *
     * <p>
     * The valid range of the index depends on the concrete widget type and is
     * defined by the concrete subclass. Clients must not call this method with
     * an index outside the range of valid indices. If this method is called
     * with an invalid index, it will return a property ID which is not defined
     * for the concrete widget type.
     * </p>
     *
     * @param index
     *            the data index.
     * @return the property ID.
     */
    public static String plotColorPropertyId(final int index) {
        // Note: we cannot check here whether the index is valid because the
        // valid range is defined by the subclass and Java does not have virtual
        // static methods.

        return INTERNAL_PROP_PLOT_COLOR + Integer.toString(index + 1);
    }

}
