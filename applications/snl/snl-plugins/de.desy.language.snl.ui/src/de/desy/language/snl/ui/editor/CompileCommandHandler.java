package de.desy.language.snl.ui.editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class CompileCommandHandler extends AbstractHandler implements IHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {

        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        if (editor instanceof SNLEditor) {
            if (editor.isDirty()) {
                MessageBox box = new MessageBox(editor.getSite().getShell(),
                        SWT.ICON_INFORMATION);
                box.setText("Unsaved changes");
                box.setMessage("There are unsaved changes.\nPlease save before compilation.");
                box.open();
            } else {
                ((SNLEditor)editor).compileFile(new NullProgressMonitor());
            }
        }
        return null;
    }

}
