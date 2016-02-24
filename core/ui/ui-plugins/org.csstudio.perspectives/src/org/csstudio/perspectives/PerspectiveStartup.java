package org.csstudio.perspectives;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

public class PerspectiveStartup implements IStartup {

    /**
     * Create a perspective saver on startup.
     */
    @Override
    public void earlyStartup() {
        IEclipseContext context = PlatformUI.getWorkbench().getService(IEclipseContext.class);
        context.set(IPerspectiveUtils.class.getCanonicalName(), new PerspectiveUtils());
        ContextInjectionFactory.make(PerspectiveSaver.class, context);
    }

}
