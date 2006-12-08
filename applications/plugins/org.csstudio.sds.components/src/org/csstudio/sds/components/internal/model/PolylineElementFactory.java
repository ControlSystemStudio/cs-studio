package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.IModelElementFactory;
import org.csstudio.sds.model.DisplayModelElement;

/**
 * Model element factory for polyline model elements.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class PolylineElementFactory implements IModelElementFactory {
	/**
	 * {@inheritDoc}
	 */
	public DisplayModelElement createModelElement() {
		return new PolylineElement();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getModelElementType() {
		return PolylineElement.class;
	}
}
