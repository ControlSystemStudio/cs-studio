/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.samples.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.csstudio.sds.samples.Activator;
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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstallExamplesAction extends Action implements ICheatSheetAction, IWorkbenchWindowActionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(InstallExamplesAction.class);
	
	public void run(final String[] params, final ICheatSheetManager manager) {
		run(null);
	}

	public void dispose() {

	}

	public void init(final IWorkbenchWindow window) {

	}

	public void run(final IAction action) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		final String projectName = "SDS Demo Display";
		final IProject project = root.getProject(projectName);
		boolean install = !project.exists();
		if (!install) {
			install = MessageDialog.openConfirm(new Shell(), "Project exists", "Project already exists!\r\nOverride?");
//			if (install) {
//				try {
//					project.create(new NullProgressMonitor());
//				} catch (CoreException e) {
//					e.printStackTrace();
//				}
//			}
		}

		if (install) {

			Job job = new Job("Import SDS Sample Displays") {

				@SuppressWarnings("unchecked")
				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					try {
						// copy the sample displays
						if (!project.exists()) {
							project.create(monitor);
						}

						if (!project.isOpen()) {
							project.open(new NullProgressMonitor());
						}

						URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("demoDisplays"), new HashMap());

						try {
							File directory = new File(FileLocator.toFileURL(url).getPath());
							if (directory.isDirectory()) {
								File[] files = directory.listFiles();
								monitor.beginTask("Copying Samples", count(files));
								copy(files, project, monitor);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

						// TODO: 2008-10-07: Kopieren von Scripted Rules
						// einbauen, die in den Sample-Displays benötigt
						// werden!!

					} catch (CoreException e) {
						e.printStackTrace();
					}

					return Status.OK_STATUS;
				}

			};

			job.schedule();
		}
	}

	private int count(final File[] files) {
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

	private void copy(final File[] files, final IContainer container, final IProgressMonitor monitor) {
		try {
			for (File file : files) {
				monitor.subTask("Copying " + file.getName());
				if (file.isDirectory()) {
					IFolder folder = container.getFolder(new Path(file.getName()));

					if (!folder.exists()) {
						folder.create(true, true, null);
						copy(file.listFiles(), folder, monitor);
					}
				} else {
					IFile pFile = container.getFile(new Path(file.getName()));
					if (!pFile.exists()) {
						pFile.create(new FileInputStream(file), true, new NullProgressMonitor());
					}
					monitor.internalWorked(1);
				}

			}
		} catch (Exception e) {
			LOG.error(e.toString());
		}
	}

	public void selectionChanged(final IAction action, final ISelection selection) {

	}
}