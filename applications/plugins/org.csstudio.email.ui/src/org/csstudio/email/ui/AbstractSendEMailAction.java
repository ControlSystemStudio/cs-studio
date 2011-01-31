/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email.ui;

import org.csstudio.email.Preferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

/** Base class for Action that sends an email
 *  @author Kay Kasemir
 */
abstract public class AbstractSendEMailAction extends Action
{
    final protected Shell shell;
    final private String from, subject, body;
    
    public AbstractSendEMailAction(final Shell shell, final String from,
            final String subject,
            final String body)
    {
        super(Messages.SendEmail,
              Activator.getImageDescriptor("icons/email.gif")); //$NON-NLS-1$
        this.shell = shell;
        this.from = from;
        this.subject = subject;
        this.body = body;
    }

    @Override
    public void run()
    {
        final String image_filename = getImage();
        Dialog dlg = new EMailSenderDialog(shell, Preferences.getSMTP_Host(), from,
                Messages.DefaultDestination, subject, body, image_filename);
        dlg.open();
    }

    abstract protected String getImage();
}
