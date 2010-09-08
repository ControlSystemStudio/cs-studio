package org.remotercp.progress.handler;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ProgressViewHandler {

	public static void setFocus() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {

					// bring view to front
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(
									"org.eclipse.ui.views.ProgressView");

				} catch (PartInitException e) {
					/*
					 * do nothing if view not found. A working application is
					 * more important than a focus on view
					 */
					e.printStackTrace();
				}
			}
		});
	}
}
