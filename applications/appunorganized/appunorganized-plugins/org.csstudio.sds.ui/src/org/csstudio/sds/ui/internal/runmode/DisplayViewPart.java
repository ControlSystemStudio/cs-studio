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
 package org.csstudio.sds.ui.internal.runmode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * A view implementation that uses a GEF graphical viewer to display SDS
 * displays.
 *
 * Note: This view is enabled for multiple use.
 *
 * @author Sven Wende
 */
public final class DisplayViewPart extends ViewPart {

    private IMemento _memento;
    private Map<String, String> _mementoInfos;
    /**
     * The primary ID of this view.
     */
    public static final String PRIMARY_ID = "org.csstudio.sds.ui.internal.runmode.DisplayViewPart";

    /**
     * The graphical viewer.
     */
    private GraphicalViewer _graphicalViewer;

    /**
     * Action for "Zoom In" functionality.
     */
    private IAction _zoomInAction;

    /**
     * Action for "Zoom Out" functionality.
     */
    private IAction _zoomOutAction;

    /**
     * Action for "Change Layer Visibility" functionality.
     */
    private IAction _changeLayerVisibilityAction;

    public DisplayViewPart() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        _graphicalViewer = AbstractRunModeBox.createGraphicalViewer(parent);

        configureGraphicalViewer(_graphicalViewer);

        final ZoomManager zm = ((ScalableFreeformRootEditPart) _graphicalViewer
                .getRootEditPart()).getZoomManager();

        _zoomInAction = new ZoomInAction(zm);
        _zoomOutAction = new ZoomOutAction(zm);
        _changeLayerVisibilityAction = new ChangeLayerVisibilityAction(_graphicalViewer);

        // open a run mode box which is responsible for this view

        if (_memento != null) {
            RunModeService.getInstance().openDisplayViewInRunMode(this,
                    _memento);
        }

        createContextMenu();

    }

    /**
     * Returns the "Zoom In" action.
     *
     * @return the "Zoom In" action
     */
    public IAction getZoomInAction() {
        return _zoomInAction;
    }

    /**
     * Returns the "Zoom Out" action.
     *
     * @return the "Zoom Out" action
     */
    public IAction getZoomOutAction() {
        return _zoomOutAction;
    }

    /**
     * Returns the "Change Layer Visibility" action.
     *
     * @return the "Change Layer Visibility" action
     */
    public IAction getChangeLayerVisibilityAction() {
        return _changeLayerVisibilityAction;
    }

    /**
     * Sets the information that will be stored with this view´s memento object.
     *
     * @param mementoInfos
     */
    public void setMementoInfos(final Map<String, String> mementoInfos) {
        _mementoInfos = mementoInfos;
    }

    /**
     * Cache the memento which will be used in
     * {@link #createPartControl(Composite)} to create a run mode box.
     *
     * {@inheritDoc}
     */
    @Override
    public void init(final IViewSite site, final IMemento memento)
            throws PartInitException {
        _memento = memento;
        super.init(site, memento);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
    }

    @Override
    public void setTitleToolTip(String toolTip) {
        super.setTitleToolTip(toolTip);
    }

    /**
     * Configures the specified graphical viewer.
     *
     * @param viewer
     *            the graphical viewer
     */
    private void configureGraphicalViewer(final GraphicalViewer viewer) {
        assert viewer != null;

        final ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
        viewer.setRootEditPart(root);

        // configure zoom actions
        final ZoomManager zm = root.getZoomManager();

        final List<String> zoomLevels = new ArrayList<String>(3);
        zoomLevels.add(ZoomManager.FIT_ALL);
        zoomLevels.add(ZoomManager.FIT_WIDTH);
        zoomLevels.add(ZoomManager.FIT_HEIGHT);
        zm.setZoomLevelContributions(zoomLevels);

        if (zm != null) {
            final IAction zoomIn = new ZoomInAction(zm);
            final IAction zoomOut = new ZoomOutAction(zm);
            // getActionRegistry().registerAction(zoomIn);
            // getActionRegistry().registerAction(zoomOut);

            getSite().getKeyBindingService().registerAction(zoomIn);
            getSite().getKeyBindingService().registerAction(zoomOut);
        }

        /* scroll-wheel zoom */
        viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1),
                MouseWheelZoomHandler.SINGLETON);

        viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, false);
        viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, false);
        viewer.setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, false);

    }

    private void createContextMenu() {
        final GraphicalViewer viewer = getGraphicalViewer();
        ActionRegistry actionRegistry = new ActionRegistry();
        this.createActions(actionRegistry);
        final ContextMenuProvider cmProvider = new RunModeContextMenuProvider(
                viewer, actionRegistry);

        cmProvider.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(final IMenuManager manager) {
                manager.setRemoveAllWhenShown(true);
            }
        });
        viewer.setContextMenu(cmProvider);
        getSite().registerContextMenu(cmProvider, viewer);
    }

    private void createActions(final ActionRegistry actionRegistry) {
        Action closeAction = new Action() {
            @Override
            public void run() {
                Display.getCurrent().asyncExec(new Runnable() {
                    public void run() {
                        getSite().getWorkbenchWindow().getActivePage().hideView(DisplayViewPart.this);
                    }
                });
            }
        };
        closeAction.setText("Close View");
        closeAction.setId(RunModeContextMenuProvider.CLOSE_ACTION_ID);
        actionRegistry.registerAction(closeAction);
    }

    public GraphicalViewer getGraphicalViewer() {
        return _graphicalViewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveState(IMemento memento) {
        if (_mementoInfos != null) {
            for (String key : _mementoInfos.keySet()) {
                memento.putString(key, _mementoInfos.get(key));
            }
        }
        super.saveState(memento);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPartName(String partName) {
        super.setPartName(partName);
    }

}
