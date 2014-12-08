/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.undo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.csstudio.swt.rtplot.Activator;

/** Manager for {@link UndoableAction}s
 *
 *  @author Xihui Chen original org.csstudio.swt.xygraph.undo.OperationsManager
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class UndoableActionManager
{
	final private SizeLimitedStack<UndoableAction> undoStack = new SizeLimitedStack<UndoableAction>(30);
	final private SizeLimitedStack<UndoableAction> redoStack = new SizeLimitedStack<UndoableAction>(30);
	final private List<UndoRedoListener> listeners = new CopyOnWriteArrayList<>();

	/** @param listener Listener to add */
	public void addListener(final UndoRedoListener listener)
	{
        listeners.add(listener);
    }

    /** @param listener Listener to remove */
    public boolean removeListener(final UndoRedoListener listener)
    {
        return listeners.remove(listener);
    }

    /** @return <code>true</code> if there is an action to un-do */
    public boolean canUndo()
    {
        return ! undoStack.isEmpty();
    }

    /** @return <code>true</code> if there is an action to re-do */
    public boolean canRedo()
    {
        return ! redoStack.isEmpty();
    }

	/** @param action Action to perform and add to the un-do stack */
	public void execute(final UndoableAction action)
	{
	    try
	    {
	        action.run();
	    }
	    catch (Throwable ex)
	    {
	        Activator.getLogger().log(Level.WARNING, "Action failed: " + action, ex);
	        return;
	    }
	    add(action);
	}

    /** @param action Action that has already been performed, which can be un-done */
    public void add(final UndoableAction action)
    {
        undoStack.push(action);
        redoStack.clear();
        fireOperationsHistoryChanged();
    }

	/** Undo the last command */
	public void undoLast()
	{
	    if (undoStack.isEmpty())
	        return;
	    final UndoableAction action = undoStack.pop();
	    try
	    {
	        action.undo();
	    }
	    catch (Throwable ex)
        {
            Activator.getLogger().log(Level.WARNING, "Undo failed: " + action, ex);
            return;
        }
		redoStack.push(action);
		fireOperationsHistoryChanged();
	}

	/** Re-do the last command */
	public void redoLast()
	{
	    if (redoStack.isEmpty())
	        return;
        final UndoableAction action = redoStack.pop();
        action.run();
        undoStack.push(action);
        fireOperationsHistoryChanged();
	}

	private void fireOperationsHistoryChanged()
	{
	    final String to_undo = undoStack.isEmpty() ? null : undoStack.peek().toString();
        final String to_redo = redoStack.isEmpty() ? null : redoStack.peek().toString();
		for (UndoRedoListener listener : listeners)
			listener.operationsHistoryChanged(to_undo, to_redo);
	}
}
