/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.view;

import java.util.logging.Level;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.diag.pvfields.Activator;
import org.csstudio.diag.pvfields.gui.GUI;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class PVFieldsView  extends ViewPart
{
    final public static String ID = PVFieldsView.class.getName();
    /** Memento tag */
    private static final String PV_TAG = "pv"; //$NON-NLS-1$
    final public static String PV_LIST_TAG = "pv_list"; //$NON-NLS-1$

    private GUI gui;
    private IMemento memento;

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
        // TODO
//        memento.putString(PV_TAG, gui.getPVName());
//        memento.putString(FIELD_TAG, gui.getFieldValue());
    }

    @Override
    public void createPartControl(Composite parent)
    {
    	gui = new GUI(parent,  Activator.getDefault().getDialogSettings());
    	
    	// TODO Restore
//    	if (memento != null)
//        {
//            String pv_name = memento.getString(PV_TAG);
//            String field = memento.getString(FIELD_TAG);
//            if (field != null  &&  field.length() > 0 )
//                field_value.getCombo().setText(field);
//            if (pv_name != null  &&  pv_name.length() > 0)
//                setPVName(pv_name);
//        }

    	// TODO Enable 'Drop' on to combo box (entry box)
//        new ControlSystemDropTarget(cbo_name.getCombo(), ProcessVariable.class, String.class)
//        {
//            @Override
//            public void handleDrop(final Object item)
//            {
//                PVFieldsView.this.handleDrop(item);
//            }
//        };
//
//        final Table fields_table = gui.getFieldsTable().getTable();
//
//        // Enable 'Drop' on to table.
//        new ControlSystemDropTarget(fields_table, ProcessVariable.class, String.class)
//        {
//            @Override
//            public void handleDrop(final Object item)
//            {
//                PVFieldsView.this.handleDrop(item);
//            }
//        };

        // TODO Add empty context menu so that other CSS apps can
        // add themselves to it
        final MenuManager menuMgr = new MenuManager("");
//        menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//        Menu menu = menuMgr.createContextMenu(fields_viewer.getControl());
//        fields_viewer.getControl().setMenu(menu);
//        getSite().registerContextMenu(menuMgr, fields_viewer);
    }

    /** Set PV name from dropped item, if possible
     *  @param item ProcessVariable or String
     */
    private void handleDrop(final Object item)
    {
        if (item instanceof ProcessVariable)
            setPVName(((ProcessVariable)item).getName());
        else if (item instanceof String)
            setPVName(item.toString().trim());
    }

    /** Create or re-display a probe view with the given PV name.
     *  <p>
     *  Invoked by the PVpopupAction.
     *
     *  @param pv_name The PV to 'probe'
     *  @return Returns <code>true</code> when successful.
     */
    public static boolean activateWithPV(ProcessVariable pv_name)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            PVFieldsView pvFields = (PVFieldsView) page.showView(PVFieldsView.ID);
            pvFields.setPVName(pv_name.getName());
            return true;
        }
        catch (Exception e)
        {
            Activator.getLogger().log(Level.SEVERE, "PVFieldsView activation error", e); //$NON-NLS-1$
        }
        return false;
    }

    // ViewPart interface
    @Override
    public void setFocus()
    {
        if (gui != null)
            gui.setFocus();
    }

    private void setPVName(final String pv_name)
    {
        gui.setPVName(pv_name);
    }
}
