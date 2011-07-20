/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** RCP View for the group chat
 *  @author Kay Kasemir
 */
public class ViewPart extends org.eclipse.ui.part.ViewPart
	implements GroupChatListener, GroupChatGUIListener
{
	private Display display;
	private GroupChat chat_group = null;
	private GroupChatGUI gui;

	/** {@inheritDoc} */
	@Override
	public void createPartControl(final Composite parent)
	{
		display = parent.getDisplay();
		gui = new GroupChatGUI(parent, this);
        parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				if (chat_group != null)
				{
					chat_group.disconnect();
					chat_group = null;
				}
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public void setFocus()
	{
		gui.setFocus();
	}

	/** {@inheritDoc} */
	@Override
    public void doStartLogin(final String name)
	{
		if (chat_group != null)
		{
			chat_group.disconnect();
			chat_group = null;
		}
		// Perform connection in background thread
		new Job("Connect")
		{
			private GroupChat new_chat;
			private String error = null;
			
			@Override
            protected IStatus run(IProgressMonitor monitor)
            {
				try
				{
					new_chat = new GroupChat(
							"localhost", "css@conference.localhost");
    				new_chat.addListener(ViewPart.this);
					new_chat.connect(name);
		        }
		        catch (Exception ex)
		        {
		        	if (new_chat != null)
		        		new_chat.disconnect();
		        	error = NLS.bind("Error connecting to chat server:\n{0}\n",
		   					 ex.getMessage());
		        }

		        // Handle result in GUI thread
		        display.asyncExec(new Runnable()
		        {
		        	@Override
                    public void run()
		        	{
		        		if (new_chat != null)
		        		{
		        			chat_group = new_chat;
		        		}
		        		gui.updateLogin(name);
		        		if (error != null)
		        			gui.showError(error);
		        	}
		        });
		        
		        return Status.OK_STATUS;
            }
		}.schedule();
	}
	
	/** {@inheritDoc} */
    @Override
    public void doSend(final String message_text)
	{
		if (chat_group == null)
			return;
		try
		{
			chat_group.send(message_text);
		}
		catch (Exception ex)
		{
        	final String error = 
    			NLS.bind("Error sending to chat server:\n{0}\n",
   					 ex.getMessage());
        	gui.showError(error);
		}
	}
		
	/** {@inheritDoc} */
	@Override
    public void receive(final String from, final boolean is_self, final String text)
    {
		display.asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				gui.addMessage(from, is_self, text);
			}
		});
    }

	/** {@inheritDoc} */
	@Override
    public void groupMemberUpdate(final String[] nerds)
    {
		display.asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				gui.showGroupMembers(nerds);
			}
		});
    }

	/** {@inheritDoc} */
	@Override
    public IndividualChatGUI receivedInvitation(String from)
    {
	    // TODO Auto-generated method stub
		System.out.println("Received invitation from " + from);
	    return null;
    }
}
