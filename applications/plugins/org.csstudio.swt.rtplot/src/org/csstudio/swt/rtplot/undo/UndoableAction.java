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
public interface UndoableAction
{
    /** Perform the action.
     *
     *  <p>Will be called by the {@link UndoableActionManager} to first
     *  perform the action.
     *  Might be called again after an 'un-do' to re-perform the action.
     */
    public void perform(); // change into run()

    /** Called by the {@link UndoableActionManager} to un-do the action. */
	public void undo();

	// Should implement toString(),
	// which it used to display this action in the GUI
}
