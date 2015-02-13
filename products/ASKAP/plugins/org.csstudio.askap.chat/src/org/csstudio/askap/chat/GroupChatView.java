package org.csstudio.askap.chat;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.csstudio.security.SecurityListener;
import org.csstudio.security.SecuritySupport;
import org.csstudio.security.authorization.Authorizations;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;


public class GroupChatView extends ViewPart implements ChatListener, SecurityListener {
	
	private static Logger logger = Logger.getLogger(GroupChatView.class.getName());
	public static final String ID = "org.csstudio.askap.chat.group";
	
	private String userName = null;
	private Composite parent;
	private TableViewer groupMembers;
	private MessageTable messagesTable;
	private Text sendMessage;
	private ChatMessageHandler messageHandler;


	public GroupChatView() throws Exception {
		
		Subject subject = SecuritySupport.getSubject();
		if (subject==null) {
			userName = null;
		} else {
			userName = SecuritySupport.getSubjectName(subject);
		}

		
        // Update when security info changes
        SecuritySupport.addListener(this);		
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(new FillLayout());

		
		SashForm form = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
    	form.setLayout(new FillLayout());
    	
    	final Composite left = new Composite(form, SWT.BORDER);
    	createPeoplePanel(left);
    	
    	final Composite right = new Composite(form, SWT.BORDER);
    	createChatPanel(right);

    	form.setWeights(new int[] {20, 80});
    	
        parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e){
				try {
					if (messageHandler!=null)
						messageHandler.stopChat();
				} catch (Exception ex) {
					logger.log(Level.INFO, "could not stop chat", ex);
				}
			}
		});
		
		sendMessage.addSelectionListener(new SelectionAdapter()
		{
			@Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
				try
				{
					messageHandler.sendChatMessage(sendMessage.getText().trim());
					sendMessage.setText(""); //$NON-NLS-1$
				}
				catch (Exception ex)
				{
					MessageDialog.openError(sendMessage.getShell(),
							"Send Error",
							"Could not send chat message: " + ex.getMessage());
				}
            }
		});

		startChat();
	}
	@Override
	public void setFocus() {
		sendMessage.setFocus();
	}
	
	/** Create panel that displays chat group members
	 *  @param parent
	 */
	private void createPeoplePanel(final Composite parent)
	{
		parent.setLayout(new GridLayout());
		final Label label = new Label(parent, 0);
		
		label.setText("Participants");
		
		label.setLayoutData(new GridData());
		
		// To use TableColumnLayout, the table must
		// be the only child in a container
		final Composite box = new Composite(parent, 0);
		box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final TableColumnLayout table_layout = new TableColumnLayout();
		box.setLayout(table_layout);
		
		groupMembers = new TableViewer(box, SWT.V_SCROLL);
		groupMembers.setLabelProvider(new GroupMemberLabelProvider());
		
		final TableViewerColumn view_col = new TableViewerColumn(groupMembers, 0);
		table_layout.setColumnData(view_col.getColumn(), new ColumnWeightData(100));
		view_col.setLabelProvider(new GroupMemberLabelProvider());
		groupMembers.setContentProvider(new ArrayContentProvider());
		
        ColumnViewerToolTipSupport.enableFor(groupMembers);
	}
	
	/** Create panel that displays chat messagesTable
	 *  @param parent
	 */
	protected void createChatPanel(final Composite parent)
	{
		final GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);
				
		// Message Box
		final Composite message_box = new Composite(parent, 0);
		
		message_box.setLayout(new GridLayout(2, false));
		
        messagesTable = new MessageTable(message_box, 0);
        messagesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        
        // Send: ____send__
        final Label l = new Label(message_box, 0);
        l.setText("Send:");

        sendMessage = new Text(parent, SWT.BORDER);
        sendMessage.setLayoutData(new GridData(SWT.FILL, 0, true, false));

		message_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
	}

	@Override
	public void receive(final String from, final String text, final long timeStamp, final boolean isSelf) {
		if (parent==null || parent.isDisposed())
			return;

		parent.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if (messagesTable.isDisposed())
					return;

				messagesTable.addMessage(from, isSelf, text, timeStamp);
			}
		});
	}


	@Override
	public void addParticipant(final String name) {
		if (parent==null || parent.isDisposed())
			return;

		parent.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if (parent.isDisposed())
					return;

				// just in case the user exists already
				groupMembers.remove(name);
				
				groupMembers.add(name);
			}
		});
	}


	@Override
	public void removeParticiparnt(final String name) {
		if (parent==null || parent.isDisposed())
			return;

		parent.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if (parent.isDisposed())
					return;

				groupMembers.remove(name);
			}
		});
	}


	@Override
	public void changedSecurity(Subject subject, boolean is_current_user,
			Authorizations authorizations) {
		
		if (subject==null) {
			userName = null;
		} else {
			userName = SecuritySupport.getSubjectName(subject);
		}
		
		parent.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if (parent.isDisposed())
					return;
				messagesTable.clear();
				groupMembers.refresh();
			}
		});
		startChat();
	}

	private void startChat() {
		if (messageHandler!=null) {
			try {
				messageHandler.stopChat();
			} catch (Exception ex) {
				messageHandler = null;
				logger.log(Level.INFO, "could not stop chat", ex);
			}
		}
		
		if (userName==null || userName.trim().length()==0) {
			messageHandler = null;
			return;
		}
		
    	messageHandler = new JMSChatMessageHandler(userName, this);
    	
		try {
			messageHandler.startChat();
		} catch (Exception ex) {
			MessageDialog.openError(sendMessage.getShell(),
					"Open Error",
					"Could not connect to chat server: " + ex.getMessage());
		}
	}
	
}
