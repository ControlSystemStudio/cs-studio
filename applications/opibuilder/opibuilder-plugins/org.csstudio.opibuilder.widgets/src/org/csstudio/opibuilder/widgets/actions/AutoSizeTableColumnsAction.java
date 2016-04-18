/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.widgets.editparts.TableEditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**Auto size columns of the table widget.
 * @author Xihui Chen
 *
 */
public class AutoSizeTableColumnsAction implements IObjectActionDelegate {



    private IStructuredSelection selection;

    public AutoSizeTableColumnsAction() {
    }


    @Override
    public void run(IAction action) {
        TableEditPart tableEditPart = getSelectedWidget();

        tableEditPart.getTable().autoSizeColumns();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
        }
    }

    private TableEditPart getSelectedWidget(){
        if(selection.getFirstElement() instanceof TableEditPart){
            return (TableEditPart)selection.getFirstElement();
        }else
            return null;
    }


    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {

    }

}
