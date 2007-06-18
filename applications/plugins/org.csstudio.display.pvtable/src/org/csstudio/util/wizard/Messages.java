package org.csstudio.util.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.util.wizard.messages"; //$NON-NLS-1$

    public static String CannotOpenEditor;
    public static String ContainerNotFound;
    public static String CreateNew___;
    public static String Creating___;
    public static String ___TypeFile;
    public static String Error;
    public static String OpeningFile___;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
