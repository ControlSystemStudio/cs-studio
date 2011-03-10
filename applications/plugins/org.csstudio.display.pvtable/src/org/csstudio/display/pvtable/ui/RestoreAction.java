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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that restores values from a snapshot.
 *  @author Kay Kasemir
 */
public class RestoreAction extends PVListModelAction
{
    final private Shell shell;

    public RestoreAction(final Shell shell, final PVListModel pv_list)
    {
        super(pv_list);
        this.shell = shell;
        setText("Restore");
        setToolTipText("Restore values from snapshot");
        setImageDescriptor(Plugin.getImageDescriptor("icons/restore.gif")); //$NON-NLS-1$
    }

    @SuppressWarnings("nls")
    @Override
    public void run()
    {
        PVListModel pv_list = getPVListModel();
        if (pv_list == null)
            return;
        try
        {
            pv_list.restore();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, "Restore Error",
                    NLS.bind("Error restoring values:\n{0}", ex.getMessage()));
        }
    }
}
