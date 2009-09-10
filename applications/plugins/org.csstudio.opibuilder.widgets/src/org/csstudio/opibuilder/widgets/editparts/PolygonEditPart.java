package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.PolygonFigure;
import org.csstudio.opibuilder.widgets.model.AbstractPolyModel;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.PolygonModel;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the Polygon widget. The controller mediates between
 * {@link PolygonModel} and {@link PolygonFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class PolygonEditPart extends AbstractPolyEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		PolygonFigure polygon = new PolygonFigure();
		PolygonModel model = getCastedModel();
		polygon.setPoints(model.getPoints());
		polygon.setFill(model.getFillLevel());
		polygon.setAntiAlias(model.isAntiAlias());
		polygon.setOrientation(model.isHorizontalFill());
		polygon.setTransparent(model.isTransparent());
		return polygon;
	}

	@Override
	public PolygonModel getCastedModel() {
		return (PolygonModel)getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
		
		// fill
		IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				PolygonFigure polygon = (PolygonFigure) refreshableFigure;
				polygon.setFill((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyModel.PROP_FILL_LEVEL, fillHandler);		
		
		// anti alias
		IWidgetPropertyChangeHandler antiAliasHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				PolygonFigure polygon = (PolygonFigure) refreshableFigure;
				polygon.setAntiAlias((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyModel.PROP_ANTIALIAS, antiAliasHandler);		
		
		// fill orientaion
		IWidgetPropertyChangeHandler fillOrientHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				PolygonFigure figure = (PolygonFigure) refreshableFigure;
				figure.setOrientation((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_HORIZONTAL_FILL, fillOrientHandler);
		
		// transparent
		IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				PolygonFigure figure = (PolygonFigure) refreshableFigure;
				figure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_TRANSPARENT, transparentHandler);	
		
	}
}
