package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.RefreshableTankFigure;
import org.csstudio.opibuilder.widgets.model.TankModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the tank widget. The controller mediates between
 * {@link TankModel} and {@link RefreshableTankFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class TankEditPart extends AbstractMarkedWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		TankModel model = getCastedModel();

		RefreshableTankFigure tank = new RefreshableTankFigure();
		
		initializeCommonFigureProperties(tank, model);		
		tank.setFillColor(model.getFillColor());
		tank.setEffect3D(model.isEffect3D());	
		tank.setFillBackgroundColor(model.getFillbackgroundColor());
		return tank;

	}

	@Override
	public TankModel getCastedModel() {
		return (TankModel)getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		//fillColor
		IWidgetPropertyChangeHandler fillColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableTankFigure tank = (RefreshableTankFigure) refreshableFigure;
				tank.setFillColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(TankModel.PROP_FILL_COLOR, fillColorHandler);	
		
		//fillBackgroundColor
		IWidgetPropertyChangeHandler fillBackColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableTankFigure tank = (RefreshableTankFigure) refreshableFigure;
				tank.setFillBackgroundColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(TankModel.PROP_FILLBACKGROUND_COLOR, fillBackColorHandler);	
		
		//effect 3D
		IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableTankFigure tank = (RefreshableTankFigure) refreshableFigure;
				tank.setEffect3D((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(TankModel.PROP_EFFECT3D, effect3DHandler);	
		
		
		
	}

}
