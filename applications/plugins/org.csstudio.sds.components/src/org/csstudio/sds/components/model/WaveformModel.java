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
import org.csstudio.sds.model.properties.OptionProperty;
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
	 * Property ID for the <i>wave</i>.
	 */
	public static final String PROP_WAVE_FORM = "wave"; //$NON-NLS-1$
	
	/**
	 * Property ID for the scale.
	 */
	public static final String PROP_SHOW_SCALE = "show_scale"; //$NON-NLS-1$
	
	/**
	 * Property ID for the help lines.
	 */
	public static final String PROP_SHOW_LEDGER_LINES = "show_ledger_lines"; //$NON-NLS-1$
	
	/**
	 * Property ID for the point-lines.
	 */
	public static final String PROP_SHOW_CONNECTION_LINES = "show_connection_lines"; //$NON-NLS-1$
	
	/**
	 * Property ID for the color of the graph.
	 */
	public static final String PROP_GRAPH_COLOR = "graph_color"; //$NON-NLS-1$
	
	/**
	 * Property ID for the color of the connection lines.
	 */
	public static final String PROP_CONNECTION_LINE_COLOR = "connection_lines_color"; //$NON-NLS-1$
	
	/**
	 * Property ID for the color of the ledger lines.
	 */
	public static final String PROP_LEDGER_LINE_COLOR = "ledger_lines_color"; //$NON-NLS-1$
	
	/**
	 * The diplay options (0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both).
	 */
	private static final String[] DISPLAY_OPTIONS = new String[] {"None", "Vertical", "Horizontal", "Both"};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_WAVE_FORM, new DoubleArrayProperty("Waveform Array",
				WidgetPropertyCategory.Behaviour, new double[] { 20.0, 15.0,
						33.0, 44.0, 22.0, 3.0, 25.0, 4.0 }));
		addProperty(PROP_SHOW_SCALE, new OptionProperty("Scale",WidgetPropertyCategory.Display, DISPLAY_OPTIONS,0));
		addProperty(PROP_SHOW_LEDGER_LINES, new OptionProperty("Ledger lines", WidgetPropertyCategory.Display, DISPLAY_OPTIONS, 0));
		addProperty(PROP_SHOW_CONNECTION_LINES, new BooleanProperty("Show connection lines", WidgetPropertyCategory.Display, false));
		addProperty(PROP_GRAPH_COLOR, new ColorProperty("Color graph", WidgetPropertyCategory.Display, new RGB(255,0,0)));
		addProperty(PROP_CONNECTION_LINE_COLOR, new ColorProperty("Color connection line", WidgetPropertyCategory.Display, new RGB(255,100,100)));
		addProperty(PROP_LEDGER_LINE_COLOR, new ColorProperty("Color ledger lines", WidgetPropertyCategory.Display, new RGB(210,210,210)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return "element.waveform"; //$NON-NLS-1$
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
	 * Return the RGB for the color of the graph.
	 * 
	 * @return RGB 
	 * 			The RGB for the color of the graph
	 */
	public RGB getGraphColor() {
		return (RGB) getProperty(PROP_GRAPH_COLOR).getPropertyValue();
	}
	
	/**
	 * Return the RGB for the color of the ledger lines.
	 * 
	 * @return RGB 
	 * 			The RGB for the color of the ledger lines
	 */
	public RGB getLedgerLineColor() {
		return (RGB) getProperty(PROP_LEDGER_LINE_COLOR).getPropertyValue();
	}
	
	/**
	 * Return the RGB for the color of the connection lines.
	 * 
	 * @return RGB 
	 * 			The RGB for the color of the connection lines
	 */
	public RGB getConnectionLineColor() {
		return (RGB) getProperty(PROP_CONNECTION_LINE_COLOR).getPropertyValue();
	}
	
	/**
	 * Returns, if the scales should be shown or not.
	 * @return int
	 * 				0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowScale() {
		return (Integer) getProperty(PROP_SHOW_SCALE).getPropertyValue(); 
	}
	
	/**
	 * Returns, if the ledger lines should be shown or not.
	 * @return int
	 * 				0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowLedgerLines() {
		return (Integer) getProperty(PROP_SHOW_LEDGER_LINES).getPropertyValue(); 
	}
	
	/**
	 * Returns, if the point lines should be shown or not.
	 * @return boolean
	 * 				true, if they should be shown, false otherwise
	 */
	public boolean getShowConnectionLines() {
		return (Boolean) getProperty(PROP_SHOW_CONNECTION_LINES).getPropertyValue(); 
	}
	
}
