package org.csstudio.ui.menu.app;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.ui.menu.app.messages"; //$NON-NLS-1$

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
    public static String Workspace_EmptyError;
    public static String Workspace_Error;
    public static String Workspace_NestedErrorFMT;

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
