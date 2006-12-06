package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.DataTypeEnum;
import org.csstudio.sds.model.DisplayModelElement;

/**
 * An ellipse model element.
 * 
 * @author Sven Wende & Alexander Will
 * @version $Revision$
 * 
 */
public final class EllipseElement extends DisplayModelElement {
	/**
	 * The ID of the fill grade property.
	 */
	public static final String PROP_FILL_PERCENTAGE = "ellipse.fillpercentage";

	/**
	 * The ID of this model element.
	 */
	public static final String ID = "element.ellipse";

	/**
	 * The default value of the Y coordinate property.
	 */
	private static final int DEFAULT_Y = 100;

	/**
	 * The default value of the X coordinate property.
	 */
	private static final int DEFAULT_X = 100;

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 10;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 20;

	/**
	 * The default value of the fill grade property.
	 */
	private static final double DEFAULT_FILL_GRADE = 100.0;

	/**
	 * Standard constructor.
	 * 
	 */
	public EllipseElement() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setLocation(DEFAULT_X, DEFAULT_Y);
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
		addProperty(PROP_FILL_PERCENTAGE, "fill percentage",
				DataTypeEnum.DOUBLE, DEFAULT_FILL_GRADE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_FILL_PERCENTAGE;
	}
}
