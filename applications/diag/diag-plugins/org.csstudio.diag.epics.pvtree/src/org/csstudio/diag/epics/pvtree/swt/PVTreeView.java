/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree.swt;

import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.autocomplete.ui.AutoCompleteTypes;
import org.csstudio.autocomplete.ui.AutoCompleteWidget;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.diag.epics.pvtree.CollapseTreeAction;
import org.csstudio.diag.epics.pvtree.ExpandAlarmTreeAction;
import org.csstudio.diag.epics.pvtree.ExpandTreeAction;
import org.csstudio.diag.epics.pvtree.Messages;
import org.csstudio.diag.epics.pvtree.TreeModeAction;
import org.csstudio.diag.epics.pvtree.TreeValueUpdateThrottle;
import org.csstudio.diag.epics.pvtree.model.TreeModel;
import org.csstudio.diag.epics.pvtree.model.TreeModelItem;
import org.csstudio.diag.epics.pvtree.model.TreeModelListener;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/** Eclipse ViewPart for the EPICS PV Tree.
 */
public class PVTreeView extends ViewPart
{
    /** View ID, defined in plugin.xml */
    public static final String ID = "org.csstudio.diag.epics.pvtree.PVTreeView";

    /** (Numeric) secondary view ID */
    final private static AtomicInteger instance = new AtomicInteger();

    // Memento tags
    private static final String PV_TAG = "pv"; //$NON-NLS-1$

    private IMemento memento;

    /** The root PV name. */
    private Text pv_name;

    private final TreeModel model = new TreeModel();

    private TreeViewer viewer;

    private TreeValueUpdateThrottle<TreeModelItem> value_throttle;

    /** Allows 'zoom in' and then going back up via context menu. */
    private DrillDownAdapter drillDownAdapter;

    private final TreeModelListener model_listener = new TreeModelListener()
    {
        @Override
        public void itemLinkAdded(TreeModelItem item, TreeModelItem link)
        {
            /// Refresh the tree from the item on down
            final Tree tree = viewer.getTree();
            if (tree.isDisposed())
                return;
            tree.getDisplay().asyncExec(() ->
            {
                if (tree.isDisposed())
                    return;
                if (item == model.getRoot())
                    viewer.refresh();
                else
                    viewer.refresh(item);
            });

            link.start();
        }

        @Override
        public void itemChanged(TreeModelItem item)
        {
            value_throttle.scheduleUpdate(item);
        }

        @Override
        public void latchStateChanged(final boolean latched)
        {
            final Tree tree = viewer.getTree();
            if (tree.isDisposed())
                return;
            tree.getDisplay().asyncExec(() ->
            {
                if (tree.isDisposed())
                    return;
                if (latched)
                    tree.setBackground(tree.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
                else
                    tree.setBackground(null);
            });
        }

        @Override
        public void allLinksResolved()
        {
            final Tree tree = viewer.getTree();
            if (tree.isDisposed())
                return;
            tree.getDisplay().asyncExec(() ->
            {
                if (tree.isDisposed())
                    return;
                viewer.expandAll();
            });
        }
    };

    /** @return New unique code to allow multiple instances of this view */
    public static String newInstance()
    {
        return Integer.toString(instance.incrementAndGet());
    }

    /** ViewPart interface, keep the memento. */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento = memento;
    }

    /** ViewPart interface, persist state */
    @Override
    public void saveState(IMemento memento)
    {
        super.saveState(memento);
        memento.putString(PV_TAG, pv_name.getText());
    }

    /** Create the GUI. */
    @Override
    public void createPartControl(final Composite parent)
    {
        createGUI(parent);

        hookContextMenu();

        final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add(new TreeModeAction(model));
        toolbar.add(new CollapseTreeAction(viewer));
        toolbar.add(new ExpandAlarmTreeAction(viewer));
        toolbar.add(new ExpandTreeAction(viewer));
    }

    private void createGUI(final Composite parent)
    {
        GridLayout gl = new GridLayout();
        gl.numColumns = 2;
        parent.setLayout(gl);
        GridData gd;

        Label l = new Label(parent, SWT.LEFT);
        l.setText(Messages.PV_Label);
        gd = new GridData();
        l.setLayoutData(gd);

        pv_name = new Text(parent, SWT.LEFT | SWT.BORDER);
        pv_name.setToolTipText(Messages.PV_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        pv_name.setLayoutData(gd);
        pv_name.addListener(SWT.DefaultSelection, new Listener()
        {
            @Override
            public void handleEvent(Event e)
            {
                setPVName(pv_name.getText());
            }
        });
        new AutoCompleteWidget(pv_name, AutoCompleteTypes.PV);

        final Tree tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        gd = new GridData();
        gd.horizontalSpan = gl.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        tree.setLayoutData(gd);

        viewer = new TreeViewer(tree);
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new TreeModelContentProvider(viewer, model));
        viewer.setLabelProvider(new PVTreeLabelProvider(tree));
        // One and only model _is_ the content.
        // Setting the Input will trigger refresh,
        // but the exact 'Input' doesn't really matter:
        // Model used as content provider will ignore it.
        viewer.setInput(model);

        value_throttle = new TreeValueUpdateThrottle<TreeModelItem>(items ->
        {
            tree.getDisplay().asyncExec(() ->
            {
                if (tree.isDisposed())
                    return;
                for (TreeModelItem item : items)
                    viewer.update(item, null);
            });

        });

        model.addListener(model_listener);

        // Support drop
        new ControlSystemDropTarget(parent, ProcessVariable.class, String.class)
        {
            @Override
            public void handleDrop(final Object item)
            {
                if (item instanceof ProcessVariable)
                    setPVName(((ProcessVariable) item).getName());
                else
                    setPVName((String) item);
            }
        };

        // Stop the press when we're no more
        pv_name.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                model.dispose();
                value_throttle.shutdown();
                // pv_name_helper.saveSettings();
            }
        });

        if (memento != null)
        {
            final String pv_name = memento.getString(PV_TAG);
            if (pv_name != null  &&  pv_name.length() > 0)
                setPVName(pv_name);
        }
    }

    /** Set initial focus. */
    @Override
    public void setFocus()
    {
        pv_name.setFocus();
    }

    /** Update the tree with information for a newly entered PV name. */
    public void setPVName(String new_pv_name)
    {
        new_pv_name = new_pv_name.trim();
        if (! pv_name.getText().equals(new_pv_name))
            pv_name.setText(new_pv_name);

        value_throttle.clearPendingUpdates();
        model.dispose();

        model.setRootPV(new_pv_name);
        viewer.refresh();
        model.getRoot().start();
    }

    private void hookContextMenu()
    {
        final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            @Override
            public void menuAboutToShow(IMenuManager manager)
            {
                PVTreeView.this.fillContextMenu(manager);
            }
        });
        final Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(final IMenuManager manager)
    {
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute their actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
}
