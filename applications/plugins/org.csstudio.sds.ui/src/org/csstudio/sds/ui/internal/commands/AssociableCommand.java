package org.csstudio.sds.ui.internal.commands;

/**
 * This interface can be implemented by Commands.  
 * If an AssociableCommandListener is registered at the appropriate CommandStack, 
 * Associable Commands are combined in Undo and Redo actions when they are associated and 
 * no not associated Commands were executed in between.
 * 
 * @see AssociableCommandListener
 * 
 * @author Christian Zoller
 */
public interface AssociableCommand {
    
    /**
     * Checks whether another AssociableCommand is associated to this command and can be combined
     * in one undo/redo action.
     * 
     * Commands can only be associated if the undo of the lowest command in the command stack is 
     * able to undo the effects of all associated following commands and - vice versa - the highest 
     * command in the stack can redo all previous associated commands. 
     * 
     * @param AssociableCommand associable
     * @return true if the <code>command</code> is associated to this command, else false.
     */
    public boolean isAssociated(AssociableCommand command);
    
    /**
     * This method is used by AssociableCommandListener to tell this command that the next 
     * undo/redo action has to be skip when an associated command was found.
     */
    public void skipNextStackAction();
    
}