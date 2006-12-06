package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.IModelElementFactory;
import org.csstudio.sds.model.DisplayModelElement;

/**
 * This class defines a model element factory for rectangle model elements.
 * 
 * @author Sven Wende & Alexander Will
 * @version $Revision$
 * 
 */
public final class RectangleElementFactory implements IModelElementFactory {
	/**
	 * {@inheritDoc}
	 */
	public DisplayModelElement createModelElement() {
		return new RectangleElement();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getModelElementType() {
		return RectangleElement.class;
	}
}
