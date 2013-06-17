/**
 * 
 */
package org.csstudio.opibuilder.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;

/**
 * @author shroffk
 * 
 */
public class Install extends AbstractHandler {

    private static final String PROJECT_NAME = "Samples";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

	if (root.getProject(PROJECT_NAME).exists()) {
	    MessageDialog
		    .openError(
			    null,
			    "Failed",
			    NLS.bind(
				    "There is already a project named \"{0}\"."
					    + "Please make sure there is no project named {0} in the workspace.",
				    PROJECT_NAME));
	    return null;
	}

	Job job = new Job("Import BOY Examples") {

	    @Override
	    protected IStatus run(IProgressMonitor monitor) {
		// copy the sample displays
		try {
		    IProject project = root.getProject(PROJECT_NAME);
		    project.create(new NullProgressMonitor());
		    project.open(new NullProgressMonitor());

		    // get the list of properties and extensions to handle these
		    // properties.
		    Map<String, URL> urls = new HashMap<String, URL>();
		    IConfigurationElement[] config = Platform
			    .getExtensionRegistry()
			    .getConfigurationElementsFor(
				    "org.csstudio.opibuilder.samples.sampleset");
		    if (config.length > 0) {
			for (IConfigurationElement iConfigurationElement : config) {
			    urls.put(
				    iConfigurationElement.getAttribute("name"),
				    FileLocator
					    .find(Activator.getDefault()
						    .getBundle(),
						    new Path(
							    ((SampleSet) iConfigurationElement
								    .createExecutableExtension("sampleset")).getDirectory()), //$NON-NLS-1$
						    null));
			}

			try {
			    for (Entry<String, URL> entry : urls.entrySet()) {
				String name = entry.getKey();
				File directory = new File(FileLocator
					.toFileURL(entry.getValue()).getPath());
				if (directory.isDirectory()) {
				    copyDirectory(directory, name, project,
					    monitor);
				}

			    }
			} catch (IOException e) {
			    e.printStackTrace();
			}

			return Status.OK_STATUS;
		    }
		} catch (CoreException e1) {
		    e1.printStackTrace();
		    return null;
		}
		return null;
	    }
	};
	job.schedule();
	return null;
    }

    private void copy(File[] files, IContainer container,
	    IProgressMonitor monitor) {
	try {
	    for (File file : files) {
		monitor.subTask("Copying " + file.getName());
		if (file.isDirectory()) {
		    if (!file.getName().equals("CVS")) {//$NON-NLS-1$
			IFolder folder = container.getFolder(new Path(file
				.getName()));
			if (!folder.exists()) {
			    folder.create(true, true, null);
			    copy(file.listFiles(), folder, monitor);
			}
		    }
		} else {
		    IFile pFile = container.getFile(new Path(file.getName()));
		    if (!pFile.exists()) {
			pFile.create(new FileInputStream(file), true,
				new NullProgressMonitor());
		    }
		    monitor.internalWorked(1);
		}

	    }
	} catch (Exception e) {
	    MessageDialog.openError(null, "Error",
		    NLS.bind("Error happened during copy: \n{0}.", e));
	}
    }

    private void copyDirectory(File directory, String name,
	    IContainer container, IProgressMonitor monitor) {

	monitor.subTask("Copying " + directory.getName());
	if (directory.isDirectory()) {
	    if (name == null) {
		name = "";
	    }
	    IFolder folder = container.getFolder(new Path(
		    name.trim().isEmpty() ? directory.getName() : name.trim()));
	    if (!folder.exists()) {
		try {
		    folder.create(true, true, null);
		} catch (CoreException e) {
		    e.printStackTrace();
		}
		copy(directory.listFiles(), folder, monitor);
	    }
	}
    }
}
