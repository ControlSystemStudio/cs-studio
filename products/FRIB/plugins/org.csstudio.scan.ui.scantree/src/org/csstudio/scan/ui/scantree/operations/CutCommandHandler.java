/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.gui.ScanEditorContributor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/** Handler to remove selected commands from tree,
 *  putting it onto clipboard
 *  @author Kay Kasemir
 */
public class CutCommandHandler extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        final ScanEditor editor = ScanEditorContributor.getCurrentScanEditor();
        if (editor != null)
        {
            // Execute the 'cut'
            final List<ScanCommand> to_remove = editor.getSelectedCommands();
            editor.executeForUndo(new CutOperation(editor.getModel(), to_remove));
        }
        return null;
    }
}
