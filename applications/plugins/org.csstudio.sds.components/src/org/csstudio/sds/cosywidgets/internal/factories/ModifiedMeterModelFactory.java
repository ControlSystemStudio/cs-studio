package org.csstudio.sds.cosywidgets.internal.factories;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;
import org.csstudio.sds.cosywidgets.models.MeterModel;
import org.csstudio.sds.cosywidgets.models.ModifiedMeterModel;

/**
 * Factory for {@link MeterModel}.
 * 
 * @author jbercic
 * 
 */
public final class ModifiedMeterModelFactory implements IWidgetModelFactory {

	/**
	 * {@inheritDoc}
	 */
	public AbstractWidgetModel createWidgetModel() {
		return new ModifiedMeterModel();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getWidgetModelType() {
		return ModifiedMeterModel.class;
	}
}
