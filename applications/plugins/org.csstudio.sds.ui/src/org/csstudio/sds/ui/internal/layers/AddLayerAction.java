/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.internal.model.Layer;
import org.csstudio.sds.internal.model.LayerSupport;
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
        // nothing to do
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
