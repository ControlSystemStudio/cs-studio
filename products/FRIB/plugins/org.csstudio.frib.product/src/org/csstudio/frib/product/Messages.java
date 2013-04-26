package org.csstudio.frib.product;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.frib.product.messages"; //$NON-NLS-1$

	public static String Application_DefaultProject;
    public static String Menu_CSS_Alarm;
    public static String Menu_CSS_Configuration;
    public static String Menu_CSS_CSS;
    public static String Menu_CSS_Debug;
    public static String Menu_CSS_Diagnostics;
    public static String Menu_CSS_Display;
    public static String Menu_CSS_Editors;
    public static String Menu_CSS_Management;
    public static String Menu_CSS_Other;
    public static String Menu_CSS_Test;
    public static String Menu_CSS_Trends;
    public static String Menu_CSS_Utilities;
    public static String Menu_CSS_Weblinks;
    public static String Menu_File;
    public static String Menu_Help;
    public static String Menu_Help_CheatSheet;

	public static String Menu_New;
    public static String Menu_Perspectives;
    public static String Menu_Views;
    public static String Menu_Window;
    public static String Project_SharedFolderName; 
    public static String Project_ShareError;
    public static String Project_ShareErrorDetail;
    public static String Window_Title;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
