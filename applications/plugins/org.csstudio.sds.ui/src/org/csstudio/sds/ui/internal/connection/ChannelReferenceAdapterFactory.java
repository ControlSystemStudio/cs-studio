package org.csstudio.sds.ui.internal.connection;

import org.csstudio.sds.internal.connection.ChannelReference;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * Adapter factory implementation for {@link ChannelReference}.
 * 
 * @author Sven Wende
 * 
 */
public final class ChannelReferenceAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof ChannelReference : "adaptableObject instanceof ChannelReference"; //$NON-NLS-1$

		if (adapterType == IWorkbenchAdapter.class) {
			return new WorkbenchAdapter() {
				/**
				 * {@inheritDoc}
				 */
				@Override
				public ImageDescriptor getImageDescriptor(final Object object) {
					return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID, "icons/connector.png");
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public String getLabel(final Object object) {
					assert object instanceof ChannelReference : "object instanceof ChannelReference";
					return ((ChannelReference) object).getRawChannelName();
				}

			};
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}
