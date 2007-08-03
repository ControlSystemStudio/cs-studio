package org.csstudio.sds.cosywidgets.internal.factories;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

import org.csstudio.sds.cosywidgets.models.SwitchModel;

/**
 * A switch model factory for {@link SwitchModel}.
 *
 * @author jbercic
 */
public final class SwitchModelFactory implements IWidgetModelFactory {

	/**
	 * {@inheritDoc}.
	 */
	public AbstractWidgetModel createWidgetModel() {
		return new SwitchModel();
	}

	/**
	 * {@inheritDoc}.
	 */
	public Class getWidgetModelType() {
		return SwitchModel.class;
	}
}
