package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.TankModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableTankFigure;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the tank widget. The controller mediates between
 * {@link TankModel} and {@link RefreshableTankFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class TankEditPart extends AbstractScaledWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		TankModel model = (TankModel) getWidgetModel();

		RefreshableTankFigure tank = new RefreshableTankFigure();
		
		initializeCommonFigureProperties(tank, model);		
		tank.setFillColor(model.getFillColor());
		tank.setEffect3D(model.isEffect3D());	
		tank.setFillBackgroundColor(model.getFillbackgroundColor());
		return tank;

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
		
		//show bulb
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
