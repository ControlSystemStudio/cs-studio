/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** 'Dynamic' context menu contribution.
 * 
 *  <p>plugin.xml hooks this into the ProcessVariable
 *  context menu.
 *  Creates one entry per registered script.
 *  @author Kay Kasemir
 */
public class ContextMenuItems extends CompoundContributionItem
{
	/** {@inheritDoc} */
    @Override
    protected IContributionItem[] getContributionItems()
    {
    	// Fetch icons...
        final ImageDescriptor menu_icon =
            AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/menu.gif"); //$NON-NLS-1$
        final ImageDescriptor script_icon =
            AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/script.gif"); //$NON-NLS-1$

        // Create a (sub) menu that will list
        final IMenuManager items = new MenuManager("External Scripts", menu_icon, null);
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        for (ScriptInfo info : Preferences.getCommandInfos())
        {
        	final CommandContributionItemParameter params
        	 = new CommandContributionItemParameter(window, null, RunScriptHandler.COMMAND_ID,
        			 CommandContributionItem.STYLE_PUSH);
        	params.label = info.getDescription();
        	params.icon = script_icon;
        	final Map<String, String> cmd_parms = new HashMap<String, String>();
        	cmd_parms.put(RunScriptHandler.PARAM_SCRIPT, info.getScript());
			params.parameters = cmd_parms;
			items.add(new CommandContributionItem(params));
        }
        return new IContributionItem[] { items };
    }
}
