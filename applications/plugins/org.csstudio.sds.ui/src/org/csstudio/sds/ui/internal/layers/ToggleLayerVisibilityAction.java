package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;

/**
 * Action, which toggles the visibility of a layer.
 * 
 * @author swende
 * 
 */
public final class ToggleLayerVisibilityAction extends AbstractLayerAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void selectedLayerChanged(final Layer layer, final LayerSupport layerSupport, final IAction action) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createCommand(final Layer selectedLayer,
			final LayerSupport layerSupport, final IAction action) {
		Command result = null;

		if (selectedLayer != null && layerSupport != null) {
			result = new ToggleVisibilityCommand(selectedLayer);
		}

		return result;
	}

}
