package org.csstudio.util.editor;

import org.csstudio.platform.ui.dialogs.SaveAsDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Shell;

/** Helper for running 'SaveAs' dialog for new XML file.
 *  @author Kay Kasemir
 */
public class PromptForNewXMLFileDialog
{
    /** Prevent instantiation. */
    private PromptForNewXMLFileDialog()
    {}
    
    /** Run the dialog, and return the new IFile.
     * 
     * @param shell The shell to use
     * @param extension File extension (without the '.')
     * @param old_file The original file or <code>null</code>.
     * @return Returns the new <code>IFile</code> or <code>null</code>.
     */
    public static IFile run(Shell shell, String extension, IFile old_file)
    {
        // Query for new name.
        // The path to the new resource relative to the workspace
        // Use the OS-specific org.eclipse.swt.widgets.FileDialog fs;
        SaveAsDialog dlg = new SaveAsDialog(shell);
        dlg.setBlockOnOpen(true);
        if (old_file != null)
            dlg.setOriginalFile(old_file);
        dlg.open();
        // The path to the new resource relative to the workspace
        IPath new_resource_path = dlg.getResult();
        if (new_resource_path == null)
            return null;
        // Assert it's an '.xml' file
        String ext = new_resource_path.getFileExtension();
        if (ext == null  ||  !ext.equals(extension))
        {
            String filename = new_resource_path.lastSegment();
            new_resource_path =
                new_resource_path.removeLastSegments(1)
                .append(filename + "." + extension); //$NON-NLS-1$
        }
        // Get the file for the new resource's path.
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.getFile(new_resource_path);
    }
}
