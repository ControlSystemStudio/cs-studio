/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import java.util.Optional;

import org.csstudio.opibuilder.runmode.RunModeService.DisplayMode;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

/** {@link IEditorLauncher} that opens display in runtime.
 *
 *  <p>Registered in plugin.xml as "Editor" for *.opi files,
 *  allowing users to launch displays from the Eclipse Navigator,
 *  or by opening the file in the "default editor" based
 *  on the Eclipse registry.
 *
 *  @author Kay Kasemir
 */
public class DisplayLauncher implements IEditorLauncher
{
    @Override
    public void open(final IPath path)
    {
        IPath workspacePath = LauncherHelper.systemPathToWorkspacePath(path);
        RunModeService.openDisplay(workspacePath, Optional.empty(), DisplayMode.NEW_TAB, Optional.empty());
    }
}
