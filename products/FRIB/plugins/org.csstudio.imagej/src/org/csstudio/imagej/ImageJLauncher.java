/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.imagej;

import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.PlatformUI;

/** Launcher that starts ImageJ for provided file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ImageJLauncher implements IEditorLauncher
{
    @Override
    public void open(final IPath file)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String tool = prefs.getString(Activator.ID, "tool", "/usr/local/bin/ij", null);
        final String image = file.toString();
        try
        {
            Runtime.getRuntime().exec(new String[] { tool, image }, null, null);
        }
        catch (Exception ex)
        {
            final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            ExceptionDetailsErrorDialog.openError(shell, "Cannot open ImageJ", ex);
        }
    }
}
