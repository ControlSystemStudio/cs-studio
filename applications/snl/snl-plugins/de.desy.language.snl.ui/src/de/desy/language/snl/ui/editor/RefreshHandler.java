package de.desy.language.snl.ui.editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.desy.language.editor.ui.eventing.UIEvent;

public class RefreshHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        UIEvent.HIGHLIGHTING_REFRESH_REQUEST.triggerEvent();
        return null;
    }
}
