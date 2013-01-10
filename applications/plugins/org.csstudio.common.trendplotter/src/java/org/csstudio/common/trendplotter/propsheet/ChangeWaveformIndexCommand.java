/*******************************************************************************
 * Copyright (c) 2012 Cosylab.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.propsheet;

import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.ModelItem;
import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;

/** Undo-able command to change item's waveform index
 *  @author Takashi Nakamoto (Cosylab)
 */
public class ChangeWaveformIndexCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private int old_index, new_index;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param new_index New value
     */
    public ChangeWaveformIndexCommand(final OperationsManager operations_manager,
            final ModelItem item, final int new_index)
    {
        this.item = item;
        this.old_index = item.getWaveformIndex();
        this.new_index = new_index;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        item.setWaveformIndex(new_index);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        item.setWaveformIndex(old_index);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.WaveformIndexColTT;
    }
}
