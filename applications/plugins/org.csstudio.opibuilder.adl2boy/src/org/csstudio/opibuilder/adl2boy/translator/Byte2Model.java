package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.ByteMonitorModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ByteMonitor;
import org.csstudio.utility.adlparser.fileParser.widgets.Meter;
import org.eclipse.swt.graphics.RGB;

public class Byte2Model extends AbstractADL2Model {
	ByteMonitorModel byteModel = new ByteMonitorModel();

	public Byte2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(byteModel, true);
		ByteMonitor byteWidget = new ByteMonitor(adlWidget);
		if (byteWidget != null) {
			setADLObjectProps(byteWidget, byteModel);
			setADLMonitorProps(byteWidget, byteModel);
		}
		//TODO many things
		TranslatorUtils.printNotHandledWarning(className, "many things");
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return byteModel;
	}

}
