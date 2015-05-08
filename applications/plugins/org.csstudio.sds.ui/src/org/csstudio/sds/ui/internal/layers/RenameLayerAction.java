package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.internal.model.Layer;
import org.csstudio.sds.internal.model.LayerSupport;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class RenameLayerAction extends AbstractLayerAction {

    public RenameLayerAction() {
    }

    @Override
    protected Command createCommand(Layer selectedLayer, LayerSupport layerSupport, IAction action) {
        Command result = null;

        if (layerSupport != null) {
            InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(),
                                                 "Rename",
                                                 "Please enter the new name for the selected layer:",
                                                 selectedLayer.getDescription(),
                                                 new IInputValidator() {
                                                     public String isValid(final String newText) {
                                                         if (newText.length() <= 0) {
                                                             return "Please provide at least one char for the layer name!";
                                                         }
                                                         return null;
                                                     }
                                                 });

            if (Window.OK == dialog.open()) {
                result = new RenameLayerCommand(layerSupport, selectedLayer, dialog.getValue());
            }
        }

        return result;
    }

    @Override
    protected void selectedLayerChanged(Layer layer, LayerSupport layerSupport, IAction action) {
        action.setEnabled(layer != null);
    }

}
