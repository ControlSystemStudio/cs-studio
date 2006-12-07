package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.DataTypeEnum;
import org.csstudio.sds.model.DisplayModelElement;

/**
 * A meter model element.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class MeterElement extends DisplayModelElement {

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
	public static final String PROP_INTERVAL1_LOWER_BORDER = "meter.PROP_INTERVAL1_LOWER_BORDER";

	/**
	 * The property id for the interval 1 upper border setting.
	 */
	public static final String PROP_INTERVAL1_UPPER_BORDER = "meter.PROP_INTERVAL1_UPPER_BORDER";

	/**
	 * The property id for the interval 2 lower border setting.
	 */
	public static final String PROP_INTERVAL2_LOWER_BORDER = "meter.PROP_INTERVAL2_LOWER_BORDER";

	/**
	 * The property id for the interval 2 upper border setting.
	 */
	public static final String PROP_INTERVAL2_UPPER_BORDER = "meter.PROP_INTERVAL2_UPPER_BORDER";

	/**
	 * The property id for the interval 3 lower border setting.
	 */
	public static final String PROP_INTERVAL3_LOWER_BORDER = "meter.PROP_INTERVAL3_LOWER_BORDER";

	/**
	 * The property id for the interval 3 upper border setting.
	 */
	public static final String PROP_INTERVAL3_UPPER_BORDER = "meter.PROP_INTERVAL3_UPPER_BORDER";

	/**
	 * The property id for the value setting.
	 */
	public static final String PROP_VALUE = "meter.PROP_VALUE";

	/**
	 * The ID of this model element.
	 */
	public static final String ID = "element.meter";

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
		addProperty(PROP_INTERVAL1_LOWER_BORDER, "Interval 1 lower border",
				DataTypeEnum.DOUBLE, INTERVAL1_LOWER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL1_UPPER_BORDER, "Interval 1 upper border",
				DataTypeEnum.DOUBLE, INTERVAL1_UPPER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL2_LOWER_BORDER, "Interval 2 lower border",
				DataTypeEnum.DOUBLE, INTERVAL2_LOWER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL2_UPPER_BORDER, "Interval 2 upper border",
				DataTypeEnum.DOUBLE, INTERVAL2_UPPER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL3_LOWER_BORDER, "Interval 3 lower border",
				DataTypeEnum.DOUBLE, INTERVAL3_LOWER_BORDER_DEFAULT);
		addProperty(PROP_INTERVAL3_UPPER_BORDER, "Interval 3 upper border",
				DataTypeEnum.DOUBLE, INTERVAL3_UPPER_BORDER_DEFAULT);
		addProperty(PROP_VALUE, "value", DataTypeEnum.DOUBLE, VALUE_DEFAULT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_VALUE;
	}
}
