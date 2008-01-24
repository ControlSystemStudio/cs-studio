package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.gef.commands.Command;

/**
 * Command that removes a layer.
 * 
 * @author swende
 * 
 */
final class RemoveLayerCommand extends Command {
	/**
	 * Access object to the layer model.
	 */
	private LayerSupport _layerSupport;

	/**
	 * The layer that is removed.
	 */
	private Layer _layer;

	/**
	 * The former index position of the removed layer.
	 */
	private int _oldIndex;

	/**
	 * Constructor.
	 * 
	 * @param layerSupport
	 *            acces object to the layer model
	 * @param layer
	 *            the layer that is about to be removed
	 */
	public RemoveLayerCommand(final LayerSupport layerSupport, final Layer layer) {
		assert layerSupport != null;
		assert layer != null;
		setLabel("Remove Layer");
		_layerSupport = layerSupport;
		_layer = layer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_oldIndex = _layerSupport.getLayerIndex(_layer);
		_layerSupport.removeLayer(_layer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_layerSupport.addLayer(_layer, _oldIndex);
	}

}
