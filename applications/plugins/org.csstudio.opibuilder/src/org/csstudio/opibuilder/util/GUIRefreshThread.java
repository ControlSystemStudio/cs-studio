package org.csstudio.opibuilder.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.platform.ExecutionService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;



/**
 * 
 * A singleton back thread which will help to execute tasks for OPI GUI refreshing.
 * This way we avoid slow downs, that occur on several
 * operating systems, when Display.asyncExec() is called very often from
 * background threads.
 * 
 * This thread sleeps for a time which can be set in the preference page.
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
	 * A queue, which contains runnables that process the events that occured
	 * during the last SLEEP_TIME milliseconds.
	 */
	private Queue<Runnable> tasksQueue;

	/**
	 * Standard constructor.
	 */
	private GUIRefreshThread() {
		tasksQueue = new ConcurrentLinkedQueue<Runnable>();
		
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
	private void processQueue() {
		Display display = Display.getCurrent();
		//System.out.println(tasksQueue.size());
		if (display == null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					Runnable r;

					while ((r = tasksQueue.poll()) != null) {
						r.run();
					}
				}
			});
		} else {
			display.asyncExec(new Runnable(){
				public void run() {
					Runnable r;
					while ((r = tasksQueue.poll()) != null) {
						r.run();
					}
				}
			});
			
		}
	}

	/**
	 * Adds the specified runnable to the queue.
	 * 
	 * @param runnable
	 *            the runnable
	 */
	public void addRunnable(final Runnable runnable) {
		tasksQueue.add(runnable);
	}

}
