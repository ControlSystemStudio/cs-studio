/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.jface.action.Action;

/** Action to add new value axis to model
 *  @author Kay Kasemir
 */
public class AddAxisAction extends Action
{
    final private OperationsManager operations_manager;
    final private Model model;

    public AddAxisAction(final OperationsManager operations_manager,
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
        new AddAxisCommand(operations_manager, model);
    }
}
