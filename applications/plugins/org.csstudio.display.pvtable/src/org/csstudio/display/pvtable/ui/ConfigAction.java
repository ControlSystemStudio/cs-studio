/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.PVListModel;


/** Action that adds a new PV to the model.
 *  @author Kay Kasemir
 */
public class ConfigAction extends PVListModelAction
{
    public ConfigAction(PVListModel pv_list)
    {
        super(pv_list);
        setText("Config");
        setToolTipText("Configure PV Table");
        setImageDescriptor(Plugin.getImageDescriptor("icons/config.gif")); //$NON-NLS-1$
    }

    @Override
    public void run()
    {
        PVListModel pv_list = getPVListModel();
        if (pv_list == null)
            return;
        
        ConfigDialog dlg = new ConfigDialog(null,
                pv_list.getDescription(),
                pv_list.getTolerance(),
                pv_list.getUpdatePeriod());
        if (dlg.open() == ConfigDialog.OK)
        {
            pv_list.setDescription(dlg.getDescription());
            pv_list.setTolerance(dlg.getTolerance());
            pv_list.setUpdatePeriod(dlg.getUpdatePeriod());
        }
    }
}
