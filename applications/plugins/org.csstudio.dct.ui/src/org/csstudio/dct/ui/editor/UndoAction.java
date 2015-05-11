package org.csstudio.dct.ui.editor;

import org.eclipse.gef.commands.CommandStack;

/**
 * Undo Action that handles the {@link CommandStack} of a
 * {@link DctEditor}.
 *
 * @author Sven Wende
 */
public final class UndoAction extends AbstractCommandStackAction {

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRun(CommandStack commandStack) {
        commandStack.undo();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected boolean isActionEnabled(CommandStack commandStack) {
        return commandStack.canUndo();
    }

}
