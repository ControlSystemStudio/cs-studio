package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

import org.csstudio.sds.components.model.ImageModel;

/**
 * An image model factory for {@link ImageModel}.
 *
 * @author jbercic
 */
public final class ImageModelFactory implements IWidgetModelFactory {

	/**
	 * {@inheritDoc}.
	 */
	public AbstractWidgetModel createWidgetModel() {
		return new ImageModel();
	}

	/**
	 * {@inheritDoc}.
	 */
	public Class getWidgetModelType() {
		return ImageModel.class;
	}
}
