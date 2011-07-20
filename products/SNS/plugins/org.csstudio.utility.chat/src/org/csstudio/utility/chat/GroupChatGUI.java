/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import java.net.InetAddress;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/** GUI for a {@link GroupChat}
 *  @author Kay Kasemir
 */
public class GroupChatGUI
{
	final private GroupChatGUIListener listener;

	final private Display display;
	
	final private StyleRange error_style = new StyleRange();
	final private StyleRange from_style = new StyleRange();
	final private StyleRange self_style = new StyleRange();

	private TableViewer group_members;
	private Text name, send;
	private StyledText messages;

	/** Initialize
	 *  @param parent
	 *  @param listener
	 */
	public GroupChatGUI(final Composite parent,
			final GroupChatGUIListener listener)
    {
		this.listener = listener;
		display = parent.getDisplay();
        error_style.background = display.getSystemColor(SWT.COLOR_RED);
        from_style.foreground = display.getSystemColor(SWT.COLOR_DARK_GRAY);
        self_style.foreground = display.getSystemColor(SWT.COLOR_BLUE);
		
        createComponents(parent);
        connectActions();
    }

	/** Set initial focus */
	public void setFocus()
	{
		name.setFocus();
	}
	
	/** Create GUI elements
	 *  @param parent
	 */
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

	/** Create panel that displays chat group members
	 *  @param parent
	 */
	private void createPeoplePanel(final Composite parent)
	{
		parent.setLayout(new GridLayout());
		final Label label = new Label(parent, 0);
		label.setText(Messages.Participants);
		label.setLayoutData(new GridData());
		
		// To use TableColumnLayout, the table must
		// be the only child in a container
		final Composite box = new Composite(parent, 0);
		box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final TableColumnLayout table_layout = new TableColumnLayout();
		box.setLayout(table_layout);
		
		group_members = new TableViewer(box, SWT.V_SCROLL);
		group_members.setLabelProvider(new GroupMemberLabelProvider());
		
		final TableViewerColumn view_col = new TableViewerColumn(group_members, 0);
		table_layout.setColumnData(view_col.getColumn(), new ColumnWeightData(100));
		view_col.setLabelProvider(new GroupMemberLabelProvider());
		group_members.setContentProvider(new ArrayContentProvider());

	}
	
	/** Create panel that displays chat messages
	 *  @param parent
	 */
	private void createChatPanel(final Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		
	    // Name: ..
        Label l = new Label(parent, 0);
        l.setText(Messages.UserName);
        l.setLayoutData(new GridData());
        
        name = new Text(parent, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        messages = new StyledText(parent, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        messages.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        
        // Send: ____send__
        l = new Label(parent, 0);
        l.setText(Messages.Send);

        send = new Text(parent, SWT.BORDER);
        send.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Initialize name with user @ host
		String user = System.getProperty("user.name"); //$NON-NLS-1$
		try
		{
			final String host = InetAddress.getLocalHost().getCanonicalHostName();
			user = user + "_" + host; //$NON-NLS-1$
		}
		catch (Exception ex)
		{
			// Ignore
		}
		name.setText(user);
	}

	/** Connect actions to the GUI items */
	private void connectActions()
    {
		name.addSelectionListener(new SelectionAdapter()
		{
			@Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
		    	messages.setText(""); //$NON-NLS-1$
		    	name.setEnabled(false);
				listener.doStartLogin(name.getText().trim());
            }
		});
		
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

	/** Update log in name */
	public void updateLogin(final String user_name)
	{
    	name.setText(user_name);
    	name.setEnabled(true);
    	send.setFocus();
	}
	
	/** Display chat group members
	 *  @param nerds People in the chat group
	 */
    public void showGroupMembers(final String[] nerds)
    {
		if (group_members.getControl().isDisposed())
			return;
		group_members.setInput(nerds);
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

		// Style the 'from' section
		final StyleRange style = is_self ? self_style : from_style;
		final int orig_length = messages.getText().length();
		style.start = orig_length;
		style.length = from.length() + 2;
		messages.append(from + ": "); //$NON-NLS-1$
		messages.setStyleRange(style);
		
		messages.append(text + "\n"); //$NON-NLS-1$
		
		// Scroll to location of the newly added text
		messages.setSelection(orig_length);
    }
    
    /** Display error
     *  @param error Error text
     */
    public void showError(final String error)
    {
    	messages.setStyleRange(null);
    	messages.setText(error);
    	error_style.start = 0;
    	error_style.length = error.length();
    	messages.setStyleRange(error_style);
    }
}
