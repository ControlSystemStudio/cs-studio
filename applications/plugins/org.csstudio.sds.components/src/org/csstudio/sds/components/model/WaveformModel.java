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

import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * This class defines a simple waverform widget model.
 *
 * @author Joerg Rathlev, Sven Wende, Kai Meyer
 * @version $Revision: 1.35 $
 *
 */
public final class WaveformModel extends AbstractChartModel {

    /**
     * The number of data arrays this model supports.
     */
    public static final int NUMBER_OF_ARRAYS = 4;

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Waveform"; //$NON-NLS-1$

    /**
     * The base property ID for the data properties. Use the
     * {@link #dataPropertyId(int)} method to get the property ID for the value
     * of a specific data series.
     */
    private static final String INTERNAL_PROP_DATA = "data";

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
     *            the data index. The valid range for the index is
     *            <code>0 &lt;= index &lt; NUMBER_OF_ARRAYS</code>.
     * @return the property ID.
     * @throws IndexOutOfBoundsException
     *             if the index is invalid.
     */
    public static String dataPropertyId(final int index) {
        if ((index < 0) || (index >= NUMBER_OF_ARRAYS)) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        return INTERNAL_PROP_DATA + Integer.toString(index + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int numberOfDataSeries() {
        return NUMBER_OF_ARRAYS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureProperties() {
        super.configureProperties();

        // The waveform data properties
        for (int i = 0; i < numberOfDataSeries(); i++) {
            addDoubleArrayProperty(dataPropertyId(i), "Data #" + (i + 1), WidgetPropertyCategory.DISPLAY, new double[0], true, plotColorPropertyId(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * Returns the waveform data for the specified index.
     *
     * @param index
     *            the data index. The valid range for the index is
     *            <code>0 &lt;= index &lt; NUMBER_OF_ARRAYS</code>.
     * @return the waveform data array.
     * @throws IndexOutOfBoundsException
     *             if the index is invalid.
     */
    public double[] getData(final int index) {
        return getDoubleArrayProperty(dataPropertyId(index));
    }

}
