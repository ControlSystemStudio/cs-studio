package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.BoolSwitchFigure;
import org.csstudio.opibuilder.widgets.model.BoolSwitchModel;
import org.eclipse.draw2d.IFigure;

/**
 * Boolean Switch EditPart
 * @author Xihui Chen
 *
 */
public class BoolSwitchEditPart extends AbstractBoolControlEditPart{

	@Override
	protected IFigure doCreateFigure() {
		final BoolSwitchModel model = getWidgetModel();

		BoolSwitchFigure boolSwitch = new BoolSwitchFigure();
		
		initializeCommonFigureProperties(boolSwitch, model);			
		boolSwitch.setEffect3D(model.isEffect3D());
		return boolSwitch;
		
		
	}
	
	@Override
	public BoolSwitchModel getWidgetModel() {
		return (BoolSwitchModel)getModel();
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		//effect 3D
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				BoolSwitchFigure boolSwitch = (BoolSwitchFigure) refreshableFigure;
				boolSwitch.setEffect3D((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BoolSwitchModel.PROP_EFFECT3D, handler);	
		
		
	}

}
