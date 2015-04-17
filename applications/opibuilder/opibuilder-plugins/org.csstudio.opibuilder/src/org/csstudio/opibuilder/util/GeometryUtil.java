package org.csstudio.opibuilder.util;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

public class GeometryUtil {

	/**Get the range of children widgets.
	 * @param container editpart of the container widget.
	 * @return the range (minX, minY, maxX-minX, maxY-minY) relative to the container.
	 */
	public static Rectangle getChildrenRange(AbstractContainerEditpart container){
		
		PointList pointList = new PointList(container.getChildren().size());
		for(Object child : container.getChildren()){
			AbstractWidgetModel childModel = ((AbstractBaseEditPart)child).getWidgetModel();
			pointList.addPoint(childModel.getLocation());
			pointList.addPoint(childModel.getX()+childModel.getWidth(), 
					childModel.getY() + childModel.getHeight());
		}
		return pointList.getBounds();
	}
	
	
}
