/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import org.csstudio.scan.ui.scantree.ScanEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/** Handler for the Undo command
 *  @author Kay Kasemir
 */
public class UndoHandler  extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ScanEditor editor = ScanEditor.getActiveEditor();
        Shell shell = (editor == null) ? null : editor.getSite().getShell();
        MessageDialog.openInformation(shell, "Undo", "TODO");

        setBaseEnabled(false);
        return null;
    }
}
