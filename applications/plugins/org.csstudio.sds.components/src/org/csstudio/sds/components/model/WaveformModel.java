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
import org.csstudio.sds.model.properties.DoubleArrayProperty;
import org.csstudio.sds.model.properties.OptionProperty;

/**
 * This class defines a simple waverform widget model.
 * 
 * @author Sven Wende
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
	public static final String PROP_SHOW_HELP_LINES = "show_help_lines"; //$NON-NLS-1$
	
	/**
	 * Property ID for the point-lines.
	 */
	public static final String PROP_SHOW_POINT_LINES = "show_point_lines"; //$NON-NLS-1$
	
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
		addProperty(PROP_SHOW_HELP_LINES, new OptionProperty("Help lines", WidgetPropertyCategory.Display, DISPLAY_OPTIONS, 0));
		addProperty(PROP_SHOW_POINT_LINES, new BooleanProperty("Show point lines", WidgetPropertyCategory.Display, false));
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
	 * Returns, if the scales should be shown or not.
	 * @return int
	 * 				0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowScale() {
		return (Integer) getProperty(PROP_SHOW_SCALE).getPropertyValue(); 
	}
	
	/**
	 * Returns, if the help lines should be shown or not.
	 * @return int
	 * 				0 = None; 1 = Vertical; 2 = Horizontal; 3 = Both
	 */
	public int getShowHelpLines() {
		return (Integer) getProperty(PROP_SHOW_HELP_LINES).getPropertyValue(); 
	}
	
	/**
	 * Returns, if the point lines should be shown or not.
	 * @return boolean
	 * 				true, if they should be shown, false otherwise
	 */
	public boolean getShowPointLines() {
		return (Boolean) getProperty(PROP_SHOW_POINT_LINES).getPropertyValue(); 
	}
}
