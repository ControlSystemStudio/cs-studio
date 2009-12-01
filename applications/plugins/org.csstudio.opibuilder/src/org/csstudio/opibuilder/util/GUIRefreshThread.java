package org.csstudio.opibuilder.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.csstudio.opibuilder.datadefinition.WidgetIgnorableUITask;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.platform.ExecutionService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;



/**
 * 
 * A singleton back thread which will help to execute tasks for OPI GUI refreshing.
 * This thread sleeps for a time which can be set in the preference page. It can
 * help throttle the unnecessary repaint caused by fast PV value updating.
 * 
 * @author Xihui Chen
 * 
 */
public final class GUIRefreshThread implements Runnable {
	/**
	 * The singleton instance.
	 */
	private static GUIRefreshThread instance;

	/**
	 * A queue, which contains {@link WidgetIgnorableUITask}. 
	 * It will be processed by this thread periodically. 
	 */
	private ConcurrentLinkedQueue<WidgetIgnorableUITask> tasksQueue;

	/**
	 * Standard constructor.
	 */
	private GUIRefreshThread() {
		tasksQueue = new ConcurrentLinkedQueue<WidgetIgnorableUITask>();
		
		ExecutionService.getInstance().getScheduledExecutorService()
				.scheduleWithFixedDelay(this, 100, PreferencesHelper.getGUIRefreshCycle(), TimeUnit.MILLISECONDS);
	}

	/**
	 * Gets the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static synchronized GUIRefreshThread getInstance() {
		if (instance == null) {
			instance = new GUIRefreshThread();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}.
	 */
	public void run() {
		if(!tasksQueue.isEmpty())
			processQueue();
	}

	/**
	 * Process the complete queue.
	 */
	private synchronized void processQueue() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		WidgetIgnorableUITask r;
			while( (r=tasksQueue.poll()) != null){	
				display.asyncExec(r.getRunnableTask());
		}		
	}

	/**
	 * Adds the specified runnable to the queue.
	 * 
	 * @param task
	 *            the ignorable UI task.
	 */
	public synchronized void addIgnorableTask(final WidgetIgnorableUITask task) {
		if(tasksQueue.contains(task))
			tasksQueue.remove(task);
		tasksQueue.add(task);
	}

	
	
	
}
