package org.csstudio.sns.product;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.sns.product.messages"; //$NON-NLS-1$

    public static String Application_RestartMessage;

    public static String Application_RestartTitle;

    public static String Application_WorkspaceInUseError;

    public static String Application_WorkspaceInUseInfo;

    public static String Menu_File;
    public static String Menu_Help;

    public static String Menu_New;
    public static String Menu_Perspectives;
    public static String Menu_Views;
    public static String Menu_Window;
    public static String Window_Title;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
