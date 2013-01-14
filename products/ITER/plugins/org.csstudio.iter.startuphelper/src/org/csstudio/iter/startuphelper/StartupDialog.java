/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.iter.startuphelper;


import java.io.File;
import java.io.IOException;

import org.csstudio.platform.workspace.Messages;
import org.csstudio.platform.workspace.WorkspaceInfo;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The startup dialog could be used to merge all dialogs which should be shown during startup 
 * into one dialog, so to simplify the startup process. Now, it has workspace and login
 * dialog merged together.
 * 
 * @author Xihui Chen
 * @author Kay Kasemir
 */
public class StartupDialog extends TitleAreaDialog {

	/**
	 * Text box that holds the user name.
	 */
	private Text _usernameText;

	/**
	 * Text box that holds the password.
	 */
	private Text _passwordText;
	
	/**
	 * Checkbox for Anonymous login
	 */
	private Button _loginAnonymous;

	/**
	 * User name initially displayed in the dialog, then read from dialog.
	 */
    private String user_name = null;
    
	/**
	 * Password initially displayed in the dialog, then read from dialog.
	 */
    private String password = ""; //$NON-NLS-1$

	/**
	 * The dialog title.
	 */
	private final String _title;

	/**
	 * The message displayed in the dialog.
	 */
	private final String _message;
	
	  /** Workspace information */
    final private WorkspaceInfo info;
    
    /** Include the "show again" checkbox? */
    final private boolean with_show_again_option;

    /** show login section? */
    final private boolean showLogin;
    
    /** show workspace section*/
    final private boolean showWorkspace;
    
    /** Combo with selected and recent workspaces */
    private Combo workspaces;

	private Button show_dialog;

    /**
	 * Creates a new login dialog.
	 * 
	 * @param parentShell
	 *            the parent shell.
	 * @param title
	 *            the dialog title.
	 * @param message
	 *            the message that is displayed in the dialog.
	 * @param defaultUser
	 *            the initial user name.
	 * @param defaultPassword
	 * 			  the initial password
	 * @param info WorkspaceInfo
     * @param with_show_again_option Include the "show again" checkbox?
     * @param showLogin show login section?
     * @param showWorkspace show workspace section?
	 */
    public StartupDialog(final Shell parentShell, final String title,
    		final String message, final String defaultUser, final String defaultPassword,
    		final WorkspaceInfo info, final boolean with_show_again_option, 
    		final boolean showLogin, final boolean showWorkspace) {
    	super(parentShell);
		_title = title;
		_message = message;
		this.user_name = defaultUser;
		this.password = defaultPassword;
		this.info = info;
        this.with_show_again_option = with_show_again_option;
        this.showLogin = showLogin;
        this.showWorkspace = showWorkspace;
        // Allow resize
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	newShell.setText(_title);
    }
	
