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
package org.csstudio.samples.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.samples.SamplesActivator;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Install sample projects for SDS, DCT and SNL Editor.
 * 
 * Copy of SDS plug-in 'org.csstudio.sds.samples'.
 * 
 * @author jhatje
 * 
 */
public class InstallExamplesAction extends Action implements
		IWorkbenchWindowActionDelegate {

	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {

	}

	public void run(IAction action) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		Job job = new Job("Creating Sample Projects") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					// copy the sample displays
					IProject sdsProject = root.getProject("SDSSampleProject");
					IProject dctProject = root.getProject("DCTSampleProject");
					IProject snlProject = root.getProject("SNLSampleProject");

					sdsProject.create(new NullProgressMonitor());
					dctProject.create(new NullProgressMonitor());
					snlProject.create(new NullProgressMonitor());

					sdsProject.open(new NullProgressMonitor());
					dctProject.open(new NullProgressMonitor());
					snlProject.open(new NullProgressMonitor());

					URL sdsUrl = FileLocator.find(SamplesActivator.getDefault()
							.getBundle(), new Path("SDSSampleProject"),
							new HashMap());
					URL dctUrl = FileLocator.find(SamplesActivator.getDefault()
							.getBundle(), new Path("DCTSampleProject"),
							new HashMap());
					URL snlUrl = FileLocator.find(SamplesActivator.getDefault()
							.getBundle(), new Path("SNLSampleProject"),
							new HashMap());

					try {
						File sdsDirectory = new File(FileLocator.toFileURL(
								sdsUrl).getPath());
						File dctDirectory = new File(FileLocator.toFileURL(
								dctUrl).getPath());
						File snlDirectory = new File(FileLocator.toFileURL(
								snlUrl).getPath());

						File[] sdsFiles = null;
						File[] dctFiles = null;
						File[] snlFiles = null;

						int totalFileNumber = 0;
						if (sdsDirectory.isDirectory()) {
							sdsFiles = sdsDirectory.listFiles();
							totalFileNumber = count(sdsFiles, totalFileNumber);
						}
						if (dctDirectory.isDirectory()) {
							dctFiles = dctDirectory.listFiles();
							totalFileNumber = count(dctFiles, totalFileNumber);
						}
						if (snlDirectory.isDirectory()) {
							snlFiles = snlDirectory.listFiles();
							totalFileNumber = count(snlFiles, totalFileNumber);
						}
						monitor.beginTask("Copying Samples", totalFileNumber);
						copy(sdsFiles, sdsProject, monitor);
						copy(dctFiles, dctProject, monitor);
						copy(snlFiles, snlProject, monitor);

					} catch (IOException e) {
						e.printStackTrace();
					}

					// TODO: 2008-10-07: Kopieren von Scripted Rules einbauen,
					// die in den Sample-Displays benötigt werden!!

				} catch (CoreException e) {
				}

				return Status.OK_STATUS;
			}

		};

		job.schedule();
	}

	private int count(File[] files, int totalFileNumber) {
		int result = totalFileNumber;
		for (File file : files) {
			if (file.isDirectory()) {
				result = count(file.listFiles(), result);
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
					IFolder folder = container.getFolder(new Path(file
							.getName()));

					if (!folder.exists()) {
						folder.create(true, true, null);
						copy(file.listFiles(), folder, monitor);
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
			CentralLogger.getInstance().error(null, e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}