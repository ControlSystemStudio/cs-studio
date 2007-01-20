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

import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.model.PropertyCategory;
import org.csstudio.sds.model.properties.PropertyTypeRegistry;

/**
 * A meter element model.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class MeterElement extends AbstractElementModel {

	/**
	 * The default value.
	 */
	private static final double VALUE_DEFAULT = 38.0;

	/**
	 * The default upper border of interval 3.
	 */
	private static final double INTERVAL3_UPPER_BORDER_DEFAULT = 60.0;

	/**
	 * The default upper border of interval 2.
	 */
	private static final double INTERVAL2_UPPER_BORDER_DEFAULT = 50.0;

	/**
	 * The default upper border of interval 1.
	 */
	private static final double INTERVAL1_UPPER_BORDER_DEFAULT = 35.0;

	/**
	 * The default lower border of interval 3.
	 */
	private static final double INTERVAL3_LOWER_BORDER_DEFAULT = 50.0;

	/**
	 * The default lower border of interval 2.
	 */
	private static final double INTERVAL2_LOWER_BORDER_DEFAULT = 35.0;

	/**
	 * The default lower border of interval 1.
	 */
	private static final double INTERVAL1_LOWER_BORDER_DEFAULT = 0.0;

	/**
	 * The property id for the interval 1 lower border setting.
	 */
	public static final String PROP_INTERVAL1_LOWER_BORDER = "meter.PROP_INTERVAL1_LOWER_BORDER"; //$NON-NLS-1$

	/**
	 * The property id for the interval 1 upper border setting.
	 */
	public static final String PROP_INTERVAL1_UPPER_BORDER = "meter.PROP_INTERVAL1_UPPER_BORDER"; //$NON-NLS-1$

	/**
	 * The property id for the interval 2 lower border setting.
	 */
	public static final String PROP_INTERVAL2_LOWER_BORDER = "meter.PROP_INTERVAL2_LOWER_BORDER"; //$NON-NLS-1$

	/**
	 * The property id for the interval 2 upper border setting.
	 */
	public static final String PROP_INTERVAL2_UPPER_BORDER = "meter.PROP_INTERVAL2_UPPER_BORDER"; //$NON-NLS-1$

	/**
	 * The property id for the interval 3 lower border setting.
	 */
	public static final String PROP_INTERVAL3_LOWER_BORDER = "meter.PROP_INTERVAL3_LOWER_BORDER"; //$NON-NLS-1$

	/**
	 * The property id for the interval 3 upper border setting.
	 */
	public static final String PROP_INTERVAL3_UPPER_BORDER = "meter.PROP_INTERVAL3_UPPER_BORDER"; //$NON-NLS-1$

	/**
	 * The property id for the value setting.
	 */
	public static final String PROP_VALUE = "meter.PROP_VALUE"; //$NON-NLS-1$

	/**
	 * The ID of this model element.
	 */
	public static final String ID = "element.meter"; //$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 40;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 40;

	/**
	 * Standard constructor.
	 */
	public MeterElement() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_INTERVAL1_LOWER_BORDER, PropertyTypeRegistry.DOUBLE,
				"Interval 1 lower border", PropertyCategory.Display,
				INTERVAL1_LOWER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL1_UPPER_BORDER, PropertyTypeRegistry.DOUBLE,
				"Interval 1 upper border", PropertyCategory.Display,
				INTERVAL1_UPPER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL2_LOWER_BORDER, PropertyTypeRegistry.DOUBLE,
				"Interval 2 lower border", PropertyCategory.Display,
				INTERVAL2_LOWER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL2_UPPER_BORDER, PropertyTypeRegistry.DOUBLE,
				"Interval 2 upper border", PropertyCategory.Display,
				INTERVAL2_UPPER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL3_LOWER_BORDER, PropertyTypeRegistry.DOUBLE,
				"Interval 3 lower border", PropertyCategory.Display,
				INTERVAL3_LOWER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL3_UPPER_BORDER, PropertyTypeRegistry.DOUBLE,
				"Interval 3 upper border", PropertyCategory.Display,
				INTERVAL3_UPPER_BORDER_DEFAULT);

		addProperty(PROP_VALUE, PropertyTypeRegistry.DOUBLE, "value",
				PropertyCategory.Behaviour, VALUE_DEFAULT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_VALUE;
	}
}
