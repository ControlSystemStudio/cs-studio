package org.csstudio.utility.chat;

import java.net.InetAddress;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class ChatGUI implements NerdbinListener
{
	final private Display display;
	
	private Nerdbin nerdbin;

	final private StyleRange error_style = new StyleRange();
	final private StyleRange from_style = new StyleRange();
	final private StyleRange self_style = new StyleRange();

	private List nerdlist;
	private Text name, send;
	private StyledText messages;

	public ChatGUI(final Composite parent)
    {
		display = parent.getDisplay();
        error_style.background = display.getSystemColor(SWT.COLOR_RED);
        from_style.foreground = display.getSystemColor(SWT.COLOR_DARK_GRAY);
        self_style.foreground = display.getSystemColor(SWT.COLOR_BLUE);
		
        createComponents(parent);
        connectActions();
        
        parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				if (nerdbin != null)
				{
					nerdbin.disconnect();
					nerdbin = null;
				}
			}
		});
    }

	public void setFocus()
	{
		String user = System.getProperty("user.name");
		try
		{
			final String host = InetAddress.getLocalHost().getCanonicalHostName();
			user = user + "_" + host;
		}
		catch (Exception ex)
		{
			// Ignore
		}
		name.setText(user);
		name.setFocus();
	}
	
	private void createComponents(final Composite parent)
    {
		parent.setLayout(new FillLayout());
        
        final SashForm form = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
    	form.setLayout(new FillLayout());
    	
    	final Composite left = new Composite(form, SWT.BORDER);
    	createPeoplePanel(left);
    	
    	final Composite right = new Composite(form, SWT.BORDER);
    	createChatPanel(right);

    	form.setWeights(new int[] {20, 80});
    }

	private void createPeoplePanel(final Composite parent)
	{
		parent.setLayout(new GridLayout());
		final Label label = new Label(parent, 0);
		label.setText("Participants");
		label.setLayoutData(new GridData());
		
		nerdlist = new List(parent, SWT.V_SCROLL);
		nerdlist.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}
	
	private void createChatPanel(final Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		
	    // Name: ..
        Label l = new Label(parent, 0);
        l.setText("Name: ");
        l.setLayoutData(new GridData());
        
        name = new Text(parent, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        messages = new StyledText(parent, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        messages.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        
        // Send: ____send__
        l = new Label(parent, 0);
        l.setText("Send: ");

        send = new Text(parent, SWT.BORDER);
        send.setLayoutData(new GridData(SWT.FILL, 0, true, false));
    }

	private void connectActions()
    {
		name.addSelectionListener(new SelectionAdapter()
		{
			@Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
				performConnection();
            }
		});
		
		send.addSelectionListener(new SelectionAdapter()
		{
			@Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
				try
				{
					nerdbin.send(send.getText().trim());
					send.setText("");
				}
				catch (Exception ex)
				{
					MessageDialog.openError(send.getShell(),
							"Error",
							NLS.bind("Error sending to chat server:\n{0}",
									 ex.getMessage()));
				}
            }
		});
    }

	private void performConnection()
    {
    	messages.setText("");
    	name.setEnabled(false);
    	if (nerdbin != null)
    	{
    		nerdbin.disconnect();
    		nerdbin = null;
    	}
    	
    	// Connect in background thread
    	new Job("ChatConnect")
    	{
    		private String real_name;
			private Nerdbin new_nerdbin;
			private String error;

			{
	        	real_name = name.getText().trim();
	        	real_name.replace("@", "_");
			}
			
			@Override
            protected IStatus run(IProgressMonitor monitor)
            {
		        try
		        {
		        	new_nerdbin = new Nerdbin("localhost", "css@conference.localhost");
		        	new_nerdbin.addListener(ChatGUI.this);
		        	new_nerdbin.connect(real_name);
					error = null;
		        }
		        catch (Exception ex)
		        {
		        	error = NLS.bind("Error connecting to chat server:\n{0}\n",
							 ex.getMessage());
		        }
		        // Handle success or failure back in GUI thread
	        	display.asyncExec(new Runnable()
	        	{
					@Override
                    public void run()
                    {
						nerdbin = new_nerdbin;
						if (error != null)
						{
				        	messages.setText(error);
				        	error_style.start = 0;
				        	error_style.length = error.length();
				        	messages.setStyleRange(error_style);
						}
			        	name.setEnabled(true);
                    }
	        	});
		        
		        return null;
            }
    	}.schedule();
    }

	@Override
    public void nerdAlert(final String[] nerds)
    {
		if (nerdlist.isDisposed())
			return;
		display.asyncExec(new Runnable()
		{
			@Override
            public void run()
            {
				if (nerdlist.isDisposed())
					return;
				nerdlist.setItems(nerds);
            }
		});
    }

	@Override
    public void receive(final String from, final boolean is_self, final String text)
    {
		if (messages.isDisposed())
			return;
		display.asyncExec(new Runnable()
		{
			@Override
            public void run()
            {
				if (messages.isDisposed())
					return;

				// Style the 'from' section
				final StyleRange style = is_self ? self_style : from_style;
				style.start = messages.getText().length();
				style.length = from.length() + 2;
				messages.append(from + ": ");
				messages.setStyleRange(style);
				
				messages.append(text + "\n");

            }
		});
    }
}
