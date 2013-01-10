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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
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
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        final ScriptInfo[] infos;
        try
        {
        	infos = Preferences.getCommandInfos();
        }
        catch (Exception ex)
        {
        	MessageDialog.openError(window.getShell(), Messages.Error,
        			NLS.bind(Messages.PreferenceErrorFmt, ex.getMessage()));
        	return new IContributionItem[0];
        }
    	
    	
    	final ImageDescriptor script_icon =
            AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/script.gif"); //$NON-NLS-1$

        // Create a CommandContributionItem for each script
        final IContributionItem[] items = new IContributionItem[infos.length];
		for (int i=0; i<items.length; ++i)
        {
			// The command to invoke is RunScriptHandler.COMMAND_ID
        	final CommandContributionItemParameter params
        		= new CommandContributionItemParameter(window, null, RunScriptHandler.COMMAND_ID,
        			 CommandContributionItem.STYLE_PUSH);
        	// Label of the command (= displayed menu item) is the script's description
        	params.label = infos[i].getDescription();
        	params.icon = script_icon;
        	// Name of the script to invoke is passed as a command parameter
        	final Map<String, String> cmd_parms = new HashMap<String, String>();
        	cmd_parms.put(RunScriptHandler.PARAM_SCRIPT, infos[i].getScript());
			params.parameters = cmd_parms;
			items[i] = new CommandContributionItem(params);
        }
        return items;
    }
}
