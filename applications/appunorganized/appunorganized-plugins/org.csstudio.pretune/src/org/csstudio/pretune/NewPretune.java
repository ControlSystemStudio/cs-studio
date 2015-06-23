package org.csstudio.pretune;

import org.csstudio.ui.util.EmptyEditorInput;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * A Open the pretune editor without any associated configuration file and let
 * the user browse and load any json configuration file.
 *
 * @author Kunal Shroff
 *
 */
public class NewPretune extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        IWorkbenchPage page = window.getActivePage();
        try {
            page.openEditor(new EmptyEditorInput() , PreTuneEditor.ID);
        } catch (PartInitException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
