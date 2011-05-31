/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.util.editor;

import java.util.logging.Level;

import org.csstudio.display.pvtable.Plugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SaveAsDialog;

/** Helper for running 'SaveAs' dialog for new XML file.
 *  @author Kay Kasemir
 */
public class PromptForNewXMLFileDialog
{
    /** Run the dialog, and return the new IFile.
     *
     * @param shell The shell to use
     * @param old_file The original file or <code>null</code>.
     * @return Returns the new <code>IFile</code> or <code>null</code>.
     */
    public static IFile run(Shell shell, IFile old_file)
    {
        // Query for new name.
        // The path to the new resource relative to the workspace
        IPath new_resource_path = null;
        try
        {
            SaveAsDialog dlg = new SaveAsDialog(shell);
            dlg.setBlockOnOpen(true);
            if (old_file != null)
                dlg.setOriginalFile(old_file);
            if (dlg.open() != Window.OK)
                return null;
            // The path to the new resource relative to the workspace
            new_resource_path = dlg.getResult();
        }
        catch (Exception ex)
        {
            Plugin.getLogger().log(Level.SEVERE, "SaveAsDialog error", ex); //$NON-NLS-1$
            return null;
        }
        if (new_resource_path == null)
            return null;
        // Assert it's an '.xml' file
        String ext = new_resource_path.getFileExtension();
        if (ext == null  ||  !ext.equals(Plugin.FileExtension))
        {
            String filename = new_resource_path.lastSegment();
            new_resource_path =
                new_resource_path.removeLastSegments(1)
                .append(filename + "." + Plugin.FileExtension); //$NON-NLS-1$
        }
        // Get the file for the new resource's path.
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.getFile(new_resource_path);
    }
}
