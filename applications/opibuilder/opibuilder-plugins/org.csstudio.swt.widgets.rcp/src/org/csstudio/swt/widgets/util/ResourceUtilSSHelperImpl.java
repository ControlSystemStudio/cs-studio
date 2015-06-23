/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 * @author Xihui Chen
 *
 */
public class ResourceUtilSSHelperImpl extends ResourceUtilSSHelper {

    /*
     * (non-Javadoc)
     * @see
     * org.csstudio.opibuilder.util.ResourceUtilSSHelper#workspacePathToSysPath
     * (org.eclipse.core.runtime.IPath)
     */
    @Override
    public IPath workspacePathToSysPath(IPath path) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IResource resource = root.findMember(path);
        if (resource != null) return resource.getLocation(); // existing resource
        else return root.getFile(path).getLocation(); // for not existing resource
    }

}
