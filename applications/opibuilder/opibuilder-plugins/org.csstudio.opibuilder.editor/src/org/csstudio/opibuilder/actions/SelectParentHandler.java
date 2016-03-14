/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.scriptUtil.ConsoleUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to handle the select parent command which has a key binding of
 * Ctrl+R.
 *
 * @author Xihui Chen
 *
 */
public class SelectParentHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        GraphicalViewer viewer = HandlerUtil.getActivePart(event).getAdapter(
                GraphicalViewer.class);
        if (viewer == null)
            return null;

        ISelection currentSelection = viewer.getSelection();

        if (currentSelection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) currentSelection)
                    .getFirstElement();
            if (element instanceof AbstractBaseEditPart
                    && !(element instanceof DisplayEditpart)) {
                if (((AbstractBaseEditPart) element).getParent().isSelectable())
                    ((AbstractBaseEditPart) element).getViewer().select(
                            ((AbstractBaseEditPart) element).getParent());
                else
                    ConsoleUtil
                            .writeWarning("Parent of the selected widget is unselectable. Its grandparent may be locked.");

            }
        }

        return null;
    }

}
