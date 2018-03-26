/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.alarm.beast.msghist.gui.ColumnConfigureAction;
import org.csstudio.alarm.beast.msghist.gui.GUI;
import org.csstudio.alarm.beast.msghist.gui.MaxMessagesConfigureAction;
import org.csstudio.alarm.beast.msghist.gui.NewMessageHistoryAction;
import org.csstudio.alarm.beast.msghist.gui.ShowFilterAction;
import org.csstudio.alarm.beast.msghist.model.FilterQuery;
import org.csstudio.alarm.beast.msghist.model.Message;
import org.csstudio.alarm.beast.msghist.model.Model;
import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Eclipse View for the Message History
 *
 * @author Kay Kasemir
 * @author Xihui Chen
 * @author Borut Terpinc
 */
public class MessageHistoryView extends ViewPart {
    /** ID under which this view is registered in plugin.xml */
    final public static String ID = "org.csstudio.alarm.beast.msghist.MessageHistoryView"; //$NON-NLS-1$

    /** Memento tag for storing filter */
    final public static String M_FILTER = "msghist_filter";
    /** Memento tag for storing columns */
    final public static String M_COLUMNS = "msghist_columns";
    /** Memento tag for storing max. messages */
    final public static String M_MAX_MESSAGES = "msghist_max_messages";

    private static AtomicInteger secondaryId = new AtomicInteger(1);

    private Model model;
    private GUI gui;
    private Composite parent;
    private PropertyColumnPreference[] columns;
    private IMemento memento;

    /**
     * Return the next secondary id that has not been opened.
     *
     * @return part
     */
    public static String newSecondaryID(IViewPart part) {
        while (part.getSite().getPage().findViewReference(part.getSite().getId(),
                String.valueOf(secondaryId.get())) != null) {
            secondaryId.incrementAndGet();
        }

        return String.valueOf(secondaryId.get());
    }

    public Model getModel() {
        return model;
    }

    public PropertyColumnPreference[] getColumns() {
        return columns;
    }

    /**
     * Sets column properties and recreates GUI to show changes.
     *
     * @param columns
     *            column properties
     */
    public void setColumns(PropertyColumnPreference[] columns) {
        this.columns = columns;
        parent.getDisplay().asyncExec(() -> {
            try {
                createGUI();
            } catch (Exception e) {
                MessageDialog.openError(parent.getShell(), "Error", e.getMessage());
            }
        });
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        // restore columns from memento
        this.memento = memento;
        try {
            columns = Preferences.getPropertyColumns();
            if (memento != null && memento.getChild(M_COLUMNS) != null)
                columns = PropertyColumnPreference.restoreColumns(memento.getChild(M_COLUMNS), columns);
        } catch (Exception e) {
            MessageDialog.openError(site.getShell(), Messages.Error, e.getMessage());
        }
        super.init(site, memento);
    }

    @SuppressWarnings("nls")
    @Override
    public void createPartControl(final Composite parent) {
        try {
            this.parent = parent;

            // Read settings from preferences
            final IPreferencesService service = Platform.getPreferencesService();
            final String url = service.getString(Activator.ID, "rdb_url", null, null);
            final String user = SecurePreferences.get(Activator.ID, "rdb_user", null);
            final String password = SecurePreferences.get(Activator.ID, "rdb_password", null);
            final String schema = service.getString(Activator.ID, "rdb_schema", null, null);

            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(Preferences.getTimeFormat())
                    .withZone(ZoneId.systemDefault());

            // restore max messages from memento
            int maxMessages = Preferences.getMaxMessages();
            if (memento != null && memento.getInteger(M_MAX_MESSAGES) != null)
                maxMessages = memento.getInteger(M_MAX_MESSAGES);

            model = new Model(url, user, password, schema, maxMessages, timeFormat, getSite().getShell());

            // restore filter from memento
            if (memento != null && memento.getString(M_FILTER) != null)
                FilterQuery.apply(memento.getString(M_FILTER), model);

            createGUI();
            createToolbar();

            getSite().setSelectionProvider(gui.getSelectionProvider());
        } catch (Exception e) {
            MessageDialog.openError(parent.getShell(), Messages.Error, e.getMessage());
        }
    }

    /**
     * Disposes of the existing GUI and creates new gui instance.
     */
    private void createGUI() throws Exception {
        if (gui != null)
            gui.dispose();
        gui = new GUI(getSite(), parent, model, columns, Message.SEQ, true, true);
        parent.layout();

        // Trigger update
        model.setTimerange(model.getStartSpec(), model.getEndSpec());
    }

    private void createToolbar() {
        final IMenuManager menu = getViewSite().getActionBars().getMenuManager();
        menu.add(new NewMessageHistoryAction(this));
        menu.add(new ColumnConfigureAction(this));
        menu.add(new ShowFilterAction(this));
        menu.add(new MaxMessagesConfigureAction(this));
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);

        IMemento columnsMemento = memento.createChild(M_COLUMNS);
        PropertyColumnPreference.saveColumns(columnsMemento, getColumns());

        memento.putString(M_FILTER, FilterQuery.fromModel(model));
        memento.putInteger(M_MAX_MESSAGES, model.getMaxMessages());
    }

    @Override
    public void dispose() {
        if (secondaryId.get() > 1) {
            secondaryId.decrementAndGet();
        }
    }

    @Override
    public void setFocus() {
        // NOP
    }
}
