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
package org.csstudio.opibuilder.examples;

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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**The action that install examples
 * @author Xihui Chen
 *
 */
public class InstallExamplesAction extends Action implements IWorkbenchWindowActionDelegate {
	public static final String PROJECT_NAME = "BOY Examples";

	public void dispose() {
	    // NOP
	}

	public void init(IWorkbenchWindow window) {
        // NOP
	}

	public void run(IAction action) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		if(root.getProject(PROJECT_NAME).exists()){
			MessageDialog.openError(null, "Failed",
					NLS.bind("There is already a project named \"{0}\"." +
							"Please make sure there is no project named {0} in the workspace.",
							PROJECT_NAME));
			return;
		}

		Job job = new Job("Import BOY Examples") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						// copy the sample displays
						IProject project = root.getProject(PROJECT_NAME);
						project.create(new NullProgressMonitor());
						project.open(new NullProgressMonitor());
						URL url = FileLocator.find(Activator.getDefault()
								.getBundle(), new Path("examples/BOY Examples"), //$NON-NLS-1$
								null);

						try {
							File directory = new File(FileLocator
									.toFileURL(url).getPath());
							if (directory.isDirectory()) {
								File[] files = directory.listFiles();
								monitor.beginTask("Copying Examples", count(files));
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
				result+=count(file.listFiles());
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
					if(!file.getName().equals("CVS")){//$NON-NLS-1$
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