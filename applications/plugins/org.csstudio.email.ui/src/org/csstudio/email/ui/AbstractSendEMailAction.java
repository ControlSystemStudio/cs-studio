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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Base class for Action that sends an email
 *  @author Kay Kasemir
 */
abstract public class AbstractSendEMailAction extends Action
{
    final protected Shell shell;
    final private String from, subject;
    private String body;
    
    /** Initialize with body, awaiting image
     *  @param shell
     *  @param from
     *  @param subject
     *  @param body
     */
    public AbstractSendEMailAction(final Shell shell, final String from,
            final String subject,
            final String body)
    {
        super(Messages.SendEmail,
              Activator.getImageDescriptor("icons/mail-send-16.png")); //$NON-NLS-1$
        this.shell = shell;
        this.from = from;
        this.subject = subject;
        this.body = body;
    }

    /** Initialize with body, awaiting body and optional image
     *  @param shell
     *  @param from
     *  @param subject
     */
    public AbstractSendEMailAction(final Shell shell, final String from,
            final String subject)
    {
        super(Messages.SendEmail,
              Activator.getImageDescriptor("icons/mail-send-16.png")); //$NON-NLS-1$
        this.shell = shell;
        this.from = from;
        this.subject = subject;
        this.body = null;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
    	if (body == null)
    		body = getBody();
    	if (body == null)
    		body = ""; //$NON-NLS-1$

    	// This action might be invoked from a context menu. In principle, RCP
    	// closes the context menu before invoking this action.
        // Tools that need to implement getImage() by taking a screenshot thus capture the original display,
    	// without the context menu.
    	// On Linux (X11, GTK), however, the context menu is still visible.
    	// Presumably, the X11 display update queue is not 'flushed'?
    	// By delaying the getImage() call into another Runnable, the context menu
    	// was successfully closed in tests on Linux.
    	final Display display = shell == null ? Display.getCurrent() : shell.getDisplay();
    	display.asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                final String image_filename = getImage();
                
                final Dialog dlg;
                if (image_filename == null)
                    dlg = new EMailSenderDialog(shell, Preferences.getSMTP_Host(), from,
                            Messages.DefaultDestination, subject, body);
                else
                    dlg = new EMailSenderDialog(shell, Preferences.getSMTP_Host(), from,
                            Messages.DefaultDestination, subject, body, image_filename);
                dlg.open();
            }
        });
    }

    /** To override by implementations that use the constructor
     *  without body parameter
     *  @return Text for body of email
     */
    public String getBody()
    {
	    return null;
    }

	/** @return Image attachment. May return <code>null</code> for 'no image' */
    abstract protected String getImage();
}
