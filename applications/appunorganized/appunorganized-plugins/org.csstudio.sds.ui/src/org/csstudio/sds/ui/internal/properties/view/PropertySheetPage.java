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
 package org.csstudio.sds.ui.internal.properties.view;

import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.csstudio.sds.ui.internal.localization.Messages;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.help.IContext;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IContextComputer;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.eclipse.ui.part.Page;

/**
 * The standard implementation of property sheet page which presents a table of
 * property names and values obtained from the current selection in the active
 * workbench part.
 * <p>
 * This page obtains the information about what properties to display from the
 * current selection (which it tracks).
 * </p>
 * <p>
 * The model for this page is a hierarchy of <code>IPropertySheetEntry</code>.
 * The page may be configured with a custom model by setting the root entry.
 * <p>
 * If no root entry is set then a default model is created which uses the
 * <code>IPropertySource</code> interface to obtain the properties of the
 * current selection. This requires that the selected objects provide an
 * <code>IPropertySource</code> adapter (or implement
 * <code>IPropertySource</code> directly). This restriction can be overcome by
 * providing this page with an <code>IPropertySourceProvider</code>. If
 * supplied, this provider will be used by the default model to obtain a
 * property source for the current selection
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @see IPropertySource
 *
 * @author Sven Wende
 */
