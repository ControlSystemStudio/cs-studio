/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import java.io.File;
import java.net.InetAddress;

import org.csstudio.apputil.ui.swt.Screenshot;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
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
	private Text name, password;

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
		final GridLayout layout = new GridLayout(4, false);
		parent.setLayout(layout);
		
	    // Name: __name__  Password: __pass__
        Label l = new Label(parent, 0);
        l.setText(Messages.UserName);
        l.setLayoutData(new GridData());
        name = new Text(parent, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        l = new Label(parent, 0);
        l.setText(Messages.Password);
        l.setLayoutData(new GridData());
        password = new Text(parent, SWT.BORDER | SWT.PASSWORD);
        password.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
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
		
		// Set default password
		password.setText("$" + name); //$NON-NLS-1$
		
		// Message Box
		final Composite message_box = new Composite(parent, 0);
		super.createChatPanel(message_box);
		message_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
	}

	/** Connect actions to the GUI items */
	@Override
    protected void connectActions()
    {
		super.connectActions();

		// Context menu for chat group members
        final MenuManager manager = new MenuManager();
        
        manager.add(new SendToPersonAction(this, Messages.StartIndividualChat,
        		"icons/person16.png", group_members)//$NON-NLS-1$
        {
        	@Override
        	protected void doSendToPerson(final Person person)
            {
        		if (listener != null)
        			listener.doContact(person);
            }
        });
        manager.add(new SendToPersonAction(this, Messages.SendFile,
        		"icons/send_file.png", group_members) //$NON-NLS-1$
        {
        	@Override
        	protected void doSendToPerson(final Person person)
            {
        		if (listener == null)
        			return;
        		final FileDialog dlg = new FileDialog(group_members.getControl().getShell(), SWT.OPEN);
        		dlg.setFilterExtensions(new String[] { "*.*" }); //$NON-NLS-1$
        		final String filename = dlg.open();
        		if (filename != null)
        			listener.doSendFile(person, new File(filename));
            }
        });
        manager.add(new SendToPersonAction(this, Messages.SendScreenshot,
        		"icons/send_image.png", group_members) //$NON-NLS-1$
        {
        	@Override
        	protected void doSendToPerson(final Person person)
            {
        		if (listener == null)
        			return;
        		
        		final Image image = Screenshot.getFullScreenshot();
            	final File screenshot_file;
                try
                {
                	screenshot_file = File.createTempFile("screenshot", ".png"); //$NON-NLS-1$ //$NON-NLS-2$
                	screenshot_file.deleteOnExit();

                	final ImageLoader loader = new ImageLoader();
                    loader.data = new ImageData[] { image.getImageData() };
                    image.dispose();
            	    // Save
            	    loader.save(screenshot_file.getPath(), SWT.IMAGE_PNG);
                }
                catch (Exception ex)
                {
                	MessageDialog.openError(group_members.getControl().getShell(),
                			Messages.Error, ex.getMessage());
                	return;
                }
    			listener.doSendFile(person, screenshot_file);
            }
        });
        final Menu menu = manager.createContextMenu(group_members.getTable());
        group_members.getTable().setMenu(menu);

        final FocusAdapter select_on_focus = new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				((Text)e.widget).selectAll();
			}
		};
		name.addFocusListener(select_on_focus);
		password.addFocusListener(select_on_focus);
        
        // Log in on <return> in name or password
		final SelectionAdapter login = new SelectionAdapter()
		{
			@Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
				clearMessages();
		    	name.setEnabled(false);
				listener.doStartLogin(name.getText().trim(), password.getText().trim());
            }
		};
		name.addSelectionListener(login);
		password.addSelectionListener(login);
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

    /** @param person {@link Person}
     *  @return <code>true</code> if person matches ourself
     */
	boolean isOurself(final Person person)
    {
		if (listener == null)
			return true;
	    return listener.isOurself(person);
    }
}
