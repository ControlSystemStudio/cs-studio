package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.AbstractPolyModel;
import org.csstudio.opibuilder.widgets.util.RotationUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.EditPart;

/**
 * Abstract EditPart controller for the Polyline/polygon widget.
 * 
 * @author Xihui Chen
 * 
 */
public abstract class AbstractPolyEditPart extends AbstractShapeEditPart {
	
	
	@Override
	public AbstractPolyModel getWidgetModel() {
		return (AbstractPolyModel)getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
				
		// points
		IWidgetPropertyChangeHandler pointsHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				Polyline polyline = (Polyline) refreshableFigure;

				PointList points = (PointList) newValue;

				// deselect the widget (this refreshes the polypoint drag
				// handles)
				int selectionState = getSelected();
				setSelected(EditPart.SELECTED_NONE);

				polyline.setPoints(points);
				doRefreshVisuals(polyline);

				// restore the selection state
				setSelected(selectionState);
				
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPolyModel.PROP_POINTS, pointsHandler);
		
		
		IWidgetPropertyChangeHandler rotationHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				getWidgetModel().setPoints(
						RotationUtil.rotatePoints(getWidgetModel().getOriginalPoints().getCopy(), 
								(Double)newValue), false);
				return false;
			}
		};
		
		setPropertyChangeHandler(AbstractPolyModel.PROP_ROTATION, rotationHandler);
		
		
	}
	
	
}
