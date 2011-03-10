/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.AbstractPVListModelListener;
import org.csstudio.display.pvtable.model.PVListModel;
import org.csstudio.display.pvtable.model.PVListModelListener;

/** Start PV updates.
 *  <p>
 *  Used by the view, where the PVListModel is known
 *  from the start and stays that way.
 *  Also used by the editor contributor, which sets the pv list to
 *  the one from the current editor.
 *  
 *  @author Kay Kasemir
 */
public class StartStopAction extends PVListModelAction
{
    private PVListModelListener listener;
    
    /** Views initialize with their mode, editor contributor uses null. */
    public StartStopAction(PVListModel pv_list)
    {
        super(pv_list);
        // Update the action's look when the model starts or stops
        listener = new AbstractPVListModelListener()
        {
            @Override
            public void runstateChanged(boolean isRunning)
            {
                update();
            }
        };
        update();
        if (pv_list != null)
            pv_list.addModelListener(listener);
    }

    /** Update the text/image/tooltip to reflect the current state. */
    private void update()
    {
        PVListModel pv_list = getPVListModel();
        if (pv_list == null  ||  pv_list.isRunning())
        {
            setText(Messages.StartStop_Stop);
            setToolTipText(Messages.StartStop_Stop_TT);
            setImageDescriptor(Plugin.getImageDescriptor("icons/off.gif")); //$NON-NLS-1$
        }
        else
        {
            setText(Messages.StartStop_Start);
            setToolTipText(Messages.StartStop_Start_TT);
            setImageDescriptor(Plugin.getImageDescriptor("icons/on.gif")); //$NON-NLS-1$
        }
    }
        
	/** When the model changes, we need to remove/add the listener. */
    @Override
    public void setPVListModel(PVListModel pv_list)
    {
        // Remove from olf model
        if (getPVListModel() != null)
            getPVListModel().removeModelListener(listener);
        // Switch model
        super.setPVListModel(pv_list);
        // connect to new model
        if (pv_list != null)
            pv_list.addModelListener(listener);
        update();
    }

    @Override
    public void run()
    {
        PVListModel pv_list = getPVListModel();
	    if (pv_list == null)
            return;
        if (pv_list.isRunning())
            pv_list.stop();
        else
            pv_list.start();
    }
}
