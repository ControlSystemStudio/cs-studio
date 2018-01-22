/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import org.csstudio.alarm.beast.msghist.MessageHistoryView;
import org.csstudio.alarm.beast.msghist.Messages;
import org.csstudio.alarm.beast.msghist.model.FilterQuery;
import org.csstudio.alarm.beast.msghist.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

/**
 * <code>ShowFilterAction</code> opens a dialog through which user can type in the clause used for message history filtering.
 *
 * @author Borut Terpinc
 */
public class ShowFilterAction extends Action {

    private final MessageHistoryView view;

    public ShowFilterAction(MessageHistoryView view) {
        super(Messages.SelectFilter, AS_PUSH_BUTTON);
        this.view = view;
    }

    @Override
    public void run() {
        // open dialog with initial filter value
        Model model = view.getModel();
        String initialQuery = FilterQuery.fromModel(model);
        InputDialog dialog = new InputDialog(view.getViewSite().getShell(), Messages.SelectFilterDialogTitle,
                Messages.SelectFilterDialogMessage, initialQuery.toString(), FilterQuery::validateQuery);

        // set new filter values on dialog confirm
        if (dialog.open() == Window.OK) {
            try {
                String query = dialog.getValue().trim();
                FilterQuery.apply(query, model);
            } catch (Exception e) {
                MessageDialog.openError(view.getViewSite().getShell(), "Error", "Error in filter:\n" + e.getMessage());
            }
        }
    }

}
