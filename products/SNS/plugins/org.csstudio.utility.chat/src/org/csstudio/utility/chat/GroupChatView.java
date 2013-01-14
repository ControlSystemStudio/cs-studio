/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;

/** RCP View for the group chat
 * 
 *  <p>The View is also the 'Controller'
 *  for a {@link GroupChat} model and {@link GroupChatGUI} display.
 *  
 *  @author Kay Kasemir
 */
public class GroupChatView extends org.eclipse.ui.part.ViewPart
	implements GroupChatListener, GroupChatGUIListener
{
	private Display display;
	private GroupChat chat_group = null;
	private GroupChatGUI gui;

	/** Queue of views opened when accepting an individual chat,
	 *  to be used when that chat actually starts. 
	 */
	final private Queue<IndividualChatView> pending_views
	  = new ConcurrentLinkedQueue<IndividualChatView>();
	
	/** Closing the 'group' chat disconnects us
	 *  from the server and thus invalidates the
	 *  individual chats.
	 *  Keeping track of opened indiv. chats allows
	 *  us to close them.
	 */
	final private List<IndividualChatView> individual_views
	  = new ArrayList<IndividualChatView>();
	
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
				for (IndividualChatView view : individual_views)
					view.close();
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
    public void doStartLogin(final String name, final String password)
	{
		if (chat_group != null)
		{
			chat_group.disconnect();
			chat_group = null;
		}
		// Perform connection in background thread
		new Job("Connect") //$NON-NLS-1$
		{
			private GroupChat new_chat;
			private String error = null;
			
			@Override
            protected IStatus run(IProgressMonitor monitor)
            {
				try
				{
					new_chat = new GroupChat(Preferences.getChatServer(),
							Preferences.getGroup());
    				new_chat.addListener(GroupChatView.this);
					new_chat.connect(name, password);
		        }
		        catch (Exception ex)
		        {
		        	if (new_chat != null)
		        		new_chat.disconnect();
		        	error = NLS.bind(Messages.ConnectionErrorFmt,
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
    public boolean isOurself(final Person person)
    {
		if (chat_group == null  ||  person == null)
			return false;
		return person.equals(chat_group.getUser());
    }
	
	/** {@inheritDoc} */
	@Override
    public void doContact(final Person person)
    {
		// Open new View
		final IndividualChatView view = createIndividualChatView();
		if (view == null)
			return;
		individual_views.add(view);

		// Run chat in there
		final IndividualChat chat = chat_group.createIndividualChat(person);
		view.setChat(person.getName(), chat);
    }
	
	/** {@inheritDoc} */
	@Override
    public void doSendFile(final Person person, final File file)
    {
		try
		{
			chat_group.sendFile(person, file);
		}
		catch (Exception ex)
		{
			MessageDialog.openError(gui.getShell(),	Messages.Error,
					NLS.bind(Messages.SendFileErrorFmt, ex.getMessage()));
		}
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
    			NLS.bind(Messages.SendErrorFmt,
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
    public void groupMemberUpdate(final Person[] nerds)
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

	/** Received invitation to individual chat
	 * 
	 *  <p>Prompt user if this is accepted,
	 *  then open a view for the separate chat
	 *  
	 *  {@inheritDoc}
	 */
	@Override
    public IndividualChatGUI receivedInvitation(final String from)
    {
		final AtomicReference<IndividualChatGUI> new_gui
			= new AtomicReference<IndividualChatGUI>(null);
		
		display.syncExec(new Runnable()
		{
			@Override
            public void run()
            {
				if (! MessageDialog.openQuestion(gui.getShell(),
						Messages.ChatInvitation,
						NLS.bind(Messages.AcceptInvitationFmt, from)))
					return;
				final IndividualChatView view = createIndividualChatView();
				if (view == null)
					return;
				individual_views.add(view);
				pending_views.add(view);
				new_gui.set(view.getGUI());
            }
		});
		
	    return new_gui.get();
    }

	/** {@inheritDoc} */
	@Override
    public File receivedFile(final String requestor, final String file_name)
    {
		final AtomicReference<File> name = new AtomicReference<File>(null);
		// Query in UI thread
		display.syncExec(new Runnable()
		{
			@Override
            public void run()
            {
				final FileDialog dlg = new FileDialog(gui.getShell(), SWT.SAVE);
				dlg.setText(NLS.bind(Messages.SaveReceivedFileFmt, requestor));
				dlg.setFileName(file_name);
				final String selected = dlg.open();
				if (selected != null)
					name.set(new File(selected));
            }
		});
		return name.get();
    }

	/** Counter for IndividualChatView instances
	 *  to allow multiple copies of the view
	 */
	final private static AtomicInteger view_ids = new AtomicInteger(0);
	
	/** Create view for individual chat.
	 *  <p>Will be called on the UI thread
	 *  @return newly created {@link IndividualChatView} or <code>null</code> on error
	 */
	protected IndividualChatView createIndividualChatView()
    {
		try
		{
			return (IndividualChatView)
				getSite().getPage().showView(IndividualChatView.ID,
						"View" + view_ids.incrementAndGet(), //$NON-NLS-1$
						IWorkbenchPage.VIEW_ACTIVATE);
		}
		catch (Exception ex)
		{
			MessageDialog.openError(gui.getShell(),
					Messages.Error,
					NLS.bind(Messages.OpenViewErrorFmt, ex.getMessage()));						
		}
		return null;
    }

	/** {@inheritDoc} */
	@Override
    public void startIndividualChat(final String from, final IndividualChat chat)
    {	// Recently opened view should be in the queue...
		final IndividualChatView view = pending_views.remove();
		view.setChat(from, chat);
    }
}
