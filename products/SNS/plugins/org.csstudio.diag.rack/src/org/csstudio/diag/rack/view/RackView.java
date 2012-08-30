/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.rack.view;

import java.util.logging.Level;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.diag.rack.Activator;
import org.csstudio.diag.rack.gui.GUI;
import org.csstudio.diag.rack.model.RackModel;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class RackView extends ViewPart
	{
	    final public static String ID = RackView.class.getName();
	    private RackModel control = null;
	    private GUI gui;

	    public RackView()
	    {
        	try
	        {
        		control = new RackModel();
	        }
	        catch (Exception ex)
	        {
	            Activator.getLogger().log(Level.SEVERE, "Rack model exception", ex); //$NON-NLS-1$
	        }
    	}


	    @Override
	    public void createPartControl(Composite parent)
	    {
	        if (control != null)
	        {
	            gui = new GUI(parent, control);

	            // Enable 'Drop'
	            final Text dvcOrPVFilter = gui.getDVCOrPVEntry();
	            new ControlSystemDropTarget(dvcOrPVFilter, ProcessVariable.class)
                {
                    @Override
                    public void handleDrop(final Object item)
                    {
                        if (item instanceof ProcessVariable)
                        {
                            final ProcessVariable pv = (ProcessVariable) item;
                            setDVCOrPVFilter(pv.getName());
                        }
                    }
                };
	        }
	    }


	    @Override
	    public void setFocus()
	    {
	    	gui.setFocus();
	    }

	    public static boolean activateWithPV(final ProcessVariable pv_name)
	    {
	        try
	        {
	            IWorkbench workbench = PlatformUI.getWorkbench();
	            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	            IWorkbenchPage page = window.getActivePage();
	            RackView rack_view = (RackView) page.showView(RackView.ID);
	            rack_view.setDVCOrPVFilter(pv_name.getName());
	        }
	        catch (Exception ex)
	        {
                Activator.getLogger().log(Level.SEVERE, "Rack View activation error", ex); //$NON-NLS-1$
	        }
	        return false;
	    }

	    public void setDVCOrPVFilter(String pv_name)
	    {
	    	gui.rackList.deselectAll();
	    	gui.getDVCOrPVEntry().setText(pv_name);
	        control.setSelectedRack(pv_name);

	    }
	}
