package org.csstudio.sds.components.ui.internal.feedback;

import org.csstudio.sds.components.internal.model.PolylineElement;
import org.eclipse.gef.requests.CreationFactory;

/**
 * A custom creation tool for polygon elements.
 * 
 * @author Sven Wende
 * 
 */

public final class PolylineCreationTool extends PointListCreationTool {
	/**
	 * Constructor.
	 */
	public PolylineCreationTool() {
		CreationFactory factory = new CreationFactory() {
			public Object getNewObject() {
				PolylineElement polygon = new PolylineElement();
//				PointList points = getCreateRequest().getPoints();
//
//				polygon.setPoints(points);

				return polygon;
			}

			public Object getObjectType() {
				return PolylineElement.class;
			}
		};
		
		setFactory(factory);
	}

}
