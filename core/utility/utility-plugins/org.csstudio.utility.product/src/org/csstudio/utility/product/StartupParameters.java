/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.security.PasswordInput;
import org.csstudio.security.preferences.SecurePreferences;
import org.csstudio.startup.module.LoginExtPoint;
import org.csstudio.startup.module.StartupParametersExtPoint;
import org.csstudio.startup.module.WorkspaceExtPoint;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.swt.widgets.Display;

/** Implementation of {@link StartupParametersExtPoint}
 *
 *  <p>Parses command line, sets {@link Map} of startup parameters.
 *
 *  <p>Note on Eclipse plugin class loader:
 *  Assume you create a product o.c.mysite.product,
 *  and in there you want to specify org.csstudio.utility.product.StartupParameters
 *  as startupParameters for the org.csstudio.startup.module extension point.
 *
 *  By default, the Eclipse classloader will then attempt to load
 *  the class org.csstudio.utility.product.StartupParameters from
 *  your o.c.mysite.product bundle, because it is listed as the
 *  contributor to the org.csstudio.startup.module extension point.
 *
 *  You must specify the fully qualified startupParameter as
 *  org.csstudio.utility.product/org.csstudio.utility.product.StartupParameters.
 *  That way the Eclipse class loader will activate the bundle org.csstudio.utility.product
 *  and then load org.csstudio.utility.product.StartupParameters from it.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @author Kay Kasemir
 */
public class StartupParameters implements StartupParametersExtPoint
{
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
     *  The value is stored in the returned map. */
    public static final String FORCE_WORKSPACE_PROMPT_PARAM = "css.forceWorkspacePrompt"; //$NON-NLS-1$

    /** Parameter tag defines the shared link. The value is stored in the returned map. */
    public static final String SHARE_LINK_PARAM = "css.shareLink"; //$NON-NLS-1$

    /** Command-line switch to install workbench.xmi */
    private static final String WORKBENCH_XMI = "-workbench_xmi"; //$NON-NLS-1$

    /** Parameter tag for WORKBENCH_XMI. Map value contains path to workbench.xmi. */
    public static final String WORKBENCH_XMI_PARAM = "css.workbench.xmi"; //$NON-NLS-1$

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public Map<String, Object> readStartupParameters(final Display display,
            final IApplicationContext context) throws Exception
    {
        final Map<String, Object> parameters = new HashMap<String, Object>();
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
            else if (arg.equalsIgnoreCase(WORKBENCH_XMI))
            {
                String template = null;
                if ((i + 1) < args.length)
                {
                    final String next = args[i+1];
                    if (!next.startsWith("-")) //$NON-NLS-1$
                    {
                        template = next;
                        ++i;
                    }
                }
                if (template != null)
                    parameters.put(WORKBENCH_XMI_PARAM, template);
                else
                {
                    System.out.println("Error: Missing /path/to/workspace.xmi"); //$NON-NLS-1$
                    showHelp();
                    // Exit ASAP, see comment below.
                    parameters.put(EXIT_CODE, IApplication.EXIT_OK);
                    System.exit(0);
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
                    password = PasswordInput.readPassword("Enter password: ");
            }
            else if (arg.equalsIgnoreCase("-set_password"))
            {
                String preference_name = null;
                if ((i + 1) < args.length)
                {
                    preference_name = args[i+1];
                    if (preference_name.startsWith("-"))
                        preference_name = null;
                }
                if (preference_name == null)
                {
                    System.out.println("Missing preference name");
                    System.exit(0);
                }
                if (password == null)
                    password = PasswordInput.readPassword("Enter password: ");

                final String[] path_key = preference_name.split("/");
                if (path_key.length != 2)
                {
                    System.out.println("preference name must be plugin_id/key");
                    System.exit(0);
                }
                System.out.println("Setting plugin " + path_key[0] + " setting " + path_key[1]);

                final ISecurePreferences sec_prefs = SecurePreferences.getSecurePreferences();
                sec_prefs.node(path_key[0]).put(path_key[1], password, true);
                sec_prefs.flush();
                System.exit(0);
            }
            // The "comment below" that was mentioned several times above
            // .. has unfortunately been lost.
            // Could be related in not continuing further with the product startup,
            // because as soon as the product looks for preferences, it will use or
            // even create(!) a workspace. Calling the product with "-help" is not
            // supposed to create directories and files on the disk,
            // so best to call System.exit() and NOT allow RCP to continue.
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
        System.out.format("  %-40s : This help\n", HELP);
        System.out.format("  %-40s : Version info\n", "-version");
        System.out.format("  %-40s : Always present workspace dialog, with preconfigured default\n",
                WORKSPACE_PROMPT);
        System.out.format("  %-40s : Present workspace dialog with given default\n",
                WORKSPACE_PROMPT + " /some/workspace");
        System.out.format("  %-40s : Initialize workbench.xmi with a given template\n",
                WORKBENCH_XMI + " /path/to/workbench.xmi");
        System.out.format("  %-40s : Log all messages to the console\n",
                "-consoleLog");
        System.out.format("  %-40s : Select workspace on command-line, no prompt\n",
                "-data /some/workspace");
        System.out.format("  %-40s : Create links to shared folder\n",
                SHARE_LINK + " /path/to/folder=/CSS/Share");
        System.out.format("  %-40s : Present login dialog (user, password)\n",
                LOGIN_PROMPT);
        System.out.format("  %-40s : provide the default user in login dialog\n",
                USER + " username");
        System.out.format("  %-40s : provide the password of default user in login dialog\n",
                PASSWORD + " username");
        System.out.format("  %-40s : set a password preferences (will prompt for password or use previous -p {password} option)\n",
                "-set_password preference_name");
    }
}
