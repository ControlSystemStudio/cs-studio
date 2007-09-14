package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.MenuButtonModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

public class MenuButtonModelFactory implements IWidgetModelFactory {

	public AbstractWidgetModel createWidgetModel() {
		return new MenuButtonModel();
	}

	public Class getWidgetModelType() {
		return MenuButtonModel.class;
	}

}
