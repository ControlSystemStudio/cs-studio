package org.csstudio.runtimetests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayRunner implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayRunner.class);
    
	private final List<IResource> displays = new ArrayList<IResource>();
	private final List<IResource> runningDisplays = new ArrayList<IResource>();

	public DisplayRunner(IResource[] members) {
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
				Random rand = new Random();
				Double randomNumber = rand.nextDouble();
				if (randomNumber < 0.5) {
					if (runningDisplays.size() > 0) {
						Collections.shuffle(runningDisplays);
						closeDisplay(runningDisplays.get(0));
						runningDisplays.remove(0);
					}
				}
				Thread.sleep(5000);
				randomNumber = rand.nextDouble();
				if (randomNumber < 0.5) {
					if ((runningDisplays.size() < 10) && (displays.size() > 0)) {
						Collections.shuffle(displays);
						openDisplay(displays.get(0));
						runningDisplays.add(displays.get(0));
					}
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
		LOG.debug("open: {}", iPath);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
            public void run() {
				RunModeService.getInstance().openDisplayShellInRunMode(iPath);
			}
		});

	}

	private void closeDisplay(IResource iResource) {
		final IPath iPath = iResource.getFullPath();
		LOG.debug("close: {}", iPath);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
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
