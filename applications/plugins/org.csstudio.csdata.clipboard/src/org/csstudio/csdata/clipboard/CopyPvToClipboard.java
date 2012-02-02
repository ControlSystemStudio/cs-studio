/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.csdata.clipboard;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/** Copy names of selected {@link ProcessVariable}s to the clipboard.
 *
 *  @author Joerg Rathlev - Original org.csstudio.platform.ui.internal.developmentsupport.util.CopyPvNameToClipboardAction
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CopyPvToClipboard extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pv_names = AdapterUtil.convert(selection, ProcessVariable.class);
        
        // Create string with "PV" or "PV1, PV2, PV3"
        final StringBuilder pvs = new StringBuilder();
        for (ProcessVariable pv : pv_names)
        {
            if (pvs.length() > 0)
                pvs.append(", ");
            pvs.append(pv.getName());
        }
        
        if(pvs.length() ==0){
        	MessageDialog.openError(null, "Empty PV Name", "PV name is empty! Nothing will be copied.");
        	return null;
        }

        // Copy as text to clipboard
        final Clipboard clipboard = new Clipboard(
            PlatformUI.getWorkbench().getDisplay());
        clipboard.setContents(new String[] { pvs.toString() },
            new Transfer[] { TextTransfer.getInstance() });

        return null;
    }
}