public final class PropertySheetPage extends Page implements
        IPropertySheetPage, IAdaptable {
    /**
     * Help context id (value
     * <code>"org.eclipse.ui.property_sheet_page_help_context"</code>).
     */
    public static final String HELP_CONTEXT_PROPERTY_SHEET_PAGE = "org.eclipse.ui.property_sheet_page_help_context"; //$NON-NLS-1$

    /**
     * The property sheet viewer.
     */
    private PropertySheetViewer _viewer;

    /**
     * A property sheet sorter.
     */
    private PropertySheetSorter _sorter;

    /**
     * The root entry.
     */
    private IPropertySheetEntry _rootEntry;

    /**
     * A property source provider.
     */
    private IPropertySourceProvider _provider;

    /**
     * The "restore defaults" action.
     */
    private DefaultsAction _defaultsAction;

    /**
     * The "filter" action.
     */
    private FilterAction _filterAction;

    /**
     * The "hide/show categories" action.
     */
    private CategoriesAction _categoriesAction;

    /**
     * The "copy property" action.
     */
    private CopyPropertyAction _copyAction;

    /**
     * The "configure dynamic aspects" action.
     */
    private ConfigureDynamicAspectsAction _configureDynamicAspectsAction;

    /**
     * The "remove dynamic aspects" action.
     */
    private RemoveDynamicAspectsAction _removeDynamicAspectsAction;

    /**
     * A cell editor activation listener.
     */
    private ICellEditorActivationListener _cellEditorActivationListener;

    /**
     * A cell editor action handler.
     */
    private CellEditorActionHandler _cellEditorActionHandler;

    /**
     * A clipboard instance.
     */
    private Clipboard _clipboard;

    /**
     * The source workbench part, which provides the selections and property
     * sources.
     */
    private IWorkbenchPart _sourcePart;

    /**
     * A part listener.
     */
    private PartListener _partListener = new PartListener();

    /**
     * Creates a new property sheet page.
     */
    public PropertySheetPage() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(final Composite parent) {
        // create a new viewer
        _viewer = new PropertySheetViewer(parent);
        _viewer.setSorter(_sorter);

        // set the model for the viewer
        if (_rootEntry == null) {
            // create a new root
            PropertySheetEntry root = new PropertySheetEntry();
            if (_provider != null) {
                // set the property source provider
                root.setPropertySourceProvider(_provider);
            }
            _rootEntry = root;
        }
        _viewer.setRootEntry(_rootEntry);
        _viewer.addActivationListener(getCellEditorActivationListener());
        // add a listener to track when the entry selection changes
        _viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                handleEntrySelection(event.getSelection());
            }
        });
        initDragAndDrop();
        makeActions();

        // Create the popup menu for the page.
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.add(_copyAction);
        menuMgr.add(_configureDynamicAspectsAction);
        menuMgr.add(_removeDynamicAspectsAction);
        menuMgr.add(new Separator());
        menuMgr.add(_defaultsAction);
        Menu menu = menuMgr.createContextMenu(_viewer.getControl());
        _viewer.getControl().setMenu(menu);
        // TODO: Men� f�r Object Contributions offen halten, oder nicht ?
        // (swende)
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite().registerContextMenu(menuMgr,
        // viewer);

        // Set help on the viewer
        _viewer.getControl().addHelpListener(new HelpListener() {
            /*
             * @see HelpListener#helpRequested(HelpEvent)
             */
            public void helpRequested(final HelpEvent e) {
                // Get the context for the selected item
                IStructuredSelection selection = (IStructuredSelection) _viewer
                        .getSelection();
                if (!selection.isEmpty()) {
                    IPropertySheetEntry entry = (IPropertySheetEntry) selection
                            .getFirstElement();
                    Object helpContextId = entry.getHelpContextIds();
                    if (helpContextId != null) {
                        if (helpContextId instanceof String) {
                            PlatformUI.getWorkbench().getHelpSystem()
                                    .displayHelp((String) helpContextId);
                            return;
                        }

                        // Since 2.0 the only valid type for helpContextIds
                        // is a String (a single id).
                        // However for backward compatibility we have to handle
                        // and array of contexts (Strings and/or IContexts)
                        // or a context computer.
                        Object[] contexts = null;
                        if (helpContextId instanceof IContextComputer) {
                            // get local contexts
                            contexts = ((IContextComputer) helpContextId)
                                    .getLocalContexts(e);
                        } else {
                            contexts = (Object[]) helpContextId;
                        }
                        IWorkbenchHelpSystem help = PlatformUI.getWorkbench()
                                .getHelpSystem();
                        // Ignore all but the first element in the array
                        if (contexts[0] instanceof IContext) {
                            help.displayHelp((IContext) contexts[0]);
                        } else {
                            help.displayHelp((String) contexts[0]);
                        }
                        return;
                    }
                }

                // No help for the selection so show page help
                PlatformUI.getWorkbench().getHelpSystem().displayHelp(
                        HELP_CONTEXT_PROPERTY_SHEET_PAGE);
            }
        });
    }

    /**
     * The <code>PropertySheetPage</code> implementation of this
     * <code>IPage</code> method disposes of this page's entries.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (_sourcePart != null) {
            _sourcePart.getSite().getPage().removePartListener(_partListener);
        }
        if (_rootEntry != null) {
            _rootEntry.dispose();
            _rootEntry = null;
        }
        if (_clipboard != null) {
            _clipboard.dispose();
            _clipboard = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAdapter(final Class adapter) {
        if (ISaveablePart.class.equals(adapter)) {
            return getSaveablePart();
        }
        return null;
    }

    /**
     * Returns an <code>ISaveablePart</code> that delegates to the source part
     * for the current page if it implements <code>ISaveablePart</code>, or
     * <code>null</code> otherwise.
     *
     * @return an <code>ISaveablePart</code> or <code>null</code>
     * @since 3.2
     */
    protected ISaveablePart getSaveablePart() {
        if (_sourcePart instanceof ISaveablePart) {
            return (ISaveablePart) _sourcePart;
        }
        return null;
    }

    /**
     * Returns the cell editor activation listener for this page.
     *
     * @return ICellEditorActivationListener the cell editor activation listener
     *         for this page
     */
    private ICellEditorActivationListener getCellEditorActivationListener() {
        if (_cellEditorActivationListener == null) {
            _cellEditorActivationListener = new ICellEditorActivationListener() {
                public void cellEditorActivated(final CellEditor cellEditor) {
                    if (_cellEditorActionHandler != null) {
                        _cellEditorActionHandler.addCellEditor(cellEditor);
                    }
                }

                public void cellEditorDeactivated(final CellEditor cellEditor) {
                    if (_cellEditorActionHandler != null) {
                        _cellEditorActionHandler.removeCellEditor(cellEditor);
                    }
                }
            };
        }
        return _cellEditorActivationListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Control getControl() {
        if (_viewer == null) {
            return null;
        }
        return _viewer.getControl();
    }

    /**
     * Handles a selection change in the entry table.
     *
     * @param selection
     *            the new selection
     */
    public void handleEntrySelection(final ISelection selection) {
        if (_defaultsAction != null) {
            if (selection.isEmpty()) {
                _defaultsAction.setEnabled(false);
                return;
            }
            // see if item is editable
            boolean editable = _viewer.getActiveCellEditor() != null;
            _defaultsAction.setEnabled(editable);
        }
    }

    /**
     * Adds drag and drop support.
     */
    protected void initDragAndDrop() {
        int operations = DND.DROP_COPY;
        Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
        DragSourceListener listener = new DragSourceAdapter() {
            @Override
            public void dragSetData(final DragSourceEvent event) {
                performDragSetData(event);
            }

            @Override
            public void dragFinished(final DragSourceEvent event) {
                // Nothing to do here
            }
        };
        DragSource dragSource = new DragSource(_viewer.getControl(), operations);
        dragSource.setTransfer(transferTypes);
        dragSource.addDragListener(listener);
    }

    /**
     * The user is attempting to drag. Add the appropriate data to the event.
     *
     * @param event
     *            The event sent from the drag and drop support.
     */
    void performDragSetData(final DragSourceEvent event) {
        // Get the selected property
        IStructuredSelection selection = (IStructuredSelection) _viewer
                .getSelection();
        if (selection.isEmpty()) {
            return;
        }
        // Assume single selection
        IPropertySheetEntry entry = (IPropertySheetEntry) selection
                .getFirstElement();

        // Place text as the data
        StringBuffer buffer = new StringBuffer();
        buffer.append(entry.getDisplayName());
        buffer.append("\t"); //$NON-NLS-1$
        buffer.append(entry.getValueAsString());

        event.data = buffer.toString();
    }

    /**
     * Make action objects.
     */
    private void makeActions() {
        ISharedImages sharedImages = PlatformUI.getWorkbench()
                .getSharedImages();

        // Restore Default Value
        _defaultsAction = new DefaultsAction(_viewer, "defaults"); //$NON-NLS-1$
        _defaultsAction.setText(Messages.Defaults_text);
        _defaultsAction.setToolTipText(Messages.Defaults_toolTip);
        _defaultsAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                        "icons/defaults_ps.gif")); //$NON-NLS-1$
