/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.startup.module.defaults;

import java.util.Map;

import org.csstudio.startup.module.ProjectExtPoint;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/** DefaultProject opens and closes the application's default
 *  project. The name of the project is localized.
 *
 *  @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *  @author Kay Kasemir
 */
public class DefaultProject implements ProjectExtPoint
{
    /** {@inheritDoc} */
    @Override
    public Object openProjects(Display display, IApplicationContext context,
            Map<String, Object> parameters)
    {
        // Assert that there is an open "CSS" project.
        // Without that, an existing 'CSS' might show up,
        // but a 'new Folder' action would run into
        // 'project not open' error...
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
                Messages.DefaultProjectName);
        // Assert that it exists...
        if (!project.exists())
        {
            try
            {
                project.create(new NullProgressMonitor());
            }
            catch (CoreException ex)
            {
                MessageDialog.openError(null, Messages.Error,
                        NLS.bind(Messages.CreateProjectErrorFmt,
                                 project.getName(), ex.getMessage()));
                // Give up, quit.
                return null;
            }
        }
        // .. and open it
        try
        {
            project.open(new NullProgressMonitor());
            // put project into parameters map as requested by API to make it
            // available to other extension points
            parameters.put(PROJECTS, new IProject[] { project });
            return null;
        }
        catch (CoreException ex)
        {
            MessageDialog.openError(null, Messages.Error,
                    NLS.bind(Messages.OpenProjectErrorFmt,
                            project.getName(), ex.getMessage()));
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Object closeProjects(Display display, IApplicationContext context,
            Map<String, Object> parameters) throws Exception
    {
        // Could close all projects in (IProject[]) parameters.get(PROJECTS),
        // but since this method is called _after_ WorkbenchAdvisor#preShutdown()
        // that will result in "exited with unsaved changes" warning on the
        // following CSS startup
        return null;
    }
}
