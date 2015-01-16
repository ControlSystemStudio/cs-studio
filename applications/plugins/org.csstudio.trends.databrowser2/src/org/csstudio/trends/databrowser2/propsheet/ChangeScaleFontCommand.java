/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.rtplot.undo.UndoableAction;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.swt.graphics.FontData;

/** Undo-able command to change plot fonts
 *  @author Kay Kasemir
 */
public class ChangeScaleFontCommand extends UndoableAction
{
    final private Model model;
    final private FontData old_font, new_font;

    /** Register and perform the command
     *  @param model Model to configure
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param new_color New value
     */
    public ChangeScaleFontCommand(final Model model,
            final UndoableActionManager operations_manager,
            final FontData new_font)
    {
        super(Messages.ScaleFontLbl);
        this.model = model;
        this.old_font = model.getScaleFont();
        this.new_font = new_font;
        operations_manager.execute(this);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        model.setScaleFont(new_font);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.setScaleFont(old_font);
    }
}
