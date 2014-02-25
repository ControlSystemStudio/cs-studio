/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.utility.dbparser;

import java.util.List;
import java.util.logging.Level;

import org.csstudio.utility.dbparser.data.Record;
import org.csstudio.utility.dbparser.util.DbUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IStartup;

/**
 * Scan workspace looking for DB files.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class StartupLauncher implements IStartup {

	@Override
	public void earlyStartup() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			workspace.getRoot().accept(new IResourceVisitor() {
				public boolean visit(IResource resource) 
				{
					if (!(resource.getType() == IResource.FILE))
						return true;
					if (resource.getName().endsWith(".db")) {
						try {
							parseDB((IFile) resource);
						} catch (Exception e) {
							Activator.getLogger().log(
									Level.WARNING,
									"Failed to parse " + resource.getFullPath() + ": " + e.getMessage());
						}
					}
					return true;
				}
			});
		} catch (CoreException e) {
			Activator.getLogger().log(Level.SEVERE,
					"Failed to read workspace: " + e.getMessage());
		}
		IResourceChangeListener listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					if (event == null || event.getDelta() == null)
						return;
					event.getDelta().accept(new IResourceDeltaVisitor() {
						public boolean visit(IResourceDelta delta) {
							IResource resource = delta.getResource();
							if (resource.getType() != IResource.FILE)
								return true;
							if (resource.getFileExtension() != null
									&& resource.getFileExtension()
											.toLowerCase().equals("db")) {
								switch (delta.getKind()) {
								case IResourceDelta.REMOVED:
									DBContext.get().removeFile((IFile) resource);
									break;

								case IResourceDelta.CHANGED:
								case IResourceDelta.CONTENT:
									DBContext.get().removeFile((IFile) resource);
									parseDB((IFile) resource);
									break;
									
								case IResourceDelta.ADDED:
									parseDB((IFile) resource);
									break;

								default:
									break;
								}
							}
							return true;
						}
					});
				} catch (CoreException e) {
					Activator.getLogger().log(Level.SEVERE,
							"Failed to read workspace on change: " + e.getMessage());
				}
			}
		};
		workspace.addResourceChangeListener(listener);
	}

	private void parseDB(IFile file) {
		try {
			String dbContent = DbUtil.readFile(file);
			List<Record> records = DbUtil.parseDb(dbContent);
			for (Record r : records)
				DBContext.get().addRecord(file, r);
		} catch (Exception e) {
			Activator.getLogger().log(Level.WARNING,
					"Failed to parse " + file.getFullPath() + ": " + e.getMessage());
		}
	}

}
