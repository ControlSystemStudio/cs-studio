package org.csstudio.util.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.util.wizard.messages"; //$NON-NLS-1$

    public static String ___TypeFile;

    public static String Browse;

    public static String CannotOpenEditor;

    public static String ContainerNotFound;

    public static String CreateNew___;

    public static String Creating___;

    public static String Error;

    public static String Filename;

    public static String Folder;

    public static String FolderNotFound;

    public static String InvalidFilename;

    public static String NeedXML;

    public static String NoEditorFound;

    public static String NoFilenameSelected;

    public static String NoFolderSelected;

    public static String Open;

    public static String OpeningFile___;

    public static String ReadonlyProject;

    public static String SelectFile;

    public static String SelectFolder;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
