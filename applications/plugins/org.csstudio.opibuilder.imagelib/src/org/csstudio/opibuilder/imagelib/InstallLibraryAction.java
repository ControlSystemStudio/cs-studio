/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.imagelib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.osgi.framework.Bundle;

/**
 * The action to install BOY symbol images library.
 */
public class InstallLibraryAction extends Action implements
		IWorkbenchWindowActionDelegate {

	public static final String PROJECT_NAME = "BOY Image Library";
	private static final String SRC_FOLDER_TOCOPY = "./SymbolLibrary/";
	private static final String JOB_NAME = "Import BOY image library";
	private static final String TASK_NAME = "Copying BOY image library";

	public void dispose() {
		// NOP
	}

	public void init(IWorkbenchWindow window) {
		// NOP
	}

	public void run(IAction action) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		if (root.getProject(PROJECT_NAME).exists()) {
			MessageDialog.openError(
							null,
							"Failed",
							NLS.bind(
									"There is already a project named \"{0}\"."
											+ "Please make sure there is no project named {0} in the workspace.",
									PROJECT_NAME));
			return;
		}
		Job job = new Job(JOB_NAME) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					// copy the sample displays
					IProject project = root.getProject(PROJECT_NAME);
					project.create(new NullProgressMonitor());
					project.open(new NullProgressMonitor());
					Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
					URL url = FileLocator.find(bundle, new Path(SRC_FOLDER_TOCOPY), null);
					try {
						File directory = new File(FileLocator.toFileURL(url)
								.getPath());
						if (directory.isDirectory()) {
							File[] files = directory.listFiles();
							monitor.beginTask(TASK_NAME, count(files));
							copy(files, project, monitor);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (CoreException e) {
					e.printStackTrace();
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

	public void selectionChanged(IAction action, ISelection selection) {
		// NOP
	}
}