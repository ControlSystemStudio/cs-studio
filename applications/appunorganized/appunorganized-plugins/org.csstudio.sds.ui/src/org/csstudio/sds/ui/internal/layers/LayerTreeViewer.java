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

import org.csstudio.sds.internal.model.ILayerModelListener;
import org.csstudio.sds.internal.model.Layer;
import org.csstudio.sds.internal.model.LayerSupport;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * This class provides a {@link TreeViewer} to display the defined {@link Layer}s
 * of a Display in the Run-Mode. It also allows to toggle the visibility of the
 * Layers. If an {@link IWorkbenchPartSite} is set, the {@link TreeViewer} as a
 * popup-menu to add or remove or to change the order of the layer.
 *
 * @author Kai Meyer
 *
 */
public final class LayerTreeViewer implements ILayerModelListener {

    /**
     * The id of the parent view.
     */
    private final String _viewID;

    /**
     * The {@link IWorkbenchPartSite} where the popup-menu is registered.
     */
    private IWorkbenchPartSite _site;

    /**
     * The treeviewer which displays the layers.
     */
    private TreeViewer _treeViewer;

    /**
     * The current {@link LayerSupport}.
     */
    private LayerSupport _layerSupport;

    /**
     * The {@link CommandStack}, where several commands are executed.
     * If t is not <code>null</code> the commands can be undone.
     */
    private CommandStack _commandStack = null;

    /**
     * Constructor.
     * @param site The {@link IWorkbenchPartSite} for the popup-menu (can be null)
     * @param parent The parent {@link Composite} for the {@link TreeViewer}
     * @param viewID The ID of the parent view
     */
    public LayerTreeViewer(final IWorkbenchPartSite site,
            final Composite parent, final String viewID) {
        _site = site;
        _viewID = viewID;
        // configure viewer
        _treeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.BORDER);

        _treeViewer.setLabelProvider(new WorkbenchLabelProvider());
        _treeViewer.setContentProvider(new BaseWorkbenchContentProvider() {

            @Override
            public Object[] getElements(final Object element) {
                return ((LayerSupport) element).getLayers().toArray();
            }

        });

