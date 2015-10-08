package org.csstudio.opibuilder.util;

import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIRunner;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective;
import org.csstudio.opibuilder.runmode.RunModeService.DisplayMode;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

public class SingleSourceRuntimeTypeHelperImpl extends SingleSourceRuntimeTypeHelper {

    private IWorkbenchWindow runWindow;

    @Override
    protected IOPIRuntime iGetOPIRuntimeForEvent(ExecutionEvent event) {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        return part instanceof IOPIRuntime ? (IOPIRuntime)part : null;
    }

    @Override
    protected void iOpenOPIShell(IPath path, MacrosInput input) {
        try {
            if (runWindow == null) {
                runWindow = PlatformUI.getWorkbench().openWorkbenchWindow(
                        OPIRunnerPerspective.ID, null);
                runWindow.addPageListener(new IPageListener() {
                    public void pageClosed(IWorkbenchPage page) {
                        runWindow = null;
                    }

                    public void pageActivated(IWorkbenchPage page) {
                        // NOP
                    }

                    public void pageOpened(IWorkbenchPage page) {
                        // NOP
                    }
                });
            }
            IWorkbenchPage page = runWindow.getActivePage();
            Shell shell = runWindow.getShell();
            if (shell.getMinimized())
                shell.setMinimized(false);
            shell.forceActive();
            shell.forceFocus();
            iOpenDisplay(page, new RunnerInput(path, null, input), DisplayMode.NEW_TAB);
            shell.moveAbove(null);
        } catch (WorkbenchException e) {
            ErrorHandlerUtil.handleError(NLS.bind("Failed to open {0}", path), e);
        }
    }

    @Override
    protected IOPIRuntime iGetOPIRuntimeForShell(Shell shell) {
        return null;
    }

    @Override
    protected void iOpenDisplay(IWorkbenchPage page, RunnerInput input, DisplayMode mode) {
        try {
            page.openEditor(input, OPIRunner.ID);
        } catch (PartInitException e) {
            ErrorHandlerUtil.handleError(NLS.bind("Failed to open {0}", input.getPath()), e);
        }

    }
}
