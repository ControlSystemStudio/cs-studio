/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** GUI for individual, non-group chat
 *  @author Kay Kasemir
 */
public class IndividualChatGUI
{
	final private IndividualChatGUIListener listener;

	final protected Display display;
	private MessageTable messages;
	private Text send;

	public IndividualChatGUI(final Composite parent,
			IndividualChatGUIListener listener)
    {
		this.listener = listener;
		display = parent.getDisplay();
        createComponents(parent);
        connectActions();
    }
	
	/** @return Shell associated with this GUI */
	public Shell getShell()
	{
		return send.getShell();
	}
	
	/** Set initial focus */
	public void setFocus()
	{
		send.setFocus();
	}

	/** Create GUI elements
	 *  @param parent
	 */
	protected void createComponents(final Composite parent)
    {
		createChatPanel(parent);
    }
	
	/** Create panel that displays chat messages
	 *  @param parent
	 */
	protected void createChatPanel(final Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		
        messages = new MessageTable(parent, 0);
        messages.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        
        // Send: ____send__
        final Label l = new Label(parent, 0);
        l.setText(Messages.Send);

        send = new Text(parent, SWT.BORDER);
        send.setLayoutData(new GridData(SWT.FILL, 0, true, false));
	}
	
	/** Connect actions to the GUI items */
	protected void connectActions()
    {
		send.addSelectionListener(new SelectionAdapter()
		{
			@Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
				try
				{
					listener.doSend(send.getText().trim());
					send.setText(""); //$NON-NLS-1$
				}
				catch (Exception ex)
				{
					MessageDialog.openError(send.getShell(),
							Messages.Error,
							NLS.bind(Messages.SendErrorFmt,
									 ex.getMessage()));
				}
            }
		});
    }

	/** Clear message display, removing all messages */
	public void clearMessages()
	{
		messages.clear();
	}
	
    /** Add a message to the display
     *  @param from
     *  @param is_self
     *  @param text
     */
    public void addMessage(final String from, final boolean is_self, final String text)
    {
		if (messages.isDisposed())
			return;

		messages.addMessage(from, is_self, text);
    }
    
    /** Display error
     *  @param error Error text
     */
    public void showError(final String error)
    {
    	messages.showError(error);
    }
}
