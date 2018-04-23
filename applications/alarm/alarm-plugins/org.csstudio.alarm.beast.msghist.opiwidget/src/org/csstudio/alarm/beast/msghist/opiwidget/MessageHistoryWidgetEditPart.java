/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.msghist.Activator;
import org.csstudio.alarm.beast.msghist.model.FilterQuery;
import org.csstudio.alarm.beast.msghist.model.Model;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.draw2d.IFigure;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchSite;

/**
 *
 * <code>MessageHistoryWidgetEditPart</code> is the edit part for the message
 * history opi widget.
 *
 * @author Borut Terpinc
 *
 */
public class MessageHistoryWidgetEditPart extends AbstractWidgetEditPart {

    private static final Logger LOGGER = Logger.getLogger(MessageHistoryWidgetEditPart.class.getName());

    private Model model;

    @Override
    public MessageHistoryWidgetModel getWidgetModel() {
        return (MessageHistoryWidgetModel) super.getWidgetModel();
    }

    public Model getMessageHistoryModel() {
        return model;
    }

    /**
     * Sets up model and creates figure.
     */
    @Override
    protected IFigure doCreateFigure() {
        if (getExecutionMode() == ExecutionMode.RUN_MODE)
            setUpModel();

        return new MessageHistoryWidgetFigure(this);
    }

    /**
     * Creates message history model from preferences and binds widget model's
     * properties.
     */
    private void setUpModel() {
        try {
            MessageHistoryWidgetModel widgetModel = getWidgetModel();

            // read settings from preferences and initialize model
            final IPreferencesService service = Platform.getPreferencesService();
            final String url = service.getString(Activator.ID, "rdb_url", null, null);
            final String user = SecurePreferences.get(Activator.ID, "rdb_user", null);
            final String password = SecurePreferences.get(Activator.ID, "rdb_password", null);
            final String schema = service.getString(Activator.ID, "rdb_schema", null, null);

            model = new Model(url, user, password, schema, widgetModel.getMaxMessages(), widgetModel.getTimeFormat(),
                    getSite().getShell());

            // apply filter property to model
            String filterQuery = widgetModel.getFilter();
            FilterQuery.apply(filterQuery, model);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, NLS.bind(Messages.ModelCreationError, ex.getMessage()));
        }
    }

    /**
     * Triggers update on widget's activation.
     */
    @Override
    protected void doActivate() {
        if (getExecutionMode() == ExecutionMode.RUN_MODE) {
            // set selection provider if one is not already present
            IWorkbenchSite site = getSite();
            if (site != null && site.getSelectionProvider() == null)
                site.setSelectionProvider(new SelectionProviderWrapper());


            try {
                model.setTimerange(model.getStartSpec(), model.getEndSpec());
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, NLS.bind(Messages.ModelUpdateError, ex.getMessage()));
            }
        }
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        // TODO: bind propery change handlers where needed. After creating GUI
        // setters for those properties, get rid of them in constructor, if not
        // needed.
        return;
    }
}
