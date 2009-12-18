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
	 * @param genericEntity
	 * @throws EdmException
	 */
	public EdmWidget(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}
}
