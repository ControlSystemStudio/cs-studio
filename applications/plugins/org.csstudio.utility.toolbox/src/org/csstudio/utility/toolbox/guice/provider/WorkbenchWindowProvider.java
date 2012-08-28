package org.csstudio.utility.toolbox.guice.provider;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Provider;

public class WorkbenchWindowProvider implements Provider<IWorkbenchWindow> {

	@Override
	public IWorkbenchWindow get() {
		IWorkbench wb = PlatformUI.getWorkbench();
		return wb.getActiveWorkbenchWindow();
	}

}
