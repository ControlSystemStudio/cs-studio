/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.widgets.MultiSelectionCombo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for submitting logbook entry
 *  @author Kay Kasemir
 */
public abstract class ElogDialog extends Dialog
{
    private String logbook;
    private String title;
    private String body;
    private Text txt_user;
    private Text txt_password;
    private MultiSelectionCombo<String> cmb_logbook;
    private Text txt_title;
    private Text txt_body;

    /** Initialize
     *  @param shell
     *  @param logbook
     *  @param title
     *  @param body
     */
    public ElogDialog(final Shell shell, final String logbook, final String title, final String body)
    {
        super(shell);
        this.logbook = logbook;
        this.title = title;
        this.body = body;
    }
    
    /** Set title */
    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.SaveTitle);
    }

    /** Allow resize */
    @Override
    protected boolean isResizable()
    {
        return true;
    }
    
    /** Create content */
    protected Control createDialogArea(final Composite parent)
    {
        final Composite composite = (Composite) super.createDialogArea(parent);
        
        composite.setLayout(new GridLayout(2, false));
        
        Label l = new Label(composite, 0);
        l.setText(Messages.User);
        l.setLayoutData(new GridData());

        txt_user = new Text(composite, SWT.BORDER);
        txt_user.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        l = new Label(composite, 0);
        l.setText(Messages.Password);
        l.setLayoutData(new GridData());

        txt_password = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        txt_password.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        l = new Label(composite, 0);
        l.setText(Messages.Logbook);
        l.setLayoutData(new GridData());
        
        cmb_logbook = new MultiSelectionCombo<>(composite, 0);
        cmb_logbook.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        l = new Label(composite, 0);
        l.setText(Messages.EntryTitle);
        l.setLayoutData(new GridData());

        txt_title = new Text(composite, SWT.BORDER);
        txt_title.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        txt_body = new Text(composite, SWT.BORDER | SWT.MULTI);
        final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gd.minimumWidth = 500;
        gd.minimumHeight = 300;
        txt_body.setLayoutData(gd);
        
        txt_title.setText(title);
        
        try
        {
            final LogbookClient client = LogbookClientManager.getLogbookClientFactory().getClient();
            final List<String> names = new ArrayList<>();
            for (Logbook logbook : client.listLogbooks())
                names.add(logbook.getName());
            cmb_logbook.setItems(names);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(getShell(), Messages.SaveError, ex);
        }
        cmb_logbook.setSelection(logbook);
        
        txt_body.setText(body);
        
        return composite;
    }

    /** {@inheritDoc} */
    @Override
    protected void okPressed()
    {
        try
        {
            save(txt_user.getText(),
                 txt_password.getText(),
                 cmb_logbook.getSelection(),
                 txt_title.getText(),
                 txt_body.getText());
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(getShell(), Messages.SaveError, ex);
            return;
        }
        super.okPressed();
    }
    
    /** Invoked when user entered log entry detail and selected 'OK'
     *  @param user
     *  @param password
     *  @param logbook
     *  @param title
     *  @param body
     *  @throws Exception on error
     */
    abstract public void save(String user, String password, Collection<String> logbooks, String title, String body) throws Exception;
}
