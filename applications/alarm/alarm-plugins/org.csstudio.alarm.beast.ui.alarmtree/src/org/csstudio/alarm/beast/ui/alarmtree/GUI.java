/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import java.util.List;

import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.client.GUIUpdateThrottle;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.alarm.beast.ui.ContextMenuHelper;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.SelectionHelper;
import org.csstudio.alarm.beast.ui.actions.AddComponentAction;
import org.csstudio.alarm.beast.ui.actions.AlarmPerspectiveAction;
import org.csstudio.alarm.beast.ui.actions.ConfigureItemAction;
import org.csstudio.alarm.beast.ui.actions.DisableComponentAction;
import org.csstudio.alarm.beast.ui.actions.DuplicatePVAction;
import org.csstudio.alarm.beast.ui.actions.EnableComponentAction;
import org.csstudio.alarm.beast.ui.actions.MoveItemAction;
import org.csstudio.alarm.beast.ui.actions.RemoveComponentAction;
import org.csstudio.alarm.beast.ui.actions.RenameItemAction;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;
import org.csstudio.security.SecuritySupport;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.csstudio.utility.singlesource.UIHelper.UI;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

/** GUI for the alarm tree viewer
 *  @author Kay Kasemir
 *  @author Jaka Bobnar
 */
public class GUI implements AlarmClientModelListener
{
    /** Model for this GUI */
    final private AlarmClientModel model;

    final private Display display;

    /** Error message.
     *  @see #setErrorMessage(String)
     */
    private Label error_message;

    /** Parent container of the tree */
    private Composite tree_parent;

    /** Tree */
    private TreeViewer tree_viewer;

    /** Show only alarms, or all items? */
    private boolean show_only_alarms;

    private GUIUpdateThrottle throttle;


    /** Initialize GUI
     *  @param parent SWT parent
     *  @param model AlarmClientModel to display in GUI
     *  @param site Workbench site or <code>null</code>
     */
    public GUI(final Composite parent, final AlarmClientModel model,
            final IWorkbenchPartSite site)
    {
        this.model = model;
        this.display = parent.getDisplay();
        createGUI(parent);

        throttle = new GUIUpdateThrottle() {
            @Override
            protected void fire() {
                display.syncExec(() ->
                {
                    if (!tree_viewer.getTree().isDisposed())
                    {
                        if (model.isServerAlive())
                            setErrorMessage(null);
                        tree_viewer.refresh();
                    }
                });
            }
        };
        throttle.start();

        // Subscribe to model updates, arrange to un-subscribe
        model.addListener(this);
        parent.addDisposeListener(e -> {throttle.dispose(); model.removeListener(GUI.this);});

        if (model.isServerAlive()) {
            setErrorMessage(null);
        } else {
            setErrorMessage(Messages.WaitingForServer);
        }

        connectContextMenu(site);

        // Allow 'drag' of alarm info as text
        new ControlSystemDragSource(tree_viewer.getTree())
        {
            @Override
            public Object getSelection()
            {
                return SelectionHelper.getAlarmTreePVsForDragging((IStructuredSelection)tree_viewer.getSelection());
            }
        };
    }

    /** Create the GUI elements */
    private void createGUI(final Composite parent)
    {
        parent.setLayout(new FormLayout());

        // Error label in top-right
        error_message = new Label(parent, 0);
        error_message.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        error_message.setLayoutData(fd);

        tree_parent = new Composite(parent, 0);
        final TreeColumnLayout tree_layout = new TreeColumnLayout();
        tree_parent.setLayout(tree_layout);

        fd = new FormData();
        fd.top = new FormAttachment(error_message);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        fd.bottom = new FormAttachment(100, 0);
        tree_parent.setLayoutData(fd);

        final IPreferencesService service = Platform.getPreferencesService();
        final boolean allow_multiselection =
            service.getBoolean(Activator.ID, "allow_multi_selection", false, null); //$NON-NLS-1$

        // Tree with single, max-width column
        final Tree tree =
            allow_multiselection
            ? new MultiSelectionTree(tree_parent,
                // Must be virtual for ILazyTreeContentProvider
                SWT.VIRTUAL |
                // V_SCROLL seems automatic, but H_SCROLL can help when view is small
                SWT.H_SCROLL | SWT.V_SCROLL |
                // Used to have a border, not really needed
                // SWT.BORDER |
                // Used to have full-line-selection.
                // Actually looks better when only the elements are selected
                // SWT.FULL_SELECTION |
                // Multi-element selection
                // (via Shift-click, Ctrl-click).
                // with the original SWT.Tree is very slow
                // because of the way SWT preserves the selection
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=259141
                // With the patched MultiSelectionTree, it's OK
                SWT.MULTI)
            : new Tree(tree_parent, SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL);
        tree.setLinesVisible(true);
        final TreeColumn column = new TreeColumn(tree,  SWT.LEFT);
        tree_layout.setColumnData(column, new ColumnWeightData(100, true));

        tree_viewer = new TreeViewer(tree);

        // Connect tree viewer to data model
        tree_viewer.setUseHashlookup(true);
        tree_viewer.setContentProvider(new AlarmTreeContentProvider(this));
        tree_viewer.setLabelProvider(new AlarmTreeLabelProvider(tree));
        tree_viewer.setInput(model.getConfigTree());

        // Double-click on item invokes configuration dialog (if allowed)
        tree_viewer.addDoubleClickListener(new IDoubleClickListener()
        {
            @Override
            public void doubleClick(final DoubleClickEvent event)
            {
                if (! SecuritySupport.havePermission(AuthIDs.CONFIGURE))
                    return;
                final IStructuredSelection selection = (IStructuredSelection)tree_viewer.getSelection();
                final AlarmTreeItem item = (AlarmTreeItem) selection.getFirstElement();
                ConfigureItemAction.performItemConfiguration(tree_viewer.getTree().getShell(), model, item);
            }
        });

        ColumnViewerToolTipSupport.enableFor(tree_viewer);
    }

