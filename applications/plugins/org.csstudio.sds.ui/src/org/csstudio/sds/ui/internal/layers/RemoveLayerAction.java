package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.layers.LayerSupport;
import org.csstudio.sds.ui.internal.commands.SetPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;

/**
 * Action that removes a layer.
 * 
 * @author swende
 * 
 */
public final class RemoveLayerAction extends AbstractLayerAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void selectedLayerChanged(final Layer layer, final LayerSupport layerSupport, final IAction action) {
		action.setEnabled(layer != null && !layer.getId().equals(LayerSupport.DEFAULT_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createCommand(final Layer selectedLayer,
			final LayerSupport layerSupport, final IAction action) {
		CompoundCommand result = null;
		if (layerSupport != null && selectedLayer != null) {
			result = new CompoundCommand();
			result.setLabel("Remove Layer '"+selectedLayer.getId()+"'");
			for (AbstractWidgetModel model : layerSupport.getParent().getWidgets()) {
				if (model.getLayer().equals(selectedLayer.getId())) {
					result.add(new SetPropertyCommand(model, AbstractWidgetModel.PROP_LAYER, LayerSupport.DEFAULT_NAME)); 
				}
			}
			result.add(new RemoveLayerCommand(layerSupport, selectedLayer)); 
		}
		
		return result ;
	}

}
