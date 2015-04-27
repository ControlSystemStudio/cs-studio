/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for the {@link GUI}
 *  @author Kay Kasemir
 */
public class PVFieldsView  extends ViewPart
{
    final public static String ID = PVFieldsView.class.getName();
    /** Memento tag */
    private static final String PV_TAG = "pv"; //$NON-NLS-1$

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
    public void saveState(final IMemento memento)
    {
        super.saveState(memento);
        memento.putString(PV_TAG, gui.getPVName());
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(Composite parent)
    {
    	gui = new GUI(parent, Activator.getDefault().getDialogSettings(), getSite());
    	
    	// Restore
    	if (memento != null)
        {
            String pv_name = memento.getString(PV_TAG);
            if (pv_name != null  &&  pv_name.length() > 0)
                setPVName(pv_name);
        }
    }

    /** Create or re-display a probe view with the given PV name.
     *  <p>
     *  Invoked by the PVpopupAction.
     *
     *  @param pv_name The PV to 'probe'
     *  @return Returns <code>true</code> when successful.
     */
    public static boolean activateWithPV(final ProcessVariable pv_name)
    {
        try
        {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            final PVFieldsView pvFields = (PVFieldsView) page.showView(PVFieldsView.ID);
            pvFields.setPVName(pv_name.getName());
            return true;
        }
        catch (Exception e)
        {
            Activator.getLogger().log(Level.SEVERE, "PVFieldsView activation error", e); //$NON-NLS-1$
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        if (gui != null)
            gui.setFocus();
    }

    /** @param pv_name PV name to display */
    private void setPVName(final String pv_name)
    {
        gui.setPVName(pv_name);
    }
}
