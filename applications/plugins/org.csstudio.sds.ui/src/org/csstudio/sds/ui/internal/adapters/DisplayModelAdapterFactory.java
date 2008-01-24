package org.csstudio.sds.ui.internal.adapters;

import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.internal.properties.view.IPropertySource;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factory for display widget models.
 * 
 * @author Sven Wende
 *
 */
public final class DisplayModelAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject, final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof DisplayModel : "adaptableObject instanceof DisplayModel"; //$NON-NLS-1$

		DisplayModel model = (DisplayModel) adaptableObject;
		if (adapterType == IPropertySource.class) {
			return new WidgetPropertySourceAdapter(model);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}

}
