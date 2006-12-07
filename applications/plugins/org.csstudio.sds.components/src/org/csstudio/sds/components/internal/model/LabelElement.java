package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.DataTypeEnum;
import org.csstudio.sds.model.DisplayModelElement;

/**
 * An label model element.
 * 
 * @author Sven Wende & Alexander Will
 * @version $Revision$
 * 
 */
public final class LabelElement extends DisplayModelElement {
	/**
	 * The ID of this model element.
	 */
	public static final String ID = "element.label";

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 20;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 80;

	/**
	 * The default value of the fill grade property.
	 */
	public static final String PROP_LABEL = "label";

	/**
	 * Standard constructor.
	 * 
	 */
	public LabelElement() {
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
		addProperty(PROP_LABEL, "Label", DataTypeEnum.DOUBLE, "empty");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_LABEL;
	}
}
