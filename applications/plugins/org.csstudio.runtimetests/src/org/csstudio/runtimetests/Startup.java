package org.csstudio.runtimetests;

import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class Startup implements IStartupServiceListener {

	@Override
	public void run() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		IProject runtimeProject = null;
		for (IProject iProject : projects) {
			if (iProject.getName().equals("RuntimeTest")) {
				runtimeProject = iProject;
				break;
			}
		}
		if (runtimeProject != null) {
			System.out.println(runtimeProject.getName());
			try {
				IResource[] members = runtimeProject.members();
				Thread runner = new Thread(new DisplayRunner(members));
				runner.start();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

}
