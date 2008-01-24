package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * Action that adds a new layer.
 * 
 * @author swende
 * 
 */
public final class AddLayerAction extends AbstractLayerAction {

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

		if (layerSupport != null) {
			InputDialog dialog = new InputDialog(Display.getCurrent()
					.getActiveShell(), "Create a new layer",
					"Please enter a name for the new layer:", "",
					new IInputValidator() {
						public String isValid(final String newText) {
							// TODO: Validieren der Eingabe?
							if (newText.length() <= 0) {
								return "Please provide at least one char for the layer name!";
							}
							return null;
						}
					});

			if (Window.OK == dialog.open()) {
				Layer newLayer = new Layer(dialog.getValue(), dialog.getValue());

				if (selectedLayer != null) {
					result = new AddLayerCommand(layerSupport, newLayer,
							layerSupport.getLayerIndex(selectedLayer) + 1);
				} else {
					result = new AddLayerCommand(layerSupport, newLayer);

				}
			}
		}

		return result;
	}

}
