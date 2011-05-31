/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import java.util.Calendar;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;

/** Undo-able command to change time axis
 *  @author Kay Kasemir
 */
public class ChangeTimerangeCommand implements IUndoableCommand
{
    final private Model model;
    final private boolean old_scroll, new_scroll;
    final private ITimestamp old_start, new_start, old_end, new_end;

    /** Register and perform the command
     *  @param model Model
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param scroll Scroll, using start/end to determine time span? Or absolute start/end time?
     *  @param start
     *  @param end
     */
    public ChangeTimerangeCommand(final Model model, final OperationsManager operationsManager,
            final boolean scroll, final Calendar start, final Calendar end)
    {
        this.model = model;
        this.old_scroll = model.isScrollEnabled();
        this.old_start = model.getStartTime();
        this.old_end = model.getEndTime();
        this.new_scroll = scroll;
        this.new_start = TimestampFactory.fromCalendar(start);
        this.new_end = TimestampFactory.fromCalendar(end);
        operationsManager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
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
    private void apply(final boolean scroll, final ITimestamp start, final ITimestamp end)
    {
        if (scroll)
        {
            model.enableScrolling(true);
            final double time_span = end.toDouble() - start.toDouble();
            model.setTimespan(time_span);
        }
        else
        {
            model.enableScrolling(false);
            model.setTimerange(start, end);
        }
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.TimeAxis;
    }
}
