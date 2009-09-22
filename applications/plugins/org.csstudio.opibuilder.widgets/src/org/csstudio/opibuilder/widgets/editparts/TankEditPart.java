package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.figures.TankFigure;
import org.csstudio.opibuilder.widgets.model.TankModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the tank widget. The controller mediates between
 * {@link TankModel} and {@link TankFigure}.
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
		TankModel model = getWidgetModel();

		TankFigure tank = new TankFigure();
		
		initializeCommonFigureProperties(tank, model);		
		tank.setFillColor(model.getFillColor());
		tank.setEffect3D(model.isEffect3D());	
		tank.setFillBackgroundColor(model.getFillbackgroundColor());
		return tank;

	}

	@Override
	public TankModel getWidgetModel() {
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
				TankFigure tank = (TankFigure) refreshableFigure;
				tank.setFillColor(((OPIColor) newValue).getRGBValue());
				return true;
			}
		};
		setPropertyChangeHandler(TankModel.PROP_FILL_COLOR, fillColorHandler);	
		
		//fillBackgroundColor
		IWidgetPropertyChangeHandler fillBackColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				TankFigure tank = (TankFigure) refreshableFigure;
				tank.setFillBackgroundColor(((OPIColor) newValue).getRGBValue());
				return true;
			}
		};
		setPropertyChangeHandler(TankModel.PROP_FILLBACKGROUND_COLOR, fillBackColorHandler);	
		
		//effect 3D
		IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				TankFigure tank = (TankFigure) refreshableFigure;
				tank.setEffect3D((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(TankModel.PROP_EFFECT3D, effect3DHandler);	
		
		
		
	}

}
