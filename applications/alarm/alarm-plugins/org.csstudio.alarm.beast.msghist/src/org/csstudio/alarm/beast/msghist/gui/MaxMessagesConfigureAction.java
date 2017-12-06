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
import org.csstudio.alarm.beast.msghist.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

/**
 * <code>MaxMessagesConfigureAction</code> opens a dialog that allows
 * configuring maximum number of messages shown.
 *
 * @author Borut Terpinc
 */
public class MaxMessagesConfigureAction extends Action {
    private final MessageHistoryView view;

    /**
     * Constructs a new action that acts on the message history view.
     *
     * @param view
     *            the view to configure its columns
     */
    public MaxMessagesConfigureAction(final MessageHistoryView view) {
        super(Messages.SetMaxMessages, AS_PUSH_BUTTON);
        this.view = view;
    }

    @Override
    public void run() {
        // open dialog with current max messages value
        Model model = view.getModel();
        InputDialog dialog = new InputDialog(view.getViewSite().getShell(), Messages.SetMaxMessagesDialogTitle,
                Messages.SetMaxMessagesDialogMessage, Integer.toString(model.getMaxMessages()), s -> {
                    try {
                        int i = Integer.parseInt(s.trim());
                        if (i > 0)
                            return null;
                        return Messages.SetMaxMessagesInputError;
                    } catch (NumberFormatException ex) {
                        return Messages.SetMaxMessagesInputError;
                    }

                });

        // set new filter values on dialog confirm
        if (dialog.open() == Window.OK) {
            try {
                int maxMessages = Integer.parseInt(dialog.getValue().trim());
                model.setMaxMessages(maxMessages);
            } catch (Exception e) {
                MessageDialog.openError(view.getViewSite().getShell(), Messages.Error,
                        Messages.SetMaxMessagesError + e.getMessage());
            }
        }
    }
}