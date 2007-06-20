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
import org.csstudio.sds.model.properties.DoubleProperty;

/**
 * A meter widget model.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class MeterModel extends AbstractWidgetModel {

	/**
	 * A constant value, which describes the circle radius.
	 */
	private static final double CIRCLE_DEGREES_FROM = 0.0;

	/**
	 * A constant value, which describes the circle radius.
	 */
	private static final double CIRCLE_DEGREES_TO = 360.0;

	/**
	 * The default value.
	 */
	private static final double VALUE_DEFAULT = 180.0;

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
	public static final String PROP_INTERVAL1_LOWER_BORDER = "interval1.lower"; //$NON-NLS-1$

	/**
	 * The property id for the interval 1 upper border setting.
	 */
	public static final String PROP_INTERVAL1_UPPER_BORDER = "interval1.upper"; //$NON-NLS-1$

	/**
	 * The property id for the interval 2 lower border setting.
	 */
	public static final String PROP_INTERVAL2_LOWER_BORDER = "interval2.lower"; //$NON-NLS-1$

	/**
	 * The property id for the interval 2 upper border setting.
	 */
	public static final String PROP_INTERVAL2_UPPER_BORDER = "interval2.upper"; //$NON-NLS-1$

	/**
	 * The property id for the interval 3 lower border setting.
	 */
	public static final String PROP_INTERVAL3_LOWER_BORDER = "interval3.lower"; //$NON-NLS-1$

	/**
	 * The property id for the interval 3 upper border setting.
	 */
	public static final String PROP_INTERVAL3_UPPER_BORDER = "interval3.uppper"; //$NON-NLS-1$

	/**
	 * The property id for the value setting.
	 */
	public static final String PROP_VALUE = "value"; //$NON-NLS-1$

	/**
	 * The ID of this widget model.
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
	public MeterModel() {
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
		addProperty(PROP_INTERVAL1_LOWER_BORDER, new DoubleProperty(
				"Interval 1 lower border", WidgetPropertyCategory.Display,
				INTERVAL1_LOWER_BORDER_DEFAULT, CIRCLE_DEGREES_FROM,
				CIRCLE_DEGREES_TO));
		addProperty(PROP_INTERVAL1_UPPER_BORDER, new DoubleProperty(
				"Interval 1 upper border", WidgetPropertyCategory.Display,
				INTERVAL1_UPPER_BORDER_DEFAULT, CIRCLE_DEGREES_FROM,
				CIRCLE_DEGREES_TO));
		addProperty(PROP_INTERVAL2_LOWER_BORDER, new DoubleProperty(
				"Interval 2 lower border", WidgetPropertyCategory.Display,
				INTERVAL2_LOWER_BORDER_DEFAULT, CIRCLE_DEGREES_FROM,
				CIRCLE_DEGREES_TO));
		addProperty(PROP_INTERVAL2_UPPER_BORDER, new DoubleProperty(
				"Interval 2 upper border", WidgetPropertyCategory.Display,
				INTERVAL2_UPPER_BORDER_DEFAULT, CIRCLE_DEGREES_FROM,
				CIRCLE_DEGREES_TO));
		addProperty(PROP_INTERVAL3_LOWER_BORDER, new DoubleProperty(
				"Interval 3 lower border", WidgetPropertyCategory.Display,
				INTERVAL3_LOWER_BORDER_DEFAULT, CIRCLE_DEGREES_FROM,
				CIRCLE_DEGREES_TO));
		addProperty(PROP_INTERVAL3_UPPER_BORDER, new DoubleProperty(
				"Interval 3 upper border", WidgetPropertyCategory.Display,
				INTERVAL3_UPPER_BORDER_DEFAULT, CIRCLE_DEGREES_FROM,
				CIRCLE_DEGREES_TO));
		addProperty(PROP_VALUE, new DoubleProperty("value",
				WidgetPropertyCategory.Behaviour, VALUE_DEFAULT,
				CIRCLE_DEGREES_FROM, CIRCLE_DEGREES_TO));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_VALUE;
	}

	/**
	 * Return the lower border value of interval #1.
	 * 
	 * @return The lower border value of interval #1.
	 */
	public double getInterval1LowerBorder() {
		return (Double) getProperty(PROP_INTERVAL1_LOWER_BORDER)
				.getPropertyValue();
	}

	/**
	 * Return the upper border value of interval #1.
	 * 
	 * @return The upper border value of interval #1.
	 */
	public double getInterval1UpperBorder() {
		return (Double) getProperty(PROP_INTERVAL1_UPPER_BORDER)
				.getPropertyValue();
	}

	/**
	 * Return the lower border value of interval #2.
	 * 
	 * @return The lower border value of interval #2.
	 */
	public double getInterval2LowerBorder() {
		return (Double) getProperty(PROP_INTERVAL2_LOWER_BORDER)
				.getPropertyValue();
	}

	/**
	 * Return the upper border value of interval #2.
	 * 
	 * @return The upper border value of interval #2.
	 */
	public double getInterval2UpperBorder() {
		return (Double) getProperty(PROP_INTERVAL2_UPPER_BORDER)
				.getPropertyValue();
	}

	/**
	 * Return the lower border value of interval #3.
	 * 
	 * @return The lower border value of interval #3.
	 */
	public double getInterval3LowerBorder() {
		return (Double) getProperty(PROP_INTERVAL3_LOWER_BORDER)
				.getPropertyValue();
	}

	/**
	 * Return the upper border value of interval #3.
	 * 
	 * @return The upper border value of interval #3.
	 */
	public double getInterval3UpperBorder() {
		return (Double) getProperty(PROP_INTERVAL3_UPPER_BORDER)
				.getPropertyValue();
	}

	/**
	 * Return the current meter value.
	 * 
	 * @return The current meter value.
	 */
	public double getValue() {
		return (Double) getProperty(PROP_VALUE).getPropertyValue();
	}
}
