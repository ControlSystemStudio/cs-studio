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
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.DoubleArrayProperty;
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines a simple waverform widget model.
 * 
 * @author Joerg Rathlev, Sven Wende, Kai Meyer
 * @version $Revision$
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
     * Internal array of the property IDs for the data properties.
     * Implementation note: the array is private to prevent modifications by
     * clients (arrays cannot be immutable in Java). Clients should call
     * {@link #dataPropertyId(int)} to request a property ID.
     */
    private static final String[] INTERNAL_DATA_PROPERTY_ID = {
    	"wave", "wave2", "wave3", "wave4"
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
	public int numberOfDataSeries() {
		return NUMBER_OF_ARRAYS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(dataPropertyId(0), new DoubleArrayProperty("Data #1",
				WidgetPropertyCategory.Behaviour, new double[] { 20.0, 15.0,
						33.0, 44.0, 22.0, 3.0, 25.0, 4.0 }));
		addProperty(dataPropertyId(1), new DoubleArrayProperty("Data #2",
				WidgetPropertyCategory.Behaviour, new double[] { }));
		addProperty(dataPropertyId(2), new DoubleArrayProperty("Data #3",
				WidgetPropertyCategory.Behaviour, new double[] { }));
		addProperty(dataPropertyId(3), new DoubleArrayProperty("Data #4",
				WidgetPropertyCategory.Behaviour, new double[] { }));
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
	 *            the zero-based index of the waveform data. Must be in range
	 *            <code>0..NUMBER_OF_ARRAYS</code>.
	 * @return the waveform data array.
	 */
	public double[] getData(final int index) {
		return (double[]) getProperty(dataPropertyId(index)).getPropertyValue();
	}

}
