package org.csstudio.dct.ui.editor;

import org.eclipse.gef.commands.CommandStack;

/**
 * Redo Action that handles the {@link CommandStack} of a
 * {@link DctEditor}.
 *
 * @author Sven Wende
 */
public final class RedoAction extends AbstractCommandStackAction {

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRun(CommandStack commandStack) {
        commandStack.redo();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected boolean isActionEnabled(CommandStack commandStack) {
        return commandStack.canRedo();
    }

}
