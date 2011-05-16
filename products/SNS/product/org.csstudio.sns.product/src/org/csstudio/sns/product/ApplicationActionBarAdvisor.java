/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.product;

import java.util.ArrayList;
import java.util.logging.Level;

import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.csstudio.auth.ui.internal.actions.LogoutAction;
import org.csstudio.platform.ui.workbench.CssWorkbenchActionConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
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
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.internal.ReopenEditorMenu;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.CoolItemGroupMarker;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/** Create workbench window actions, menu bar, coolbar.
 *  @author Kay Kasemir
 *  @author Alexander Will provided most of the hints
 *          in the CssWorkbenchAdvisor code
 *  @author Xihui Chen
 */
@SuppressWarnings("restriction")
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
	private static final String MENU_WORKSPACE = "workspace";	//$NON-NLS-1$
	private static final String RECENT_FILES = "recent_files"; //$NON-NLS-1$

	/**
     * Menu and cool bar ID of switch user
     */
    private static final String MENU_TOOLBAR_LOGIN = "css_login"; //$NON-NLS-1$

	/**
     * Group ID of switch user and logout toolbar
     */
    private static final String TOOLBAR_USER = "user"; //$NON-NLS-1$

	/** ID of CSS SNS Menu */
    private static final String CSS_MENU_WEB = "web"; //$NON-NLS-1$

    //File menu
    private IAction create_new;
    private IAction close;
    private IAction close_all;
    private IAction save;
    private IAction save_as;
    private IAction save_all;
    private IAction importAction;
    private IAction exportAction;
    private IAction logout;
    private IAction quit;

    //Edit menu
    private IAction undo;
    private IAction redo;
    private IAction cut;
    private IAction copy;
    private IAction paste;
    private IAction delete;
    private IAction selectAll;
    private IAction findReplace;

    private IAction new_window;
    private IContributionItem open_windows;
    private IAction intro;
    private IAction help;
    private IAction about;
    private IContributionItem menu_perspectives;
    private IContributionItem recent_files;
    private IContributionItem menu_views;

    /**
     * The coolbar context menu manager.
     * @since 2.2.1
     */
	private MenuManager coolbarPopupMenuManager;

    // SNS Actions
    private ArrayList<IAction> web_actions = new ArrayList<IAction>();
	private IWorkbenchWindow window;
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
		//File menu
        create_new = ActionFactory.NEW.create(window);
        register(create_new);

        close = ActionFactory.CLOSE.create(window);
        register(close);

        close_all = ActionFactory.CLOSE_ALL.create(window);
        register(close_all);

        save = ActionFactory.SAVE.create(window);
        register(save);

        save_as = ActionFactory.SAVE_AS.create(window);
        register(save_as);

        save_all = ActionFactory.SAVE_ALL .create(window);
        register(save_all);

        importAction = ActionFactory.IMPORT.create(window);
        register(importAction);

        exportAction = ActionFactory.EXPORT.create(window);
        register(exportAction);

        logout = new LogoutAction(window);
        register(logout);

        quit = ActionFactory.QUIT.create(window);
        register(quit);

        //Edit menu
        undo = ActionFactory.UNDO.create(window);
        register(undo);

        redo = ActionFactory.REDO.create(window);
        register(redo);

        cut = ActionFactory.CUT.create(window);
        register(cut);

        copy = ActionFactory.COPY.create(window);
        register(copy);

        paste = ActionFactory.PASTE.create(window);
        register(paste);

        delete = ActionFactory.DELETE.create(window);
        register(delete);

        selectAll = ActionFactory.SELECT_ALL.create(window);
        register(selectAll);

        findReplace = ActionFactory.FIND.create(window);
        register(findReplace);

        new_window = ActionFactory.OPEN_NEW_WINDOW.create(window);
        register(new_window);

        lockToolBarAction = ActionFactory.LOCK_TOOL_BAR.create(window);
        register(lockToolBarAction);

        editActionSetAction = ActionFactory.EDIT_ACTION_SETS
        .create(window);
        register(editActionSetAction);

        open_windows = ContributionItemFactory.OPEN_WINDOWS.create(window);

        menu_perspectives =
            ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
        menu_views = ContributionItemFactory.VIEWS_SHORTLIST.create(window);

        recent_files = new ReopenEditorMenu(window, "reopenEditors", false); //$NON-NLS-1$
        //Following is the standard way to create this menu, but it
        //will create a separator before the menu.
        //ContributionItemFactory.REOPEN_EDITORS.create(window);

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

        createWeblinkActions(window);
    }

    /** Create actions for web links.
     *  <p>
     *  Expects a preference "weblinks" that lists further prefs,
     *  separated by space.
     *  Each of those is then LABEL|URL.
     *  Example:
     *  <pre>
     *   org.csstudio.sns.product/weblinks=sns_elog sns_wiki
     *   org.csstudio.sns.product/sns_elog=E-Log|https://snsapp1.sns.ornl.gov/Logbook/WebObjects/Logbook.woa
     *   org.csstudio.sns.product/sns_wiki=...
     *  </pre>
     */
    @SuppressWarnings("nls")
    private void createWeblinkActions(IWorkbenchWindow window)
    {
        // SNS Actions
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String weblinks = prefs.getString(Activator.PLUGIN_ID, "weblinks", null, null);
        if (weblinks == null)
            return;
        final String[] link_prefs = weblinks.split("[ \t]+");
        for (String pref : link_prefs)
        {
            final String descriptor = prefs.getString(Activator.PLUGIN_ID, pref, null, null);
            if (descriptor == null)
                continue;
            final String[] link = descriptor.split("\\|");
            if (link.length != 2)
            {
                Activator.getLogger().log(Level.WARNING,
                    "Web link doesn't follow the LABEL|URL pattern: {0}", pref);
                continue;
            }
            final String label = link[0];
            final String url = link[1];
            Activator.getLogger().log(Level.FINE, "Web link {0} = {1} ({2})",
                    new Object[] { pref, label, url });

            web_actions.add(new OpenWebBrowserAction(window, label, url));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void fillMenuBar(IMenuManager menubar)
    {
        // See org.eclipse.ui.internal.ide.WorkbenchActionBuilder
        // for IDE example.
        createFileMenu(menubar);
        createEditMenu(menubar);
        createCSSMenu(menubar);
        // Placeholder for possible additions
        menubar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        createWindowMenu(menubar);
        createHelpMenu(menubar);
    }

    /** Create the file menu. */
    private void createFileMenu(IMenuManager menubar)
    {
        final MenuManager menu_file =
            new MenuManager(Messages.Menu_File, IWorkbenchActionConstants.M_FILE);
        // Markers allow other code to use MenuManager.appendToGroup(...)...
        menu_file.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));

        final MenuManager new_sub =
            new MenuManager(Messages.Menu_New, ActionFactory.NEW.getId());
        new_sub.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        new_sub.add(create_new);
        menu_file.add(new_sub);
        menu_file.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
        menu_file.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
        menu_file.add(close);
        menu_file.add(close_all);
        menu_file.add(new Separator());
        menu_file.add(save);
        menu_file.add(save_as);
        menu_file.add(new Separator());
        menu_file.add(importAction);
        menu_file.add(exportAction);
        menu_file.add(new Separator());
        menu_file.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
        menu_file.add(new Separator());
        menu_file.add(new GroupMarker(MENU_WORKSPACE));
        menu_file.add(new Separator());

        menu_file.add(new GroupMarker(RECENT_FILES));
        final MenuManager recentFilesSubMenu =	new MenuManager(Messages.Menu_File_Recent);
        recentFilesSubMenu.add(recent_files);
        menu_file.add(recentFilesSubMenu);

        menu_file.add(new Separator());
        menu_file.add(new GroupMarker(MENU_TOOLBAR_LOGIN));
        menu_file.add(logout);
        menu_file.add(quit);
        menubar.add(menu_file);
    }

    /** Create the file menu. */
    private void createEditMenu(IMenuManager menubar)
    {
        final MenuManager menu_edit =
            new MenuManager(Messages.Menu_Edit, IWorkbenchActionConstants.M_EDIT);
        // Markers allow other code to use MenuManager.appendToGroup(...)...
        menu_edit.add(new GroupMarker(IWorkbenchActionConstants.EDIT_START));

        menu_edit.add(undo);
        menu_edit.add(redo);
        menu_edit.add(new Separator());
        menu_edit.add(cut);
        menu_edit.add(copy);
        menu_edit.add(paste);
        menu_edit.add(new Separator());

        menu_edit.add(delete);
        menu_edit.add(selectAll);
        menu_edit.add(new Separator());
        menu_edit.add(new GroupMarker(IWorkbenchActionConstants.EDIT_END));
        menu_edit.add(new Separator());
        menu_edit.add(findReplace);
        menu_edit.add(new Separator());
        menubar.add(menu_edit);
    }

    /** Create the CSS menu. */
    private void createCSSMenu(IMenuManager menubar)
    {
        final MenuManager menu_css = new MenuManager(Messages.Menu_CSS_CSS,
                        CssWorkbenchActionConstants.CSS_MENU);
        // Alphabetical order
        menu_css.add(new MenuManager(Messages.Menu_CSS_Alarm,
                CssWorkbenchActionConstants.CSS_ALARM_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Configuration,
                CssWorkbenchActionConstants.CSS_CONFIGURATION_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Debug,
                CssWorkbenchActionConstants.CSS_DEBUGGING_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Diagnostics,
                CssWorkbenchActionConstants.CSS_DIAGNOSTICS_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Display,
                        CssWorkbenchActionConstants.CSS_DISPLAY_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Editors,
                        CssWorkbenchActionConstants.CSS_EDITORS_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Management,
                CssWorkbenchActionConstants.CSS_MANAGEMENT_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Trends,
                        CssWorkbenchActionConstants.CSS_TRENDS_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Utilities,
                        CssWorkbenchActionConstants.CSS_UTILITIES_MENU));
        // .. except for test and other
        menu_css.add(new MenuManager(Messages.Menu_CSS_Test,
                        CssWorkbenchActionConstants.CSS_TEST_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Other,
                        CssWorkbenchActionConstants.CSS_OTHER_MENU));
        final MenuManager menu_sns = new MenuManager(Messages.Menu_CSS_Weblinks,
                    CSS_MENU_WEB);
        for (IAction weblink : web_actions)
            menu_sns.add(weblink);
        menu_css.add(menu_sns);
        menu_css.add(new Separator(CssWorkbenchActionConstants.CSS_END));
        menubar.add(menu_css);
    }

    /** Create the window menu. */
    private void createWindowMenu(IMenuManager menubar)
    {
        final MenuManager menu_window =
            new MenuManager(Messages.Menu_Window, IWorkbenchActionConstants.M_WINDOW);
        menu_window.add(new_window);
        menu_window.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        final MenuManager persp_sub = new MenuManager(Messages.Menu_Perspectives);
        persp_sub.add(menu_perspectives);
        menu_window.add(persp_sub);

        final MenuManager view_sub = new MenuManager(Messages.Menu_Views);
        view_sub.add(menu_views);
        menu_window.add(view_sub);
        menu_window.add(open_windows);
        menubar.add(menu_window);
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

        file_bar.add(create_new);
        file_bar.add(save);
        file_bar.add(new CoolItemGroupMarker(IWorkbenchActionConstants.FILE_END));
        file_bar.add(new Separator());

        user_bar.add(new CoolItemGroupMarker(MENU_TOOLBAR_LOGIN));
        user_bar.add(logout);

    }

}
