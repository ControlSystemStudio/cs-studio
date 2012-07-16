/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.imports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    /** Map of file types to importers */
    private static Map<String, SampleImporter> importers = null;

    /** Prevent instantiation */
    private SampleImporters()
    {
    }

    /** Locate all available importers */
    private static synchronized void init()
    {
        if (importers == null)
        {
            importers = new HashMap<String, SampleImporter>();

            // TODO Get importers from extension point
            final SampleImporter importer = new CSVSampleImporter();
            importers.put(importer.getType(), importer);
        }
    }

    /** Obtain sample importer
     *  @param type
     *  @return {@link SampleImporter} for that type or <code>null</code> if not known
     */
    public static SampleImporter getImporter(final String type)
    {
        init();
        return importers.get(type);
    }

    /** Create menu actions to invoke importers
     *  @param op_manager
     *  @param shell
     *  @param model
     *  @return
     */
    public static IAction[] createImportActions(final OperationsManager op_manager, final Shell shell, final Model model)
    {
        init();
        final List<IAction> actions = new ArrayList<IAction>();
        for (SampleImporter importer : importers.values())
        {
            actions.add(new SampleImportAction(op_manager, shell, model, importer.getType(), importer.getDescription()));
        }
        return actions.toArray(new IAction[actions.size()]);
    }

}
