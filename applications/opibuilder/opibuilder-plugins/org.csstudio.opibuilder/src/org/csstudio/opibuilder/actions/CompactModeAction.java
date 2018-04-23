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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * The action to make CSS full screen.
 *
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public class CompactModeAction extends Action implements
        IWorkbenchWindowActionDelegate {

    private static final String COMPACT_MODE = "Compact Mode";

    private static final String EXIT_COMPACT_MODE = "Exit Compact Mode";

    public static final String ID = "org.csstudio.opibuilder.actions.compactMode"; //$NON-NLS-1$

    private Menu menuBar;
    private boolean inCompactMode = false;
    private Shell shell;
    private ImageDescriptor compactModeImage = CustomMediaFactory.getInstance()
            .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                    "icons/compact_mode.png");
    private ImageDescriptor exitCompactModeImage = CustomMediaFactory
            .getInstance().getImageDescriptorFromPlugin(
                    OPIBuilderPlugin.PLUGIN_ID, "icons/exit_compact_mode.gif");
    private IWorkbenchWindow window;
    private boolean toolbarWasInvisible;

    /**
     * Constructor.
     *
     * @param part
     *            The workbench part associated with this PrintAction
     */
    public CompactModeAction() {
        setActionDefinitionId(ID);
        setText(COMPACT_MODE);
        setImageDescriptor(compactModeImage);
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        //Do nothing if in full screen
        FullScreenAction fullScreenAction = WorkbenchWindowService.getInstance().
                getFullScreenAction(window);
        if( fullScreenAction != null && fullScreenAction.isInFullScreen()){
            MessageDialog.openWarning(shell, "Warning",
                    "This operation does not work in full screen. \n" +
                    "Please exit full screen (press F11) and try again.");
            return;
        }
        if (inCompactMode) {
            if (!toolbarWasInvisible){
                WorkbenchWindowService.setToolbarVisibility((WorkbenchWindow) window, true);
            }
           //status line is always visible in normal mode.
            WorkbenchWindowService.setStatusLineVisibility((WorkbenchWindow) window, true);

            //toggleToolbarAction.run();
            shell.setMenuBar(menuBar);
            inCompactMode = false;
            WorkbenchWindowService.setInCompactMode(false);
            setText(COMPACT_MODE);
            setImageDescriptor(compactModeImage);
        } else {

            WorkbenchWindowService.setStatusLineVisibility((WorkbenchWindow) window, PreferencesHelper.showStatusLineInCompactMode());

            if(PreferencesHelper.isShowCompactModeDialog()){
                TipDialog dialog = new TipDialog(shell, "Tip", "Press F8 to exit compact mode.");
                dialog.open();
                if(!dialog.isShowThisDialogAgain())
                    PreferencesHelper.setShowCompactModeDialog(false);
            }

            if (window instanceof WorkbenchWindow
                    && !((WorkbenchWindow) window).getCoolBarVisible()) {
                toolbarWasInvisible = true;
            } else {
                toolbarWasInvisible = false;
                WorkbenchWindowService.setToolbarVisibility((WorkbenchWindow) window, false);

                //toggleToolbarAction.run();
            }
            shell.setMenuBar(null);
            inCompactMode = true;
            WorkbenchWindowService.setInCompactMode(true);

            setText(EXIT_COMPACT_MODE);
            setImageDescriptor(exitCompactModeImage);
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

        if(WorkbenchWindowService.getInstance().getCompactModeAction(window) != null){
            copyFrom(WorkbenchWindowService.getInstance().getCompactModeAction(window));
            WorkbenchWindowService.getInstance().registerCompactModeAction(this, window);
            return;
        }

        setId(ID);
        this.window = window;
        //if already registered

        shell = window.getShell();

        menuBar = shell.getMenuBar();
        if(menuBar == null &&
                WorkbenchWindowService.getInstance().getCompactModeAction(window) != null)
            menuBar = WorkbenchWindowService.getInstance().getCompactModeAction(window).getMenuBar();

        WorkbenchWindowService.getInstance().registerCompactModeAction(this, window);

//        if (WorkbenchWindowService.isInCompactMode()) {
//            inCompactMode = true;
//            WorkbenchWindowService.setToolbarVisibility((WorkbenchWindow) window, false);
//            shell.setMenuBar(null);
//            setImageDescriptor(exitCompactModeImage);
//            setText(EXIT_COMPACT_MODE);
//
//        } else {
//            setText(COMPACT_MODE);
//            setImageDescriptor(compactModeImage);
//        }
    }

    @Override
    public void dispose() {
        WorkbenchWindowService.getInstance().unregisterCompactModeAction(window);
    }

    protected Menu getMenuBar() {
        return menuBar;
    }

    public boolean isInCompactMode() {
        return inCompactMode;
    }

    public void copyFrom(CompactModeAction action){
        this.shell=action.shell;
        this.window=action.window;
        this.menuBar = action.getMenuBar();
        this.inCompactMode = action.inCompactMode;
        this.toolbarWasInvisible=action.toolbarWasInvisible;
        setText(action.getText());
        setImageDescriptor(action.getImageDescriptor());
    }

}
