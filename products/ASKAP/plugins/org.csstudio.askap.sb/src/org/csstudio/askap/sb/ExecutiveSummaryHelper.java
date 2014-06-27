package org.csstudio.askap.sb;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import askap.interfaces.monitoring.MonitorPoint;

/**
 * @author Xinyu Wu
 * 
 * base on org.csstudio.opibuilder.util.ConsoleServiceSSHelperImpl
 * 
 */
public class ExecutiveSummaryHelper {

	private static Logger logger = Logger.getLogger(ExecutiveSummaryHelper.class.getName());
	
	private ExecutiveSummaryView summaryView = null;

	private static ExecutiveSummaryHelper helper = new ExecutiveSummaryHelper();
	
	private ExecutiveSummaryHelper() {
	}

	public static ExecutiveSummaryHelper getInstance() {
		return helper;
	}

	public void updateValue(final MonitorPoint point) {
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
			public void run() {
				// find the view
				if (summaryView==null)
					summaryView= (ExecutiveSummaryView) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage().findView(ExecutiveSummaryView.ID);
				
				// if view has not been created, created it					
				if (summaryView==null) {
					try {
						summaryView= (ExecutiveSummaryView) PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage().showView(ExecutiveSummaryView.ID);
					} catch (PartInitException e) {
						logger.log(Level.WARNING, "ExecutiveSummaryView activation error", e);
					}	
				}
				
				summaryView.update(point);
			}
		});
	}

	public void disconnected(final String pointName) {
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
			public void run() {
				// find the view
				if (summaryView==null)
					summaryView= (ExecutiveSummaryView) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage().findView(ExecutiveSummaryView.ID);
				
				// if view has not been created, created it					
				if (summaryView==null) {
					try {
						summaryView= (ExecutiveSummaryView) PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage().showView(ExecutiveSummaryView.ID);
					} catch (PartInitException e) {
						logger.log(Level.WARNING, "ExecutiveSummaryView activation error", e);
					}	
				}
				
				summaryView.disconnected(pointName);
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
												
						summaryView= (ExecutiveSummaryView) PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage().showView(ExecutiveSummaryView.ID);	
					}
				} catch (PartInitException e) {
					logger.log(Level.WARNING, "ExecutiveSummaryView activation error", e);
				}
			}
		});
	}
}
