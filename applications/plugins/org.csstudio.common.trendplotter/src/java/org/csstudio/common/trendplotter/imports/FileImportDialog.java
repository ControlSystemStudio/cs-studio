/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.imports;

import org.csstudio.common.trendplotter.Messages;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for importing a file, promping for the type
 *  @author Kay Kasemir
 */
public class FileImportDialog extends Dialog
{
    private static int count = 0;
    private String file_name;
    private String type;
    private String item_name;

    /** Initialize
     *  @param shell Parent shell
     *  @param file_name File name to import
     */
    public FileImportDialog(final Shell shell, final String file_name)
    {
        super(shell);
        this.file_name = file_name;
        item_name = Messages.ImportedChannelBaseName + (count ++);
    }

    public String getFileName()
    {
        return file_name;
    }

    public String getType()
    {
        return type;
    }


    public String getItemName()
    {
        return item_name;
    }



    /** Set title */
    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.ImportTitle);
    }

    /** Allow resize */
    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** Create content */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite composite = (Composite) super.createDialogArea(parent);
        final GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;

        // Message
        Label l = new Label(composite, 0);
        l.setText(Messages.ImportDialogMessage);
        l.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));

        // Filename __________
        final Text f_name = new Text(composite, SWT.BORDER);
        f_name.setText(file_name);
        f_name.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));
        f_name.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                file_name = f_name.getText().trim();
            }
        });

        // Type: __________
        l = new Label(composite, 0);
        l.setText(Messages.ImportTypeLbl);
        l.setLayoutData(new GridData());

        final Combo type_sel = new Combo(composite, SWT.READ_ONLY);
        type_sel.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        try
        {
            String[] types = SampleImporters.getTypes();
            type_sel.setItems(types);
            type_sel.select(0);
            type = types[0];
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(parent.getShell(), Messages.Error, ex);
        }
        type_sel.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                type = type_sel.getText();
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                type = type_sel.getText();
            }
        });
        // Channel Name: __________
        l = new Label(composite, 0);
        l.setText(Messages.NameLbl);
        l.setLayoutData(new GridData());

        final Text i_name = new Text(composite, SWT.BORDER);
        i_name.setText(item_name);
        i_name.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        i_name.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                item_name = i_name.getText().trim();
            }
        });

        return composite;
    }



}
