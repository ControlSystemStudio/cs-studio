package org.csstudio.runtimetests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class DisplayRunner implements Runnable {

	private List<IResource> displays;
	private List<IResource> runningDisplays;

	public DisplayRunner(IResource[] members) {
		displays = new ArrayList<IResource>();
		for (IResource iResource : members) {
			if (iResource.getName().contains("css-sds")) {
				displays.add(iResource);
			}
		}
	}

	@Override
	public void run() {

		try {
			waitForWorkbench();
			while (true) {
				if (runningDisplays.size() > 0) {
					Collections.shuffle(runningDisplays);
					closeDisplay(runningDisplays.get(0));
				}
				Thread.sleep(10000);
				if (runningDisplays.size() < 10) {
					Collections.shuffle(displays);
					openDisplay(displays.get(0));
				}
				Thread.sleep(30000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void openDisplay(IResource iResource) {
		final IPath iPath = iResource.getFullPath();
		CentralLogger.getInstance().debug(this, "open: " + iPath);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				RunModeService.getInstance().openDisplayShellInRunMode(iPath);
			}
		});

	}

	private void closeDisplay(IResource iResource) {
		final IPath iPath = iResource.getFullPath();
		CentralLogger.getInstance().debug(this, "close: " + iPath);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				RunModeService.getInstance().closeDisplayShellInRunMode(iPath);
			}
		});
	}

	/**
	 * Wait until the workbench is available to start SDS displays.
	 * 
	 * @throws InterruptedException
	 */
	private void waitForWorkbench() throws InterruptedException {
		boolean workbenchNotAvailable = true;
		while (workbenchNotAvailable) {
			try {
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench != null) {
					workbenchNotAvailable = false;
				}
			} catch (IllegalStateException e) {
			}
			Thread.sleep(1000);
		}
	}

}
