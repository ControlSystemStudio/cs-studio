package org.csstudio.alarm.treeview.model;

import javax.annotation.Nonnull;

/**
 * This listener may be attached to process variable nodes during their creation with the builder.
 * It allows for tracking the life cycle of the node in the tree (adding and removing).
 * 
 * @author jpenning
 * @since 09.11.2010
 */
public interface IProcessVariableNodeListener {

    /**
     * Called when the node is added to its parent
     * 
     * @param newName
     */
    void wasAdded(@Nonnull final String newName);
    
    /**
     * Called when the node is removed from the parent.
     * 
     * @param newName
     */
    void wasRemoved(@Nonnull final String newName);
}
