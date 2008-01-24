package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;

/**
 * Action that moves a layer down.
 * 
 * @author swende
 * 
 */
public final class MoveLayerDownAction extends AbstractLayerAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void selectedLayerChanged(final Layer layer, final LayerSupport layerSupport, final IAction action) {
		action.setEnabled(layerSupport.getLayerIndex(layer)!=
				layerSupport.getLayers().size()-1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createCommand(final Layer selectedLayer,
			final LayerSupport layerSupport, final IAction action) {

		Command result = null;

		if (selectedLayer != null && layerSupport != null) {
			int oldIndex = layerSupport.getLayerIndex(selectedLayer);
			int layerCount = layerSupport.getLayers().size();

			if (oldIndex <= layerCount - 2) {
				result = new MoveLayerCommand(layerSupport, selectedLayer, oldIndex + 1);
			}
		}

		return result;
	}

}
