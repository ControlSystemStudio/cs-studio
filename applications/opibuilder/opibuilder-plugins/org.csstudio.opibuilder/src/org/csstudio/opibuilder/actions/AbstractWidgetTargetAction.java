/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**The abstract action which will be performed on a widget target.
 * @author Xihui Chen
 *
 */
public abstract class AbstractWidgetTargetAction  implements IObjectActionDelegate {

    protected IWorkbenchPart targetPart;
    /**
     * The current selection.
     */
    protected IStructuredSelection selection;

    public AbstractWidgetTargetAction() {
        super();
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * Executes the given {@link Command} using the command stack.  The stack is obtained by
     * calling {@link #getCommandStack()}, which uses <code>IAdapatable</code> to retrieve the
     * stack from the workbench part.
     * @param command the command to execute
     */
    protected void execute(Command command) {
        if (command == null || !command.canExecute() || getCommandStack() == null)
            return;
        getCommandStack().execute(command);
    }

    /**
     * Returns the editor's command stack. This is done by asking the workbench part for its
     * CommandStack via
     * {@link org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)}.
     * @return the command stack
     */
    protected CommandStack getCommandStack() {
        return targetPart.getAdapter(CommandStack.class);
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
        }
    }

    protected IStructuredSelection getSelection(){
        if(selection !=null)
            return selection;
        else
             return  StructuredSelection.EMPTY;
    }

}
