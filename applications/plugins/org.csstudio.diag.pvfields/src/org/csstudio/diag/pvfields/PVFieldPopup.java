/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.diag.pvfields.view.PVFieldsView;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** Handle activation of Fields Table from the object contrib. context menu.
 *  @author Kay Kasemir
 */
public class PVFieldPopup extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
        // Open my view, display the PVs, ...        return null;
        if (pvs != null  &&  pvs.length > 0)
            PVFieldsView.activateWithPV(pvs[0]);
        return null;
    }
}
