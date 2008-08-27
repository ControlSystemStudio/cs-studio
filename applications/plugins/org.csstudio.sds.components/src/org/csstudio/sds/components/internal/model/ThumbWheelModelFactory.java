package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.ThumbWheelModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

public class ThumbWheelModelFactory implements IWidgetModelFactory {

	/**
	 * {@inheritDoc}
	 */
	public AbstractWidgetModel createWidgetModel() {
		return new ThumbWheelModel();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getWidgetModelType() {
		return ThumbWheelModel.class;
	}

}
