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
		BarMonitor barWidget = new BarMonitor(adlWidget);
		parentModel.addChild(tankModel, true);
		if (barWidget != null) {
			setADLObjectProps(barWidget, tankModel);
			setADLControlProps(barWidget, tankModel);
		}
		tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_LOLO, false);
		tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_LO, false);
		tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HIHI, false);
		tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HI, false);
		tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HI, false);
		tankModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HI, false);
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return tankModel;
	}

}
