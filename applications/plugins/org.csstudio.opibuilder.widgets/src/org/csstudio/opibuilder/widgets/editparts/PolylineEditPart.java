package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.PolylineFigure;
import org.csstudio.opibuilder.widgets.model.AbstractPolyModel;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.PolyLineModel;
import org.csstudio.opibuilder.widgets.model.PolyLineModel.ArrowType;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the Polyline widget. The controller mediates between
 * {@link PolylineModel} and {@link PolylineFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class PolylineEditPart extends AbstractPolyEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		PolylineFigure polyline = new PolylineFigure();
		PolyLineModel model = getWidgetModel();		
		polyline.setPoints(model.getPoints());
		polyline.setFill(model.getFillLevel());
		polyline.setAntiAlias(model.isAntiAlias());
		polyline.setOrientation(model.isHorizontalFill());
		polyline.setTransparent(model.isTransparent());
		polyline.setArrowLineLength(model.getArrowLength());
		polyline.setArrowType(ArrowType.values()[model.getArrowType()]);
		polyline.setFillArrow(model.isFillArrow());
		
		return polyline;
	}

	
	@Override
	public PolyLineModel getWidgetModel() {
		return (PolyLineModel)getModel();
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
				PolylineFigure polyline = (PolylineFigure) refreshableFigure;
				polyline.setFill((Double) newValue);				
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyModel.PROP_FILL_LEVEL, fillHandler);
		
		// anti alias
		IWidgetPropertyChangeHandler antiAliasHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				PolylineFigure figure = (PolylineFigure) refreshableFigure;
				figure.setAntiAlias((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyModel.PROP_ANTIALIAS, antiAliasHandler);
		
		// fill orientaion
		IWidgetPropertyChangeHandler fillOrientHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				PolylineFigure figure = (PolylineFigure) refreshableFigure;
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
				PolylineFigure figure = (PolylineFigure) refreshableFigure;
				figure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_TRANSPARENT, transparentHandler);	
		
		// arrow Type
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				PolylineFigure figure = (PolylineFigure) refreshableFigure;
				figure.setArrowType(ArrowType.values()[(Integer)newValue]);
				getWidgetModel().updateBounds();
				return true;
			}
		};
		setPropertyChangeHandler(PolyLineModel.PROP_ARROW, handler);
		
		
		// arrow length
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				PolylineFigure figure = (PolylineFigure) refreshableFigure;
				figure.setArrowLineLength((Integer)newValue);
				getWidgetModel().updateBounds();
				return true;
			}
		};
		setPropertyChangeHandler(PolyLineModel.PROP_ARROW_LENGTH, handler);
		
		// Fill Arrow
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				PolylineFigure figure = (PolylineFigure) refreshableFigure;
				figure.setFillArrow((Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(PolyLineModel.PROP_FILL_ARROW, handler);
		
		
	}
	
	
}
