package org.csstudio.opibuilder.util;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.csstudio.opibuilder.datadefinition.WidgetIgnorableUITask;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.platform.logging.CentralLogger;
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
	
	private Thread thread;
	
	private int guiRefreshCycle = 100;
	
	private long start;

	/**
	 * Standard constructor.
	 */
	private GUIRefreshThread() {
		tasksQueue = new ConcurrentLinkedQueue<WidgetIgnorableUITask>();
		reSchedule();
		thread = new Thread(this, "OPI GUI Rrefresh Thread"); //$NON-NLS-1$
		thread.start();
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
	 * Reschedule this task upon the new GUI refresh cycle.
	 */
	public void reSchedule(){
		guiRefreshCycle = PreferencesHelper.getGUIRefreshCycle();
	}

	/**
	 * {@inheritDoc}.
	 */
	public void run() {
		while (true) {		
			if(!tasksQueue.isEmpty()){
					start = System.currentTimeMillis();
					processQueue();	
				try {
					long current = System.currentTimeMillis();
					if(current - start < guiRefreshCycle)
						Thread.sleep(guiRefreshCycle - (current -start));
					} catch (InterruptedException e) {
						//ignore
					}
			}else
				try {					
						Thread.sleep(guiRefreshCycle);
					} catch (InterruptedException e) {
						//ignore
					}
			
		}
	}

	/**
	 * Process the complete queue.
	 */
	private void processQueue() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Object[] tasksArray;
		//copy the tasks queue.
		synchronized (this) {
			tasksArray = tasksQueue.toArray();
			tasksQueue.clear();
		}		
		for(Object o : tasksArray){	
				if(display!=null && !display.isDisposed())
					try {
						display.syncExec(((WidgetIgnorableUITask) o).getRunnableTask());
					} catch (Exception e) {
						CentralLogger.getInstance().error(this, e);
					}
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
