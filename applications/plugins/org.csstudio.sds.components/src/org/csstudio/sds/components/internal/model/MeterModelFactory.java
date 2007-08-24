package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

import org.csstudio.sds.components.model.MeterModel;

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
