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
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Base action for layer manipulating actions.
 *
 * @author swende
 *
 */
abstract class AbstractLayerAction implements IViewActionDelegate {

    /**
     * The active layer management view.
     */
    private LayerManagementView _activeView;

    /**
     * The current selected layer.
     */
    private Layer _selectedLayer;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void init(final IViewPart view) {
        assert view instanceof LayerManagementView : "view instanceof LayerManagementView";
        _activeView = (LayerManagementView) view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void selectionChanged(final IAction action,
            final ISelection selection) {
        IStructuredSelection sel = (IStructuredSelection) selection;

        if (sel != null && sel.getFirstElement() != null) {
            _selectedLayer = (Layer) sel.getFirstElement();
        } else {
            _selectedLayer = null;
        }

        selectedLayerChanged(_selectedLayer, _activeView.getCurrentLayerSupport(),  action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void run(final IAction action) {
        Command cmd = createCommand(_selectedLayer, _activeView
                .getCurrentLayerSupport(), action);

        if (cmd != null) {
            CommandStack commandStack = _activeView.getCommandStack();

            if (commandStack != null) {
                commandStack.execute(cmd);
            }
        }
        this.selectedLayerChanged(_selectedLayer, _activeView.getCurrentLayerSupport(), action);
    }

    /**
     * Subclasses have to implement and return a command, which does the real
     * action work.
     *
     * @param selectedLayer
     *            the currently selected layer
     * @param layerSupport
     *            the access class to the layer model
     * @param action
     *            the workbench proxy action
     * @return a command, which does the work of this action or null, if the
     *         provided parameters do not allow any action
     */
    protected abstract Command createCommand(final Layer selectedLayer,
            final LayerSupport layerSupport, final IAction action);

    /**
     * This method informs subclasses when the currently selected layer changes.
     *
     * Subclasses have the opportunity to change the state of the workbench
     * proxy action accordingly.
     *
     * @param layer
     *            the currently selected layer
     * @param layerSupport
     *               the currently used {@link LayerSupport}
     * @param action
     *            the workbench proxy action
     */
    protected abstract void selectedLayerChanged(final Layer layer, final LayerSupport layerSupport,
            final IAction action);

}
