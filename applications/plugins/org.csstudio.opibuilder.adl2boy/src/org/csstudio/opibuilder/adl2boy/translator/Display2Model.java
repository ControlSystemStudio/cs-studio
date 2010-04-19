package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLDisplay;
import org.eclipse.swt.graphics.RGB;

public class Display2Model extends AbstractADL2Model {
	DisplayModel displayModel = new DisplayModel();
	
	public Display2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		ADLDisplay adlDisp = new ADLDisplay(adlWidget);
		int displayForeColor = 0;
		int displayBackColor = 0;
	
		if (adlDisp !=null){
			setADLObjectProps(adlDisp, displayModel);
			setADLBasicAttributeProps(adlDisp, displayModel, false);
			displayForeColor = Integer.parseInt(adlDisp.get_clr());
			displayBackColor = Integer.parseInt(adlDisp.get_bclr());
			displayModel.setBackgroundColor(colorMap[displayBackColor]);
			displayModel.setForegroundColor(colorMap[displayForeColor]);
			
		}
		
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return displayModel;
	}

}
