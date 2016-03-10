package org.csstudio.dct.ui.editor;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.jface.action.IAction;

/**
 * Base class for actions that deal with the command stack of a DCT editor
 * (Undo/Redo).
 *
 * @author Sven Wende
 *
 */
public abstract class AbstractCommandStackAction extends AbstractDctEditorAction implements CommandStackEventListener {
    private CommandStack commandStack;

    /**
     *{@inheritDoc}
     */
    @Override
    protected final void activeEditorChanged(DctEditor editor) {
        if (commandStack != null) {
            commandStack.removeCommandStackEventListener(this);
        }

        commandStack = editor.getCommandStack();
        commandStack.addCommandStackEventListener(this);

        getActionProxy().setEnabled(isActionEnabled(commandStack));
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final void run(IAction action) {
        doRun(commandStack);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final void stackChanged(CommandStackEvent event) {
        getActionProxy().setEnabled(isActionEnabled(commandStack));
    }

    /**
     * Template method which is called when the action is executed.
     *
     * @param commandStack
     *            the command stack
     */
    protected abstract void doRun(CommandStack commandStack);

    /**
     * Template method. Inheriting classes must return the action enabled
     * state.
     *
     * @param commandStack
     *            the command stack
     * @return true, if the action is enabled, false otherwise
     */
    protected abstract boolean isActionEnabled(CommandStack commandStack);

}
