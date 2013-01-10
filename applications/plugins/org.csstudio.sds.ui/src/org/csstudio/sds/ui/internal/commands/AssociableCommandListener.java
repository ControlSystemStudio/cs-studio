package org.csstudio.sds.ui.internal.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CommandStackEventListener;

/**
 * Has to be registered at the CommandStack to handle AccosiableCommands.
 * 
 * @see AssociableCommand
 * 
 * @author Christian Zoller
 */
public class AssociableCommandListener implements CommandStackEventListener {
    
    private final CommandStack _commandStack;
    
    public AssociableCommandListener(CommandStack commandStack) {
        _commandStack = commandStack;
    }
    
    @Override
    public void stackChanged(CommandStackEvent event) {
        if (event.getCommand() instanceof AssociableCommand) {
            if (event.getDetail() == CommandStack.PRE_UNDO) {
                skipUndoWhenAssociated((AssociableCommand) event.getCommand());
            } else if (event.getDetail() == CommandStack.PRE_REDO) {
                skipRedoWhenAssociated((AssociableCommand) event.getCommand());
            } else if (event.getDetail() == CommandStack.POST_UNDO) {
                undoAssociatedCommands((AssociableCommand) event.getCommand());
            } else if (event.getDetail() == CommandStack.POST_REDO) {
                redoAssociatedCommands((AssociableCommand) event.getCommand());
            }
        }
    }
    
    private void skipUndoWhenAssociated(AssociableCommand associable) {
        Command predecessor = _commandStack.getUndoCommand();
        if (associated(associable, predecessor)) {
            associable.skipNextStackAction();
        }
    }
    
    private void skipRedoWhenAssociated(AssociableCommand associable) {
        Command successor = _commandStack.getRedoCommand();
        if (associated(associable, successor)) {
            associable.skipNextStackAction();
        }
    }
    
    private void undoAssociatedCommands(AssociableCommand associable) {
        Command predecessor = _commandStack.getUndoCommand();
        if (associated(associable, predecessor)) {
            _commandStack.undo();
        }
    }
    
    private void redoAssociatedCommands(AssociableCommand associable) {
        Command successor = _commandStack.getRedoCommand();
        if (associated(associable, successor)) {
            _commandStack.redo();
        }
    }
    
    private boolean associated(AssociableCommand associable, Command command) {
        if (!(command instanceof AssociableCommand)) {
            return false;
        }
        
        AssociableCommand associable2 = (AssociableCommand) command;
        return associable.isAssociated(associable2);
    }
}