    /** Set or clear error message.
     *  Setting an error message also disables the GUI.
     *  <p>
     *  OK to call multiple times or after disposal.
     *  @param error Error message or <code>null</code> to clear error
     */
    public void setErrorMessage(final String error)
    {
        if (error_message.isDisposed())
            return;
        if (error == null)
        {
            if (!error_message.getVisible())
                return; // msg already hidden
            // Hide error message and unlink from layout
            error_message.setVisible(false);
            final FormData fd = (FormData) tree_parent.getLayoutData();
            fd.top = new FormAttachment(0, 0);
            tree_parent.getParent().layout();
        }
        else
        {   // Update the message
            error_message.setText(error);
            if (!error_message.getVisible())
            {   // Show error message and link to layout
                error_message.setVisible(true);
                final FormData fd = (FormData) tree_parent.getLayoutData();
                fd.top = new FormAttachment(error_message);
            }
            error_message.getParent().layout();
        }
    }

    /** Add context menu to tree
     *  @param site Workbench site or <code>null</code>
     */
    private void connectContextMenu(final IWorkbenchPartSite site)
    {
        final Tree tree = tree_viewer.getTree();
        final MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(m -> fillContextMenu(m));
        tree.setMenu(manager.createContextMenu(tree));

        // Allow extensions to add to the context menu
        if (site != null)
        {
            site.registerContextMenu(manager, tree_viewer);
            site.setSelectionProvider(tree_viewer);
        }
    }

