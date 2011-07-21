/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import java.net.InetAddress;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

/** GUI for a {@link GroupChat}
 *  @author Kay Kasemir
 */
public class GroupChatGUI extends IndividualChatGUI
{
	final private GroupChatGUIListener listener;
	private TableViewer group_members;
	private Text name;

	/** Initialize
	 *  @param parent
	 *  @param listener
	 */
	public GroupChatGUI(final Composite parent,
			final GroupChatGUIListener listener)
    {
		super(parent, listener);
		this.listener = listener;
    }

	/** Set initial focus */
	@Override
    public void setFocus()
	{
		name.setFocus();
	}
	
	/** Create GUI elements
	 *  @param parent
	 */
	@Override
    protected void createComponents(final Composite parent)
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
		
        ColumnViewerToolTipSupport.enableFor(group_members);
	}
	
	/** Create panel that displays chat messages
	 *  @param parent
	 */
	@Override
	protected void createChatPanel(final Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		
	    // Name: __name__
        Label l = new Label(parent, 0);
        l.setText(Messages.UserName);
        l.setLayoutData(new GridData());
        
        name = new Text(parent, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
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
		
		// Message Box
		final Composite message_box = new Composite(parent, 0);
		super.createChatPanel(message_box);
		message_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	}

	/** Connect actions to the GUI items */
	@Override
    protected void connectActions()
    {
		super.connectActions();

		// Context menu for chat group members
        final MenuManager manager = new MenuManager();
        manager.add(new Action("Contact", Activator.getImage("icons/person16.png"))
        {
        	@Override
            public void run()
            {
        		final IStructuredSelection selection = (IStructuredSelection)group_members.getSelection();
        		final Person person = (Person)selection.getFirstElement();
        		if (listener != null)
        			listener.doContact(person);
            }
        });
        final Menu menu = manager.createContextMenu(group_members.getTable());
        group_members.getTable().setMenu(menu);

		name.addSelectionListener(new SelectionAdapter()
		{
			@Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
				clearMessages();
		    	name.setEnabled(false);
				listener.doStartLogin(name.getText().trim());
            }
		});
    }

	/** Update log in name */
	public void updateLogin(final String user_name)
	{
    	name.setText(user_name);
    	name.setEnabled(true);
    	super.setFocus();
	}
	
	/** Display chat group members
	 *  @param nerds People in the chat group
	 */
    public void showGroupMembers(final Person[] nerds)
    {
		if (group_members.getControl().isDisposed())
			return;
		group_members.setInput(nerds);
    }
}
