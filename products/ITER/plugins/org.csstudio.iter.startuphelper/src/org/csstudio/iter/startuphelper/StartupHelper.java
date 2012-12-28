package org.csstudio.iter.startuphelper;


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
    private boolean force_prompt;
	private WorkspaceInfo workspace_info = null;
	private String username = null;
	private String password = ""; //$NON-NLS-1$
	private Boolean show_Workspace = false;
	private Boolean show_Login = false;		
	private String productName;
	
	/**Constructor
	 * @param shell
	 *            the parent shell.
	 * @param force_prompt Force a workspace prompt, no "use as default" option
	 * @param workspace_info WorkspaceInfo
	 * @param userName
	 *            the initial user name.
	 * @param password
	 * 			  the initial password
     * @param show_Login show login section?
     * @param show_Workspace show workspace section?
	 */
	public StartupHelper(final Shell shell,
			final boolean force_prompt, final WorkspaceInfo workspace_info,
			final String userName,
			final String password,
			final Boolean show_Login,
			final Boolean show_Workspace) {
		this.shell = shell;
		this.force_prompt = force_prompt;
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
	
	/**set show_Workspace
	 * @param show_Workspace the show_Workspace to set
	 */
	public void setShow_Workspace(Boolean show_Workspace) {
		this.show_Workspace = show_Workspace;
	}



	/**set show_Login
	 * @param show_Login the show_Login to set
	 */
	public void setShow_Login(Boolean show_Login) {
		this.show_Login = show_Login;
	}

	
	@SuppressWarnings("nls")
    public boolean openStartupDialog(){
		
		String title="", message="";
		if (show_Login)
		{
	        if(username == null)
	        {
	            username = WorkspaceIndependentStore.readLastLoginUser();
	            password = "";
	        }
			title += Messages.StartupHelper_Login;
			message += Messages.StartupHelper_LoginTip;
		}
		if (show_Workspace)
		{								
			if(!title.equals(""))
				title += Messages.StartupHelper_And;
			if(!message.equals(""))
				message += "\n";
			title += Messages.StartupHelper_SelectWorkspace;
			message += NLS.bind(
					Messages.StartupHelper_SelectWorkspaceTip,
					productName);			
		}
		
		StartupDialog startupDialog = new StartupDialog(shell, title, message, username, password,
				workspace_info, !force_prompt, show_Login, show_Workspace);
		if(!startupDialog.prompt())
			return false; // in case of cancel selected
		
		username = startupDialog.getUser();
		password = startupDialog.getPassword();
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