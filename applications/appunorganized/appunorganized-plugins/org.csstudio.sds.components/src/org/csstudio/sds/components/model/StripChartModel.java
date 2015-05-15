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
    public static final int NUMBER_OF_CHANNELS = 8;

    /**
     * The ID of the x-axis timespan property.
     */
    public static final String PROP_X_AXIS_TIMESPAN = "x_axis_timespan";

    /**
     * The ID of the update interval property.
     */
    public static final String PROP_UPDATE_INTERVAL = "update_interval";

    /**
     * The base property ID for the value properties. Use the
     * {@link #valuePropertyId(int)} method to get the property ID for the value
     * of a specific data series.
     */
    private static final String INTERNAL_PROP_VALUE = "value";

    /**
     * The base property ID for the enable plot properties. Use the
     * {@link #enablePlotPropertyId(int)} method to get the property ID for the
     * property of a specific data series.
     */
    private static final String INTERNAL_PROP_ENABLE_PLOT = "enable_plot";

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

        // The value properties.
        for (int i = 0; i < numberOfDataSeries(); i++) {
            // By default, enable the plot for the first value, and disable
            // all other plots:
            boolean enableByDefault = (i == 0);
            addBooleanProperty(enablePlotPropertyId(i), "Enable plot #" + (i + 1), WidgetPropertyCategory.DISPLAY, enableByDefault, true,plotColorPropertyId(i));
            // The value property is a double property because the strip chart
            // reads only individual values from the channel. They will be
            // aggregated into an array by the edit part.
            addDoubleProperty(valuePropertyId(i), "Value #" + (i + 1), WidgetPropertyCategory.DISPLAY, 0.0, false,enablePlotPropertyId(i));

        }

        // The minimum range (size) of the x-axis is one second; the maximum is
        // practically unbounded.
        addDoubleProperty(PROP_X_AXIS_TIMESPAN, "X-axis timespan (seconds)", WidgetPropertyCategory.BEHAVIOR, 300.0, 1.0, Double.MAX_VALUE, false);

        // The minimum update interval is 100 milliseconds. Shorter update
        // intervals may cause problems because the widget redraws after every
        // update. The maximum interval is 60 seconds.
        addDoubleProperty(PROP_UPDATE_INTERVAL, "Update interval (seconds)", WidgetPropertyCategory.BEHAVIOR, 1.0, 0.1, 60.0, false);
    }

    /**
     * Returns the current value of the value property with the specified index.
     *
     * @param index
     *            the value property index. The valid range for the index is
     *            <code>0 &lt;= index &lt; NUMBER_OF_CHANNELS</code>.
     * @return the current value of the property.
     * @throws IndexOutOfBoundsException
     *             if the index is invalid.
     */
    public double getCurrentValue(final int index) {
        return getDoubleProperty(valuePropertyId(index));
    }

    /**
     * Returns whether the data series with the specified index should be
     * plotted.
     *
     * @param index
     *            the data index.
     * @return <code>true</code> if the plot is enabled, <code>false</code>
     *         otherwise.
     * @throws IndexOutOfBoundsException
     *             if the index is invalid.
     */
    public boolean isPlotEnabled(final int index) {
        return getBooleanProperty(enablePlotPropertyId(index));
    }

    /**
     * Returns the timespan displayed on the x-axis in seconds.
     *
     * @return x-axis timespan in seconds.
     */
    public double getXAxisTimespan() {
        return getDoubleProperty(PROP_X_AXIS_TIMESPAN);
    }

    /**
     * Returns the update interval in seconds.
     *
     * @return the update interval in seconds.
     */
    public double getUpdateInterval() {
        return getDoubleProperty(PROP_UPDATE_INTERVAL);
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
     *            the value index. The valid range for the index is
     *            <code>0 &lt;= index &lt; NUMBER_OF_CHANNELS</code>.
     * @return the property ID.
     * @throws IndexOutOfBoundsException
     *             if the index is invalid.
     */
    public static String valuePropertyId(final int index) {
        if ((index < 0) || (index >= NUMBER_OF_CHANNELS)) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        return INTERNAL_PROP_VALUE + Integer.toString(index + 1);
    }

    /**
     * Returns the property ID of the enable plot property for the data series
     * with the specified index.
     *
     * @param index
     *            the value index. The valid range for the index is
     *            <code>0 &lt;= index &lt; NUMBER_OF_CHANNELS</code>.
     * @return the property ID.
     * @throws IndexOutOfBoundsException
     *             if the index is invalid.
     */
    public static String enablePlotPropertyId(final int index) {
        if ((index < 0) || (index >= NUMBER_OF_CHANNELS)) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        return INTERNAL_PROP_ENABLE_PLOT + Integer.toString(index + 1);
    }

}
