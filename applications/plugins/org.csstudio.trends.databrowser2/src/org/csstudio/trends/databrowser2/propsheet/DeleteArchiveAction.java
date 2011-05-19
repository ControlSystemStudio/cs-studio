/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Context menu action that removes archive data sources from a PV item
 *  @author Kay Kasemir
 */
public class DeleteArchiveAction extends Action
{
    final private OperationsManager operations_manager;
    final private PVItem pv;
    final private ArchiveDataSource archives[];
    
    /** Initialize
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param pv PV from which to delete archives
     *  @param archives Archive data sources to remove
     */
    public DeleteArchiveAction(final OperationsManager operations_manager,
            final PVItem pv, final ArchiveDataSource archives[])
    {
        super(Messages.DeleteArchive,
              PlatformUI.getWorkbench().getSharedImages()
                      .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        this.operations_manager = operations_manager;
        this.pv = pv;
        this.archives = archives;
    }

    @Override
    public void run()
    {
        // Delete via undo-able command
        new DeleteArchiveCommand(operations_manager, pv, archives);
    }
}