//        _defaultsAction.setDisabledImageDescriptor(CustomMediaFactory
//                .getInstance().getImageDescriptorFromPlugin(
//                        SdsUiPlugin.PLUGIN_ID, "icons/defaults_ps.gif")); //$NON-NLS-1$
        _defaultsAction.setEnabled(false);

        // Show Advanced Properties
        _filterAction = new FilterAction(_viewer, "filter"); //$NON-NLS-1$
        _filterAction.setText(Messages.Filter_text);
        _filterAction.setToolTipText(Messages.Filter_toolTip);
        _filterAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                        "icons/filter_ps.gif")); //$NON-NLS-1$
        _filterAction.setChecked(false);

        // Show Categories
        _categoriesAction = new CategoriesAction(_viewer, "categories"); //$NON-NLS-1$
        _categoriesAction.setText(Messages.Categories_text);
        _categoriesAction.setToolTipText(Messages.Categories_toolTip);
        _categoriesAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                        "icons/tree_mode.gif")); //$NON-NLS-1$
        _categoriesAction.setChecked(true);

        // Copy
        Shell shell = _viewer.getControl().getShell();
        _clipboard = new Clipboard(shell.getDisplay());
        _copyAction = new CopyPropertyAction(_viewer, "copy", _clipboard); //$NON-NLS-1$
        _copyAction.setText(Messages.CopyProperty_text);
        _copyAction.setImageDescriptor(sharedImages
                .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

        // Configure dynamic aspects of a property
        _configureDynamicAspectsAction = new ConfigureDynamicAspectsAction(
                _viewer, "configureDynamicAspects"); //$NON-NLS-1$
        _configureDynamicAspectsAction.setText("Configure Dynamic Aspects");
        _configureDynamicAspectsAction.setImageDescriptor(sharedImages
                .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

        // Remove dynamic aspects of a property
        _removeDynamicAspectsAction = new RemoveDynamicAspectsAction(_viewer,
                "removeDynamicAspects");
        _removeDynamicAspectsAction.setText("Remove Dynamic Aspects");
        _removeDynamicAspectsAction.setImageDescriptor(sharedImages
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeContributions(final IMenuManager menuManager,
            final IToolBarManager toolBarManager,
            final IStatusLineManager statusLineManager) {

        // add actions to the tool bar
        toolBarManager.add(_categoriesAction);
        toolBarManager.add(_filterAction);
        toolBarManager.add(_defaultsAction);

        // add actions to the menu
        menuManager.add(_categoriesAction);
        menuManager.add(_filterAction);

        // set status line manager into the viewer
        _viewer.setStatusLineManager(statusLineManager);
    }

    /**
     * Updates the model for the viewer.
     * <p>
     * Note that this means ensuring that the model reflects the state of the
     * current viewer input.
     * </p>
     */
    public void refresh() {
        if (_viewer == null) {
            return;
        }
        // calling setInput on the viewer will cause the model to refresh
        _viewer.setInput(_viewer.getInput());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part,
            final ISelection selection) {
        if (_viewer == null) {
            return;
        }

        if (part instanceof DisplayEditor) {
            if (_sourcePart != null) {
                _sourcePart.getSite().getPage().removePartListener(_partListener);
                _sourcePart = null;
            }

            // change the viewer input since the workbench selection has changed.
            if (selection instanceof IStructuredSelection) {
                _sourcePart = part;
                _viewer.setInput(((IStructuredSelection) selection).toArray());
            }

            if (_sourcePart != null) {
                _sourcePart.getSite().getPage().addPartListener(_partListener);
            }
        }
    }

    /**
     * The <code>PropertySheetPage</code> implementation of this
     * <code>IPage</code> method calls <code>makeContributions</code> for
     * backwards compatibility with previous versions of <code>IPage</code>.
     * <p>
     * Subclasses may reimplement.
     * </p>
     *
     * @param actionBars
     *            the action bars
     */
    @Override
    public void setActionBars(final IActionBars actionBars) {
        super.setActionBars(actionBars);
        _cellEditorActionHandler = new CellEditorActionHandler(actionBars);
        _cellEditorActionHandler.setCopyAction(_copyAction);
    }

    /**
     * Sets focus to a part in the page.
     */
    @Override
    public void setFocus() {
        _viewer.getControl().setFocus();
    }

    /**
     * Sets the given property source provider as the property source provider.
     * <p>
     * Calling this method is only valid if you are using this page's default
     * root entry.
     * </p>
     *
     * @param newProvider
     *            the property source provider
     */
    public void setPropertySourceProvider(
            final IPropertySourceProvider newProvider) {
        _provider = newProvider;
        if (_rootEntry instanceof PropertySheetEntry) {
            ((PropertySheetEntry) _rootEntry)
                    .setPropertySourceProvider(_provider);
            // the following will trigger an update
            _viewer.setRootEntry(_rootEntry);
        }
    }

    /**
     * Sets the given entry as the model for the page.
     *
     * @param entry
     *            the root entry
     */
    public void setRootEntry(final IPropertySheetEntry entry) {
        _rootEntry = entry;
        if (_viewer != null) {
            // the following will trigger an update
            _viewer.setRootEntry(_rootEntry);
        }
    }

    /**
     * Sets the sorter used for sorting categories and entries in the viewer of
     * this page.
     * <p>
     * The default sorter sorts categories and entries alphabetically.
     * </p>
     *
     * @param sorter
     *            the sorter to set (<code>null</code> will reset to the
     *            default sorter)
     * @since 3.1
     */
    protected void setSorter(final PropertySheetSorter sorter) {
        this._sorter = sorter;
        if (_viewer != null) {
            _viewer.setSorter(sorter);

            // the following will trigger an update
            if (null != _viewer.getRootEntry()) {
                _viewer.setRootEntry(_rootEntry);
            }
        }
    }

    /**
     * Part listener which cleans up this page when the source part is closed.
     * This is hooked only when there is a source part.
     *
     * @since 3.2
     */
    protected final class PartListener implements IPartListener {
        /**
         * {@inheritDoc}
         */
        @Override
        public void partActivated(final IWorkbenchPart part) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void partBroughtToTop(final IWorkbenchPart part) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void partClosed(final IWorkbenchPart part) {
            if (_sourcePart == part) {
                _sourcePart = null;
                if (_viewer != null && !_viewer.getControl().isDisposed()) {
                    _viewer.setInput(new Object[0]);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void partDeactivated(final IWorkbenchPart part) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void partOpened(final IWorkbenchPart part) {
        }
    }
}
