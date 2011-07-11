/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.util.LinkedHashSet;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.datadefinition.WidgetIgnorableUITask;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
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
	 * A LinkedHashset, which contains {@link WidgetIgnorableUITask}.
	 * It will be processed by this thread periodically. Use hashset
	 * can help to improve the performance.
	 */
	//private ConcurrentLinkedQueue<WidgetIgnorableUITask> tasksQueue;
	private LinkedHashSet<WidgetIgnorableUITask> tasksQueue;
	private Thread thread;

	private int guiRefreshCycle = 100;

	private long start;

	private volatile boolean asyncEmpty = true;

	private Runnable resetAsyncEmpty;

	/**
	 * Standard constructor.
	 */
	private GUIRefreshThread() {
		//tasksQueue = new ConcurrentLinkedQueue<WidgetIgnorableUITask>();
		tasksQueue = new LinkedHashSet<WidgetIgnorableUITask>();
		resetAsyncEmpty = new Runnable() {

			public void run() {
				asyncEmpty = true;
			}
		};
		reSchedule();
		thread = new Thread(this, "OPI GUI Refresh Thread"); //$NON-NLS-1$
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
		boolean isEmpty;
		while (true) {
			synchronized (this){
					isEmpty = tasksQueue.isEmpty();
			}
			if(!isEmpty){
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
		if(!asyncEmpty)
			return;
		asyncEmpty = false;
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
						display.asyncExec(((WidgetIgnorableUITask) o).getRunnableTask());
					} catch (Exception e) {
					    OPIBuilderPlugin.getLogger().log(Level.WARNING, "GUI refresh error", e); //$NON-NLS-1$
					}
		}
		if(display!=null && !display.isDisposed())
			display.asyncExec(resetAsyncEmpty);
	}

	/**
	 * Adds the specified runnable to the queue.
	 *
	 * @param task
	 *            the ignorable UI task.
	 */
	public synchronized void addIgnorableTask(final WidgetIgnorableUITask task) {
		tasksQueue.remove(task);
		tasksQueue.add(task);

	}




}
