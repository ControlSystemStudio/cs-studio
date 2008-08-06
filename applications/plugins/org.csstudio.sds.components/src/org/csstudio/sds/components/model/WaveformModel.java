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
package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.DoubleArrayProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.ArrayOptionProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines a simple waverform widget model.
 * 
 * @author Joerg Rathlev, Sven Wende, Kai Meyer
 * @version $Revision$
 * 
 */
public final class WaveformModel extends AbstractWidgetModel {
	
	/**
	 * The number of data arrays this model supports.
	 */
	public static final int NUMBER_OF_ARRAYS = 4;
	
	/**
	 * Property ID for the <i>autoscale</i> setting.
	 */
	public static final String PROP_AUTO_SCALE = "autoscale"; //$NON-NLS-1$
	
	/**
	 * Property ID for the <i>minimum</i> scale value.
	 */
	public static final String PROP_MIN = "min"; //$NON-NLS-1$
	
	/**
	 * Property ID for the <i>maximum</i> scale value.
	 */
	public static final String PROP_MAX = "max"; //$NON-NLS-1$

	/**
	 * Property ID for the scale.
	 */
	public static final String PROP_SHOW_VALUES = "show_values"; //$NON-NLS-1$
	
	/**
	 * Property ID for the scale.
	 */
	public static final String PROP_SHOW_SCALE = "show_scale"; //$NON-NLS-1$

	/**
	 * Property ID for the grid lines property.
	 */
	public static final String PROP_SHOW_GRID_LINES = "show_ledger_lines"; //$NON-NLS-1$

	/**
	 * Property ID for the point-lines.
	 */
	public static final String PROP_SHOW_CONNECTION_LINES = "show_connection_lines"; //$NON-NLS-1$
	
	/**
	 * Property ID for the width of the connection lines.
	 */
	public static final String PROP_GRAPH_LINE_WIDTH = "connection_line_width"; //$NON-NLS-1$

	/**
	 * Property ID for the color of the connection lines.
	 * @deprecated This property is no longer used.
	 */
	@Deprecated
	public static final String PROP_CONNECTION_LINE_COLOR = "connection_lines_color"; //$NON-NLS-1$

	/**
	 * Property ID for the color of the grid lines.
	 */
	public static final String PROP_GRID_LINE_COLOR = "ledger_lines_color"; //$NON-NLS-1$
	
	/**
	 * Property ID for the maximum number of tickmarks to show on the x-axis.
	 * @deprecated This property is no longer used.
	 */
	@Deprecated
	public static final String PROP_X_AXIS_MAX_TICKMARKS = "x_axis_max_tickmarks"; //$NON-NLS-1$
	
	/**
	 * The ID of the transparent property.
	 */
	public static final String PROP_TRANSPARENT = "transparency"; //$NON-NLS-1$
	
	/**
	 * The ID of the data point drawing style property.
	 */
	public static final String PROP_DATA_POINT_DRAWING_STYLE = "data_point_drawing_style";
	
	/**
	 * The ID for the y-axis scaling property.
	 */
	public static final String PROP_Y_AXIS_SCALING = "y_axis_scaling";
	
	/**
	 * The ID for the label property.
	 */
	public static final String PROP_LABEL = "label";
	
	/**
	 * The ID for the x-axis label property.
	 */
	public static final String PROP_X_AXIS_LABEL = "x_axis_label";

	/**
	 * The ID for the y-axis label property.
	 */
	public static final String PROP_Y_AXIS_LABEL = "y_axis_label";

	/**
	 * The display options (0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both).
	 */
	private static final String[] DISPLAY_OPTIONS = new String[] { "None",
			"X-axis", "Y-axis", "Both" };
	
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
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Waveform"; //$NON-NLS-1$
    
    /**
     * Internal array of the property IDs for the data properties.
     * Implementation note: the array is private to prevent modifications by
     * clients (arrays cannot be immutable in Java). Clients should call
     * {@link #dataPropertyId(int)} to request a property ID.
     */
    private static final String[] INTERNAL_DATA_PROPERTY_ID = {
    	"wave", "wave2", "wave3", "wave4"
    };
    
    /**
     * Internal array of the property IDs for the plot color properties.
     * Implementation note: the array is private to prevent modifications by
     * clients (arrays cannot be immutable in Java). Clients should call
     * {@link #plotColorPropertyId(int)} to request a property ID.
     */
    private static final String[] INTERNAL_PLOT_COLOR_PROPERTY_ID = {
    	"graph_color", "graph_color_wave2", "graph_color_wave3",
    	"graph_color_wave4" 
    };