        // handle selections (sets active layer)
        _treeViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {

                    @Override
                    public void selectionChanged(
                            final SelectionChangedEvent event) {
                        IStructuredSelection sel = (IStructuredSelection) event
                                .getSelection();

                        Layer layer = (Layer) sel.getFirstElement();

                        if (layer != null && _layerSupport != null) {
                            _layerSupport.setActiveLayer(layer);
                        }
                    }
                });
        // handle doubleclicks (toggles layer visibility)
        _treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(final DoubleClickEvent event) {
                IStructuredSelection sel = (IStructuredSelection) event
                        .getSelection();
                Layer layer = (Layer) sel.getFirstElement();

                if (layer != null) {
                    if (_commandStack == null) {
                        layer.setVisible(!layer.isVisible());
                    } else {
                        _commandStack
                                .execute(new ToggleVisibilityCommand(layer));
                    }
                }
            }

        });

        // DnD
        addDragSupport(_treeViewer);
        addDropSupport(_treeViewer);

        // context menu
        hookPopupMenu(_treeViewer);
    }

    /**
     * Hooks the PopupMenu to the given TreeViewer.
     *
     * @param viewer
     *            The TreeViewer
     */
    private void hookPopupMenu(final TreeViewer viewer) {
        MenuManager popupMenu = new MenuManager(_viewID);
        popupMenu.add(new Separator("additions"));
        Menu menu = popupMenu.createContextMenu(viewer.getTree());

        viewer.getTree().setMenu(menu);
        if (_site != null) {
            _site.registerContextMenu(popupMenu, viewer);
        }
    }

    /**
     * Adds drag support for layers to the specified viewer.
     *
     * @param treeViewer
     *            the viewer that should support dragging of layers
     */
    private void addDragSupport(final TreeViewer treeViewer) {
        // Allow data to be copied or moved from the drag source
        treeViewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY
                | DND.DROP_DEFAULT, new Transfer[] { LayerTransfer
                .getInstance() }, new DragSourceAdapter() {
            @Override
            public void dragStart(final DragSourceEvent event) {
                if (_treeViewer.getSelection() == null) {
                    event.doit = false;
                }
            }

            @Override
            public void dragSetData(final DragSourceEvent event) {
                if (LayerTransfer.getInstance().isSupportedType(event.dataType)) {

                    IStructuredSelection sel = (IStructuredSelection) _treeViewer
                            .getSelection();
                    Layer layer = (Layer) sel.getFirstElement();

                    if (layer != null) {
                        LayerTransfer.getInstance().setSelectedLayer(layer);
                        event.data = layer;
                    }
                }
            }

            @Override
            public void dragFinished(final DragSourceEvent event) {
                LayerTransfer.getInstance().setSelectedLayer(null);
            }

        });
    }

    /**
     * Adds drop support for layers to the specified viewer.
     *
     * @param treeViewer
     *            the viewer that should support dropping of layers
     */
    private void addDropSupport(final TreeViewer treeViewer) {
        // drop
        treeViewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY
                | DND.DROP_DEFAULT, new Transfer[] { LayerTransfer
                .getInstance() }, new DropTargetAdapter() {
            @Override
            public void drop(final DropTargetEvent event) {
                if (LayerTransfer.getInstance().isSupportedType(
                        event.currentDataType)) {
                    // get the layer that was moved
                    Layer movedLayer = (Layer) LayerTransfer.getInstance()
                            .nativeToJava(event.currentDataType);

                    // get the layer, on which the moved layer was dropped
                    Layer dropLayer = (Layer) (event.item != null ? event.item
                            .getData() : null);

                    if (movedLayer != null && dropLayer != null
                            && _layerSupport != null) {
                        int index = _layerSupport.getLayerIndex(dropLayer);
                        if (_commandStack == null) {
                            _layerSupport
                                    .changeLayerPosition(movedLayer, index);
                        } else {
                            _commandStack.execute(new MoveLayerCommand(
                                    _layerSupport, movedLayer, index));
                        }
                    }
                }
            }

            @Override
            public void dropAccept(final DropTargetEvent event) {
                if (!LayerTransfer.getInstance().isSupportedType(
                        event.currentDataType)
                        || event.item == null) {
                    event.detail = DND.DROP_NONE;
                } else {
                    event.detail = DND.DROP_MOVE;
                }
            }

            @Override
            public void dragEnter(final DropTargetEvent event) {
                if (!LayerTransfer.getInstance().isSupportedType(
                        event.currentDataType)
                        || event.item == null) {
                    event.detail = DND.DROP_NONE;
                } else {
                    event.detail = DND.DROP_MOVE;
                }
            }

            @Override
            public void dragOver(final DropTargetEvent event) {
                if (!LayerTransfer.getInstance().isSupportedType(
                        event.currentDataType)
                        || event.item == null) {
                    event.detail = DND.DROP_NONE;
                } else {
                    event.detail = DND.DROP_MOVE;
                }
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void layerChanged(final Layer layer, final String property) {
        // just refresh the viewer completely if anything changes
        _treeViewer.refresh();
        _treeViewer.setSelection(new StructuredSelection(_layerSupport
                .getActiveLayer()));
    }

    /**
     * Sets the selection of the internal {@link TreeViewer}.
     * @param selection The new selection
     */
    public void setSelection(final ISelection selection) {
        _treeViewer.setSelection(selection);
    }

    /**
     * Set the layout data for the internal {@link TreeViewer}.
     * @param layoutData The layout Data
     */
    public void setLayoutData(final Object layoutData) {
        _treeViewer.getTree().setLayoutData(layoutData);
    }

    /**
     * Sets the currently used {@link LayerSupport}.
     * @param layerSupport The {@link LayerSupport}
     */
    public void setLayerSupport(final LayerSupport layerSupport) {
        _layerSupport = layerSupport;
        _layerSupport.addLayerModelListener(this);
        _treeViewer.setInput(_layerSupport);
    }

    /**
     * Sets the currently used {@link CommandStack}.
     * Set the {@link CommandStack} to enable Undo/Redo functionality.
     * @param commandStack The {@link CommandStack} (can be null)
     */
    public void setCommandStack(final CommandStack commandStack) {
        _commandStack = commandStack;
    }

    /**
     * Disposes the {@link TreeViewer}.
     */
    public void dispose() {
        _layerSupport.removeLayerModelListener(this);
        _layerSupport = null;
        _treeViewer.getTree().dispose();
    }

}
