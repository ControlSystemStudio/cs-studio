package org.csstudio.utility.adlparser;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    // Actions - important to allocate these only in makeActions, and then use
    // them
    // in the fill methods. This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction exitAction;
    private IWorkbenchAction perspectiveAction;
    private IWorkbenchAction saveAction;
    private IWorkbenchAction saveAsAction;
    private IWorkbenchAction preferencesAction;
    private IContributionItem showViewAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml
        // file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        exitAction = ActionFactory.QUIT.create(window);
        perspectiveAction = ActionFactory.OPEN_PERSPECTIVE_DIALOG.create(window);
        showViewAction = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
        saveAction = ActionFactory.SAVE.create(window);
        saveAsAction = ActionFactory.SAVE_AS.create(window);
        preferencesAction = ActionFactory.PREFERENCES.create(window);
        register(exitAction);
        register(perspectiveAction);
        register(saveAction);
        register(saveAsAction);
        register(preferencesAction);
    }

    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&File",
                IWorkbenchActionConstants.M_FILE);
        MenuManager windowMenu = new MenuManager("&Window",
                IWorkbenchActionConstants.M_WINDOW);
        menuBar.add(fileMenu);
        menuBar.add(windowMenu);
        windowMenu.add(perspectiveAction);
        windowMenu.add(showViewAction);
        fileMenu.add(saveAction);
        System.out.println ("hello from fill menubar");
        fileMenu.add(saveAsAction);
        fileMenu.add(preferencesAction);
        fileMenu.add(exitAction);
    }

}
