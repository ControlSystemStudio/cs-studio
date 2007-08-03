package org.csstudio.sds.cosywidgets.internal.factories;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

import org.csstudio.sds.cosywidgets.models.ArcModel;

/**
 * A widget model factory for {@link ArcModel}. 
 * 
 * @author jbercic
 */
public final class ArcModelFactory implements IWidgetModelFactory {
	
	/**
	 * {@inheritDoc}.
	 */
	public AbstractWidgetModel createWidgetModel() {
		return new ArcModel();
	}

	/**
	 * {@inheritDoc}.
	 */
	public Class getWidgetModelType() {
		return ArcModel.class;
	}
}
