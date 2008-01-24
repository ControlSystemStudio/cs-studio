package org.csstudio.sds.ui.internal.layers;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.csstudio.sds.model.layers.Layer;

/**
 * Adapter factory for layers.
 * 
 * @author swende
 * 
 */
public final class LayerAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject, final Class adapterType) {
		assert adaptableObject instanceof Layer : "adaptableObject instanceof Layer";

		if (adapterType == IWorkbenchAdapter.class) {
			return new LayerWorkbenchAdapter();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}
