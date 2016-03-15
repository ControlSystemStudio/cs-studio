/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.editparts.AbstractLayoutEditpart;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Handler to handle the layout widgets command which has a key binding of Ctrl+L.
 * @author Xihui Chen
 *
 */
public class LayoutWidgetsHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart activeEditor = page.getActiveEditor();


        if (activeEditor instanceof OPIEditor) {
            ISelection currentSelection =
                ((GraphicalViewer)((OPIEditor)activeEditor).getAdapter(GraphicalViewer.class)).getSelection();
            if(currentSelection instanceof IStructuredSelection){
                Object element = ((IStructuredSelection) currentSelection)
                        .getFirstElement();
                if(element instanceof AbstractLayoutEditpart){
                    CommandStack commandStack =
                        (CommandStack) ((OPIEditor)activeEditor).getAdapter(CommandStack.class);
                    if(commandStack != null)
                        LayoutWidgetsImp.run((AbstractLayoutEditpart)element, commandStack);
                }
            }

        } else {
            return null;
        }


        return null;
    }

}
