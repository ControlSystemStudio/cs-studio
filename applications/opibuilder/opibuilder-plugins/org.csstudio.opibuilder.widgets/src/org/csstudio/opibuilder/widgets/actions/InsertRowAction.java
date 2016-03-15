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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**Insert a row on a Table widget.
 * @author Xihui Chen
 *
 */
public class InsertRowAction implements IObjectActionDelegate {

    private class InsertRowDialog extends Dialog {
        private boolean isBefore;

        /**
         * Create the dialog.
         * @param parentShell
         */
        public InsertRowDialog(Shell parentShell) {
            super(parentShell);
        }

        /**
         * Create contents of the dialog.
         * @param parent
         */
        @Override
        protected Control createDialogArea(Composite parent) {
            getShell().setText("Insert Row");
            Composite container = (Composite) super.createDialogArea(parent);

            Group grpPosition = new Group(container, SWT.NONE);
            grpPosition.setText("Insert");
            GridData gd_grpPosition = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
            grpPosition.setLayoutData(gd_grpPosition);
            FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
            fillLayout.marginHeight=5;
            fillLayout.marginWidth = 5;
            fillLayout.spacing = 5;
            grpPosition.setLayout(fillLayout);

            final Button beforeRadio = new Button(grpPosition, SWT.RADIO);
            beforeRadio.setText("Before this row");
            beforeRadio.setSelection(true);
            isBefore = true;

            beforeRadio.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    isBefore = beforeRadio.getSelection();
                }
            });

            Button afterRadio = new Button(grpPosition, SWT.RADIO);
            afterRadio.setText("After this row");

            return container;
        }

        public boolean isBefore(){
            return isBefore;
        }
    }

    private IStructuredSelection selection;
    private IWorkbenchPart targetPart;

    public InsertRowAction() {
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    @Override
    public void run(IAction action) {
        TableEditPart tableEditPart = getSelectedWidget();
        if(tableEditPart.getTable().isEmpty()){
            if(tableEditPart.getTable().getColumnCount() ==0)
                tableEditPart.getTable().insertColumn(0);
            tableEditPart.getTable().insertRow(0);
            return;
        }

        InsertRowDialog dialog = new InsertRowDialog(targetPart.getSite().getShell());
        if (dialog.open() == Dialog.OK){
            boolean before = dialog.isBefore();
            tableEditPart.getTable().insertRow(tableEditPart.getMenuTriggeredCell().x + (before? 0:1));
        }

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

}
