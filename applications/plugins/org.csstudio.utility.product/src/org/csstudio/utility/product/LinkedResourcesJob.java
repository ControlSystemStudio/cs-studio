/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Eclipse Job that creates linked resources
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LinkedResourcesJob extends Job
{
	final private String settings;

	/** Initialize
	 *  @param settings "-share_link" settings, see {@link LinkedResource}
	 */
	public LinkedResourcesJob(final String settings)
    {
	    super(Messages.CreateLinkedResources);
	    this.settings = settings;
    }

	/** Assert that a project is available in the workspace
     *  @param monitor Progress monitor
     *  @param project_name Name of the project
     *  @param location File system location. <code>null</code> to use project name within workspace
     *  @return {@link IProject}, opened
     *  @throws Exception on error
     */
    private IProject assertProject(final IProgressMonitor monitor, final String project_name, final IPath location) throws Exception
    {
    	final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject project = workspace.getRoot().getProject(project_name);
        // Assert that it exists...
        if (!project.exists())
        {
            IProjectDescription description = workspace.newProjectDescription(project_name);
            description.setLocation(location);
			project.create(description, monitor);
        }
        // .. and is open
        if (!project.isOpen())
        	project.open(monitor);
        return project;
    }

	/** @param path Path, may be <code>null</code> to start with a new Path
     *  @param new_segment Path segment to add to the end of the path
     *  @return Path with new segment added
     */
    private IPath extendPath(final IPath path, final String new_segment)
    {
    	if (path == null)
    		return new Path(new_segment);
    	return path.append(new_segment);
    }

	/** Assert/update a lined resource
     *  @param monitor Progress monitor
	 *  @param link Description of the desired linked resource
	 *  @throws Exception on error
     */
    private void linkResource(final IProgressMonitor monitor, final LinkedResource link) throws Exception
    {
    	final Path resource_path = new Path(link.getResourceName());
    	if (link.isProject())
    	{
    		assertProject(monitor, resource_path.segment(0), new Path(link.getFileSystemName()));
    	}
    	else
    	{
    		final String[] segments = resource_path.segments();
    		if (segments.length < 2)
    			throw new Exception("Expecting at least /Project/Share for linked resource name, got '" + link.getResourceName() + "'");
    		// Get project
    		final IProject project = assertProject(monitor, segments[0], null);
    		// Create folders for intermediate path segments
    		IPath folder_path = null;
    		for (int i=1; i<segments.length-1; ++i)
    		{
    			folder_path = extendPath(folder_path, segments[i]);
    			 final IFolder folder = project.getFolder(folder_path);
    			 if (! folder.exists())
    				 folder.create(true, true, monitor);
    		}
    		// Create linked folder
			folder_path = extendPath(folder_path, segments[segments.length-1]);
            final IFolder folder = project.getFolder(folder_path);
            // if (folder.exists()) ...?
            // No. Re-create in any case to assert that it has the correct link
            folder.createLink(new Path(link.getFileSystemName()), IResource.REPLACE, new NullProgressMonitor());
    	}
    }

    /** {@inheritDoc} */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
    	try
    	{
    		final LinkedResource[] links = LinkedResource.fromString(settings);
    		monitor.beginTask(Messages.CreateLinkedResources, links.length);
    		for (LinkedResource link : links)
    		{
    			linkResource(monitor, link);
    			monitor.worked(1);
    		}
    		monitor.done();
    	}
    	catch (Exception ex)
    	{
    		return new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Linked Resource problem", ex);
    	}
    	return Status.OK_STATUS;
    }
}
