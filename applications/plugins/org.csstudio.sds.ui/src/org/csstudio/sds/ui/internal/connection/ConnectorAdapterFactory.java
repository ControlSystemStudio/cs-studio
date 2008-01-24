package org.csstudio.sds.ui.internal.connection;

import org.csstudio.sds.internal.connection.Connector;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * Adapter factory implementation for {@link Connector} objects.
 * 
 * @author Sven Wende
 * 
 */
public final class ConnectorAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof Connector : "adaptableObject instanceof Connector"; //$NON-NLS-1$

		if (adapterType == IWorkbenchAdapter.class) {
			return new WorkbenchAdapter() {
				/**
				 * {@inheritDoc}
				 */
				@Override
				public String getLabel(final Object o) {
					Connector connector = (Connector) o;
					return connector.getProcessVariable().toString();
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public ImageDescriptor getImageDescriptor(final Object object) {
					// fallback
					ImageDescriptor result = null;

					result = CustomMediaFactory.getInstance()
							.getImageDescriptorFromPlugin(
									SdsUiPlugin.PLUGIN_ID,
									"icons/rule_java.gif"); //$NON-NLS-1$

					return result;
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public Object[] getChildren(final Object o) {
					return new Object[0];
				}
			};
		}
		return null;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}
