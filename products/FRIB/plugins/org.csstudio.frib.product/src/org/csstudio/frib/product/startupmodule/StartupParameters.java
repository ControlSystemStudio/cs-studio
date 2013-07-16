package org.csstudio.frib.product.startupmodule;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.frib.product.Messages;
import org.csstudio.frib.startuphelper.PasswordInput;
import org.csstudio.startup.module.LoginExtPoint;
import org.csstudio.startup.module.StartupParametersExtPoint;
import org.csstudio.startup.module.WorkspaceExtPoint;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

/** 
 * Implementation of {@link StartupParametersExtPoint} which provides the following parameters:
 * {@value #FORCE_WORKSPACE_PROMPT_PARAM}, {@link WorkspaceExtPoint#WORKSPACE}
 * {@link #SHARE_LINK_PARAM}, {@link LoginExtPoint#USERNAME}, and
 * {@link LoginExtPoint#PASSWORD}.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class StartupParameters implements StartupParametersExtPoint {

	 /** Command-line switch for help */
    private static final String HELP = "-help"; //$NON-NLS-1$
    
    /** Command-line switch to show login dialog */
    private static final String LOGIN_PROMPT = "-login"; //$NON-NLS-1$
    
    /** Command-line switch to force workspace dialog */
    private static final String WORKSPACE_PROMPT = "-workspace_prompt"; //$NON-NLS-1$

    /** Command-line switch to provide link behind <code>SHARE_NAME</code> */
    private static final String SHARE_LINK = "-share_link"; //$NON-NLS-1$
    
    /** Command-line switch to provide the default user in login dialog */
    private static final String USER = "-u"; //$NON-NLS-1$
   
    /** Command-line switch to provide the password of default user in login dialog */
    private static final String PASSWORD = "-p"; //$NON-NLS-1$

    /** Parameter tag which defines if login dialog should be displayed
     *  The value is stored in the returned map. */ 
    public static final String LOGIN_PROMPT_PARAM = "css.showLogin"; //$NON-NLS-1$
    
    /** Parameter tag which defines if prompt for workspace is forced. 
     * The value is stored in the returned map. */ 
    public static final String FORCE_WORKSPACE_PROMPT_PARAM = "css.forceWorkspacePrompt"; //$NON-NLS-1$
   
    /** Parameter tag defines the shared link. The value is stored in the returned map. */
    public static final String SHARE_LINK_PARAM = "css.shareLink"; //$NON-NLS-1$
    
	/** {@inheritDoc} */
	public Map<String, Object> readStartupParameters(Display display,
			IApplicationContext context) throws Exception {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		 // Check command-line arguments
		final String args[] =
            (String []) context.getArguments().get("application.args"); //$NON-NLS-1$
        
        boolean force_workspace_prompt = false;
        boolean login = false;
        URL default_workspace = null;
        String share_link = null;
        String username = null;
        String password = null;
        
        for (int i=0; i<args.length; ++i)
        {
            final String arg = args[i];
         
            if (arg.equalsIgnoreCase(HELP) ||
                arg.equalsIgnoreCase("-?")) //$NON-NLS-1$
            {
                showHelp();
                parameters.put(EXIT_CODE, IApplication.EXIT_OK);
                // Exit ASAP, see comment below.
                System.exit(0);
                return parameters;
            }
            else if (arg.equalsIgnoreCase(LOGIN_PROMPT))
            {
                login = true;
            }
            else if (arg.equalsIgnoreCase(WORKSPACE_PROMPT))
            {
                force_workspace_prompt = true;
                if ((i + 1) < args.length)
                {
                    final String next = args[i+1];
                    if (!next.startsWith("-")) //$NON-NLS-1$
                    {
                        default_workspace = new URL("file:" + next); //$NON-NLS-1$
                        ++i;
                    }
                }
            }
            else if (arg.equalsIgnoreCase(SHARE_LINK))
            {
                if ((i + 1) < args.length)
                {
                    final String next = args[i+1];
                    if (!next.startsWith("-")) //$NON-NLS-1$
                    {
                        share_link = next;
                        ++i;
                    }
                }
                if (share_link == null)
                {
                    System.out.println("Error: Missing name of shared folder"); //$NON-NLS-1$
                    showHelp();
                    parameters.put(EXIT_CODE, IApplication.EXIT_OK);
                    // Exit ASAP, see comment below.
                    System.exit(0);
                    return parameters;
                }
            }
            else if (arg.equalsIgnoreCase(USER))
            {
            	if ((i + 1) < args.length)
                {
                    final String next = args[i+1];
                    if (!next.startsWith("-")) //$NON-NLS-1$
                    {
                        username = next;
                        ++i;
                    }
                }
                if (username == null)
                {
                    System.out.println("Error: Missing username"); //$NON-NLS-1$
                    showHelp();
                    // Exit ASAP, see comment below.
                    parameters.put(EXIT_CODE, IApplication.EXIT_OK);
                    System.exit(0);
                    return parameters;
                }
            }
            else if (arg.equalsIgnoreCase(PASSWORD))
            {
            	if ((i + 1) < args.length)
                {
                    final String next = args[i+1];
                    if (!next.startsWith("-")) //$NON-NLS-1$
                    {
                        password = next;
                        ++i;
                    }
                }
                if (password == null)
                {      
                    password = PasswordInput.readPassword("Enter password: ");  //$NON-NLS-1$          
                }
            }
        }

        parameters.put(LOGIN_PROMPT_PARAM, login);
        parameters.put(LoginExtPoint.USERNAME, username);
        parameters.put(LoginExtPoint.PASSWORD, password);
        parameters.put(FORCE_WORKSPACE_PROMPT_PARAM, force_workspace_prompt);
        parameters.put(WorkspaceExtPoint.WORKSPACE, default_workspace);
        parameters.put(SHARE_LINK_PARAM, share_link);
        
        return parameters;
        
	}
	
    /** 
     * Prints the help to system output.
     */
    @SuppressWarnings("nls")
    private void showHelp()
    {
        System.out.println("Command-line options:");
        System.out.format("  %-35s : This help\n", HELP);
        System.out.format("  %-35s : Always present workspace dialog, with preconfigured default\n",
                WORKSPACE_PROMPT);
        System.out.format("  %-35s : Present workspace dialog with given default\n",
                WORKSPACE_PROMPT + " /some/workspace");
        System.out.format("  %-35s : Log all messages to the console\n",
                "-consoleLog");
        System.out.format("  %-35s : Select workspace on command-line, no prompt\n",
                "-data /some/workspace");
        System.out.format("  %-35s : Create '%s' link to shared folder\n",
                SHARE_LINK + " /path/to/some/folder",
                Messages.Project_SharedFolderName);
        System.out.format("  %-35s : Present login dialog (user, password)\n",
                LOGIN_PROMPT);
        System.out.format("  %-35s : provide the default user in login dialog\n",
                USER + " username");
        System.out.format("  %-35s : provide the password of default user in login dialog\n",
                PASSWORD + " username");
    }
}
