/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.util;

import org.eclipse.core.runtime.IPath;

/**
 * ResourceUtil Single Source helper. The IMPL should not be null.
 */
public abstract class ResourceUtilSSHelper {

    /**
     * Convert workspace path to OS system path. If this resource is a project
     * that does not exist in the workspace, or a file or folder below such a
     * project, this method returns null.
     *
     * @param path the workspace path
     * @return the corresponding system path. null if it is not exist.
     */
    public abstract IPath workspacePathToSysPath(IPath path);

}
