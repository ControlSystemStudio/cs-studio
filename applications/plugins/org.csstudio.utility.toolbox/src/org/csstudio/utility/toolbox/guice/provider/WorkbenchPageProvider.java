package org.csstudio.utility.toolbox.guice.provider;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Provider;

public class WorkbenchPageProvider implements Provider<IWorkbenchPage> {

	@Override
	public IWorkbenchPage get() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		return win.getActivePage();
	}

}
