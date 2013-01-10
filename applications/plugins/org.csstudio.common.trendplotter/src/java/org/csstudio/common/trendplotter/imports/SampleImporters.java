/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.imports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;

/** API for tool that imports data
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SampleImporters
{
    /** Extension point for {@link SampleImporter}s */
    final private static String IMPORTERS_EXT_POINT = "org.csstudio.trends.databrowser2.SampleImporter";

    /** Map of file types to importers */
    private static Map<String, SampleImporterInfo> importers = null;

    /** Prevent instantiation */
    private SampleImporters()
    {
    }

    /** Locate all available importers
     *  @throws Exception on error locating extension points
     */
    private static synchronized void init() throws Exception
    {
        if (importers == null)
        {
            importers = new HashMap<String, SampleImporterInfo>();

            // Get importers from extension point
            final IConfigurationElement[] configs =
                Platform.getExtensionRegistry().getConfigurationElementsFor(IMPORTERS_EXT_POINT);
            for (IConfigurationElement config : configs)
            {
                final String type = config.getAttribute("type");
                final String description = config.getAttribute("description");
                final SampleImporter importer = (SampleImporter)config.createExecutableExtension("class");
                importers.put(type, new SampleImporterInfo(type, description, importer));
            }
        }
    }


    /** @return Array of supported types
     *  @throws Exception on error initializing available importers
     */
    public static String[] getTypes() throws Exception
    {
        init();
        final Set<String> keys = importers.keySet();
        return keys.toArray(new String[keys.size()]);
    }


    /** Obtain sample importer
     *  @param type
     *  @return {@link SampleImporterInfo} for that type or <code>null</code> if not known
     *  @throws Exception on error initializing available importers
     */
    public static SampleImporterInfo getImporter(final String type) throws Exception
    {
        init();
        return importers.get(type);
    }

    /** Create menu actions to invoke importers
     *  @param op_manager
     *  @param shell
     *  @param model
     *  @return {@link IAction}
     *  @throws Exception on error initializing available importers
     */
    public static IAction[] createImportActions(final OperationsManager op_manager,
            final Shell shell, final Model model) throws Exception
    {
        init();
        final List<IAction> actions = new ArrayList<IAction>();
        for (SampleImporterInfo importer : importers.values())
            actions.add(new SampleImportAction(op_manager, shell, model, importer.getType(), importer.getDescription()));
        return actions.toArray(new IAction[actions.size()]);
    }
}
