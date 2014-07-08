package org.csstudio.utility.file;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.utility.product.IWorkbenchWindowAdvisorExtPoint;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class IFileUtilStartup implements IWorkbenchWindowAdvisorExtPoint{

	@Override
	public void preWindowOpen() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean preWindowShellClose() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void postWindowRestore() {

		
	}

	@Override
	public void postWindowCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postWindowOpen() {

	}

	@Override
	public void postWindowClose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IStatus saveState(IMemento memento) {
		IFileUtil.getInstance().saveState(memento);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus restoreState(IMemento memento) {
		IFileUtil.getInstance().restoreState(memento);	
		return Status.OK_STATUS;
	}



}
