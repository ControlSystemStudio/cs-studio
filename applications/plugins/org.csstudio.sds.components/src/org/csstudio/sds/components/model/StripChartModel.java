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

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.DoubleProperty;

/**
 * Model for strip chart widgets.
 * 
 * @author Joerg Rathlev
 */
public final class StripChartModel extends AbstractChartModel {

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.StripChart";
	
	/**
	 * The number of data channels that this model supports.
	 */
	public static final int NUMBER_OF_CHANNELS = 4;

	/**
	 * The ID of the x-axis range property.
	 */
	public static final String PROP_X_AXIS_RANGE = "x_axis_range";

	/**
	 * The ID of the update interval property.
	 */
	public static final String PROP_UPDATE_INTERVAL = "update_interval";

	/**
	 * The base property ID for the value properties. Use the
	 * {@link #valuePropertyId(int)} method to get the property ID for the
	 * value of a specific data series. 
	 */
	private static final String INTERNAL_PROP_VALUE = "value";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int numberOfDataSeries() {
		return NUMBER_OF_CHANNELS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		super.configureProperties();
		
		// The value properties. The strip chart reads only a single value from
		// the channel. The edit part is responsible for aggregating the values
		// into an array an forward that array to the figure.
		for (int i = 0; i < numberOfDataSeries(); i++) {
			addProperty(valuePropertyId(i), new DoubleProperty(
					"Value #" + (i+1), WidgetPropertyCategory.Behaviour,
					0.0));
		}
		
		// The minimum range (size) of the x-axis is one second; the maximum is
		// practically unbounded.
		addProperty(PROP_X_AXIS_RANGE, new DoubleProperty(
				"X-axis range (seconds)", WidgetPropertyCategory.Behaviour,
				300.0, 1.0, Double.MAX_VALUE));
		
		// The minimum update interval is 10 milliseconds. This minimum was
		// picked for no specific reason and may need to be adjusted.
		addProperty(PROP_UPDATE_INTERVAL, new DoubleProperty(
				"Update interval (seconds)", WidgetPropertyCategory.Behaviour,
				1.0, 0.01, Double.MAX_VALUE));
	}

	/**
	 * Returns the current value of the value property with the specified index.
	 * 
	 * @param index
	 *            the value property index.
	 * @return the current value of the property.
	 */
	public double getCurrentValue(final int index) {
		return getProperty(valuePropertyId(index)).getPropertyValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * Returns the property ID of the value property for the data series with
	 * the specified index.
	 * 
	 * @param index
	 *            the data index.
	 * @return the property ID.
	 */
	public static String valuePropertyId(final int index) {
		return INTERNAL_PROP_VALUE + Integer.toString(index + 1);
	}

}
