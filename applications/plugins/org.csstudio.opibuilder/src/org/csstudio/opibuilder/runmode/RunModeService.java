/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective.Position;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.opibuilder.visualparts.TipDialog;
import org.csstudio.ui.util.perspective.PerspectiveHelper;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/** Service for executing a display
 *  @author Xihui Chen - Original author
 *  @author Kay Kasemir
 */
public class RunModeService {

    public enum TargetWindow {
        NEW_WINDOW,
        SAME_WINDOW,
        RUN_WINDOW,
        NEW_SHELL;
    }

    private IWorkbenchWindow runWorkbenchWindow;

    private static RunModeService instance;

    public static RunModeService getInstance(){
        if(instance == null)
            instance = new RunModeService();
        return instance;
    }


    public IWorkbenchWindow getRunWorkbenchWindow(){
        return runWorkbenchWindow;
    }

    public static void replaceOPIRuntimeContent(
            final IOPIRuntime opiRuntime, final IEditorInput input) throws PartInitException{
        opiRuntime.setOPIInput(input);
    }

    /**Run an OPI file with necessary parameters. This function should be called when open an OPI
     * from another OPI.
     * @param path
     * @param targetWindow
     * @param displayOpenManager
     * @param macrosInput
     */
    public void runOPI(IPath path, TargetWindow targetWindow, DisplayOpenManager displayOpenManager,
            MacrosInput macrosInput){
        runOPI(path, targetWindow, displayOpenManager, macrosInput, null);
    }

    /**Run an OPI file in the target window.
     * @param path
     * @param targetWindow
     */
    public void runOPI(IPath path, TargetWindow targetWindow, Rectangle windowSize){
        runOPI(path, targetWindow, null, null, windowSize);
    }

