package org.csstudio.sds.cosywidgets.internal.factories;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

import org.csstudio.sds.cosywidgets.models.MeterModel;

/**
 * Factory for {@link MeterModel}.
 * 
 * @author jbercic
 * 
 */
public final class MeterModelFactory implements IWidgetModelFactory {

	/**
	 * {@inheritDoc}
	 */
	public AbstractWidgetModel createWidgetModel() {
		return new MeterModel();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getWidgetModelType() {
		return MeterModel.class;
	}
}
