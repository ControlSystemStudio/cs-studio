package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.DisplayModelElement;

/**
 * A line model element.
 * 
 * @author Alexander Will
 */
public final class LineElement extends DisplayModelElement {

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
	 * The ID of this model element.
	 */
	public static final String ID = "element.line";

	/**
	 * Constructor.
	 */
	public LineElement() {
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

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		//FIXME: swende: kein Null zurückgeben
		return null;
	}
}
