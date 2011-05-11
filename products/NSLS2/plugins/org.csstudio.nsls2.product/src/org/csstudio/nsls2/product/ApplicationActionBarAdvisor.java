package org.csstudio.nsls2.product;

import org.csstudio.auth.ui.internal.actions.LogoutAction;
import org.csstudio.auth.ui.workbench.CssWorkbenchActionConstants;
import org.csstudio.nsls2.product.Activator;
import org.csstudio.nsls2.product.Messages;
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
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.part.CoolItemGroupMarker;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	/**
	 * Menu and cool bar ID of switch user
	 */
	private static final String MENU_TOOLBAR_LOGIN = "css_login";

	private IWorkbenchWindow window;
	/**
	 * Group ID of switch user and logout toolbar
	 */
	private static final String TOOLBAR_USER = "user"; //$NON-NLS-1$

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
	
	private IAction new_window;
	private IContributionItem open_windows;
	private IAction intro;
	private IAction help;
	private IAction cheat;
	private IAction about;
	private IContributionItem menu_perspectives;
	private IContributionItem menu_views;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		window = configurer.getWindowConfigurer().getWindow();
	}

	protected void makeActions(IWorkbenchWindow window) {
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

		save_all = ActionFactory.SAVE_ALL.create(window);
		register(save_all);

		importAction = ActionFactory.IMPORT.create(window);
		register(importAction);

		exportAction = ActionFactory.EXPORT.create(window);
		register(exportAction);

		logout = new LogoutAction(window);
		register(logout);

		quit = ActionFactory.QUIT.create(window);
		register(quit);

		new_window = ActionFactory.OPEN_NEW_WINDOW.create(window);
		register(new_window);

		help = ActionFactory.HELP_CONTENTS.create(window);
		register(help);

		about = ActionFactory.ABOUT.create(window);
		about.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/css16.gif")); //$NON-NLS-1$
		register(about);

		new_window = ActionFactory.OPEN_NEW_WINDOW.create(window);
		register(new_window);

		open_windows = ContributionItemFactory.OPEN_WINDOWS.create(window);

		menu_perspectives = ContributionItemFactory.PERSPECTIVES_SHORTLIST
				.create(window);
		menu_views = ContributionItemFactory.VIEWS_SHORTLIST.create(window);

		intro = ActionFactory.INTRO.create(window);
		register(intro);

	}

	protected void fillMenuBar(IMenuManager menuBar) {
		// See org.eclipse.ui.internal.ide.WorkbenchActionBuilder
		// for IDE example.
//		createFileMenu(menuBar);
//		createCSSMenu(menuBar);
		// Placeholder for possible additions
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		createWindowMenu(menuBar);
		createHelpMenu(menuBar);
	}

	/** Create the window menu. */
	private void createWindowMenu(IMenuManager menubar) {
		final MenuManager menu_window = new MenuManager(Messages.Menu_Window,
				IWorkbenchActionConstants.M_WINDOW);
		menu_window.add(new_window);
		menu_window.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		final MenuManager persp_sub = new MenuManager(
				Messages.Menu_Perspectives);
		persp_sub.add(menu_perspectives);
		menu_window.add(persp_sub);

		final MenuManager view_sub = new MenuManager(Messages.Menu_Views);
		view_sub.add(menu_views);
		menu_window.add(view_sub);
		menu_window.add(open_windows);
		menubar.add(menu_window);
	}

	/** Create the help menu. */
	private void createHelpMenu(IMenuManager menubar) {
		final MenuManager menu_help = new MenuManager(Messages.Menu_Help,
				IWorkbenchActionConstants.M_HELP);
		menu_help.add(intro);
		menu_help.add(new Separator());
		menu_help.add(help);
		// menu_help.add(cheat);
		menu_help.add(new Separator());
		menu_help.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
		menu_help.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));
		menu_help.add(new Separator());
		menu_help.add(about);
		menu_help.add(new Separator());
		menu_help.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menu_help.add(new Separator());
		menubar.add(menu_help);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolbar) {
		IToolBarManager file_bar = new ToolBarManager();
		IToolBarManager user_bar = new ToolBarManager();
		coolbar.add(new ToolBarContributionItem(file_bar,
				IWorkbenchActionConstants.M_FILE));
		coolbar.add(new ToolBarContributionItem(user_bar, TOOLBAR_USER));

		file_bar.add(create_new);
		file_bar.add(save);
		file_bar.add(new CoolItemGroupMarker(IWorkbenchActionConstants.FILE_END));
		file_bar.add(new Separator());

		user_bar.add(new CoolItemGroupMarker(MENU_TOOLBAR_LOGIN));
		user_bar.add(logout);

	}

}
