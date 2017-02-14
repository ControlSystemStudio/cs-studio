/*******************************************************************************
 * Copyright (c) 2010-2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.openfile;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;

/**Utility class for display operation.
 *
 * Current implementation checks the registry once on startup,
 * then uses cached information.
 * In case a different approach turns out to be better, that should
 * be transparent to users of this API.
 *
 * @author Xihui Chen
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DisplayUtil
{
    private static final Logger logger = Logger.getLogger(DisplayUtil.class.getName());

    /** Singleton instance */
    final private static DisplayUtil instance = new DisplayUtil();

    /** Map of file extensions to handlers for that file type */
    final private Map<String, IOpenDisplayAction> actions = new HashMap<String, IOpenDisplayAction>();

    /** Private constructor to prevent instantiation */
    private DisplayUtil()
    {
        final IExtensionRegistry extReg = Platform.getExtensionRegistry();
        final IConfigurationElement[] confElements =
            extReg.getConfigurationElementsFor(IOpenDisplayAction.EXTENSION_POINT_ID);
        for (IConfigurationElement element : confElements)
        {
            try
            {
                final String ext = element.getAttribute("file_extension");
                final IOpenDisplayAction action =
                        (IOpenDisplayAction) element.createExecutableExtension("class");
                final boolean is_default;
                if (element.getAttribute("default") == null)
                    is_default = true;
                else
                    is_default = Boolean.parseBoolean(element.getAttribute("default"));

                logger.log(Level.FINE, () -> "IOpenDisplayAction for '" + ext + ": " +
                                             action.getClass().getName() + (is_default ? " (default)" : ""));
                final IOpenDisplayAction other = actions.get(ext);
                if (other != null)
                {
                    if (is_default)
                    {
                        actions.put(ext, action);
                        logger.log(Level.FINE, "replaces " + other.getClass().getName());
                    }
                    else
                        logger.log(Level.FINE, "keeping " + other.getClass().getName());
                }
                else
                    actions.put(ext, action);
            }
            catch (CoreException ex)
            {
                logger.log(Level.SEVERE, "Error locating IOpenDisplayActions", ex);
            }
        }
    }

    public boolean isExtensionSupported(String ext){
        return actions.containsKey(ext);
    }

    /** @return Singleton instance */
    public static DisplayUtil getInstance()
    {
        return instance;
    }

    /**Open display with corresponding runtime.
     * @param path the path of display file.
     * @param data the input data. set as null if it is not needed.
     * @see IOpenDisplayAction
     * @throws Exception on error: Unknown file type, error while trying to open the file
     */
    public void openDisplay(final String path, final String data) throws Exception
    {
        final int delim = path.lastIndexOf('.');
        if (delim < 0)
            throw new Exception(Messages.DisplayUtil_ErrorEmptyExt);
        final String ext = path.substring(delim+1);
        if (ext == null || ext.trim().length() == 0)
            throw new Exception(Messages.DisplayUtil_ErrorEmptyExt);
        final IOpenDisplayAction action = actions.get(ext);
        if (action == null)
            throw new Exception(NLS.bind(Messages.DisplayUtil_ErrorUnknownExtFmt, ext));
        action.openDisplay(path, data);
    }
}
