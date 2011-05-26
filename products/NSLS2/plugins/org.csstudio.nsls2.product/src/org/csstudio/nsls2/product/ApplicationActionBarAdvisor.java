package org.csstudio.nsls2.product;

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
	private IAction save;
	private IAction intro;
	private IAction help;
	private IAction about;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		window = configurer.getWindowConfigurer().getWindow();
	}

	protected void makeActions(IWorkbenchWindow window) {
		create_new = ActionFactory.NEW.create(window);
		register(create_new);
		
		save = ActionFactory.SAVE.create(window);
		register(save);
		
		register(ActionFactory.SAVE_AS.create(window));
		register(ActionFactory.SAVE_ALL.create(window));

		help = ActionFactory.HELP_CONTENTS.create(window);
		register(help);

		about = ActionFactory.ABOUT.create(window);
		about.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/css16.gif")); //$NON-NLS-1$
		register(about);

		intro = ActionFactory.INTRO.create(window);
		register(intro);

	}

	protected void fillMenuBar(IMenuManager menuBar) {
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
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

	}

}
