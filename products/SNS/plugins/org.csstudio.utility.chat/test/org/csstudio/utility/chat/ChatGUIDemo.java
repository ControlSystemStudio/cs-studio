package org.csstudio.utility.chat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("nls")
public class ChatGUIDemo implements GroupChatListener, GroupChatGUIListener
{
	final private Display display = Display.getCurrent();
	private GroupChat chat_group = null;
	final private GroupChatGUI gui;
	
	public ChatGUIDemo(final Composite parent)
	{
		gui = new GroupChatGUI(parent, this);
		gui.setFocus();
        
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
					new_chat = new GroupChat("localhost", "css@conference.localhost");
    				new_chat.addListener(ChatGUIDemo.this);
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
		        
		        return null;
            }
		}.schedule();
	}
	
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

	@Override
    public IndividualChatGUI receivedInvitation(String from)
    {
	    // TODO Auto-generated method stub
		System.out.println("Received invitation from " + from);
	    return null;
    }

	public static void main(final String[] args) throws Exception
    {
	    final Display display = new Display();
	    final Shell parent = new Shell(display);
	    
	    new ChatGUIDemo(parent);
        
        parent.setSize(600, 400);
        parent.open();
        while (!parent.isDisposed())
        {
	        if (!display.readAndDispatch()) display.sleep();
        }
        
        display.dispose();
    }
}
