package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.opibuilder.widgets.model.ChoiceButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ChoiceButton;
import org.eclipse.swt.graphics.RGB;

public class ChoiceButton2Model extends AbstractADL2Model {
	ChoiceButtonModel choiceModel = new ChoiceButtonModel();

	public ChoiceButton2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(choiceModel, true);

		ChoiceButton choiceWidget = new ChoiceButton(adlWidget);
		if (choiceWidget != null) {
			setADLObjectProps(choiceWidget, choiceModel);
			setADLControlProps(choiceWidget, choiceModel);
		}
		if (choiceWidget.getStacking() != null) {
			if (choiceWidget.getStacking().equals("column")){
				choiceModel.setPropertyValue(ChoiceButtonModel.PROP_HORIZONTAL, true);
			}
			else if (choiceWidget.getStacking().equals("row")){
				choiceModel.setPropertyValue(ChoiceButtonModel.PROP_HORIZONTAL, false);
			}
		}
		String color_mode = choiceWidget.getColor_mode();
		if ( color_mode.equals("static") ){
			choiceModel.setPropertyValue(ChoiceButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
		}
		else if (color_mode.equals("alarm") ){
			choiceModel.setPropertyValue(ChoiceButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, true);
		}
		else if (color_mode.equals("discrete") ){
			choiceModel.setPropertyValue(ChoiceButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
			//TODO Menu2Model Figure out what to do if colorMode is discrete
		}
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return choiceModel;
	}

}
