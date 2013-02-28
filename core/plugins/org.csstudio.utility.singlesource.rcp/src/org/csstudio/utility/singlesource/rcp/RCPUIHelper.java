/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource.rcp;

import org.csstudio.utility.singlesource.UIHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SaveAsDialog;

/** Helper for accessing RCP UI.
 * 
 *  @author Kay Kasemir
 *  @author Xihui Chen - Similar code in BOY/WebOPI
 */
public class RCPUIHelper extends UIHelper
{
    /** {@inheritDoc} */
    @Override
    public IPath openSaveDialog(final Shell shell, final IPath original, final String extension)
    {
        final SaveAsDialog dlg = new SaveAsDialog(shell);
        dlg.setBlockOnOpen(true);
        
        final IFile orig_file = RCPResourceHelper.getFileForPath(original);
        if (orig_file != null)
            dlg.setOriginalFile(orig_file);
        if (dlg.open() != Window.OK)
            return null;

        // The path to the new resource relative to the workspace
        IPath path = dlg.getResult();
        if (path == null)
            return null;
        if (extension != null)
        {
            // Assert certain file extension
            final String ext = path.getFileExtension();
            if (ext == null  ||  !ext.equals(extension))
                path = path.removeFileExtension().addFileExtension(extension);
        }
        return path;
    }
}
