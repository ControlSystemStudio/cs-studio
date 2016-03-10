package org.csstudio.dct.ui.editor;

import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * Base class for all actions dedicated to a DCT editor instance.
 *
 * @author Sven Wende
 *
 */
public abstract class AbstractDctEditorAction implements IEditorActionDelegate, CommandStackEventListener {
    private DctEditor editor;
    private IAction actionProxy;

    /**
     *{@inheritDoc}
     */
    @Override
    public final void setActiveEditor(IAction action, IEditorPart targetEditor) {
        if (targetEditor instanceof DctEditor) {
            editor = (DctEditor) targetEditor;
            actionProxy = action;

            activeEditorChanged(editor);
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {

    }

    /**
     * Returns the action proxy that handles the presentation portion of the
     * action
     *
     * @return the action proxy that handles the presentation portion of the
     *         action
     */
    public final IAction getActionProxy() {
        return actionProxy;
    }

    /**
     * Templates method which is called, when the active DCT editor changes.
     *
     * @param editor
     *            the currently active DCT editor
     */
    protected abstract void activeEditorChanged(DctEditor editor);

}
