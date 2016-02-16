package org.csstudio.utility.product;

import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * Create a PerspectiveSaver on startup.
 */
public class PerspectiveHandler extends AbstractHandler implements IWorkbenchWindowAdvisorExtPoint {

    private Logger log = Logger.getLogger(PerspectiveHandler.ID);

    @Override
    public void preWindowOpen() {
        log.config("PerspectiveHandler: preWindowOpen");
        IEclipseContext context = PlatformUI.getWorkbench().getService(IEclipseContext.class);
        PerspectiveSaver ps = ContextInjectionFactory.make(PerspectiveSaver.class, context);
        ps.init();
    }

    @Override
    public boolean preWindowShellClose() {
        return true;
    }

    @Override
    public void postWindowRestore() throws WorkbenchException {}

    @Override
    public void postWindowCreate() {}

    @Override
    public void postWindowOpen() {}

    @Override
    public void postWindowClose() {}

    @Override
    public IStatus saveState(IMemento memento) {
        return null;
    }

    @Override
    public IStatus restoreState(IMemento memento) {
        return null;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        log.config("PerspectiveHandler: execute");
        IEclipseContext context = PlatformUI.getWorkbench().getService(IEclipseContext.class);
        final PerspectiveLoader ps = ContextInjectionFactory.make(PerspectiveLoader.class, context);
        ps.loadPerspectives();
        return null;
    }

}
