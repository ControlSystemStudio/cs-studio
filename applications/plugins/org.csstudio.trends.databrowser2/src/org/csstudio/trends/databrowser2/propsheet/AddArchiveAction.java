/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.search.AddArchiveDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/** Action that allows addition of archive data sources to PVs.
 *  @author Kay Kasemir
 */
public class AddArchiveAction extends Action
{
    final private OperationsManager operations_manager;
    final private Shell shell;
    final private PVItem pvs[];

    /** Initialize
     *  @param shell Parent shell for dialog
     *  @param pvs PVs to which to add archives
     */
    public AddArchiveAction(final OperationsManager operations_manager,
            final Shell shell, final PVItem pvs[])
    {
        super(Messages.AddArchive,
              Activator.getDefault().getImageDescriptor("icons/archive.gif")); //$NON-NLS-1$
        this.operations_manager = operations_manager;
        this.shell = shell;
        this.pvs = pvs;
    }

    @Override
    public void run()
    {
        final AddArchiveDialog dlg = new AddArchiveDialog(shell);
        if (dlg.open() != Window.OK)
            return;
        new AddArchiveCommand(operations_manager, pvs, dlg.getArchives(), false);
    }
}