    /** Invoked by the manager of the context menu when menu
     *  is about to show.
     *  Fills context menu with guidance and related displays
     *  for currently selected items.
     *  @param manager Menu manager
     */
    @SuppressWarnings("unchecked")
    private void fillContextMenu(final IMenuManager manager)
    {
        final Shell shell = tree_viewer.getTree().getShell();
        final List<AlarmTreeItem> items =
            ((IStructuredSelection)tree_viewer.getSelection()).toList();
        final boolean isRcp = UI.RCP.equals(SingleSourcePlugin.getUIHelper()
                .getUI());

        new ContextMenuHelper(null, manager, shell, items, model.isWriteAllowed());
        manager.add(new Separator());
        if(model.isWriteAllowed())
        {
            // Add edit items
            if (items.size() <= 0)
            {
                // Use the 'root' element as the parent
                manager.add(new AddComponentAction(shell, model, model.getConfigTree()));
            }
            else if (items.size() == 1)
            {
                final AlarmTreeItem item = items.get(0);
                // Allow configuration of single item

                manager.add(new ConfigureItemAction(shell, model, item));
                manager.add(new Separator());
                // Allow addition of items to all but PVs (leafs of tree)
                if (! (item instanceof AlarmTreePV))
                    manager.add(new AddComponentAction(shell, model, item));
                manager.add(new RenameItemAction(shell, model, item));

                if (items.get(0).getPosition() == AlarmTreePosition.PV)
                      manager.add(new DuplicatePVAction(shell, model,
                                                        (AlarmTreePV)items.get(0)));
            }
            if (items.size() >= 1)
            {   // Allow certain actions on one or more selected items
                manager.add(new EnableComponentAction(shell, model, items));
                manager.add(new DisableComponentAction(shell, model, items));
                manager.add(new MoveItemAction(shell, model, items));
                manager.add(new RemoveComponentAction(shell, model, items));
            }
        }
        manager.add(new Separator());
        if(isRcp) {
            manager.add(new AlarmPerspectiveAction());
            manager.add(new Separator());
        }
        manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /** Set focus to desired element in GUI */
    public void setFocus()
    {
        tree_viewer.getTree().setFocus();
    }

    /** Collapse the alarm tree */
    public void collapse()
    {
        final Tree tree = tree_viewer.getTree();
        tree.setRedraw(false);

           // This was very slow (>5 seconds for 50k PVs in 250 areas)
        // tree_viewer.collapseAll();
        // tree_viewer.refresh(false);

        // Not much better:
        //        final TreePath[] expanded = tree_viewer.getExpandedTreePaths();
        //        for (TreePath path : expanded)
        //        {
        //            if (path.getSegmentCount() > 2)
        //                continue;
        //            tree_viewer.collapseToLevel(path, 1);
        //        }

        // Fastest (<1 sec), collapsing just the first level of elements
        final TreeItem[] items = tree.getItems();
        for (TreeItem item : items)
            item.setExpanded(false);

        // This was for Eclipse 3.6.2 under Windows (7)
        // Implementation might need adjustment in later versions of SWT/JFace

        tree.setRedraw(true);
    }

    /** @return <code>true</code> if we only show alarms,
     *          <code>false</code> if we show the whole configuration
     * @return
     */
    public boolean getAlarmDisplayMode()
    {
        return show_only_alarms;
    }

    /** @param only_alarms Show only alarms? */
    public void setAlarmDisplayMode(final boolean only_alarms)
    {
        show_only_alarms = only_alarms;
        tree_viewer.refresh();
        // Expanding the whole tree can be very expensive
        //        if (show_only_alarms)
        //            tree_viewer.expandAll();
    }

    // @see AlarmClientModelListener
    @Override
    public void serverModeUpdate(AlarmClientModel model, boolean maintenanceMode)
    {
        // Ignored
    }

    /** Server connection timeout
     *  @see AlarmClientModelListener
     */
    @Override
    public void serverTimeout(final AlarmClientModel model)
    {
        display.asyncExec(() -> setErrorMessage(Messages.ServerTimeout));
    }

    /** Model changed, redo the whole tree
     *  @see AlarmClientModelListener
     */
    @Override
    public void newAlarmConfiguration(final AlarmClientModel model)
    {
        final AlarmTreeRoot config = model.getConfigTree();
        display.asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                final Tree tree = tree_viewer.getTree();
                if (tree.isDisposed())
                    return;

                if (model.isServerAlive()) {
                    setErrorMessage(null);
                } else {
                    setErrorMessage(Messages.WaitingForServer);
                }

                // Try to preserve the selection
                AlarmTreeItem select =
                        (AlarmTreeItem) ((IStructuredSelection)tree_viewer.getSelection()).getFirstElement();
                String path = select != null ? select.getPathName() : null;

                // Update GUI
                tree_viewer.setInput(config);

                if (path == null)
                    return;

                // If item is still in the configuration, select it
                select = config.getItemByPath(path);
                if (select == null)
                {   // Item may have been removed, so try to select the _parent_ of the original item
                    final String[] segments = AlarmTreePath.splitPath(path);
                    path = AlarmTreePath.makePath(segments, segments.length - 1);
                    select = config.getItemByPath(path);
                }
                if (select != null)
                {   // Anything to restore?
                    tree_viewer.setSelection(new StructuredSelection(select), true);
                    tree_viewer.expandToLevel(select, 1);
                }
            }
        });
    }

    /** Alarm state changed, refresh the display
     *  @see AlarmClientModelListener
     */
    @Override
    public void newAlarmState(final AlarmClientModel model,
            final AlarmTreePV pv, final boolean parent_changed)
    {
        if (show_only_alarms || pv == null) {
            //if only alarms are shown, redo the whole tree:
            //some PVs might have appeared, some disappeared
            //it is generally faster to refresh everything
            throttle.trigger();
        } else {
            display.asyncExec(new Runnable()
            {
                @Override
                public void run()
                {
                    final Tree tree = tree_viewer.getTree();
                    if (tree.isDisposed())
                        return;
                    if (model.isServerAlive())
                        setErrorMessage(null);
                    // Refresh affected items to indicate new state.
                    // A complete tree_viewer.refresh() would 'work'
                    // but be quite slow, so try to determine what
                    // needs to be refreshed

                    tree_viewer.update(pv, null);
                    if (parent_changed)
                    {    // Update parents up to root
                        AlarmTreeItem item = pv.getParent();
                        while (! (item instanceof AlarmTreeRoot))
                        {
                            // Parent could become hidden with its PV
                               tree_viewer.update(item,null);
                            item = item.getParent();
                        }
                    }
                }
            });
        }
    }

    /** Acknowledge currently selected alarms */
    @SuppressWarnings("unchecked")
    public void acknowledgeSelectedAlarms()
    {
        final List<AlarmTreeItem> items =
            ((IStructuredSelection)tree_viewer.getSelection()).toList();
        for (AlarmTreeItem item : items)
            if (item instanceof AlarmTreePV)
                ((AlarmTreePV)item).acknowledge(true);
    }

    /** Un-acknowledge currently selected alarms */
    @SuppressWarnings("unchecked")
    public void unacknowledgeSelectedAlarms()
    {
        final List<AlarmTreeItem> items =
            ((IStructuredSelection)tree_viewer.getSelection()).toList();
        for (AlarmTreeItem item : items)
            if (item instanceof AlarmTreePV)
                ((AlarmTreePV)item).acknowledge(false);
    }

    /** @return {@link TreeViewer} for alarm tree */
    public TreeViewer getTreeViewer()
    {
        return tree_viewer;
    }
}
