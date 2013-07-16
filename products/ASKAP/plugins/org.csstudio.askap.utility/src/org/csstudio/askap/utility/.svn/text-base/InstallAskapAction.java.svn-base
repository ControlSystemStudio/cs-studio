package org.csstudio.askap.utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class InstallAskapAction extends Action implements
		IWorkbenchWindowActionDelegate {
	
	private static final Logger logger = Logger.getLogger(InstallAskapAction.class.getName());


	public static final String PROJECT_NAME = "askap";

	public InstallAskapAction() {
		// NOP
	}

	@Override
	public void run(IAction action) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		Job job = new Job("Import askap") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					// copy the sample displays
					IProject project = root.getProject(PROJECT_NAME);
					project.delete(true, true, new NullProgressMonitor());
					project.create(new NullProgressMonitor());
					project.open(new NullProgressMonitor());

					File directory = new File(Preferences.getOPIDirectory());
					if (directory.isDirectory()) {
						File[] files = directory.listFiles();
						monitor.beginTask("Copying Examples", count(files));
						copy(files, project, monitor);
					} else {
						logger.log(Level.WARNING, Preferences.getOPIDirectory() + " is not a directory.");
					}
					
				} catch (CoreException e) {
					logger.log(Level.WARNING, "Could not create project from " + Preferences.getOPIDirectory() + ": " + e.getMessage());
				}

				return Status.OK_STATUS;
			}
		};

		job.schedule();
	}

	private int count(File[] files) {
		int result = 0;
		for (File file : files) {
			if (file.isDirectory()) {
				result += count(file.listFiles());
			} else {
				result++;
			}
		}

		return result;
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

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// NOP
	}

	@Override
	public void dispose() {
		// NOP
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// NOP
	}

}
