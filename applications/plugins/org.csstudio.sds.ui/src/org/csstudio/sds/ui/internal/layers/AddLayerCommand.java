package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.gef.commands.Command;

/**
 * Command that adds a layer.
 * 
 * @author swende
 * 
 */
final class AddLayerCommand extends Command {
	/**
	 * Access object to the layer model.
	 */
	private LayerSupport _layerSupport;

	/**
	 * The layer that is added.
	 */
	private Layer _layer;

	/**
	 * The index position of the added layer.
	 */
	private int _index;

	/**
	 * Constructor.
	 * 
	 * @param layerSupport
	 *            acces object to the layer model
	 * @param layer
	 *            the layer that is about to be added
	 * @param index
	 *            the index position of the added layer
	 */
	public AddLayerCommand(final LayerSupport layerSupport, final Layer layer,
			final int index) {
		assert layerSupport != null;
		assert layer != null;
		setLabel("Add Layer");
		_layerSupport = layerSupport;
		_layer = layer;
		_index = index;
	}

	/**
	 * Constructor.
	 * 
	 * @param layerSupport
	 *            acces object to the layer model
	 * @param layer
	 *            the layer that is about to be added
	 */
	public AddLayerCommand(final LayerSupport layerSupport, final Layer layer) {
		assert layerSupport != null;
		assert layer != null;
		_layerSupport = layerSupport;
		_layer = layer;
		_index = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		if (_index > -1) {
			_layerSupport.addLayer(_layer, _index);
		} else {
			_layerSupport.addLayer(_layer);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_layerSupport.removeLayer(_layer);
	}

}
