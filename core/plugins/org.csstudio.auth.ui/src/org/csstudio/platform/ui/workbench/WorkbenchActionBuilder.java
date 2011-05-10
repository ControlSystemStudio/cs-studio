/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.platform.ui.workbench;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.part.CoolItemGroupMarker;

/**
 * An action builder for the menu bars of the control system studio.
 * 
 * @author Alexander Will
 * 
 */
public final class WorkbenchActionBuilder {
	/**
	 * The workbench window, this action builder is contributing to.
	 */
	private IWorkbenchWindow _window;

	/**
	 * A convenience variable and method so that the actionConfigurer doesn't
	 * need to get passed into registerGlobalAction every time it's called.
	 */
	private IActionBarConfigurer _actionBarConfigurer;

	/**
	 * The exit action. This action closes the workbench.
	 */
	private IWorkbenchAction _exitAction;

	/**
	 * The documentation action. This action starts up the help system.
	 */
	private IWorkbenchAction _documentationAction;

	/**
	 * The about action. This action opens up the about dialog.
	 */
	private IWorkbenchAction _aboutAction;

	/**
	 * The save action. This action saves the current editor.
	 */
	private IWorkbenchAction _saveAction;

	/**
	 * The intro action.
	 */
	private IWorkbenchAction _introAction;

	/**
	 * The generic "save as" action.
	 */
	private IWorkbenchAction _saveAsAction;

	/**
	 * The "new" action for the menu bar.
	 */
	private IWorkbenchAction _newWizardMenuAction;

	/**
	 * A special "new" action for the tool bar.
	 */
	private IWorkbenchAction _newWizardToolbarAction;

	/**
	 * Action to pull up the import wizard.
	 */
	private IWorkbenchAction _importResourcesAction;
	
	/**
	 * The print action. This action prints the content of the current editor.
	 */
	private IWorkbenchAction _printAction;

	/**
	 * Constructs a new action builder which contributes actions to the given
	 * window.
	 * 
	 * @param window
	 *            the _window
	 */
	public WorkbenchActionBuilder(final IWorkbenchWindow window) {
		_window = window;
	}

	/**
	 * Returns the window to which this action builder is contributing.
	 * 
	 * @return The window to which this action builder is contributing.
	 */
	private IWorkbenchWindow getWindow() {
		return _window;
	}

	/**
	 * Builds the actions and contributes them to the given _window.
	 * 
	 * @param windowConfigurer
	 *            The workbench configurer.
	 * @param actionBarConfigurer
	 *            The action bar configurer.
	 */
	public void makeAndPopulateActions(
			final IWorkbenchConfigurer windowConfigurer,
			final IActionBarConfigurer actionBarConfigurer) {
		makeActions(windowConfigurer, actionBarConfigurer);
		populateMenuBar(actionBarConfigurer);
		populateCoolBar(actionBarConfigurer);
	}

	/**
	 * Fills the menu bar with the workbench actions.
	 * 
	 * @param configurer
	 *            The action bar configurer.
	 */
	public void populateMenuBar(final IActionBarConfigurer configurer) {
		IMenuManager menubar = configurer.getMenuManager();
		menubar.add(createFileMenu());
		menubar.add(createCssMenu());
		menubar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menubar.add(createWindowMenu());
		menubar.add(createHelpMenu());
	}

	/**
	 * Fills the cool bar with the workbench actions.
	 * 
	 * @param configurer
	 *            The action bar configurer.
	 */
	public void populateCoolBar(final IActionBarConfigurer configurer) {
		ICoolBarManager coolbar = configurer.getCoolBarManager();
		coolbar.add(_documentationAction);

		IToolBarManager fileToolBar = new ToolBarManager();
		fileToolBar.add(_newWizardToolbarAction);
		fileToolBar.add(_saveAction);
		fileToolBar.add(new CoolItemGroupMarker(
				IWorkbenchActionConstants.FILE_END));
		fileToolBar.add(new Separator());

		// Add to the cool bar manager
		coolbar.add(fileToolBar);
	}

