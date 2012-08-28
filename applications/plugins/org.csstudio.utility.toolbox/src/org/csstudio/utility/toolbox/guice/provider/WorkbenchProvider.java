package org.csstudio.utility.toolbox.guice.provider;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Provider;

public class WorkbenchProvider implements Provider<IWorkbench> {

	@Override
	public IWorkbench get() {
		return PlatformUI.getWorkbench();
	}

}
