package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.XMeterModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Meter;
import org.eclipse.swt.graphics.RGB;

public class Meter2Model extends AbstractADL2Model {
	XMeterModel meterModel = new XMeterModel();

	public Meter2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		Meter meterWidget = new Meter(adlWidget);
		if (meterWidget != null) {
			setADLObjectProps(meterWidget, meterModel);
			setADLMonitorProps(meterWidget, meterModel);
		}
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return meterModel;
	}

}
