/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.ui.editor.PVTableEditor;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Another application sent us a PV name via its popup menu.
 *
 * @author Kay Kasemir
 */
public class PVpopupAction extends AbstractHandler {
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final PVTableEditor editor = PVTableEditor.createPVTableEditor();
        if (editor != null) {
            final PVTableModel model = editor.getModel();
            final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
            final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
            for (ProcessVariable pv : pvs)
                model.addItem(pv.getName());
            // Trigger refresh of the viewer
            editor.getTableViewer().setInput(model);
        }
        return null;
    }
}
