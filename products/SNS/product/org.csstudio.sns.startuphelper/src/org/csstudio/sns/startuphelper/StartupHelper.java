package org.csstudio.sns.startuphelper;


import org.csstudio.platform.workspace.WorkspaceIndependentStore;
import org.csstudio.platform.workspace.WorkspaceInfo;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/**Help to handle the startup dialog and input from it, for example user name and password.
 * 
 * @author Xihui Chen
 *
 */
public class StartupHelper {
	
	private Shell shell = null;
	private WorkspaceInfo workspace_info = null;
	private String username = null;
	private String password = null;
	private Boolean show_Workspace = false;
	private Boolean show_Login = false;	
	
	private String productName;
	
	/**Constructor
	 * @param shell
	 *            the parent shell.
	 * @param workspace_info WorkspaceInfo
	 * @param userName
	 *            the initial user name.
	 * @param password
	 * 			  the initial password
     * @param show_Login show login section?
     * @param show_Workspace show workspace section?
	 */
	public StartupHelper(final Shell shell,
			final WorkspaceInfo workspace_info,
			final String userName,
			final String password,
			final Boolean show_Login,
			final Boolean show_Workspace) {
		this.shell = shell;
		this.workspace_info = workspace_info;
		this.username = userName;
		this.password = password;
		this.show_Workspace = show_Workspace;
		this.show_Login = show_Login;		

	    final IProduct product = Platform.getProduct();
	    if (product != null)
	        productName = product.getName();
	    if (productName == null)
	        productName = "CSS"; //$NON-NLS-1$
	}	
	
	public boolean openStartupDialog(){
		if(username == null) {
			username = WorkspaceIndependentStore.readLastLoginUser();
			password = ""; //$NON-NLS-1$
		}
		
		String bullet = ""; //$NON-NLS-1$
		if(show_Login && show_Workspace)
			bullet = String.valueOf((char)7);
		
		String title="", message=""; //$NON-NLS-1$ //$NON-NLS-2$
		if(show_Login) {
			title += Messages.StartupHelper_Login;
			message += bullet + Messages.StartupHelper_LoginTip;
		}
		if(show_Workspace) {			
						
			if(!title.equals("")) //$NON-NLS-1$
				title += Messages.StartupHelper_And;
			if(!message.equals("")) //$NON-NLS-1$
				message += "\n"; //$NON-NLS-1$
			title += Messages.StartupHelper_SelectWorkspace;
			message += bullet + NLS.bind(
					Messages.StartupHelper_SelectWorkspaceTip,
					productName);			
		}
		
		StartupDialog startupDialog = new StartupDialog(shell, title, message, username, password,
				workspace_info, true, show_Login, show_Workspace);
		if(!startupDialog.prompt())
			return false; // in case of cancel selected
		
		if(show_Login) {
			this.username = startupDialog.getUser();
			this.password = startupDialog.getPassword();
		}
		return true;
		
	}
	
	/**
	 * @return the user name from startup dialog
	 */
	public String getUserName() {
		return username;
	}

	/**
	 * @return the password from startup dialog
	 */
	public String getPassword() {
		return password;
	}	
}