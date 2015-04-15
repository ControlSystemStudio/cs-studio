/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective.Position;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
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

    /** How/where a new display is presented */
    public enum DisplayMode
    {
        /** Replace current view or shell with new display content */
        REPLACE("Replace Current"),
        
        /** New view part within existing workbench */
        NEW_TAB("Workbench Tab"),
        
        /** .. in desired location, if possible */
        NEW_TAB_LEFT("Workbench Tab (Left)"),
        NEW_TAB_RIGHT("Workbench Tab (Right)"),
        NEW_TAB_TOP("Workbench Tab (Top)"),
        NEW_TAB_BOTTOM("Workbench Tab (Bottom)"),
        
        /** .. detached */
        NEW_TAB_DETACHED("Detached Tab"),
        
        /** New view part in new workbench window */
        NEW_WINDOW("New workbench"),
        
        /** New standalone Shell */
        NEW_SHELL("Standalone window");

        private String description;
        
        private DisplayMode(String desc)
        {
            this.description = desc;
        }

        public static String[] stringValues()
        {
            String[] sv = new String[values().length];
            int i=0;
            for (DisplayMode p : values())
                sv[i++] = p.description;
            return sv;
        }
    }

    // TODO Remove
    public enum TargetWindow {
        NEW_WINDOW,
        SAME_WINDOW,
        RUN_WINDOW,
        NEW_SHELL;
    }
    
    // TODO Check if used
    private IWorkbenchWindow runWorkbenchWindow;

    // TODO Make all static?
    private static RunModeService instance;

    public static RunModeService getInstance(){
        if(instance == null)
            instance = new RunModeService();
        return instance;
    }

    // TODO Update methods: Fewer, take DisplayMode
    
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
                        targetWindow = createNewWindow(Optional.ofNullable(windowBounds));
                    break;
                case RUN_WINDOW:
                    if(runWorkbenchWindow == null){
                        runWorkbenchWindow = createNewWindow(Optional.ofNullable(windowBounds));
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
        // Open new View
        // View will receive input from us, should ignore previous memento.
        // No need to revert back to "use memento" because that is only
        // applicable at CSS restart, loading saved state.
        // Once the user opens a new view, all mementos need to be ignored.
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

    /** Open new workbench window
     *  @param bounds Optional location (unless 0) and size (unless empty)
     *  @return Window
     */
    private static IWorkbenchWindow createNewWindow(final Optional<Rectangle> bounds)
    {
        try
        {
            final IWorkbenchWindow window = PlatformUI.getWorkbench().openWorkbenchWindow(
                    OPIRunnerPerspective.ID, null);
            if (bounds.isPresent())
            {
                if (bounds.get().x >=0  &&  bounds.get().y > 1)
                    window.getShell().setLocation(bounds.get().x, bounds.get().y);
                window.getShell().setSize(bounds.get().width+45, bounds.get().height + 165);
            }
            return window;
        }
        catch (WorkbenchException e)
        {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "Failed to open new window", e);
        }
        return null;
    }
    
    // TODO: Use only this method
    public static void openDisplay(final IPath path, final MacrosInput macros,
                              DisplayMode mode,
                              final Optional<DisplayOpenManager> display_manager, // TODO remove display_manager?
                              final Optional<Rectangle> bounds,
                              final Optional<IOPIRuntime> runtime)
    {
        final RunnerInput input = new RunnerInput(path, display_manager.orElse(null), macros);
        
        if (mode == DisplayMode.REPLACE)
        {   // Anything to replace?
            if (!runtime.isPresent())
                mode = DisplayMode.NEW_TAB;
            // Shell cannot be replaced, needs new shell
            else if (runtime.get() instanceof OPIShell)
                mode = DisplayMode.NEW_SHELL;
            else
            {   // Replace current OPIView.
                DisplayOpenManager manager = (DisplayOpenManager) (runtime.get()
                    .getAdapter(DisplayOpenManager.class));
                manager.openNewDisplay();
                try
                {
                    runtime.get().setOPIInput(new RunnerInput(path, manager, macros));
                }
                catch (PartInitException e)
                {
                    OPIBuilderPlugin.getLogger().log(Level.WARNING,
                            "Failed to open " + path, e);
                    MessageDialog.openError(Display.getDefault().getActiveShell(),
                            "Open file error",
                            NLS.bind("Failed to open {0}", path));
                }
            }
        }
        
        switch (mode)
        {
        case NEW_TAB:
        case NEW_TAB_LEFT:
        case NEW_TAB_RIGHT:
        case NEW_TAB_TOP:
        case NEW_TAB_BOTTOM:
        case NEW_TAB_DETACHED:
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            openDisplayInView(page, input, mode);
            break;
        case NEW_WINDOW:
            if (SWT.getPlatform().startsWith("rap"))
                SingleSourceHelper.rapOpenOPIInNewWindow(path);
            else
            {
                final IWorkbenchWindow targetWindow = createNewWindow(bounds);
                final Shell shell = targetWindow.getShell();
                if (shell.getMinimized())
                    shell.setMinimized(false);
                targetWindow.getShell().forceActive();
                targetWindow.getShell().forceFocus();
                openDisplayInView(targetWindow.getActivePage(), input, DisplayMode.NEW_TAB);
                targetWindow.getShell().moveAbove(null);
            }
            break;
        case NEW_SHELL:
            OPIShell.openOPIShell(path, macros);
            break;
        case REPLACE:
            break;
        default:
            throw new Error("Cannot handle " + mode);
        }
        
    }
    
    private static void openDisplayInView(final IWorkbenchPage page, final RunnerInput input, final DisplayMode mode)
    {
        OPIView.setOpenedByUser(true);
        UIBundlingThread.getInstance().addRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // Check for existing view with same input.
                    for (IViewReference viewReference : page.getViewReferences())
                        if (viewReference.getId().startsWith(OPIView.ID))
                        {
                            final IViewPart view = viewReference.getView(true);
                            if (view instanceof OPIView)
                            {
                                final OPIView opi_view = (OPIView)view;
                                if (input.equals(opi_view.getOPIInput()))
                                {
                                    page.showView(viewReference.getId(), viewReference.getSecondaryId(), IWorkbenchPage.VIEW_ACTIVATE);
                                    return;
                                }
                            }
                            else
                                OPIBuilderPlugin.getLogger().log(Level.WARNING,
                                    "Found view " + view.getTitle() + " but its type is " + view.getClass().getName());
                        }
                    
                    // Open new View
                    // View will receive input from us, should ignore previous memento.
                    // No need to revert back to "use memento" because that is only
                    // applicable at CSS restart, loading saved state.
                    // Once the user opens a new view, all mementos need to be ignored.
                    OPIView.ignoreMemento();
                    // Create view ID that - when used with OPIRunnerPerspective -
                    // causes view to appear in desired location
                    final String secondID =  OPIView.createSecondaryID();
                    final Position position;
                    switch (mode)
                    {
                    case NEW_TAB_LEFT:     position = Position.LEFT;     break;
                    case NEW_TAB_RIGHT:    position = Position.RIGHT;    break;
                    case NEW_TAB_TOP:      position = Position.TOP;      break;
                    case NEW_TAB_BOTTOM:   position = Position.BOTTOM;   break;
                    case NEW_TAB_DETACHED: position = Position.DETACHED; break;
                    default:               position = Position.DEFAULT_VIEW;
                    }
                    final IViewPart view = page.showView(position.getOPIViewID(), secondID, IWorkbenchPage.VIEW_ACTIVATE);
                    if (! (view instanceof OPIView))
                        throw new PartInitException("Expected OPIView, got " + view);
                    final OPIView opiView = (OPIView) view;

                    // Set content of view
                    opiView.setOPIInput(input);

                    // Adjust position
                    if (position == Position.DETACHED)
                        SingleSourcePlugin.getUIHelper().detachView(opiView);
                }
                catch (Exception e)
                {
                    ErrorHandlerUtil.handleError(NLS.bind("Failed to run OPI {1} in view.", input.getPath()), e);
                }
            }
        });
    }
}
