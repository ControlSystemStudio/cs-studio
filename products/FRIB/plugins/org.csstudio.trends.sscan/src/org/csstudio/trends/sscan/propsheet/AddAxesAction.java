/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.sscan.Activator;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.Model;
import org.eclipse.jface.action.Action;

/** Action to add new value axis to model
 *  @author Kay Kasemir
 */
public class AddAxesAction extends Action
{
    final private OperationsManager operations_manager;
    final private Model model;

    public AddAxesAction(final OperationsManager operations_manager,
            final Model model)
    {
        super(Messages.AddAxis,
                Activator.getDefault().getImageDescriptor("icons/add.gif")); //$NON-NLS-1$
        this.operations_manager = operations_manager;
        this.model = model;
    }

    @Override
    public void run()
    {
        new AddAxesCommand(operations_manager, model);
    }
}
