package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.RefreshableRectangleFigure;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.RectangleModel;
import org.eclipse.draw2d.IFigure;

public class RectangleEditpart extends AbstractShapeEditPart {

	

	@Override
	protected IFigure doCreateFigure() {
		RefreshableRectangleFigure figure = new RefreshableRectangleFigure();
		RectangleModel model = getWidgetModel();
		figure.setFill(model.getFillLevel());
		figure.setOrientation(model.isHorizontalFill());
		figure.setTransparent(model.isTransparent());
		figure.setAntiAlias(model.isAntiAlias());
		return figure;
	}	
	
	@Override
	public RectangleModel getWidgetModel() {
		return (RectangleModel)getModel();
	}
	

	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
		// fill
		IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableRectangleFigure figure = (RefreshableRectangleFigure) refreshableFigure;
				figure.setFill((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_FILL_LEVEL, fillHandler);	
		
		// fill orientaion
		IWidgetPropertyChangeHandler fillOrientHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableRectangleFigure figure = (RefreshableRectangleFigure) refreshableFigure;
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
				RefreshableRectangleFigure figure = (RefreshableRectangleFigure) refreshableFigure;
				figure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(RectangleModel.PROP_TRANSPARENT, transparentHandler);	
		
		// anti alias
		IWidgetPropertyChangeHandler antiAliasHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableRectangleFigure figure = (RefreshableRectangleFigure) refreshableFigure;
				figure.setAntiAlias((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_ANTIALIAS, antiAliasHandler);
		
	}



}
