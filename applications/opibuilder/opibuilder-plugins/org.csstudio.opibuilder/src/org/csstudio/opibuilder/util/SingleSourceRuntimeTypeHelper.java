package org.csstudio.opibuilder.util;

import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.RunModeService.DisplayMode;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 *
 * <code>SingleSourceRuntimeTypeHelper</code> provides methods required for opening an OPI file in runtime. The
 * fragment that provides implementation of the runtime editors or views should also implement this helper class
 * and perform those actions that can be performed in the running platform.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public abstract class SingleSourceRuntimeTypeHelper {

    private static final SingleSourceRuntimeTypeHelper IMPL;

    static {
        IMPL = (SingleSourceRuntimeTypeHelper)ImplementationLoader.newInstance(
                SingleSourceRuntimeTypeHelper.class);
    }

    /**
     * Opens a shell dedicated to displaying the OPI file specified by the given path. The macro input should be applied
     * to the new opi. It is up to the implementor to decide which shell to use for the OPI and how to attach the OPI to
     * the shell. THe implementor may even decide to share the shells among different OPIs.
     *
     * @param path the path to the OPI file that needs to be displayed in a shell
     * @param input the macro input applied to the open OPI
     */
    public static void openOPIShell(IPath path, MacrosInput input) {
        if (IMPL != null)
            IMPL.iOpenOPIShell(path, input);
        else {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Not Implemented",
                    "Sorry, open OPI shell action is not implemented for WebOPI!");
        }
    }

    protected abstract void iOpenOPIShell(IPath path, MacrosInput input);

    /**
     * Returns the OPIRuntime that is open in the given shell.
     *
     * @param shell the shell for which the runtime is requested
     * @return the opi runtime if found or null otherwise
     */
    public static IOPIRuntime getOPIRuntimeForShell(Shell shell) {
        if (IMPL == null)
            return null;
        return IMPL.iGetOPIRuntimeForShell(shell);
    }

    protected abstract IOPIRuntime iGetOPIRuntimeForShell(Shell shell);

    /**
     * Returns the OPIRuntime that is the best match for the given event. This can either be the runtime that matches
     * the shell or runtime running in the workbench page which the event originated from.
     *
     * @param event the event describing where the call initiated from
     * @return the opi runtime if found or null otherwise
     */
    public static IOPIRuntime getOPIRuntimeForEvent(ExecutionEvent event) {
        if (IMPL == null)
            return null;
        return IMPL.iGetOPIRuntimeForEvent(event);
    }

    protected abstract IOPIRuntime iGetOPIRuntimeForEvent(ExecutionEvent event);

    /**
     * Opens the OPI (defined by input) in the given page according to the display mode. The implementor can expect
     * that page already exists and does not need to value the display mode. If displaying the OPI in the requested
     * mode is not possible, other logic may be used to determine its location.
     *
     * @param page the page in which to open the OPI
     * @param input the OPI input descriptor
     * @param mode the display mode which defines the position of the new OPI
     */
    public static void openDisplay(IWorkbenchPage page, RunnerInput input, DisplayMode mode) {
        if (IMPL != null)
            IMPL.iOpenDisplay(page,input,mode);
    }

    protected abstract void iOpenDisplay(IWorkbenchPage page, RunnerInput input, DisplayMode mode);
}
