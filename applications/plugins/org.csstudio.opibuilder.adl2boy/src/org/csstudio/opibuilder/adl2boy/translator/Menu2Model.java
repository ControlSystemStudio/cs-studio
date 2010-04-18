package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Menu;
import org.eclipse.swt.graphics.RGB;

public class Menu2Model extends AbstractADL2Model {
	MenuButtonModel menuModel = new MenuButtonModel();

	public Menu2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		Menu menuWidget = new Menu(adlWidget);
		if (menuWidget != null) {
			setADLObjectProps(menuWidget, menuModel);
			setADLControlProps(menuWidget, menuModel);
		}
		menuModel.setPropertyValue(MenuButtonModel.PROP_ACTIONS_FROM_PV, true);
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return menuModel;
	}

}
