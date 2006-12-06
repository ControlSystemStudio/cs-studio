package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.DisplayModelElement;
import org.csstudio.sds.model.IModelElementFactory;
import org.eclipse.draw2d.geometry.PointList;

/**
 * A model element factory for {@link PolygonElement}.
 * 
 * @author Sven Wende
 * 
 * @version $Revision$
 * 
 */
public final class PolygonElementFactory implements IModelElementFactory {

	/**
	 * {@inheritDoc}
	 */
	public DisplayModelElement createModelElement() {
		PolygonElement polygonElement = new PolygonElement();
		PointList points = new PointList();
		points.addPoint(1, 1);
		points.addPoint(10, 1);
		points.addPoint(20, 30);
		points.addPoint(80, 90);
		points.addPoint(80, 1);

		PointListHelper.moveToLocation(points, 100, 100);

		polygonElement.setPoints(points);

		return polygonElement;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getModelElementType() {
		return PolygonElement.class;
	}

}
