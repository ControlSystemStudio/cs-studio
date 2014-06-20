/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.ui;

import java.util.List;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.converter.EDM2OPIConverterPlugin;
import org.csstudio.opibuilder.converter.writer.OpiWriter;
import org.csstudio.opibuilder.util.ConsoleService;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Convert selected EDM filef to OPI files.
 * 
 * @author Xihui Chen
 * 
 */
public class ConvertToOPIAction implements IObjectActionDelegate {

	private List<IResource> selectedFiles;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// NOP
	}

	public void run(IAction action) {
		Job job = new Job("Converting EDM files to OPI files") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Converting", selectedFiles.size());
				for (IResource selectedFile : selectedFiles) {
					monitor.subTask("Converting " + selectedFile);
					IPath convertedFilePath = selectedFile.getLocation().removeFileExtension()
							.addFileExtension(OPIBuilderPlugin.OPI_FILE_EXTENSION);
					convertFile(selectedFile, convertedFilePath);
					monitor.worked(1);
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;
				}

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/**Convert an EDM file to OPI file
	 * @param edlFile the edl file to be converted.
	 * @param convertedFilePath local file system path of the converted file.
	 * @return true if succeeded.
	 */
	public static boolean convertFile(IResource edlFile, IPath convertedFilePath) {
		try {
			OpiWriter writer = OpiWriter.getInstance();
			writer.writeDisplayFile(edlFile.getLocation().toOSString(),
					convertedFilePath.toOSString());

			IResource r = ResourcesPlugin.getWorkspace().getRoot();
			r.refreshLocal(IResource.DEPTH_INFINITE, null);
			return true;
		} catch (Exception e) {
			final String message = "Converting error in file " + edlFile;
			EDM2OPIConverterPlugin.getLogger().log(Level.WARNING, message, e);
			ConsoleService.getInstance().writeError(message + "\n" + e.getMessage()); //$NON-NLS-1$
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection
				&& !((IStructuredSelection) selection).isEmpty())
			selectedFiles = ((List<IResource>) ((IStructuredSelection) selection).toList());
	}
}
