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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.samples.SamplesActivator;
import org.csstudio.samples.internal.preferences.PreferenceConstants;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Provides an action to install sample projects.
 * 
 * Via Preferences a list of semicolon-separated source-directories may be
 * provided. The default is DEFAULT_SOURCE_DIRS.
 * 
 * If the action is started from within the IDE, the CVS information is present
 * in the source directories. To prevent it from being copied, it is filtered.
 * 
 * Replacement for SDS plug-in 'org.csstudio.sds.samples'.
 * 
 * @author jhatje, jpenning
 * 
 */
public class InstallExamplesAction extends Action implements
		IWorkbenchWindowActionDelegate {

	private final Logger log = CentralLogger.getInstance().getLogger(this);

	private static final String DEFAULT_SOURCE_DIRS = "SDS Demo Display;DCT Demo Project;SNL Demo Project;";

	public void dispose() {
		// Nothing to dispose
	}

	public void init(IWorkbenchWindow window) {
		// Only ProgressMonitor is used for user feedback
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// Selection ignored
	}

	public void run(IAction action) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		Job job = new Job("Creating Sample Projects") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return copySamples(root, monitor);
			}

		};

		job.schedule();
	}

	// visibility is protected to make this method accessible for the job
	// inside the run method.
	protected IStatus copySamples(final IWorkspaceRoot root,
			IProgressMonitor monitor) {
		IStatus result = Status.OK_STATUS;
		for (String sourceDirectory : getSourceDirectories()) {
			IStatus singleResult = copySampleFromProjectPathToWorkspaceRoot(
					root, monitor, sourceDirectory);
			result = singleResult.equals(Status.OK_STATUS) ? result
					: singleResult;
		}

		// TODO: 2008-10-07: Kopieren von Scripted Rules einbauen,
		// die in den Sample-Displays benötigt werden!!

		return result;
	}

	private String[] getSourceDirectories() {
		IPreferencesService preferencesService = Platform.getPreferencesService();
		String sourceDirPrefs = preferencesService.getString(
				SamplesActivator.PLUGIN_ID,
				PreferenceConstants.SOURCE_DIRECTORIES, DEFAULT_SOURCE_DIRS,
				null);

		return sourceDirPrefs.split(";");
	}

	private IStatus copySampleFromProjectPathToWorkspaceRoot(
			IWorkspaceRoot root, IProgressMonitor monitor, String projectPath) {
		Assert.isNotNull(root);
		Assert.isNotNull(monitor);
		Assert.isNotNull(projectPath);

		// Default status is ERROR
		IStatus result = new Status(IStatus.ERROR, SamplesActivator.PLUGIN_ID,
				IStatus.OK, "Copying from project path " + projectPath
						+ " failed", null);
		IProject project = root.getProject(projectPath);

		// Guard: Do nothing if destination already exists, Status is OK
		if (project.exists()) {
			return Status.OK_STATUS;
		}

		try {
			URL url = FileLocator.find(SamplesActivator.getDefault()
					.getBundle(), new Path(projectPath), null);

			if (url != null) {
				project.create(new NullProgressMonitor());
				project.open(new NullProgressMonitor());
				File directory = new File(FileLocator.toFileURL(url).getPath());
				// Only directories get copied
				if (directory.isDirectory()) {
					File[] files = directory.listFiles();
					int totalFileNumber = count(files, 0);
					monitor.beginTask("Copying Samples from " + projectPath,
							totalFileNumber);
					copyWithoutCVS(files, project, monitor);
					// Now we succeeded, Status is OK
					result = Status.OK_STATUS;
				}
			}
		} catch (IOException e) {
			log.warn("IOException while copying the project " + projectPath, e);
		} catch (CoreException e) {
			log.warn("Creation or opening of project " + projectPath
					+ " failed", e);
		}

		return result;
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

	private void copyWithoutCVS(File[] files, IContainer container,
			IProgressMonitor monitor) throws CoreException,
			FileNotFoundException {
		for (File file : files) {
			monitor.subTask("Copying " + file.getName());
			if (file.isDirectory()) {
				if (!file.getName().equals("CVS")) {
					IFolder folder = container.getFolder(new Path(file
							.getName()));

					if (!folder.exists()) {
						folder.create(true, true, null);
						copyWithoutCVS(file.listFiles(), folder, monitor);
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
	}
}