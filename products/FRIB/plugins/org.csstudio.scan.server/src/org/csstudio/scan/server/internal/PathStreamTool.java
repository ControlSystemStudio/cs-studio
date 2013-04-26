/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.internal;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/** Tool for opening path as stream
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PathStreamTool
{
    /** @param path Either "platform:/plugin/some.plugin.name/path/file.ext" or plain file path
     *  @return InputStream for the path
     *  @throws Exception
     */
    public static InputStream openStream(final String path) throws Exception
    {
        if (path.startsWith("platform:"))
        {   // Path within platform, for example
            // platform:/plugin/org.csstudio.scan/....
            final URL url = new URL(path);
            return url.openStream();
        }
        else
            return new FileInputStream(path);
    }
}
