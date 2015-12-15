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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/** Service for executing a display
 *  @author Xihui Chen - Original author
 *  @author Kay Kasemir
 */
public class RunModeService
{
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

    /** Open a display in runtime
     *
     *  @param path Path to the display file
     *  @param macros Optional macros
     *  @param mode {@link DisplayMode}
     *  @param runtime Runtime to update in case DisplayMode is 'replace'
     */
    public static void openDisplay(final IPath path, final Optional<MacrosInput> macros,
                              DisplayMode mode,
                              final Optional<IOPIRuntime> runtime)
    {
        final RunnerInput input = new RunnerInput(path, null, macros.orElse(null));
        try
        {
            if (mode == DisplayMode.REPLACE)
            {   // Anything to replace?
                if (!runtime.isPresent())
                    mode = DisplayMode.NEW_TAB;
                else
                {   // Replace display in current runtime
                    final DisplayOpenManager manager = runtime.get().getAdapter(DisplayOpenManager.class);
                    if (manager != null) {
                        manager.openNewDisplay();
                    }
                    runtime.get().setOPIInput(new RunnerInput(path, manager, macros.orElse(null)));
                    return;
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
            {
                final IWorkbench workbench = PlatformUI.getWorkbench();
                final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                final IWorkbenchPage page = window.getActivePage();
                openDisplayInView(page, input, mode);
                break;
            }
            case NEW_WINDOW:
                if (SWT.getPlatform().startsWith("rap"))
                    SingleSourceHelper.rapOpenOPIInNewWindow(path);
                else
                {
                    final IWorkbenchPage page = createNewWorkbenchPage(Optional.empty());
                    final Shell shell = page.getWorkbenchWindow().getShell();
                    if (shell.getMinimized())
                        shell.setMinimized(false);
                    shell.forceActive();
                    shell.forceFocus();
                    openDisplayInView(page, input, DisplayMode.NEW_TAB);
                    shell.moveAbove(null);
                }
                break;
            case NEW_SHELL:
                SingleSourceHelper.openOPIShell(path, macros.orElse(null));
                break;
            default:
                throw new Exception("Unknown display mode " + mode);
            }
        }
        catch (Exception ex)
        {
            ErrorHandlerUtil.handleError(NLS.bind("Failed to open {0}", path), ex);
        }
    }

    /** Open new workbench page
     *  @param bounds Optional location (unless 0) and size (unless empty)
     *  @return IWorkbenchPage
     *  @throws Exception on error
     */
    public static IWorkbenchPage createNewWorkbenchPage(final Optional<Rectangle> bounds) throws Exception
    {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().openWorkbenchWindow(
                OPIRunnerPerspective.ID, null);
        if (bounds.isPresent())
        {
            if (bounds.get().x >=0  &&  bounds.get().y > 1)
                window.getShell().setLocation(bounds.get().x, bounds.get().y);
            window.getShell().setSize(bounds.get().width+45, bounds.get().height + 165);
        }
        return window.getActivePage();
    }

    /** Display a view on a specific workbench page
     *  @param page Page to use
     *  @param input {@link RunnerInput}
     *  @param mode Mode, must be one related to Views ("NEW_TAB_*")
     */
    public static void openDisplayInView(final IWorkbenchPage page, final RunnerInput input, final DisplayMode mode)
    {
        UIBundlingThread.getInstance().addRunnable(() ->
        {
            if (OPIBuilderPlugin.isRAP())
            {
                try
                {
                    page.openEditor(input, OPIRunner.ID);
                }
                catch (PartInitException e)
                {
                    ErrorHandlerUtil.handleError(NLS.bind("Failed to open {0}.", input.getPath()), e);
                }
            } else {
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
                    if (position == Position.DETACHED) {
                        SingleSourcePlugin.getUIHelper().detachView(opiView);
                        opiView.positionFromModel();
                    }
                }
                catch (Exception e)
                {
                    ErrorHandlerUtil.handleError(NLS.bind("Failed to open {0} in view.", input.getPath()), e);
                }
            }
        });
    }
}
