/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.imports;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.trends.databrowser2.propsheet.AddAxisCommand;
import org.csstudio.trends.databrowser2.ui.AddModelItemCommand;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
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
    final private Model model;
    final private String type;
    final private String description;

    public SampleImportAction(final OperationsManager op_manager, final Shell shell, final Model model, final String type, final String description)
    {
        super(NLS.bind(Messages.ImportActionLabelFmt, description),
            Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/import.gif")); //$NON-NLS-1$
        this.op_manager = op_manager;
        this.shell = shell;
        this.model = model;
        this.type = type;
        this.description = description;
    }

    @Override
    public void run()
    {
        // Prompt for file
        final ResourceSelectionDialog res =
                new ResourceSelectionDialog(shell,
                        NLS.bind(Messages.ImportActionFileSelectorTitleFmt, description),
                        new String[] { "*" }); //$NON-NLS-1$
        if (res.open() != Window.OK)
            return;
        final IPath path = res.getSelectedResource();
        if (path == null)
            return;
        try
        {
            // Add to first empty axis, or create new axis
            AxisConfig axis = model.getEmptyAxis();
            if (axis == null)
                axis = new AddAxisCommand(op_manager, model).getAxis();

            // Add archivedatasource for "import:..." and let that load the file
            final String url = ImportArchiveReaderFactory.createURL(type, path.toString());
            final ArchiveDataSource imported = new ArchiveDataSource(url, 1, type);
            // Add PV Item with data to model
            AddModelItemCommand.forPV(shell, op_manager, model,
                    type, Preferences.getScanPeriod(), axis, imported);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
        }
    }
}
