/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.product;

import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.ui.plugin.AbstractUIPlugin;


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

    private IAction intro;
    private IAction help;
    private IAction about;

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

        if (window.getWorkbench().getIntroManager().hasIntro())
        {
            intro = ActionFactory.INTRO.create(window);
            register(intro);
        }
        else
            System.out.println("There is no Intro: Check for org.eclipse.ui.intro.univeral"); //$NON-NLS-1$

        help = ActionFactory.HELP_CONTENTS.create(window);
        register(help);

        about = ActionFactory.ABOUT.create(window);
        about.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/css16.gif")); //$NON-NLS-1$
        register(about);
    }

    /** {@inheritDoc} */
    @Override
    protected void fillMenuBar(IMenuManager menubar)
    {
        // TODO Check NSLS-II Product for creating menu entries via plugin.xml

        // Placeholder for possible additions
        menubar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        createHelpMenu(menubar);
    }

    /** Create the window menu. */
    private void createWindowMenu(IMenuManager menubar)
    {
//        final MenuManager persp_sub = new MenuManager(Messages.Menu_Perspectives);
//        persp_sub.add(menu_perspectives);
//        menu_window.add(persp_sub);
//
//        final MenuManager view_sub = new MenuManager(Messages.Menu_Views);
//        view_sub.add(menu_views);
//        menu_window.add(view_sub);
//        menu_window.add(open_windows);
//        menubar.add(menu_window);
    }

    /** Create the help menu. */
    private void createHelpMenu(IMenuManager menubar)
    {
        final MenuManager menu_help =
            new MenuManager(Messages.Menu_Help, IWorkbenchActionConstants.M_HELP);
       	menu_help.add(intro);
       	menu_help.add(new Separator());
        menu_help.add(help);
        // Not sure if this is the best way.
        // Is the Sheet Cheat View ID defined as a public somewhere?
        // Does org.eclipse.* already provide an action for opening it?
        // There is org.eclipse.ui.internal.cheatsheets.actions.CheatSheetHelpMenuAction(),
        // but that is "internal"...
        menu_help.add(new OpenViewAction(
                "org.eclipse.ui.cheatsheets.views.CheatSheetView", //$NON-NLS-1$
                Messages.Menu_Help_CheatSheet));
        menu_help.add(new Separator());
        menu_help.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
        // CSS platform.ui plugin hooks software update into help/group.updates
        menu_help.add(new GroupMarker("group.updates")); //$NON-NLS-1$
        menu_help.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));
        menu_help.add(new Separator());
        menu_help.add(about);
        menubar.add(menu_help);
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
