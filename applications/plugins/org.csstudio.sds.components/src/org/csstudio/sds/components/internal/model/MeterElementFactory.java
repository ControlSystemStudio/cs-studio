package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.IModelElementFactory;
import org.csstudio.sds.model.DisplayModelElement;

/**
 * A model element factory for {@link MeterElement}.
 * 
 * @author Sven Wende
 * 
 */
public final class MeterElementFactory implements IModelElementFactory {

	/**
	 * {@inheritDoc}
	 */
	public DisplayModelElement createModelElement() {
		return new MeterElement();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getModelElementType() {
		return MeterElement.class;
	}

}
