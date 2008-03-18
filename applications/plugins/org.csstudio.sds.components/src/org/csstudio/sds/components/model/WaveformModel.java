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
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines a simple waverform widget model.
 * 
 * @author Sven Wende, Kai Meyer
 * @version $Revision$
 * 
 */
public final class WaveformModel extends AbstractWidgetModel {
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
	 * Property ID for the <i>wave</i>.
	 */
	public static final String PROP_WAVE_FORM = "wave"; //$NON-NLS-1$

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
	 * Property ID for the color of the graph.
	 */
	public static final String PROP_GRAPH_COLOR = "graph_color"; //$NON-NLS-1$

	/**
	 * Property ID for the color of the connection lines.
	 */
	public static final String PROP_CONNECTION_LINE_COLOR = "connection_lines_color"; //$NON-NLS-1$

	/**
	 * Property ID for the color of the grid lines.
	 */
	public static final String PROP_GRID_LINE_COLOR = "ledger_lines_color"; //$NON-NLS-1$
	
	/**
	 * Property ID for the maximum number of tickmarks to show on the y-axis.
	 */
	public static final String PROP_Y_AXIS_MAX_TICKMARKS = "y_axis_max_tickmarks"; //$NON-NLS-1$
	
	/**
	 * Property ID for the maximum number of tickmarks to show on the x-axis.
	 */
	public static final String PROP_X_AXIS_MAX_TICKMARKS = "x_axis_max_tickmarks"; //$NON-NLS-1$
	
	/**
	 * The ID of the transparent property.
	 */
	public static final String PROP_TRANSPARENT = "transparency"; //$NON-NLS-1$

	/**
	 * The display options (0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both).
	 */
	private static final String[] DISPLAY_OPTIONS = new String[] { "None",
			"X-axis", "Y-axis", "Both" };

	/**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Waveform"; //$NON-NLS-1$

	/**
	 * Constructor.
	 */
	public WaveformModel() {
		setSize(100, 60);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleSeqTestProperty() {
		return PROP_WAVE_FORM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_WAVE_FORM, new DoubleArrayProperty("Waveform Array",
				WidgetPropertyCategory.Behaviour, new double[] { 20.0, 15.0,
						33.0, 44.0, 22.0, 3.0, 25.0, 4.0 }));
		addProperty(PROP_SHOW_SCALE, new ArrayOptionProperty("Scale",
				WidgetPropertyCategory.Display, DISPLAY_OPTIONS, 0));
		addProperty(PROP_SHOW_GRID_LINES, new ArrayOptionProperty("Grid lines",
				WidgetPropertyCategory.Display, DISPLAY_OPTIONS, 0));
		addProperty(PROP_SHOW_CONNECTION_LINES, new BooleanProperty(
				"Show connection lines", WidgetPropertyCategory.Display, false));
		addProperty(PROP_GRAPH_COLOR, new ColorProperty("Color graph",
				WidgetPropertyCategory.Display, new RGB(255, 0, 0)));
		addProperty(PROP_CONNECTION_LINE_COLOR, new ColorProperty(
				"Color connection line", WidgetPropertyCategory.Display,
				new RGB(255, 100, 100)));
		addProperty(PROP_GRID_LINE_COLOR, new ColorProperty(
				"Color ledger lines", WidgetPropertyCategory.Display, new RGB(
						210, 210, 210)));
		addProperty(PROP_MIN, new DoubleProperty(
				"Minimum", WidgetPropertyCategory.Display, -100.0));
		addProperty(PROP_MAX, new DoubleProperty(
				"Maximum", WidgetPropertyCategory.Display, 100.0));
		addProperty(PROP_AUTO_SCALE, new BooleanProperty(
				"Automatic Scaling", WidgetPropertyCategory.Display, false));
		addProperty(PROP_SHOW_VALUES, new BooleanProperty(
				"Show values", WidgetPropertyCategory.Display,false));
		addProperty(PROP_Y_AXIS_MAX_TICKMARKS, new IntegerProperty(
				"Y-axis max. number of tickmarks",WidgetPropertyCategory.Display,10));
		addProperty(PROP_X_AXIS_MAX_TICKMARKS, new IntegerProperty(
				"X-axis max. number of tickmarks",WidgetPropertyCategory.Display,10));
		addProperty(PROP_GRAPH_LINE_WIDTH, new IntegerProperty(
				"Graph line width",WidgetPropertyCategory.Display,1,1,100));
		addProperty(PROP_TRANSPARENT, new BooleanProperty("Transparent Background", 
				WidgetPropertyCategory.Display, false));
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
	 */
	public double[] getData() {
		return (double[]) getProperty(PROP_WAVE_FORM).getPropertyValue();
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
	 */
	public RGB getGraphColor() {
		return (RGB) getProperty(PROP_GRAPH_COLOR).getPropertyValue();
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
	 */
	public RGB getConnectionLineColor() {
		return (RGB) getProperty(PROP_CONNECTION_LINE_COLOR).getPropertyValue();
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
	 * Returns the count of sections on the y-axis.
	 * @return int 
	 * 				The count of sections on the y-axis
	 */
	public int getYSectionCount() {
		return (Integer) getProperty(PROP_Y_AXIS_MAX_TICKMARKS)
				.getPropertyValue();
	}
	
	/**
	 * Returns the count of sections on the x-axis.
	 * @return int 
	 * 				The count of sections on the x-axis
	 */
	public int getXSectionCount() {
		return (Integer) getProperty(PROP_X_AXIS_MAX_TICKMARKS)
				.getPropertyValue();
	}
	
	/**
	 * Returns if the background is transparent.
	 * 
	 * @return The state of the background.
	 */
	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}

}
