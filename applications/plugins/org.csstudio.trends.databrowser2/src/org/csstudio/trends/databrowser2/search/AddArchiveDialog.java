/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.search;

import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/** Dialog where user can select one or more archive data sources
 *  @author Kay Kasemir
 */
public class AddArchiveDialog extends TitleAreaDialog
{
    private ArchiveListGUI archive_gui;
    private ArchiveDataSource archives[] = null;

    /** Initialize
     *  @param shell Parent shell
     */
    public AddArchiveDialog(final Shell shell)
    {
        super(shell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        setHelpAvailable(false);
    }

    /** {@inheritDoc} */
    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        // The 'URL' combo can be very wide, which then causes
        // the whole dialog to be unreasonably big.
        // Simple hack: Just set a fixed size
        shell.setSize(600, 800);
        shell.setText(Messages.AddArchive);
    }

    /** {@inheritDoc} */
    @Override
    protected Control createDialogArea(final Composite parent_widget)
    {
        final Composite parent_composite = (Composite) super.createDialogArea(parent_widget);

        // Title & Image
        setTitle(Messages.AddArchive);
        setMessage(Messages.AddArchiveMsg);
        setTitleImage(Activator.getDefault().getImage("icons/config_archive.png")); //$NON-NLS-1$

        // Create box for widgets we're about to add
        final Composite box = new Composite(parent_composite, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // URL and list-of-archives handled by ArchiveListGUI
        archive_gui = new ArchiveListGUI(box)
        {
            @Override
            protected void handleArchiveUpdate()
            {
                // NOP
            }

            @Override
            protected void handleServerError(final String url, final Exception ex)
            {
                getShell().getDisplay().asyncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        MessageDialog.openError(getShell(),
                                Messages.Error,
                                NLS.bind(Messages.ArchiveServerErrorFmt, url, ex.getMessage()));
                    }
                });
         }
        };

        archive_gui.addSelectionListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                final boolean anything = ! event.getSelection().isEmpty();
                getButton(IDialogConstants.OK_ID).setEnabled(anything);
            }
        });

        return parent_composite;
    }

    /** {@inheritDoc} */
    @Override
    protected void createButtonsForButtonBar(final Composite parent)
    {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    protected void okPressed()
    {
        archives = archive_gui.getSelectedArchives();
        super.okPressed();
    }

    /** @return User-selected archives. Only valid when dialog was closed via 'OK' */
    public ArchiveDataSource[] getArchives()
    {
        return archives;
    }
}
