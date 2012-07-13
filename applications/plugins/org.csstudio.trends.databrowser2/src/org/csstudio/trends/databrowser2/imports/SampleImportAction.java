/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.imports;

import java.io.InputStream;
import java.util.List;

import org.csstudio.data.values.IValue;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.ui.AddModelItemCommand;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that performs a sample import
 *  @author Kay Kasemir
 */
public class SampleImportAction extends Action
{
    final private OperationsManager op_manager;
    final private Shell shell;
    final private String type;
    final private Model model;
    final private SampleImporter importer;

    public SampleImportAction(final OperationsManager op_manager, final Shell shell, final String type, final Model model, final SampleImporter importer)
    {
        super(NLS.bind("Import {0}", type),
            Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/import.gif"));
        this.op_manager = op_manager;
        this.shell = shell;
        this.type = type;
        this.model = model;
        this.importer = importer;
    }

    @Override
    public void run()
    {
        // Prompt for file
        final ResourceSelectionDialog res =
                new ResourceSelectionDialog(shell,
                        NLS.bind("Select {0}", type),
                        new String[] { "*" });
        if (res.open() != Window.OK)
            return;
        final IPath path = res.getSelectedResource();
        if (path == null)
            return;
        try
        {
            // Locate file
            final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            final InputStream input = file.getContents();

            // Import data
            final List<IValue> values = importer.importValues(input);
            final String name = "Imported";

            // Add PV Item with data to model
            final AddModelItemCommand command = AddModelItemCommand.forPV(shell, op_manager, model,
                    name, 0, model.getEmptyAxis(), null);
            final PVItem pv = (PVItem) command.getItem();
            pv.mergeArchivedSamples(type, values);

            // TODO Zoom model to show time range of imported data?
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(shell, "Import failed", ex);
        }
    }
}
