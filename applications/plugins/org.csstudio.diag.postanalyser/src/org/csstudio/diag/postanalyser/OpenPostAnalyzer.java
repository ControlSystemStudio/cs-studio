/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.postanalyser;

import org.csstudio.trends.databrowser2.ProcessVariableWithSamples;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** Open post analyzer from context menu
 *  with {@link ProcessVariableWithSamples}
 *  @author Kay Kasemir
 */
public class OpenPostAnalyzer extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariableWithSamples[] pvs = AdapterUtil.convert(selection, ProcessVariableWithSamples.class);
        View.activateWithPVs(pvs);
        return null;
    }
}
