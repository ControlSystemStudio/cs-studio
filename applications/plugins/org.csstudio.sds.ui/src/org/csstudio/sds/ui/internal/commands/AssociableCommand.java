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
    
    public boolean isAssociated(AssociableCommand associable);
    
    public void skipNextStackAction();
    
}