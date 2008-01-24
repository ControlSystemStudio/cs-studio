package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Workbench adapter for layers.
 * 
 * @author swende
 * 
 */
final class LayerWorkbenchAdapter implements IWorkbenchAdapter {

	/**
	 * {@inheritDoc}
	 */
	public Object[] getChildren(final Object o) {
		return new Object[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public ImageDescriptor getImageDescriptor(final Object object) {
		Layer layer = (Layer) object;

		return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
				SdsUiPlugin.PLUGIN_ID,
				layer.isVisible() ? "icons/layer_visible.png"
						: "icons/layer_invisible.png");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLabel(final Object o) {
		return ((Layer) o).getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getParent(final Object o) {
		return null;
	}
}
