package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;

/**
 * Action that moves a layer up.
 * 
 * @author swende
 * 
 */
public final class MoveLayerUpAction extends AbstractLayerAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void selectedLayerChanged(final Layer layer, final LayerSupport layerSupport, final IAction action) {
		action.setEnabled(layerSupport.getLayerIndex(layer)!=0);
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

			if (oldIndex >= 1) {
				result = new MoveLayerCommand(layerSupport, selectedLayer, oldIndex - 1);
			}
		}
		
		return result;
	}

}
