/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.imports;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;

/** API for tool that imports data
 *
 *  @author Kay Kasemir
 */
public class SampleImporters
{
    public static IAction[] createImportActions(final OperationsManager op_manager, final Shell shell, final Model model)
    {
        final List<IAction> actions = new ArrayList<IAction>();
        // TODO Get importers from extension point
        actions.add(new SampleImportAction(op_manager, shell, "CVS Data File", model, new CSVSampleImporter()));
        return actions.toArray(new IAction[actions.size()]);
    }
}
