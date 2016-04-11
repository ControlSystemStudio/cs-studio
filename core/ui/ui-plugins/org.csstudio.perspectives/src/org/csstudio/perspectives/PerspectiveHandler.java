package org.csstudio.perspectives;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Handler for loading perspectives.
 */
public class PerspectiveHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Plugin.getLogger().config("PerspectiveHandler: execute");
        Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        PerspectiveLoader loader = PerspectiveLoader.create();
        loader.promptAndLoadPerspective(parent);
        return null;
    }

}
