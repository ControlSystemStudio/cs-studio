/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.probe;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** Command handler for opening probe on the current selection.
 *  Linked from popup menu that is sensitive to {@link ProcessVariable}
 *  @author Kay Kasemir
 */
public class OpenProbe extends AbstractHandler implements IHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pvs = AdapterUtil.convert(selection,
                ProcessVariable.class);
        if (pvs == null  ||  pvs.length <= 0)
            Probe.activateWithPV(null);
        else
            for (ProcessVariable pv : pvs)
                Probe.activateWithPV(pv);
        return null;
    }
}
