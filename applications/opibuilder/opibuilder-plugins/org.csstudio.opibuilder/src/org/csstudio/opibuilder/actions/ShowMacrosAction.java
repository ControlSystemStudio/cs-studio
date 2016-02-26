/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.util.Map;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**Show the predefined macros of the selected widget in console and message dialog.
 * @author Xihui Chen
 *
 */
public class ShowMacrosAction implements IObjectActionDelegate {

    private IStructuredSelection selection;
    private IWorkbenchPart targetPart;



    public void run(IAction action) {
        AbstractWidgetModel widget = (AbstractWidgetModel) getSelectedWidget().getModel();
        String message = NLS.bind("The predefined macros of {0}:\n", widget.getName());
        StringBuilder sb = new StringBuilder(message);
        Map<String, String> macroMap = OPIBuilderMacroUtil.getWidgetMacroMap(widget);
        for(final Map.Entry<String, String> entry: macroMap.entrySet()){
            sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        sb.append("\n");
        sb.append("Note: Macros are loaded during OPI opening, so this won't reflect the macro changes after opening." +
                "To reflect the latest changes, please reopen the OPI and show macros again.");
        //show the dialog first, because on some linux systems the console print brings the workbench window to top,
        //blocking entire CSS
        MessageDialog.openInformation(targetPart.getSite().getShell(),
                "Predefined Macros", sb.toString());
        ConsoleService.getInstance().writeInfo(sb.toString());
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
        }
    }


    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }


    private EditPart getSelectedWidget(){
        if(selection.getFirstElement() instanceof EditPart){
            return (EditPart)selection.getFirstElement();
        }else
            return null;
    }
}
