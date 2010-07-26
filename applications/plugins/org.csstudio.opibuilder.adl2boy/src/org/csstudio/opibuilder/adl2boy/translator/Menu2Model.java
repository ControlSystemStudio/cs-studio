package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Menu;
import org.eclipse.swt.graphics.RGB;

public class Menu2Model extends AbstractADL2Model {
	MenuButtonModel menuModel = new MenuButtonModel();

	public Menu2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		className = "Menu2Model";
		parentModel.addChild(menuModel, true);

		Menu menuWidget = new Menu(adlWidget);
		if (menuWidget != null) {
			setADLObjectProps(menuWidget, menuModel);
			setADLControlProps(menuWidget, menuModel);
		}
		menuModel.setPropertyValue(MenuButtonModel.PROP_ACTIONS_FROM_PV, true);
		//set color mode
		String color_mode = menuWidget.getColor_mode();
		if ( color_mode.equals("static") ){
			menuModel.setPropertyValue(ActionButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
		}
		else if (color_mode.equals("alarm") ){
			menuModel.setPropertyValue(ActionButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, true);
		}
		else if (color_mode.equals("discrete") ){
			menuModel.setPropertyValue(ActionButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
			//TODO Menu2Model Figure out what to do if colorMode is discrete
			TranslatorUtils.printNotHandledWarning(className, "discrete color mode");
		}
		//TODO Menu2Model Handle Visual mode to allow display of rows or columns
		TranslatorUtils.printNotHandledWarning(className, "Visual");
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return menuModel;
	}


}
