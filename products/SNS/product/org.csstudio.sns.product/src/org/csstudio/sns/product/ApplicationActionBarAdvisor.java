/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.product;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.CoolItemGroupMarker;


/** Create workbench window actions, menu bar, coolbar.
 *  @author Kay Kasemir
 *  @author Alexander Will provided most of the hints
 *          in the CssWorkbenchAdvisor code
 *  @author Xihui Chen
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
	/**
     * Group ID of switch user and logout toolbar
     */
    private static final String TOOLBAR_USER = "user"; //$NON-NLS-1$

    final private IWorkbenchWindow window;

    /**
     * The coolbar context menu manager.
     * @since 2.2.1
     */
	private MenuManager coolbarPopupMenuManager;

    // SNS Actions
	private IWorkbenchAction lockToolBarAction;

	private IWorkbenchAction editActionSetAction;


    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
    {
        super(configurer);
        window = configurer.getWindowConfigurer().getWindow();
    }

    /** {@inheritDoc} */
	@Override
    protected void makeActions(IWorkbenchWindow window)
    {
        lockToolBarAction = ActionFactory.LOCK_TOOL_BAR.create(window);
        register(lockToolBarAction);

        editActionSetAction = ActionFactory.EDIT_ACTION_SETS
        .create(window);
        register(editActionSetAction);

        // The help menu tries to invoke the into(welcome)
        // and help commands, but by default no handler is
        // available. Registering these actions also
        // registers the handlers.
        if (window.getWorkbench().getIntroManager().hasIntro())
            register(ActionFactory.INTRO.create(window));
        register(ActionFactory.HELP_CONTENTS.create(window));
    }

    /** {@inheritDoc} */
    @Override
    protected void fillMenuBar(IMenuManager menubar)
    {
        // Placeholder for possible additions, rest filled from plugin.xml
        menubar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /** {@inheritDoc} */
    @Override
    protected void fillCoolBar(ICoolBarManager coolbar)
    {
        { // Set up the context Menu
            coolbarPopupMenuManager = new MenuManager();
			coolbarPopupMenuManager.add(new ActionContributionItem(lockToolBarAction));
            coolbarPopupMenuManager.add(new ActionContributionItem(editActionSetAction));
            coolbar.setContextMenuManager(coolbarPopupMenuManager);
            IMenuService menuService = (IMenuService) window.getService(IMenuService.class);
            menuService.populateContributionManager(coolbarPopupMenuManager, "popup:windowCoolbarContextMenu"); //$NON-NLS-1$
        }

        IToolBarManager file_bar = new ToolBarManager();
        IToolBarManager user_bar = new ToolBarManager();
        coolbar.add(new ToolBarContributionItem(file_bar, IWorkbenchActionConstants.M_FILE));
        coolbar.add(new ToolBarContributionItem(user_bar, TOOLBAR_USER));

//        file_bar.add(create_new);
//        file_bar.add(save);
        file_bar.add(new CoolItemGroupMarker(IWorkbenchActionConstants.FILE_END));
    }
}
