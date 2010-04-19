package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.ScaledSliderModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Valuator;
import org.eclipse.swt.graphics.RGB;

public class Valuator2Model extends AbstractADL2Model {
	ScaledSliderModel sliderModel = new ScaledSliderModel();

	public Valuator2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(sliderModel, true);
		Valuator valuatorWidget = new Valuator(adlWidget);
		if (valuatorWidget != null) {
			setADLObjectProps(valuatorWidget, sliderModel);
			setADLControlProps(valuatorWidget, sliderModel);
		}
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return sliderModel;
	}

}
