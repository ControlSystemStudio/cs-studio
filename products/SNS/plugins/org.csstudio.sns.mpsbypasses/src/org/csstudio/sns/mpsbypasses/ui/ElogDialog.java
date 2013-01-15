/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.mpsbypasses.ui;

import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for submitting logbook entry
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public abstract class ElogDialog extends Dialog
{
    private Text txt_user;
    private Text txt_password;
    private Combo cmb_logbook;
    private Text txt_title;
    private Text txt_body;

    /** Initialize
     *  @param shell
     *  @param logbook
     *  @param title
     *  @param body
     */
    public ElogDialog(final Shell shell)
    {
        super(shell);
    }
    
    /** Set title */
    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText("Submit Bypass Info to Logbook");
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
        l.setText("User:");
        l.setLayoutData(new GridData());

        txt_user = new Text(composite, SWT.BORDER);
        txt_user.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        l = new Label(composite, 0);
        l.setText("Password:");
        l.setLayoutData(new GridData());

        txt_password = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        txt_password.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        l = new Label(composite, 0);
        l.setText("Logbook:");
        l.setLayoutData(new GridData());
        
        cmb_logbook = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
        cmb_logbook.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        l = new Label(composite, 0);
        l.setText("Title:");
        l.setLayoutData(new GridData());

        txt_title = new Text(composite, SWT.BORDER);
        txt_title.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        txt_body = new Text(composite, SWT.BORDER | SWT.MULTI);
        final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gd.minimumWidth = 500;
        gd.minimumHeight = 300;
        txt_body.setLayoutData(gd);
        
        txt_title.setText("Bypass Info");
        
        try
        {
            LogbookClient client = LogbookClientManager.getLogbookClientFactory().getClient();
            for (Logbook book : client.listLogbooks())
                cmb_logbook.add(book.getName());
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(getShell(), "Cannot list logbooks", ex);
        }
        
        txt_body.setText("See attachment for bypass info");
        
        return composite;
    }
    
    
}
