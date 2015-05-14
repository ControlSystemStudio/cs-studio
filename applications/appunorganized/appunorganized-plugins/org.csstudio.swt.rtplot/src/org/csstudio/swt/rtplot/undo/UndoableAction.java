/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.undo;

/** An action that can be un-done as well as re-done
 *
 *  @author Xihui Chen original org.csstudio.swt.xygraph.undo.IUndoableCommand
 *  @author Kay Kasemir
 */
abstract public class UndoableAction implements Runnable
{
    final private String name;

    /** @param name Name used to show action in undo/redo UI */
    public UndoableAction(final String name)
    {
        this.name = name;
    }

    /** Perform the action.
     *
     *  <p>Will be called by the {@link UndoableActionManager} to first
     *  perform the action.
     *  Might be called again after an 'un-do' to re-perform the action.
     */
    @Override
    abstract public void run();

    /** Called by the {@link UndoableActionManager} to un-do the action. */
    abstract public void undo();

    /** @return Name used to show action in undo/redo UI */
    @Override
    final public String toString()
    {
        return name;
    }
}
