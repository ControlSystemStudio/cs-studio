package org.csstudio.perspectives;

import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.ui.PlatformUI;

/**
 * Handler for loading perspectives.
 */
public class PerspectiveHandler extends AbstractHandler {

    private Logger log = Logger.getLogger(PerspectiveHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        log.config("PerspectiveHandler: execute");
        IEclipseContext context = PlatformUI.getWorkbench().getService(IEclipseContext.class);
        final PerspectiveLoader ps = ContextInjectionFactory.make(PerspectiveLoader.class, context);
        ps.loadPerspectives();
        return null;
    }

}
