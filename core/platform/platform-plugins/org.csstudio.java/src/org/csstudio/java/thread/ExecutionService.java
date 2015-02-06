/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.java.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for the execution of asynchronous tasks using thread pools.
 * 
 * @author swende, Xihui Chen (move this from platform to here)
 * 
 */
public class ExecutionService {

	private static ExecutionService _instance;

	private static final int HIGH_PRIORITY_THREADS = 20;
	private static final int NORMAL_PRIORITY_THREADS = 20;
	private static final int LOW_PRIORITY_THREADS = 20;
	private static final int SCHEDULED_THREADS = 3;

	private static LinkedBlockingQueue<Runnable> _lowPriorityQueue;
	private static LinkedBlockingQueue<Runnable> _normalPriorityQueue;
	private static LinkedBlockingQueue<Runnable> _highPriorityQueue;

	private ScheduledExecutorService _scheduledExecutorService;
	private ExecutorService _lowPriorityExectorService;
	private ExecutorService _normalPriorityExectorService;
	private ExecutorService _highPriorityExecutorService;

	private ExecutionService() {
		_lowPriorityQueue = new LinkedBlockingQueue<Runnable>();
		_normalPriorityQueue = new LinkedBlockingQueue<Runnable>();
		_highPriorityQueue = new LinkedBlockingQueue<Runnable>();

		_lowPriorityExectorService = new ThreadPoolExecutor(LOW_PRIORITY_THREADS, LOW_PRIORITY_THREADS, 0L, TimeUnit.MILLISECONDS,
				_lowPriorityQueue, new CssThreadFactory(Thread.MIN_PRIORITY));

		_normalPriorityExectorService = new ThreadPoolExecutor(NORMAL_PRIORITY_THREADS, NORMAL_PRIORITY_THREADS, 0L, TimeUnit.MILLISECONDS,
				_normalPriorityQueue, new CssThreadFactory(Thread.NORM_PRIORITY));

		_highPriorityExecutorService = new ThreadPoolExecutor(HIGH_PRIORITY_THREADS, HIGH_PRIORITY_THREADS, 0L, TimeUnit.MILLISECONDS,
				_highPriorityQueue, new CssThreadFactory(Thread.MAX_PRIORITY));

		_scheduledExecutorService = Executors.newScheduledThreadPool(SCHEDULED_THREADS);
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static synchronized ExecutionService getInstance() {
		if (_instance == null) {
			_instance = new ExecutionService();
		}

		return _instance;
	}

	/**
	 * Executes the specified runnable with high priority.
	 * 
	 * @param runnable
	 *            the runnable
	 */
	public void executeWithHighPriority(final Runnable runnable) {
		doRun(_highPriorityExecutorService, runnable);
	}

	/**
	 * Executes the specified runnable with normal priority.
	 * 
	 * @param runnable
	 *            the runnable
	 */
	public void executeWithNormalPriority(final Runnable runnable) {
		doRun(_normalPriorityExectorService, runnable);
	}
	
	/**
	 * Returns the number of runnables waiting for execution with high priority.
	 */
	public int getHighPriorityQueueSize() {
		return _highPriorityQueue.size();
	}

	/**
	 * Returns the number of runnables waiting for execution with normal priority.
	 */
	public int getNormalPriorityQueueSize() {
		return _normalPriorityQueue.size();
	}
	
	/**
	 * Returns the number of runnables waiting for execution with low priority.
	 */
	public int getLowPriorityQueueSize() {
		return _lowPriorityQueue.size();
	}
	
	/**
	 * Executes the specified runnable with normal priority.
	 * 
	 * @param runnable
	 *            the runnable
	 */
	public void executeWithLowPriority(final Runnable runnable) {
		doRun(_lowPriorityExectorService, runnable);
	}

	private void doRun(ExecutorService service, final Runnable runnable) {
		service.execute(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} catch (Throwable t) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE, t.getMessage(),t);
				}
			}
		});
	}

	public ScheduledExecutorService getScheduledExecutorService() {
		return _scheduledExecutorService;
	}

	private static class CssThreadFactory implements ThreadFactory {
		static final AtomicInteger poolNumber = new AtomicInteger(1);
		final ThreadGroup group;
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;
		private int priority = Thread.NORM_PRIORITY;

		CssThreadFactory(int priority) {
			this.priority = priority;

			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = "css-threadpool-" + poolNumber.getAndIncrement() + "-thread-";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			t.setDaemon(false);
			t.setPriority(priority);
			return t;
		}
	}
}