    /**Run an OPI file.
     * @param path the file to be ran. If displayModel is not null, this will be ignored.
     * @param displayModel the display model to be ran. null for file input only.
     * @param displayOpenManager the manager help to manage the opened displays. null if the OPI is not
     * replacing the current active display.
     */
    public void runOPI(final IPath path, final TargetWindow target,
            final DisplayOpenManager displayOpenManager, final MacrosInput macrosInput, final Rectangle windowBounds){
        final RunnerInput runnerInput = new RunnerInput(path, displayOpenManager, macrosInput);
        UIBundlingThread.getInstance().addRunnable(new Runnable(){
            @Override
            public void run() {

                IWorkbenchWindow targetWindow = null;
                switch (target) {
                case NEW_WINDOW:
                    if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
                        SingleSourceHelper.rapOpenOPIInNewWindow(path);
                    else
                        targetWindow = createNewWindow(windowBounds);
                    break;
                case RUN_WINDOW:
                    if(runWorkbenchWindow == null){
                        runWorkbenchWindow = createNewWindow(windowBounds);
                        runWorkbenchWindow.addPageListener(new IPageListener(){
                            @Override
                            public void pageClosed(IWorkbenchPage page) {
                                runWorkbenchWindow = null;
                            }

                            @Override
                            public void pageActivated(IWorkbenchPage page) {
                                // NOP
                            }

                            @Override
                            public void pageOpened(IWorkbenchPage page) {
                                // NOP
                            }
                        });
                    }else{
                        for(IEditorReference editor :
                            runWorkbenchWindow.getActivePage().getEditorReferences()){
                            try {
                                if(editor.getEditorInput().equals(runnerInput))
                                    editor.getPage().closeEditor(editor.getEditor(false), false);
                            } catch (PartInitException e) {
                                 OPIBuilderPlugin.getLogger().log(Level.WARNING,
                                            "Cannot close editor", e); //$NON-NLS-1$
                            }
                        }
                    }
                    targetWindow = runWorkbenchWindow;
                    break;
                case NEW_SHELL:
                    targetWindow = null;
                    break;
                case SAME_WINDOW:
                default:
                    targetWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    break;
                }

                if(targetWindow != null){
                    try {
                        Shell shell = targetWindow.getShell();
                        if(shell.getMinimized())
                            shell.setMinimized(false);
                        targetWindow.getShell().forceActive();
                        targetWindow.getShell().forceFocus();
                        openNewOPIView(runnerInput, targetWindow.getActivePage(), Position.DEFAULT_VIEW);
                        if(!SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
                            targetWindow.getShell().moveAbove(null);
                    } catch (Exception e) {
                        OPIBuilderPlugin.getLogger().log(Level.WARNING,
                                "Failed to run OPI " + path.lastSegment(), e);
                    }
                } else {
                    OPIShell.openOPIShell(path, macrosInput);
                }
            }
        });
    }


    public static void runOPIInView(final IPath path,
            final DisplayOpenManager displayOpenManager, final MacrosInput macrosInput, final Position position)
    {
        OPIView.setOpenedByUser(true);
        final RunnerInput runnerInput = new RunnerInput(path, displayOpenManager, macrosInput);
        UIBundlingThread.getInstance().addRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                final IWorkbench workbench = PlatformUI.getWorkbench();
                final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                final IWorkbenchPage page = window.getActivePage();
                try
                {
                    IViewReference[] viewReferences = page.getViewReferences();
                    //If it is already opened.
                    for (IViewReference viewReference : viewReferences)
                        if (viewReference.getId().startsWith(OPIView.ID))
                        {
                            final IViewPart view = viewReference.getView(true);
                            if (view instanceof OPIView)
                            {
                                final OPIView opi_view = (OPIView)view;
                                if (runnerInput.equals(opi_view.getOPIInput()))
                                {
                                    page.showView(viewReference.getId(), viewReference.getSecondaryId(), IWorkbenchPage.VIEW_ACTIVATE);
                                    return;
                                }
                            }
                            else
                                OPIBuilderPlugin.getLogger().log(Level.WARNING,
                                    "Found view " + view.getTitle() + " but its type is " + view.getClass().getName());
                        }
                    openNewOPIView(runnerInput, page, position);
                }
                catch (Exception e)
                {
                    ErrorHandlerUtil.handleError(NLS.bind("Failed to run OPI {1} in view.", path), e);
                }
            }
        });
    }

    /** Open a new View that executes a display
     *  @param runnerInput {@link RunnerInput}
     *  @param page {@link IWorkbenchPage}
     *  @param position {@link Position}
     *  @return {@link OPIView}
     *  @throws Exception on error
     */
    public static OPIView openNewOPIView(final RunnerInput runnerInput, final IWorkbenchPage page, final Position position) throws Exception
    {
        // Switch to suitable perspective?
        if (position != Position.DETACHED && position != Position.DEFAULT_VIEW &&
            !(page.getPerspective().getId().equals(OPIRunnerPerspective.ID)))
        {
            if (!OPIBuilderPlugin.isRAP())
            {
                if (PreferencesHelper.isShowOpiRuntimePerspectiveDialog())
                {
                    TipDialog dialog = new TipDialog(page.getWorkbenchWindow().getShell(), MessageDialog.QUESTION,
                            "Switch to OPI Runtime Perspective",
                            "To open the OPI View in expected position, you need to switch to OPI Runtime perspective."+
                            "\nDo you want to switch to it now?");
                    PreferencesHelper.setSwitchToOpiRuntimePerspective(dialog.open() == Window.OK);
                    if (!dialog.isShowThisDialogAgain())
                        PreferencesHelper.setShowOpiRuntimePerspectiveDialog(false);
                }
                if (PreferencesHelper.isSwitchToOpiRuntimePerspective())
                    PerspectiveHelper.showPerspective(OPIRunnerPerspective.ID, page);
            }
        }

        // Open new View
        // View will receive input from us, should ignore previous memento
        OPIView.ignoreMemento();
        // Create view ID that - when used with OPIRunnerPerspective -
        // causes view to appear in desired location
        final String secondID =  OPIView.createSecondaryID();
        final IViewPart view = page.showView(position.getOPIViewID(), secondID, IWorkbenchPage.VIEW_ACTIVATE);
        if (! (view instanceof OPIView))
            throw new PartInitException("Expected OPIView, got " + view);
        final OPIView opiView = (OPIView) view;

        // Set content of view
        opiView.setOPIInput(runnerInput);

        // Adjust position
        if (position == Position.DETACHED)
            SingleSourcePlugin.getUIHelper().detachView(opiView);

        return opiView;
    }

    /**
     * @param windowBounds
     */
    private IWorkbenchWindow createNewWindow(Rectangle windowBounds) {
        IWorkbenchWindow newWindow = null;
        try {
            newWindow =
                PlatformUI.getWorkbench().openWorkbenchWindow(OPIRunnerPerspective.ID, null);
            if(windowBounds != null){
                if(windowBounds.x >=0 && windowBounds.y > 1)
                    newWindow.getShell().setLocation(windowBounds.x, windowBounds.y);
                newWindow.getShell().setSize(windowBounds.width+45, windowBounds.height + 165);
            }

        } catch (WorkbenchException e) {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "Failed to open new window", e);
        }
        return newWindow;
    }
}
