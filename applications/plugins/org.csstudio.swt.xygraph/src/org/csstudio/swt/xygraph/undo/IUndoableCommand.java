package org.csstudio.swt.xygraph.undo;

/**
 * @author Xihui Chen
 *
 */
public interface IUndoableCommand {
	
	/**
	 * Restore the state of the target to the state before this
	 * command has been executed.
	 */
	public void undo();
	
	/**
	 * Restore the state of the target to the state after this
	 * command has been executed.
	 */
	public void redo();
	
}
