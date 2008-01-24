package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.gef.commands.Command;

/**
 * A Command to move a Layer.
 * 
 * @author Kai Meyer
 */
public final class MoveLayerCommand extends Command {
	
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
	 * The new index position of the removed layer.
	 */
	private int _newIndex;
	
	/**
	 * Constructor.
	 * 
	 * @param layerSupport
	 *            access object to the layer model
	 * @param layer
	 *            the layer that is about to be removed
	 * @param newIndex
	 * 			  the new index for the layer
	 */
	public MoveLayerCommand(final LayerSupport layerSupport, final Layer layer, final int newIndex) {
		assert layerSupport != null;
		assert layer != null;
		setLabel("Move Layer '"+layer.getId()+"'");
		_layerSupport = layerSupport;
		_layer = layer;
		_newIndex = newIndex;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_oldIndex = _layerSupport.getLayerIndex(_layer);
		_layerSupport.changeLayerPosition(_layer, _newIndex);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_layerSupport.changeLayerPosition(_layer, _oldIndex);
	}

}
