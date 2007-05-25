package org.csstudio.util.file;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/** Helper for dealing with IFile
 *  @author Kay Kasemir
 */
public class FileUtil
{
    /** Prevent instantiation. */
    private FileUtil()
    {}
    
    /** Convert {@link IFile} into string.
     *  @param file
     *  @return String that describes the file, or "".
     *  @see #getWorkspaceFile(String)
     */
    @SuppressWarnings("nls")
    public static String toPortablePath(IFile file)
    {
        return file == null ? "" : file.getFullPath().toPortableString();
    }
    
    /** Convert a portable path to a workspace IFile.
     *  @param portable_path The result of FileUtil.toPortablePath(IFile)
     *  @return file for the string, or null.
     *  @see #toPortablePath(IFile)
     */
    public static IFile getWorkspaceFile(final String portable_path)
    {
        if (portable_path == null  ||  portable_path.length() < 1)
            return null;
        // Convert to path (relative to workspace root), then to file
        IPath path = Path.fromPortableString(portable_path);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.getFile(path);
    }

}
