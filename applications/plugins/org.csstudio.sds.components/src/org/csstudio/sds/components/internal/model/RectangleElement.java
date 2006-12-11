package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.DataTypeEnum;
import org.csstudio.sds.model.DisplayModelElement;
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines an rectangle model element.
 * 
 * @author Sven Wende & Alexander Will
 * @version $Revision$
 * 
 */
public final class RectangleElement extends DisplayModelElement {
	/**
	 * The ID of the fill grade property.
	 */
	public static final String PROP_FILL_PERCENTAGE = "rectangle.fillpercentage"; //$NON-NLS-1$
	
	/**
	 * The ID of the background color property.
	 */
	public static final String PROP_BACKGROUND_COLOR = "color.background"; //$NON-NLS-1$

	/**
	 * The ID of the foreground color property.
	 */
	public static final String PROP_FOREGROUND_COLOR = "color.foreground"; //$NON-NLS-1$
	
	/**
	 * The ID of this model element.
	 */
	public static final String ID = "element.rectangle"; //$NON-NLS-1$

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
	 */
	public RectangleElement() {
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
		addProperty(PROP_FILL_PERCENTAGE, "Fill Grade",
				DataTypeEnum.DOUBLE, DEFAULT_FILL_GRADE);
		addProperty(PROP_BACKGROUND_COLOR, "Background Color", DataTypeEnum.COLOR,
				new RGB(100, 100, 100));
		addProperty(PROP_FOREGROUND_COLOR, "Foreground Color", DataTypeEnum.COLOR,
				new RGB(200, 100, 100));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_FILL_PERCENTAGE;
	}
}
