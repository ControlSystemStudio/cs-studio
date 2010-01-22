package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.BooleanSwitchModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

public class BooleanSwitchModelFactory implements IWidgetModelFactory {

	@Override
	public AbstractWidgetModel createWidgetModel() {
		return new BooleanSwitchModel();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getWidgetModelType() {
		return BooleanSwitchModel.class;
	}

}
