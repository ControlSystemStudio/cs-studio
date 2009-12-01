package org.csstudio.opibuilder.converter.model;


/**
 * Base class for all specific EdmWidget classes.
 *
 * @author Matevz
 *
 */
public class EdmWidget extends EdmEntity {

	/**
	 * Constructs EdmWidget from general EdmEntity.
	 *
	 * @param copy
	 * @throws EdmException
	 */
	public EdmWidget(EdmEntity copy) throws EdmException {
		super(copy);
	}
}