	/**
	 * Creates and returns the CSS menu.
	 * 
	 * @return the CSS menu.
	 */
	private MenuManager createCssMenu() {
		MenuManager cssMenu = new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_MENU,
				CssWorkbenchActionConstants.CSS_MENU);

		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_DISPLAY_MENU,
				CssWorkbenchActionConstants.CSS_DISPLAY_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_ALARM_MENU,
				CssWorkbenchActionConstants.CSS_ALARM_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_DIAGNOSTICS_MENU,
				CssWorkbenchActionConstants.CSS_DIAGNOSTICS_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_DEBUGGING_MENU,
				CssWorkbenchActionConstants.CSS_DEBUGGING_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_CONFIGURATION_MENU,
				CssWorkbenchActionConstants.CSS_CONFIGURATION_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_MANAGEMENT_MENU,
				CssWorkbenchActionConstants.CSS_MANAGEMENT_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_EDITORS_MENU,
				CssWorkbenchActionConstants.CSS_EDITORS_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_UTILITIES_MENU,
				CssWorkbenchActionConstants.CSS_UTILITIES_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_TRENDS_MENU,
				CssWorkbenchActionConstants.CSS_TRENDS_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_TEST_MENU,
				CssWorkbenchActionConstants.CSS_TEST_MENU));
		cssMenu.add(new MenuManager(
				Messages.WorkbenchActionBuilder_CSS_OTHER_MENU,
				CssWorkbenchActionConstants.CSS_OTHER_MENU));
		cssMenu.add(new Separator(CssWorkbenchActionConstants.CSS_END));

		return cssMenu;
	}

	/**
	 * Creates and returns the Window menu.
	 * 
	 * @return The Window menu.
	 */
	private MenuManager createWindowMenu() {
		MenuManager menu = new MenuManager(
				Messages.WorkbenchActionBuilder_MENU_WINDOW,
				IWorkbenchActionConstants.M_WINDOW);
		IWorkbenchAction action = ActionFactory.OPEN_NEW_WINDOW
				.create(getWindow());
		menu.add(new Separator());
		menu.add(action);
		menu.add(new Separator());
		addPerspectiveActions(menu);
		menu.add(new Separator());

		return menu;
	}

	/**
	 * Adds the perspective actions to the specified menu.
	 * 
	 * @param menu
	 *            The menu to which the perspective actions have to be added.
	 */
	private void addPerspectiveActions(final MenuManager menu) {
		MenuManager changePerspMenuMgr = new MenuManager(
				Messages.WorkbenchActionBuilder_OPEN_PERSPECTIVE,
				"openPerspective"); //$NON-NLS-1$
		IContributionItem changePerspMenuItem = ContributionItemFactory.PERSPECTIVES_SHORTLIST
				.create(getWindow());
		changePerspMenuMgr.add(changePerspMenuItem);
		menu.add(changePerspMenuMgr);
		MenuManager showViewMenuMgr = new MenuManager(
				Messages.WorkbenchActionBuilder_SHOW_VIEW, "showView"); //$NON-NLS-1$
		IContributionItem showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST
				.create(getWindow());
		showViewMenuMgr.add(showViewMenu);
		menu.add(showViewMenuMgr);
		menu.add(new Separator());
	}

	/**
	 * Creates and returns the File menu.
	 * 
	 * @return The File menu.
	 */
	private MenuManager createFileMenu() {
		MenuManager menu = new MenuManager(
				Messages.WorkbenchActionBuilder_MENU_FILE,
				IWorkbenchActionConstants.M_FILE);
		MenuManager newMenu = new MenuManager(
				Messages.WorkbenchActionBuilder_MENU_FILE_NEW, "file/new"); //$NON-NLS-1$
		newMenu.add(_newWizardMenuAction);
		menu.add(newMenu);
		menu.add(new Separator());
		menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		menu.add(_saveAction);
		menu.add(_saveAsAction);
		menu.add(new Separator());
		menu.add(_printAction);
		menu.add(new Separator());
		menu.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
		menu.add(new Separator());
		menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
		menu.add(new Separator());
		menu.add(_importResourcesAction);
		menu.add(new Separator());
		menu.add(_exitAction);
		return menu;
	}

	/**
	 * Creates and returns the Help menu.
	 * 
	 * @return The Help menu.
	 */
	private MenuManager createHelpMenu() {
		MenuManager menu = new MenuManager(
				Messages.WorkbenchActionBuilder_MENU_HELP,
				IWorkbenchActionConstants.M_HELP);

		// See if a welcome or intro page is specified
		if (_introAction != null) {
			menu.add(_introAction);
			menu.add(new Separator());
		}
		
		// about should always be at the bottom
		menu.add(new Separator("group.about")); //$NON-NLS-1$
		menu.add(_aboutAction);

		// Not sure if this is the best way.
		// Is the Sheet Cheat View ID defined as a public somewhere?
		// Does org.eclipse.* already provide an action for opening it?
		// There is org.eclipse.ui.internal.cheatsheets.actions.CheatSheetHelpMenuAction(),
		// but that is "internal"...
        menu.add(new OpenViewAction(
	                "org.eclipse.ui.cheatsheets.views.CheatSheetView", //$NON-NLS-1$
	                "Cheat Sheets..."));
		
		menu.add(new GroupMarker("group.about.ext")); //$NON-NLS-1$
		menu.add(_documentationAction);
		return menu;
	}

	/**
	 * Disposes any resources and unhooks any listeners that are no longer
	 * needed. Called when the _window is closed.
	 */
	public void dispose() {
		if (_aboutAction != null) {
			_aboutAction.dispose();
		}

		if (_documentationAction != null) {
			_documentationAction.dispose();
		}

		if (_exitAction != null) {
			_exitAction.dispose();
		}

		if (_introAction != null) {
			_introAction.dispose();
		}

		if (_saveAsAction != null) {
			_saveAsAction.dispose();
		}

		if (_newWizardMenuAction != null) {
			_newWizardMenuAction.dispose();
		}

		if (_newWizardToolbarAction != null) {
			_newWizardToolbarAction.dispose();
		}

		if (_importResourcesAction != null) {
			_importResourcesAction.dispose();
		}
	}

	/**
	 * Creates actions (and contribution items) for the menu bar, toolbar and
	 * status line.
	 * 
	 * @param workbenchConfigurer
	 *            The workbench configurer.
	 * @param actionBarConfigurer
	 *            The action bar configurer.
	 */
	private void makeActions(final IWorkbenchConfigurer workbenchConfigurer,
			final IActionBarConfigurer actionBarConfigurer) {
		setCurrentActionBarConfigurer(actionBarConfigurer);

		_aboutAction = ActionFactory.ABOUT.create(getWindow());
		registerGlobalAction(_aboutAction);

		_documentationAction = ActionFactory.HELP_CONTENTS.create(getWindow());
		registerGlobalAction(_documentationAction);

		_exitAction = ActionFactory.QUIT.create(getWindow());
		registerGlobalAction(_exitAction);

		_saveAction = ActionFactory.SAVE.create(_window);
		registerGlobalAction(_saveAction);

		_saveAsAction = ActionFactory.SAVE_AS.create(_window);
		registerGlobalAction(_saveAsAction);

		_newWizardMenuAction = ActionFactory.NEW.create(_window);
		registerGlobalAction(_newWizardMenuAction);

		_newWizardToolbarAction = ActionFactory.NEW_WIZARD_DROP_DOWN
				.create(_window);
		registerGlobalAction(_newWizardToolbarAction);
		
		_printAction = ActionFactory.PRINT.create(_window);
		registerGlobalAction(_printAction);

		if (_window.getWorkbench().getIntroManager().hasIntro()) {
			_introAction = ActionFactory.INTRO.create(_window);
			registerGlobalAction(_introAction);
		}

		_importResourcesAction = ActionFactory.IMPORT.create(_window);
		registerGlobalAction(_importResourcesAction);
	}

	/**
	 * Set the current action bar configurer.
	 * 
	 * @param actionBarConfigurer
	 *            The current action bar configurer.
	 */
	private void setCurrentActionBarConfigurer(
			final IActionBarConfigurer actionBarConfigurer) {
		_actionBarConfigurer = actionBarConfigurer;
	}

	/**
	 * Registers the given action with the key binding service (by calling
	 * {@link IActionBarConfigurer#registerGlobalAction(IAction)}), and adds it
	 * to the list of actions to be disposed when the _window is closed.
	 * <p>
	 * In order to participate in key bindings, the action must have an action
	 * definition id (aka command id), and a corresponding command extension.
	 * See the <code>org.eclipse.ui.commands</code> extension point
	 * documentation for more details.
	 * </p>
	 * 
	 * @param action
	 *            the action to register
	 * @see IAction#setActionDefinitionId(String)
	 * @see #disposeAction(IAction)
	 */
	private void registerGlobalAction(final IAction action) {
		String id = action.getId();
		assert id != null : "id!=null"; //$NON-NLS-1$
		_actionBarConfigurer.registerGlobalAction(action);
	}
}
