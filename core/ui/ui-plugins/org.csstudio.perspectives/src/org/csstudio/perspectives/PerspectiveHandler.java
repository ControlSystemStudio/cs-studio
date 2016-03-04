package org.csstudio.perspectives;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Handler for loading perspectives.
 */
public class PerspectiveHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Plugin.getLogger().config("PerspectiveHandler: execute");
        IEclipseContext context = PlatformUI.getWorkbench().getService(IEclipseContext.class);
        context.set(IFileUtils.class.getCanonicalName(), new FileUtils());
        final PerspectiveLoader ps = ContextInjectionFactory.make(PerspectiveLoader.class, context);
        Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        ps.promptAndLoadPerspective(parent);
        return null;
    }

}
