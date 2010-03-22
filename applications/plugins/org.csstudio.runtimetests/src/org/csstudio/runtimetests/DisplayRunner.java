package org.csstudio.runtimetests;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class DisplayRunner implements Runnable {

	private List<IResource> displays;

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
			int runningDisplayNo = -1;
			while (true) {
				for (int i = 0; i < displays.size(); i++) {
					if ((runningDisplayNo > -1)
							&& (runningDisplayNo <= displays.size())) {
						final IPath iPath = displays.get(runningDisplayNo).getFullPath();
						System.out.println("close: " + runningDisplayNo
								+ iPath);
						PlatformUI.getWorkbench().getDisplay().asyncExec(
								new Runnable() {
									public void run() {
										RunModeService.getInstance()
										.closeDisplayShellInRunMode(iPath);
									}
								});
					}
					Thread.sleep(2000);
					runningDisplayNo = i;
					final IPath iPath = displays.get(i).getFullPath();
					System.out.println("start: " + i
							+ iPath);
					PlatformUI.getWorkbench().getDisplay().asyncExec(
							new Runnable() {
								public void run() {
									RunModeService.getInstance()
									.openDisplayShellInRunMode(iPath);
								}
							});
					
					Thread.sleep(10000);
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
