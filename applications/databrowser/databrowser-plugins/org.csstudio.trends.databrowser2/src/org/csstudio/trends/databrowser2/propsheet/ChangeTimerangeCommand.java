/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.swt.rtplot.undo.UndoableAction;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;

/** Undo-able command to change time axis
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChangeTimerangeCommand extends UndoableAction
{
    final private Model model;
    final private boolean old_scroll, new_scroll;
    final private String old_start, new_start, old_end, new_end;

    /** Register and perform the command
     *  @param model Model
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param scroll Scroll, using start/end to determine time span? Or absolute start/end time?
     *  @param start
     *  @param end
     */
    public ChangeTimerangeCommand(final Model model, final UndoableActionManager operationsManager,
            final boolean scroll, final String start, final String end)
    {
        super(Messages.TimeAxis);
        this.model = model;
        this.old_scroll = model.isScrollEnabled();
        this.old_start = model.getStartSpec();
        this.old_end = model.getEndSpec();
        this.new_scroll = scroll;
        this.new_start = start;
        this.new_end = end;
        operationsManager.add(this);
        run();
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        apply(new_scroll, new_start, new_end);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        apply(old_scroll, old_start, old_end);
    }

    /** Apply time configuration to model
     *  @param scroll
     *  @param start
     *  @param end
     */
    private void apply(final boolean scroll, final String start, final String end)
    {
        model.enableScrolling(scroll);
        try
        {
            model.setTimerange(start, end);
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "Cannot update time range to " + start + " .. " + end, ex);
        }
    }
}