	/**
	 * Constructor.
	 */
	public WaveformModel() {
		setSize(100, 60);
	}
	
	/**
	 * Returns the property ID for the waveform data with the specified index.
	 * 
	 * @param index
	 *            the data index. The index must be in the range
	 *            <code>0..NUMBER_OF_ARRAYS</code>.
	 * @return the property ID.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 */
	public static String dataPropertyId(final int index) {
		return INTERNAL_DATA_PROPERTY_ID[index];
	}

	/**
	 * Returns the property ID for the plot color with the specified index.
	 * 
	 * @param index
	 *            the data index. The index must be in the range
	 *            <code>0..NUMBER_OF_ARRAYS</code>.
	 * @return the property ID.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 */
	public static String plotColorPropertyId(final int index) {
		return INTERNAL_PLOT_COLOR_PROPERTY_ID[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleSeqTestProperty() {
		return dataPropertyId(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(dataPropertyId(0), new DoubleArrayProperty("Data #1",
				WidgetPropertyCategory.Behaviour, new double[] { 20.0, 15.0,
						33.0, 44.0, 22.0, 3.0, 25.0, 4.0 }));
		addProperty(dataPropertyId(1), new DoubleArrayProperty("Data #2",
				WidgetPropertyCategory.Behaviour, new double[] { }));
		addProperty(dataPropertyId(2), new DoubleArrayProperty("Data #3",
				WidgetPropertyCategory.Behaviour, new double[] { }));
		addProperty(dataPropertyId(3), new DoubleArrayProperty("Data #4",
				WidgetPropertyCategory.Behaviour, new double[] { }));
		addProperty(PROP_SHOW_SCALE, new ArrayOptionProperty("Scale",
				WidgetPropertyCategory.Display, DISPLAY_OPTIONS, 0));
		addProperty(PROP_SHOW_GRID_LINES, new ArrayOptionProperty("Grid lines",
				WidgetPropertyCategory.Display, DISPLAY_OPTIONS, 0));
		addProperty(PROP_SHOW_CONNECTION_LINES, new BooleanProperty(
				"Show connection lines", WidgetPropertyCategory.Display, false));
		addProperty(plotColorPropertyId(0), new ColorProperty("Plot color #1",
				WidgetPropertyCategory.Display, new RGB(0, 0, 0)));
		addProperty(plotColorPropertyId(1), new ColorProperty("Plot color #2",
				WidgetPropertyCategory.Display, new RGB(0, 0, 0)));
		addProperty(plotColorPropertyId(2), new ColorProperty("Plot color #3",
				WidgetPropertyCategory.Display, new RGB(0, 0, 0)));
		addProperty(plotColorPropertyId(3), new ColorProperty("Plot color #4",
				WidgetPropertyCategory.Display, new RGB(0, 0, 0)));
		addProperty(PROP_GRID_LINE_COLOR, new ColorProperty(
				"Color grid lines", WidgetPropertyCategory.Display, new RGB(
						210, 210, 210)));
		addProperty(PROP_MIN, new DoubleProperty(
				"Minimum", WidgetPropertyCategory.Display, -100.0));
		addProperty(PROP_MAX, new DoubleProperty(
				"Maximum", WidgetPropertyCategory.Display, 100.0));
		addProperty(PROP_AUTO_SCALE, new BooleanProperty(
				"Automatic Scaling", WidgetPropertyCategory.Display, false));
		addProperty(PROP_SHOW_VALUES, new BooleanProperty(
				"Show values", WidgetPropertyCategory.Display,false));
		addProperty(PROP_GRAPH_LINE_WIDTH, new IntegerProperty(
				"Graph line width",WidgetPropertyCategory.Display,1,1,100));
		addProperty(PROP_TRANSPARENT, new BooleanProperty("Transparent Background", 
				WidgetPropertyCategory.Display, false));
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
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * Return the waveform data.
	 * 
	 * @return The waveform data.
	 * @deprecated use {@link #getData(int)} instead.
	 */
	@Deprecated
	public double[] getData() {
		return getData(0);
	}
	
	/**
	 * Returns the waveform data for the specified index.
	 * 
	 * @param index
	 *            the zero-based index of the waveform data. Must be in range
	 *            0..3.
	 * @return the waveform data array.
	 */
	public double[] getData(final int index) {
		return (double[]) getProperty(dataPropertyId(index)).getPropertyValue();
	}

	/**
	 * Returns the minimum value.
	 * @return	double
	 * 				The minimum value
	 */
	public double getMin() {
		return (Double) getProperty(PROP_MIN).getPropertyValue();
	}

	/**
	 * Returns the maximum value.
	 * @return	double
	 * 				The maximum value
	 */
	public double getMax() {
		return (Double) getProperty(PROP_MAX).getPropertyValue();
	}

	/**
	 * Returns, if the graph should be automatically scaled.
	 * @return boolean
	 * 				True, if the graph should be automatically scaled, false otherwise
	 */
	public boolean getAutoscale() {
		return (Boolean) getProperty(PROP_AUTO_SCALE).getPropertyValue();
	}

	/**
	 * Return the RGB for the color of the graph.
	 * 
	 * @return RGB The RGB for the color of the graph
	 * @deprecated use {@link #getPlotColor(int)} instead.
	 */
	@Deprecated
	public RGB getGraphColor() {
		return getPlotColor(0);
	}
	
	/**
	 * Returns the color for plotting the specified data.
	 * 
	 * @param dataIndex
	 *            the index of the waveform data. Must be in range 0..3.
	 * @return the color for the plot.
	 */
	public RGB getPlotColor(final int dataIndex) {
		return (RGB) getProperty(plotColorPropertyId(dataIndex)).getPropertyValue();
	}

	/**
	 * Return the RGB for the color of the ledger lines.
	 * 
	 * @return RGB The RGB for the color of the ledger lines
	 */
	public RGB getLedgerLineColor() {
		return (RGB) getProperty(PROP_GRID_LINE_COLOR).getPropertyValue();
	}

	/**
	 * Return the RGB for the color of the connection lines.
	 * 
	 * @return RGB The RGB for the color of the connection lines
	 * @deprecated use {@link getPlotColor(int)} instead.
	 */
	@Deprecated
	public RGB getConnectionLineColor() {
		return getGraphColor();
	}

	/**
	 * Returns, if the scales should be shown or not.
	 * 
	 * @return int 0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowScale() {
		return (Integer) getProperty(PROP_SHOW_SCALE).getPropertyValue();
	}

	/**
	 * Returns, if the ledger lines should be shown or not.
	 * 
	 * @return int 0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowLedgerLines() {
		return (Integer) getProperty(PROP_SHOW_GRID_LINES).getPropertyValue();
	}

	/**
	 * Returns, if the point lines should be shown or not.
	 * 
	 * @return boolean 
	 * 				True, if they should be shown, false otherwise
	 */
	public boolean getShowConnectionLines() {
		return (Boolean) getProperty(PROP_SHOW_CONNECTION_LINES)
				.getPropertyValue();
	}
	
	/**
	 * Returns the width of the lines of the graph.
	 * 
	 * @return int 
	 * 				The width of the lines
	 */
	public int getGraphLineWidth() {
		return (Integer) getProperty(PROP_GRAPH_LINE_WIDTH).getPropertyValue();
	}
	
	/**
	 * Returns, if the values should be shown.
	 * @return boolean 
	 * 				True, if they should be shown, false otherwise
	 */
	public boolean getShowValues() {
		return (Boolean) getProperty(PROP_SHOW_VALUES).getPropertyValue();
	}
	
	/**
	 * Returns if the background is transparent.
	 * 
	 * @return The state of the background.
	 */
	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	/**
	 * Returns the data point drawing style. 0 = single pixel, 1 = small plus
	 * sign, 2 = small square, 3 = diamond.
	 * 
	 * @return the data point drawing style.
	 */
	public int getDataPointDrawingStyle() {
		return (Integer) getProperty(PROP_DATA_POINT_DRAWING_STYLE).getPropertyValue();
	}

	/**
	 * Returns the setting of the y-axis scaling property.
	 * 
	 * @return the y-axis scaling.
	 */
	public int getYAxisScaling() {
		return getProperty(PROP_Y_AXIS_SCALING).getPropertyValue();
	}
	
	/**
	 * Returns the label.
	 * 
	 * @return the label.
	 */
	public String getLabel() {
		return getProperty(PROP_LABEL).getPropertyValue();
	}
	
	/**
	 * Returns the x-axis label.
	 * 
	 * @return the x-axis label.
	 */
	public String getXAxisLabel() {
		return getProperty(PROP_X_AXIS_LABEL).getPropertyValue();
	}
	
	/**
	 * Returns the y-axis label.
	 * 
	 * @return the y-axis label.
	 */
	public String getYAxisLabel() {
		return getProperty(PROP_Y_AXIS_LABEL).getPropertyValue();
	}

}
