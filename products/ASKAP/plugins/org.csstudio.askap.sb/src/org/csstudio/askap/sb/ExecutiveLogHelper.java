package org.csstudio.askap.sb;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.utility.icemanager.LogObject;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Xinyu Wu
 * 
 * base on org.csstudio.opibuilder.util.ConsoleServiceSSHelperImpl
 * 
 */
public class ExecutiveLogHelper {

	private static Logger logger = Logger.getLogger(ExecutiveLogHelper.class.getName());
	
	ExecutiveLogView logView = null;

	private static ExecutiveLogHelper helper = new ExecutiveLogHelper();
	
	private ExecutiveLogHelper() {
	}

	public static ExecutiveLogHelper getInstance() {
		return helper;
	}

	public void writeLog(final LogObject logObj) {
			UIBundlingThread.getInstance().addRunnable(new Runnable() {
				public void run() {
					// find the view
					if (logView==null)
						logView= (ExecutiveLogView) PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage().findView(ExecutiveLogView.ID);
					
					// if view has not been created or it has been disposed, created it					
					if (logView==null || logView.isDisposed()) {
						try {
							logView= (ExecutiveLogView) PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage().showView(ExecutiveLogView.ID);
						} catch (PartInitException e) {
							logger.log(Level.WARNING, "ExecutiveLogView activation error", e);
						}	
					}
					
					logView.logMessage(logObj);
				}
			});
	}
	
	public void popConsoleView() {
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
			public void run() {
				try {
					if (PlatformUI.getWorkbench() != null
							&& PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null
							&& PlatformUI.getWorkbench().getActiveWorkbenchWindow()
									.getActivePage() != null) {
												
						logView= (ExecutiveLogView) PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage().showView(ExecutiveLogView.ID);	
					}
				} catch (PartInitException e) {
					logger.log(Level.WARNING, "ExecutiveLogView activation error", e);
				}
			}
		});
	}
}
