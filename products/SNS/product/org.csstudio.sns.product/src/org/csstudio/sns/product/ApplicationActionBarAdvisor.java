package org.csstudio.sns.product;

import org.csstudio.platform.ui.workbench.CssWorkbenchActionConstants;
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
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.part.CoolItemGroupMarker;

/** Create workbench window actions, menu bar, coolbar.
 *  @author Kay Kasemir
 *  @author Alexander Will provided most of the hints
 *          in the CssWorkbenchAdvisor code
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
    private IAction create_new;
    private IAction close;
    private IAction close_all;
    private IAction save;
    private IAction save_as;
    private IAction save_all;
    private IAction quit;
    private IAction new_window;
    private IContributionItem open_windows;
    private IAction intro;
    private IAction help;
    private IAction about;
    private IContributionItem menu_perspectives;
    private IContributionItem menu_views;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
    {
        super(configurer);
    }

    /** {@inheritDoc} */
    @Override
    protected void makeActions(IWorkbenchWindow window)
    {
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
        
        quit = ActionFactory.QUIT.create(window);
        register(quit);
        
        new_window = ActionFactory.OPEN_NEW_WINDOW.create(window);
        register(new_window);
        
        open_windows = ContributionItemFactory.OPEN_WINDOWS.create(window);
        
        menu_perspectives =
            ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
        menu_views = ContributionItemFactory.VIEWS_SHORTLIST.create(window);

        intro = ActionFactory.INTRO.create(window);
        register(intro);
        
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
        createFileMenu(menubar);
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
        
        menu_file.add(close);
        menu_file.add(close_all);
        menu_file.add(new Separator());
        menu_file.add(save);
        menu_file.add(save_as);
        menu_file.add(new Separator());
        menu_file.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
        menu_file.add(new Separator());
        menu_file.add(quit);
        menubar.add(menu_file);
    }

    /** Create the CSS menu. */
    private void createCSSMenu(IMenuManager menubar)
    {
        final MenuManager menu_css = new MenuManager(Messages.Menu_CSS_CSS,
                        CssWorkbenchActionConstants.CSS_MENU);
        menu_css.add(new MenuManager(Messages.Menu_CSS_Display,
                CssWorkbenchActionConstants.CSS_DISPLAY_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Editors,
                        CssWorkbenchActionConstants.CSS_EDITORS_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Alarm,
                        CssWorkbenchActionConstants.CSS_ALARM_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Trends,
                        CssWorkbenchActionConstants.CSS_TRENDS_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Diagnostics,
                        CssWorkbenchActionConstants.CSS_DIAGNOSTICS_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Utilities,
                        CssWorkbenchActionConstants.CSS_UTILITIES_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Configuration,
                        CssWorkbenchActionConstants.CSS_CONFIGURATION_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Debug,
                        CssWorkbenchActionConstants.CSS_DEBUGGING_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Management,
                        CssWorkbenchActionConstants.CSS_MANAGEMENT_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Test,
                        CssWorkbenchActionConstants.CSS_TEST_MENU));
        menu_css.add(new MenuManager(Messages.Menu_CSS_Other,
                        CssWorkbenchActionConstants.CSS_OTHER_MENU));
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
        IToolBarManager file_bar = new ToolBarManager();
        file_bar.add(create_new);
        file_bar.add(save);
        file_bar.add(new CoolItemGroupMarker(IWorkbenchActionConstants.FILE_END));
        file_bar.add(new Separator());
        coolbar.add(file_bar);
    }
}
