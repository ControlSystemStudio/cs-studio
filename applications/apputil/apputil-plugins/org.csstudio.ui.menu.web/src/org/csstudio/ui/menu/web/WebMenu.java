/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.web;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Menu with entries for web links.
 * <p>
 * To be registered as 'dynamic' menu in plugin.xml.
 * <p>
 * Expects a preference "weblinks" that lists further prefs,
 * separated by space. See preferences.ini for example.
 *
 * @author Kay Kasemir
 * @author Claudio Rosati
 */
public class WebMenu extends CompoundContributionItem {

    @Override
    protected IContributionItem[] getContributionItems ( ) {

        final ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/weblink.png"); //$NON-NLS-1$
        final IMenuManager itemsManager = new MenuManager(Messages.WebLinks, icon, null);
        final Object[] items = createWeblinkItems();

        for ( Object item : items ) {
            if ( item instanceof IAction ) {
                itemsManager.add((IAction) item);
            } else if ( item instanceof IContributionItem ){
                itemsManager.add((IContributionItem) item);
            }
        }

        return new IContributionItem[] { itemsManager };

    }

    /**
     * Parse preferences, obtain web link actions
     *
     * @return web link actions
     */
    private Object[] createWeblinkItems ( ) {

        final List<Object> web_items = new ArrayList<>();
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String weblinks = prefs.getString(Activator.ID, "weblinks", null, null); //$NON-NLS-1$

        if ( weblinks == null )
            return new Object[0];

        final String[] link_prefs = weblinks.split("[ \t]+"); //$NON-NLS-1$

        for ( String pref : link_prefs ) {

            if ( "|".equals(pref) ) {
                web_items.add(new Separator());
            } else {

                final String descriptor = prefs.getString(Activator.ID, pref, null, null);

                if ( descriptor == null )
                    continue;

                final String[] link = descriptor.split("\\|"); //$NON-NLS-1$

                if ( link.length != 2 ) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, "Web link doesn't follow the LABEL|URL pattern: {0}", pref); //$NON-NLS-1$
                    continue;
                }

                final String label = link[0];
                final String url = link[1];

                Logger.getLogger(getClass().getName()).log(Level.FINE, "Web link {0} = {1} ({2})", //$NON-NLS-1$
                        new Object[] { pref, label, url });

                web_items.add(new OpenWebBrowserAction(label, url));

            }

        }

        return web_items.toArray(new Object[web_items.size()]);

    }

}
