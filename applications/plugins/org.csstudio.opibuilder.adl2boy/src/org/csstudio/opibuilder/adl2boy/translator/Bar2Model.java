package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.AbstractMarkedWidgetModel;
import org.csstudio.opibuilder.widgets.model.TankModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.BarMonitor;
import org.eclipse.swt.graphics.RGB;

public class Bar2Model extends AbstractADL2Model {
	TankModel tankModel = new TankModel();

	public Bar2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		className = "Bar2Model";
		BarMonitor barWidget = new BarMonitor(adlWidget);
		parentModel.addChild(tankModel, true);
		if (barWidget != null) {
			setADLObjectProps(barWidget, tankModel);
			setADLControlProps(barWidget, tankModel);
		}
		tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
		// Decorate the tank Model
		//TODO Bar2Model cannot show value or channel at this time
		TranslatorUtils.printNotHandledWarning(className, "showing the value");
		String label = barWidget.getLabel();
		if ( label.equals("none")){
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, false);
			
		}
		if ( label.equals("no decorations")){
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, false);
		}
		if ( label.equals("outline")){
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, true);
		}
		if ( label.equals("limits")){
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, true);
		}
		if ( label.equals("channel")){
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, true);
		}
		//TODO Add PVLimits to Bar2Model
		TranslatorUtils.printNotHandledWarning(className, "Limits");
		//TODO Add BarDirection and fill mode to Bar2Model.  Currently there does not seem to be a way to do this.
		TranslatorUtils.printNotHandledWarning(className, "bar direction");
		//set color mode
		String color_mode = barWidget.getColor_mode();
		if ( color_mode.equals("static") ){
			tankModel.setPropertyValue(TankModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
		}
		else if (color_mode.equals("alarm") ){
			tankModel.setPropertyValue(TankModel.PROP_FORECOLOR_ALARMSENSITIVE, true);
		}
		else if (color_mode.equals("discrete") ){
			tankModel.setPropertyValue(TankModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
			//TODO Bar2Model Figure out what to do if colorMode is discrete
			TranslatorUtils.printNotHandledWarning(className, "discrete color mode");
		}
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return tankModel;
	}

}
