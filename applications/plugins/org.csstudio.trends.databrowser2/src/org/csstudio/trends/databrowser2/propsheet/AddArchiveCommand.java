/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.PVItem;

/** Undo-able command to add or replace archive data sources.
 *  @author Kay Kasemir
 */
public class AddArchiveCommand implements IUndoableCommand
{
    /** PVs on which to set archive data sources */
    final private PVItem pvs[];

    /** Desired archives for all PVs */
    final private ArchiveDataSource archives[];

    /** Original archives before add/replace */
    final private ArchiveDataSource original[][];

    /** Replace original archives? Else: add */
    final private boolean replace;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param pv PV where to add archive
     *  @param archive Archive data source to add
     *  @p
     */
    public AddArchiveCommand(final OperationsManager operations_manager,
            final PVItem pv, final ArchiveDataSource archive)
    {
        this(operations_manager, new PVItem[] { pv }, new ArchiveDataSource[] { archive }, false);
    }

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param pvs PVs where to add archives
     *  @param archives Archive data sources to add
     *  @param replace <code>true</code> to replace existing archive data sources. Else: 'add'
     */
    public AddArchiveCommand(final OperationsManager operations_manager,
            final PVItem pvs[], final ArchiveDataSource archives[], final boolean replace)
    {
        this.pvs = pvs;
        this.archives = archives;
        // Remember original archives
        original = new ArchiveDataSource[pvs.length][];
        for (int i=0; i<original.length; ++i)
            original[i] = pvs[i].getArchiveDataSources();
        this.replace = replace;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        if (replace)
            for (PVItem pv : pvs)
                pv.setArchiveDataSource(archives);
        else // Add
            for (PVItem pv : pvs)
                pv.addArchiveDataSource(archives);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        for (int i=0; i<original.length; ++i)
            pvs[i].setArchiveDataSource(original[i]);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.AddArchive;
    }
}
