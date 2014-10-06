/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.io.FileNotFoundException;
import java.util.logging.Level;

import org.csstudio.openfile.IOpenDisplayAction;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;

/** Run OPI from external program, such as alarm GUI, data browser...
 *  @author Xihui Chen
 *  @author Kay Kasemir
 */
public class ExternalOpenDisplayAction implements IOpenDisplayAction
{
    /** Open OPI file.
     *  @param path the path of the OPI file, it can be a workspace path, file system path, URL 
     *         or a opi file in opi search path.
     *  @param data the input macros in format of {@code "macro1 = hello", "macro2 = hello2"}
     *  @throws Exception on error
     */
    public void openDisplay(final String path, final String data) throws Exception
    {
        if (path == null || path.trim().isEmpty())
        {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "ExternalOpenDisplayAction for empty display");
            return;
        }
        // Parse macros
        MacrosInput macrosInput = null;
        if (data != null && data.trim().length() > 0)
            // MacrosInput.recoverFromString(s) wants initial "true" for 'include_parent_macros'
            macrosInput = MacrosInput.recoverFromString("\"true\"," + data);

        IPath originPath = ResourceUtil.getPathFromString(path);
        if (!originPath.isAbsolute())
        {
            originPath = ResourceUtil.getFileOnSearchPath(originPath, false);
            if (originPath == null)
                throw new FileNotFoundException(NLS.bind("File {0} doesn't exist on search path.", path));
        }        
        OpenTopOPIsAction.runOPI(macrosInput, originPath);
    }
}
