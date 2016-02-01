package org.csstudio.opibuilder.util;

import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIRunner;
import org.csstudio.opibuilder.runmode.RunModeService.DisplayMode;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

public class SingleSourceRuntimeTypeHelperImpl extends SingleSourceRuntimeTypeHelper {

    @Override
    protected void iOpenOPIShell(IPath path, MacrosInput input) {
    }

    @Override
    protected IOPIRuntime iGetOPIRuntimeForShell(Shell shell) {
        return null;
    }

    @Override
    protected IOPIRuntime iGetOPIRuntimeForEvent(ExecutionEvent event) {
        return null;
    }

    @Override
    protected void iOpenDisplay(IWorkbenchPage page, RunnerInput input, DisplayMode mode) {
        try
        {
            page.openEditor(input, OPIRunner.ID);
        }
        catch (PartInitException e)
        {
            ErrorHandlerUtil.handleError(NLS.bind("Failed to open {0}.", input.getPath()), e);
        }
    }
}
