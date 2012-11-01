/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.view;

import java.util.Arrays;
import java.util.logging.Level;

import org.csstudio.csdata.Device;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.diag.pvutil.Activator;
import org.csstudio.diag.pvutil.gui.GUI;
import org.csstudio.diag.pvutil.model.PV;
import org.csstudio.diag.pvutil.model.PVUtilModel;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** Eclipse "View" for the PV Utility
 *  <p>
 *  Creates the PVUtilDataAPI and displays the GUI within an Eclipse "View" site.
 *  @author 9pj
 */
@SuppressWarnings("nls")
public class PVUtilView extends ViewPart
{
    // View ID defined in plugin.xml
    final public static String ID = "org.csstudio.diag.pvutil.view.PVUtilView";
    private PVUtilModel model = null;
    private GUI gui;

    public PVUtilView()
    {
        try
        {
            model = new PVUtilModel();
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, "PVUtilModel error", ex);
        }
    }

    @Override
    public void createPartControl(Composite parent)
    {
        if (model == null)
        {
            new Label(parent, 0).setText("Cannot initialize"); //$NON-NLS-1$
            return;
        }
        gui = new GUI(parent, model);

        // Allow Eclipse to listen to PV selection changes
        final TableViewer pv_table = gui.getPVTableViewer();
        getSite().setSelectionProvider(pv_table);

        // Allow dragging PV names
        new ControlSystemDragSource(pv_table.getTable())
        {
            @Override
            public Object getSelection()
            {
                final IStructuredSelection selection = (IStructuredSelection) pv_table.getSelection();
                final Object[] objs = selection.toArray();
                final PV[] pvs = Arrays.copyOf(objs, objs.length, PV[].class);
                return pvs;
            }
        };

        // Enable 'Drop'
        final Text pv_filter = gui.getPVFilterText();
        new ControlSystemDropTarget(pv_filter, ProcessVariable.class, String.class)
        {
            @Override
            public void handleDrop(final Object item)
            {
                if (item instanceof ProcessVariable)
                    setPVFilter(((ProcessVariable)item).getName());
                else if (item instanceof String)
                    setPVFilter(item.toString().trim());
            }
        };

        // Add empty context menu so that other CSS apps can
        // add themselves to it
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Menu menu = menuMgr.createContextMenu(pv_table.getControl());
        pv_table.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, pv_table);

        // Add context menu to the name table.
        // One reason: Get object contribs for the NameTableItems.
        IWorkbenchPartSite site = getSite();
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Menu contextMenu = manager.createContextMenu(pv_table.getControl());
        pv_table.getControl().setMenu(contextMenu);
        site.registerContextMenu(manager, pv_table);
    }

    @Override
    public void setFocus()
    {
        gui.setFocus();
    }

    public static boolean activateWithPV(ProcessVariable pv_name)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            PVUtilView pv_view = (PVUtilView) page.showView(PVUtilView.ID);
            pv_view.setPVFilter(pv_name.getName());
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, "PVUtil activation error", ex);
        }
        return false;
    }

    public static boolean activateWithDevice(Device device_name)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            PVUtilView pv_view = (PVUtilView) page.showView(PVUtilView.ID);
            pv_view.setDeviceFilter(device_name.getName());
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, "PVUtil activation error", ex);
        }
        return false;
    }

    private void setPVFilter(final String pv_name)
    {
    	gui.getDeviceFilterText().setText("");
        model.setFECFilter("");
    	gui.getPVFilterText().setText(pv_name);
        model.setPVFilter(pv_name);

    }

    private void setDeviceFilter(final String device_name)
    {
        gui.getDeviceFilterText().setText(device_name);
        model.setFECFilter(device_name);
    }
}
