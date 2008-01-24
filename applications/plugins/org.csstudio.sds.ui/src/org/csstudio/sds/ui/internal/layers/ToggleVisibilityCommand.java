package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.eclipse.gef.commands.Command;

/**
 * Command that toggles the visibility of  a layer.
 * 
 * @author Kai Meyer
 * 
 */
public final class ToggleVisibilityCommand extends Command {	
	/**
	 * The layer, which visibility is toggled.
	 */
	private Layer _layer;
	
	/**
	 * Constructor.
	 * 
	 * @param layer
	 *            the layer that is about to be removed
	 */
	public ToggleVisibilityCommand( final Layer layer) {
		assert layer != null;
		setLabel("Switch Visibility");
		_layer = layer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {		
		_layer.setVisible(!_layer.isVisible());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_layer.setVisible(!_layer.isVisible());
	}

}
