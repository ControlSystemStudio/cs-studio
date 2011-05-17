package org.csstudio.basic.epics;

import org.csstudio.apputil.ui.workbench.OpenViewAction;
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
import org.eclipse.ui.actions.ContributionItemFactory;
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
public class BasicActionBarAdvisor extends ActionBarAdvisor
{
    /**
     * Group ID of switch user and logout toolbar
     */
    private static final String TOOLBAR_USER = "user"; //$NON-NLS-1$

    final private IWorkbenchWindow window;

    //File menu
    private IAction create_new;
    private IAction close;
    private IAction close_all;
    private IAction save;
    private IAction save_as;
    private IAction save_all;
    private IAction importAction;
    private IAction exportAction;
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
    private IContributionItem menu_views;

    /**
     * The coolbar context menu manager.
     * @since 2.2.1
     */
    private MenuManager coolbarPopupMenuManager;

    private IWorkbenchAction lockToolBarAction;

    private IWorkbenchAction editActionSetAction;

    public BasicActionBarAdvisor(final IActionBarConfigurer configurer)
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
        register(about);
    }

    /** {@inheritDoc} */
    @Override
    protected void fillMenuBar(IMenuManager menubar)
    {
        // See org.eclipse.ui.internal.ide.WorkbenchActionBuilder
        // for IDE example.
        // Placeholder for possible additions
        menubar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        createEditMenu(menubar);
        createWindowMenu(menubar);
        createHelpMenu(menubar);
    }

    /** Create the file menu. */
    private void createEditMenu(IMenuManager menubar)
    {
        final MenuManager menu_edit =
            new MenuManager("Edit", IWorkbenchActionConstants.M_EDIT);
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

    /** Create the window menu. */
    private void createWindowMenu(IMenuManager menubar)
    {
        final MenuManager menu_window =
            new MenuManager("Window", IWorkbenchActionConstants.M_WINDOW);
        menu_window.add(new_window);
        menu_window.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        final MenuManager persp_sub = new MenuManager("Perspectives...");
        persp_sub.add(menu_perspectives);
        menu_window.add(persp_sub);

        final MenuManager view_sub = new MenuManager("Views...");
        view_sub.add(menu_views);
        menu_window.add(view_sub);
        menu_window.add(open_windows);
        menubar.add(menu_window);
    }

    /** Create the help menu. */
    private void createHelpMenu(IMenuManager menubar)
    {
        final MenuManager menu_help =
            new MenuManager("Help", IWorkbenchActionConstants.M_HELP);
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
                "Cheat Sheets.."));
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
    }
}
