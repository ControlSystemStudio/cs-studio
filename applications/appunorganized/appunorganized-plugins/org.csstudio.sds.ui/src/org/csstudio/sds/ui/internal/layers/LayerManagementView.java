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
import org.csstudio.sds.model.ContainerModel;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * A view which provides access to the layers of {@link ContainerModel}. This
 * view tries to query the layer model from active workbench parts using a
 * {@link ILayerManager} adapter.
 *
 * @author swende
 *
 */
public final class LayerManagementView extends ViewPart implements
        IPartListener {
    /**
     * The view�s ID.
     */
    public static final String VIEW_ID = "org.csstudio.sds.ui.internal.layers.LayerManagementView";

    /**
     * Current layer manager adapter (is queried from active workbench parts).
     */
    private ILayerManager _currentLayerManager;

    /**
     * The workbench part, that provided the current layer manager.
     */
    private IWorkbenchPart _layerSourcePart;

    /**
     * The treeviewer which displays the layers.
     */
    private LayerTreeViewer _layerTreeViewer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        // configure viewer
        _layerTreeViewer = new LayerTreeViewer(getSite(), parent, VIEW_ID);
        _layerTreeViewer.setCommandStack(getCommandStack());

        // handle initial workbench part selection
        IWorkbenchPart activePart = getViewSite().getPage().getActivePart();
        if (activePart != null) {
            partActivated(activePart);
        }

        // listen to workbench part changes
        getViewSite().getPage().addPartListener(this);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        // stop listening to the page
        getViewSite().getPage().removePartListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partActivated(final IWorkbenchPart part) {
        ILayerManager layerManager = (ILayerManager) part
                .getAdapter(ILayerManager.class);
        if (layerManager != null && layerManager != _currentLayerManager) {
            // memorize part
            _layerSourcePart = part;

            // memorize layer manager adapter
            _currentLayerManager = layerManager;

            // feed the viewer with the current layers
            _layerTreeViewer.setLayerSupport(_currentLayerManager.getLayerSupport());

            // pre-select the active layer
            Layer activeLayer = _currentLayerManager.getLayerSupport()
                    .getActiveLayer();

            if (activeLayer != null) {
                _layerTreeViewer.setSelection(new StructuredSelection(activeLayer));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partClosed(final IWorkbenchPart part) {
        if (_layerSourcePart != null && _layerSourcePart == part) {
            // forget the part
            _layerSourcePart = null;

            // invalidate layer manager
            _currentLayerManager = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partBroughtToTop(final IWorkbenchPart part) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partDeactivated(final IWorkbenchPart part) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partOpened(final IWorkbenchPart part) {
        // do nothing
    }

    /**
     * Returns the current access object to the layer model, which backs this
     * view.
     *
     * @return the current access object to the layer model, which backs this
     *         view
     */
    public LayerSupport getCurrentLayerSupport() {
        return _currentLayerManager.getLayerSupport();
    }

    /**
     * Returns the command stack that should be used to execute commands that
     * deal with layers.
     *
     * @return the command stack that should be used to execute commands that
     *         deal with layers
     */
    public CommandStack getCommandStack() {
        CommandStack result = null;
        if (_currentLayerManager != null) {
            result = _currentLayerManager.getCommandStack();
        }
        return result;
    }

}