	/**
	 * Creates the contents of this dialog.
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		setTitle(_title);
		setMessage(_message);

		// Create the layout
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		contents.setLayout(layout);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		contents.setFont(parent.getFont());
		
		if(showLogin) {
			Group loginGroup = new Group(contents, SWT.SHADOW_ETCHED_IN);
			loginGroup.setText(Messages.LoginDialog_Login);
			createLoginSection(loginGroup);
		}
		
		if(showWorkspace) {
			Group workspaceGroup = new Group(contents, SWT.SHADOW_ETCHED_IN);
			workspaceGroup.setText(Messages.StartupDialog_SelectWorkspace);		
			createWorkspaceSection(workspaceGroup);
		}
        
		return parentComposite;
	}

    /** Add workspace selector and other GUI elements */
    private void createWorkspaceSection(final Composite group)
    {
        //  ____workspaces________ [Browse]
        // [x] ask again
        //final Composite composite = new Composite(parent_composite, 0);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        group.setLayoutData(gd);

        workspaces = new Combo(group, SWT.DROP_DOWN);
        workspaces.setToolTipText(Messages.Workspace_ComboTT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        workspaces.setLayoutData(gd);
        // Fill w/ current workspace history, select the first one
        for (int i=0; i<info.getWorkspaceCount(); ++i)
            workspaces.add(info.getWorkspace(i));
        workspaces.select(0);
        
        final Button browse = new Button(group, SWT.PUSH);
        browse.setText(Messages.Workspace_Browse);
        browse.setToolTipText(Messages.Workspace_BrowseTT);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                final DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setText(Messages.Workspace_BrowseDialogTitle);
                dialog.setMessage(Messages.Workspace_BrowseDialogMessage);
                dialog.setFilterPath(getInitialBrowsePath());
                final String dir = dialog.open();
                if (dir != null)
                    workspaces.setText(dir);
            }
        });
        
        // Pro choice, allow to _not_ show the dialog the next time around?
        if (with_show_again_option)
            createShowDialogButton(group);
        else // Always show
            info.setShowDialog(true);
    }
	
	private void createLoginSection(Composite group) {
	    GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        group.setLayoutData(gd);
		
		// user name
		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.LoginDialog_UserName);
		_usernameText = new Text(group, SWT.BORDER | SWT.FLAT);
		_usernameText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		// password
		label = new Label(group, SWT.NONE);
		label.setText(Messages.LoginDialog_Password);
		_passwordText = new Text(group, SWT.BORDER | SWT.FLAT | SWT.PASSWORD);
		_passwordText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		// Anonymous login?
		_loginAnonymous = new Button(group, SWT.CHECK);
		_loginAnonymous.setText(Messages.LoginDialog_LoginAnonymous);
		_loginAnonymous.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		_loginAnonymous.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(_loginAnonymous.getSelection()) {
					_usernameText.setEnabled(false);
					_passwordText.setEnabled(false);
				} else {
					_usernameText.setEnabled(true);
					_passwordText.setEnabled(true);
					// Allow entry of user name right away
		            _usernameText.setFocus();
				}
			}
		});

		// Init. user/password  with default
        if (user_name != null)
            _usernameText.setText(user_name);
        if (password != null)
            _passwordText.setText(password);

        // By default, the login section is actually not shown.
        // If it _is_ displayed, assume that user would start by entering a name
        _usernameText.setFocus();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void okPressed() {
		if(showLogin) {
			if(_loginAnonymous.getSelection()) { // Anonymous login
				user_name = null;
				password = ""; //$NON-NLS-1$
			}
			else {
				user_name = this._usernameText.getText();
				password = this._passwordText.getText();
			}
		}		
		if(showWorkspace) {
			if(!checkWorkspace())
				return;
			else if (with_show_again_option)
				info.setShowDialog(show_dialog.getSelection());
		}
		super.okPressed();
	}
	
	 /**
	 * @return User name entered into dialog
	 */
	public String getUser() {
		return user_name;
	}

	/**
	 * @return Password entered into dialog
	 */
	public String getPassword() {
		return password;
	}

	/** @return Directory name close to the currently entered workspace */
    private String getInitialBrowsePath()
    {
        File dir = new File(workspaces.getText());
        // Go one up
        if (dir != null)
            dir = dir.getParentFile();
        // Go further up until we find something that actually exists
        while ((dir != null) && !dir.exists())
            dir = dir.getParentFile();
        if (dir == null)
            return System.getProperty("user.dir"); //$NON-NLS-1$
        return dir.getAbsolutePath(); 
    }
    
    /** Add 'show dialog?' button */
    private void createShowDialogButton(Composite composite)
    {
        show_dialog = new Button(composite, SWT.CHECK);
        show_dialog.setText(Messages.Workspace_AskAgain);
        show_dialog.setToolTipText(Messages.Workspace_AskAgainTT);
        show_dialog.setSelection(true);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.LEFT;
        show_dialog.setLayoutData(gd);
    }
    
    /** Prompt for workspace.
     *  @return <code>false</code> when user selects to quit/cancel.
     */
    public boolean prompt()
    {
        if (open() == TitleAreaDialog.CANCEL)
            return false;
        return true;
    }
    
    /**
     * check if there is error in workspace input
     * @return true if there is no error
     */
    protected boolean checkWorkspace()
    {
        final String workspace = workspaces.getText().trim();
        
        // Must not be empty
        if (workspace.length() <= 0)
        {
            setErrorMessage(Messages.Workspace_EmptyError);
            return false;
        }
        
        // Check if this workspace is inside another workspace...
        final File ws_file = new File(workspace);
        try
        {
            File parent = ws_file.getParentFile();
            while (parent != null)
            {   // Is there a .metadata file?
                final File meta = new File(parent.getCanonicalPath()
                        + File.separator + ".metadata"); //$NON-NLS-1$
                if (meta.exists())
                {
                   setErrorMessage(NLS.bind(Messages.Workspace_NestedErrorFMT, parent.getName()));
                   return false;
                }
                // OK, go one up
                parent = parent.getParentFile();
            }
        }
        catch (IOException ex)
        {
            setErrorMessage(NLS.bind(Messages.Workspace_Error, ex.getMessage()));
            return false;
        }
        
        // Check if there are already workspaces within the selected directory.
        final String nested = checkForWorkspacesInSubdirs(ws_file);
        if (nested != null)
        {
            setErrorMessage(NLS.bind(Messages.Workspace_ContainsWorkspacesErrorFMT, nested));
            return false;
        }
        
        // Looks good so far, so report the selected workspace.
        info.setSelectedWorkspace(workspace);
        return true;
    }

    /** Check if directory or any subdirectory contains a workspace
     *  @param dir Directory where to start
     *  @return Name of workspace in subdir or <code>null</code> if none found
     * @throws Exception on error
     */
    private String checkForWorkspacesInSubdirs(final File dir)
    {
        final File subdirs[] = dir.listFiles();
        if (subdirs == null)
            return null;
        for (File subdir : subdirs)
        {
            if (! subdir.isDirectory())
                continue;
            try
            {   // Is there a .metadata file?
                final File meta = new File(subdir.getCanonicalPath()
                        + File.separator + ".metadata"); //$NON-NLS-1$
                if (meta.exists())
                    return subdir.getName();
            }
            catch (Exception ex)
            {
                // Ignore errors. If there's a workspace we can't read, don't worry.
            }
            // Could recurse further down, but that means when somebody tries
            // "/" as the workspace, it would search the whole hard drive!
            // So don't do that...
            //            final String nested = checkForWorkspacesInSubdirs(subdir);
            //            if (nested != null)
            //                return nested;
        }
        return null;
    }
}
