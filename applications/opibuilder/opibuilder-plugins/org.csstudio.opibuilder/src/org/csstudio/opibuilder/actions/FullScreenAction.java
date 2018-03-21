/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.util.WorkbenchWindowService;
import org.csstudio.opibuilder.visualparts.TipDialog;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.internal.WorkbenchWindow;


/**The action to make CSS full screen.
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public class FullScreenAction extends Action implements
    IWorkbenchWindowActionDelegate {

    public static final String ID = "org.csstudio.opibuilder.actions.fullscreen";
    private static final String FULLSCREEN = "Full Screen";

    private static final String EXIT_FULL_SCREEN = "Exit Full Screen";

    private Menu menuBar;
    private boolean inFullScreen = false;
    private Shell shell;
    private ImageDescriptor fullScreenImage =
        CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
            OPIBuilderPlugin.PLUGIN_ID, "icons/fullscreen.png");
    private ImageDescriptor exitFullScreenImage =
        CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
            OPIBuilderPlugin.PLUGIN_ID, "icons/exitfullscreen.png");
    private IWorkbenchWindow window;
    private boolean toolbarWasInvisible;
    private boolean menuBarWasInvisible;
    /**
     * Constructor.
     * @param part The workbench part associated with this PrintAction
     */
    public FullScreenAction() {
        setActionDefinitionId(ID);
    }


    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (inFullScreen) {
            shell.setFullScreen(false);
            CompactModeAction compactAction = WorkbenchWindowService.getInstance().getCompactModeAction(window);
            // set status line visibility depending on compact mode status
            WorkbenchWindowService.setStatusLineVisibility((WorkbenchWindow) window,
                    compactAction.isInCompactMode() ? PreferencesHelper.showStatusLineInCompactMode() : true);
            if (!toolbarWasInvisible){
                WorkbenchWindowService.setToolbarVisibility((WorkbenchWindow) window, true);
            }
            if(!menuBarWasInvisible)
                shell.setMenuBar(menuBar);
            inFullScreen = false;
            WorkbenchWindowService.setInFullScreenMode(false);
            setText(FULLSCREEN);
            setImageDescriptor(fullScreenImage);
        }
        else {

            //enable or disable status line visibility
            WorkbenchWindowService.setStatusLineVisibility((WorkbenchWindow) window, PreferencesHelper.showStatusLineInFullScreenMode());

            if(PreferencesHelper.isShowFullScreenDialog()){
                TipDialog dialog = new TipDialog(shell, "Tip",
                        "Press F11 to exit full screen.");
                dialog.open();
                if(!dialog.isShowThisDialogAgain())
                    PreferencesHelper.setShowFullScreenDialog(false);
            }
            shell.setFullScreen(true);
            if (window instanceof WorkbenchWindow
                    && !((WorkbenchWindow) window).getCoolBarVisible()) {
                toolbarWasInvisible = true;
            } else {
                toolbarWasInvisible = false;
                WorkbenchWindowService.setToolbarVisibility((WorkbenchWindow) window, false);
            }
            if(shell.getMenuBar() == null)
                menuBarWasInvisible = true;
            else{
                menuBar = shell.getMenuBar();
                menuBarWasInvisible = false;
            }
            shell.setMenuBar(null);
            inFullScreen = true;
            WorkbenchWindowService.setInFullScreenMode(true);

            setText(EXIT_FULL_SCREEN);
            setImageDescriptor(exitFullScreenImage);
        }
    }

    @Override
    public void run(IAction action) {
        run();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {

    }

    @Override
    public void init(IWorkbenchWindow window) {
        setId(ID);
        this.window = window;
        shell = window.getShell();
        menuBar = shell.getMenuBar();
        FullScreenAction registeredAction =
            WorkbenchWindowService.getInstance().getFullScreenAction(window);
        //copy states
        if(registeredAction != null){
            inFullScreen = registeredAction.inFullScreen;
            menuBarWasInvisible = registeredAction.menuBarWasInvisible;
            toolbarWasInvisible = registeredAction.toolbarWasInvisible;
            menuBar = registeredAction.menuBar;
        }
        WorkbenchWindowService.getInstance().registerFullScreenAction(this, window);

        setText(FULLSCREEN);
        setImageDescriptor(fullScreenImage);
    }

    public boolean isInFullScreen() {
        return inFullScreen;
    }

    @Override
    public void dispose() {
        WorkbenchWindowService.getInstance().unregisterFullScreenAction(window);

    }


}

