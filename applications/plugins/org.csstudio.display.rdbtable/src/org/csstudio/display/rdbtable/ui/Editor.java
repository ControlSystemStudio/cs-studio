/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.ui;

import org.csstudio.auth.ui.dialogs.LoginDialog;
import org.csstudio.display.rdbtable.Messages;
import org.csstudio.display.rdbtable.model.RDBTableModel;
import org.csstudio.display.rdbtable.model.RDBTableModelListener;
import org.csstudio.display.rdbtable.model.RDBTableRow;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/** Eclipse 'editor' for the RDBTableModel
 *  @author Kay Kasemir
 */
public class Editor extends EditorPart
{
    private static final int MIN_SIZE = 100;

    /** RDB table data */
    private RDBTableModel model;

    /** Table Viewer for Model */
    private TableViewer table_viewer;

    /** Initialize Editor
     *  @see EditorPart#init(IEditorSite, IEditorInput)
     */
    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
        setSite(site);

        // Get file behind input
        final IFile file = (IFile) input.getAdapter(IFile.class);
        if (file != null)
            setInput(input);
        else
            throw new PartInitException("Cannot handle " + input.getName()); //$NON-NLS-1$
        // Create model from file
        try
        {
            model = new RDBTableModel(file.getContents());
        }
        catch (Exception ex)
        {
            throw new PartInitException(NLS.bind("Error opening configuration file {0}", input.getName()), ex); //$NON-NLS-1$
        }
        // Read initial data from RDB
        try
        {
            // Does this configuration require a user/password prompt?
            if (model.needPassword())
            {
                final LoginDialog login = new LoginDialog(site.getShell(),
                        Messages.LoginTitle,
                        NLS.bind(Messages.LoginMsg,
                                 model.getTitle()),
                        model.getUser());
                if (login.open() == Window.CANCEL)
                    throw new Exception(Messages.LoginCancelled);
                // Read with user/pw from dialog
                model.read(login.getLoginCredentials().getUsername(),
                           login.getLoginCredentials().getPassword());
            }
            else // Read with user/pw that's in the configuration file
                model.read();
        }
        catch (Exception ex)
        {
            throw new PartInitException("Error reading from database", ex); //$NON-NLS-1$
        }
        // Set window title and message
        setPartName(file.getName());
        setContentDescription(model.getTitle());
    }

    /** Create GUI parts, connect listeners */
    @Override
    public void createPartControl(final Composite parent)
    {
        createGUI(parent);
        createContextMenu();

        // Whenever the model changes, we update the GUI
        model.addListener(new RDBTableModelListener()
        {
            @Override
            public void rowChanged(final RDBTableRow row)
            {
                // Update the affected row in the table viewer
                table_viewer.update(row, null);
                // Update the editor window's "dirty" indicator
                firePropertyChange(PROP_DIRTY);
            }

            @Override
            public void newRow(final RDBTableRow new_row)
            {
                // Inform table viewer about added row
                table_viewer.add(new_row);
                // Update the editor window's "dirty" indicator
                firePropertyChange(PROP_DIRTY);
            }
        });
    }

    /** Create GUI elements (TableViewer hooked to model)
     *  @param parent Parent widget
     */
    private void createGUI(final Composite parent)
    {
        // Note: TableColumnLayout requires table to be the only child widget
        final TableColumnLayout table_layout = new TableColumnLayout();
        parent.setLayout(table_layout);

        // Create TableViewer that displays Model in Table
        table_viewer = new TableViewer(parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL |
                SWT.FULL_SELECTION);

        // Some tweaks to the underlying table widget
        final Table table = table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        // Enable tool tips
        ColumnViewerToolTipSupport.enableFor(table_viewer, ToolTip.NO_RECREATE);

        // Connect TableViewer to the Model: Provide content from model...
        table_viewer.setContentProvider(new RDBTableModelContentProvider());

        // Create table columns
        for (int c=0;  c<model.getColumnCount();  ++c)
        {
            final TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
            TableColumn col = view_col.getColumn();
            col.setText(model.getHeader(c));
            col.setMoveable(true);
            col.setResizable(true);
            table_layout.setColumnData(col, new ColumnWeightData(model.getWidth(c), MIN_SIZE));
            // Tell column how to display the model elements
            view_col.setLabelProvider(new RDBTableRowLabelProvider());
            view_col.setEditingSupport(new RDBTableCellEditor(table_viewer, c));
        }
        // table viewer is set up to handle data of Model.
        // Connect to specific model
        table_viewer.setInput(model);
    }

    /** Add context menu to 'add', 'delete' rows */
    private void createContextMenu()
    {
        final MenuManager menu = new MenuManager();
        menu.setRemoveAllWhenShown(true);
        menu.addMenuListener(new IMenuListener()
        {
            @Override
            public void menuAboutToShow(final IMenuManager menu)
            {
                menu.add(new AddRowAction(model));
                menu.add(new DeleteRowAction(table_viewer));
                menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        final Table table = table_viewer.getTable();
        table.setMenu(menu.createContextMenu(table));
        // Allow others to extend the context menu? So far, not used.
        // getEditorSite().registerContextMenu(menu, table_viewer);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        table_viewer.getTable().setFocus();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDirty()
    {
        return model.wasModified();
    }

    /** Don't allow writing under new name */
    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    /** Saving means writing the RDB data out */
    @Override
    public void doSave(final IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.InfoWritingTable, 1);
        try
        {
            model.write();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(getSite().getShell(),
                Messages.ErrorTitle,
                NLS.bind(Messages.WriteError, ex.getMessage()));
        }
        // Force full update of table_viewer
        table_viewer.setInput(model);
        firePropertyChange(PROP_DIRTY);
        monitor.done();
    }

    /** Should never get called
     *  @see #isSaveAsAllowed()
     */
    @Override
    public void doSaveAs()
    {
        doSave(new NullProgressMonitor());
    }
}
