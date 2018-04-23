/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import org.apache.commons.lang3.SerializationUtils;
import org.csstudio.alarm.beast.msghist.MessageHistoryView;
import org.csstudio.alarm.beast.msghist.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;

/**
 * <code>ColumnConfigureAction</code> opens a column configuration dialog and allows configuring which column are displayed in the
 * table as well as their width and weight.
 *
 * @author Borut Terpinc
 */
public class ColumnConfigureAction extends Action {
    private final MessageHistoryView view;

    /**
     * Constructs a new action that acts on the message history view.
     *
     * @param view
     *            the view to configure its columns
     */
    public ColumnConfigureAction(final MessageHistoryView view) {
        super(Messages.ConfigureColumns);
        this.view = view;
    }

    @Override
    public void run() {
        ColumnConfigurer configurer = new ColumnConfigurer(view.getViewSite().getShell(),
                SerializationUtils.clone(view.getColumns()));
        if (configurer.open() == IDialogConstants.OK_ID) {
            view.setColumns(configurer.getColumns());
        }
    }
}