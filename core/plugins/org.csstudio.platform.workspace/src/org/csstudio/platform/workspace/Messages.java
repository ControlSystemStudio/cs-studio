package org.csstudio.platform.workspace;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.platform.workspace.messages"; //$NON-NLS-1$
    public static String LoginDialog_Anonymous;
	public static String LoginDialog_Login;
	public static String LoginDialog_LoginAnonymous;
	public static String LoginDialog_Password;
	public static String LoginDialog_UserName;

	public static String StartupDialog_SelectWorkspace;
	
	public static String Workspace_AskAgain;
    public static String Workspace_AskAgainTT;
    public static String Workspace_Browse;
    public static String Workspace_BrowseDialogMessage;
    public static String Workspace_BrowseDialogTitle;
    public static String Workspace_BrowseTT;
    public static String Workspace_ComboTT;
    public static String Workspace_DefaultProduct;
    public static String Workspace_DialogMessage;
    public static String Workspace_DialogTitle;
    public static String Workspace_DirectoryError;
    public static String Workspace_DirectoryErrorTitle;
    public static String Workspace_EmptyError;
    public static String Workspace_Error;
    public static String Workspace_GenericError;
    public static String Workspace_GenericErrorTitle;
    public static String Workspace_InUseError;
    public static String Workspace_InUseErrorTitle;
    public static String Workspace_LockError;
    public static String Workspace_LockErrorTitle;
    public static String Workspace_NestedErrorFMT;
    public static String Workspace_ContainsWorkspacesErrorFMT;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // prevent instantiation
    }
}
