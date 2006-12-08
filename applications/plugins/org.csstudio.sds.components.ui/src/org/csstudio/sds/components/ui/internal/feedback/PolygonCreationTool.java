package org.csstudio.sds.components.ui.internal.feedback;

import org.csstudio.sds.components.internal.model.PolygonElement;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.requests.CreationFactory;

/**
 * A custom creation tool for polygon elements.
 * 
 * @author Sven Wende
 * 
 */

public final class PolygonCreationTool extends PointListCreationTool {
	/**
	 * Default constructor.
	 */
	public PolygonCreationTool() {
		setDefaultCursor(SharedCursors.CURSOR_TREE_ADD);
		setDisabledCursor(Cursors.NO);
		setFactory(new CreationFactory() {
			public Object getNewObject() {
				PolygonElement polygon = new PolygonElement();
				PointList points = getCreateRequest().getPoints();

				polygon.setPoints(points);

				return polygon;
			}

			public Object getObjectType() {
				return PolygonElement.class;
			}

		});
	}
}
