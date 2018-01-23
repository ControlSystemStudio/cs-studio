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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * <code>NewTableAction</code> opens a new Alarm Table View.
 *
 * @author Borut Terpinc
 *
 */
public class NewMessageHistoryAction extends Action {
    private final MessageHistoryView view;

    /**
     * Construct a new action.
     *
     * @param view
     *            the view that owns this action
     */
    public NewMessageHistoryAction(final MessageHistoryView view) {
        super(Messages.NewMessageHistoryView);
        this.view = view;
    }

    @Override
    public void run() {
        try {
            view.getViewSite().getPage().showView(view.getViewSite().getId(), MessageHistoryView.newSecondaryID(view),
                    IWorkbenchPage.VIEW_ACTIVATE);
        } catch (PartInitException e) {
            MessageDialog.openError(view.getViewSite().getShell(), Messages.MessageHistoryOpenErrorTitle,
                    NLS.bind(Messages.MessageHistoryOpenErrorMessage, e.getMessage()));
        }
    }
}
