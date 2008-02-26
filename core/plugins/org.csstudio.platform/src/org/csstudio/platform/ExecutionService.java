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
 package org.csstudio.platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ExecutionService {
	
	private Counter _tasksInProgress;

	private AverageTime _averageTime;
	
	private ScheduledExecutorService _scheduledExecutorService;

	private ExecutorService _executorService;

	private ExecutorService _executorService2;

	private static ExecutionService _instance;

	private Counter _threadCounter;
	
	private ExecutionService() {
		_threadCounter = new Counter();
		_tasksInProgress  = new Counter();
		_averageTime = new AverageTime();
		_executorService = Executors.newFixedThreadPool(2);
		_executorService2 = Executors.newFixedThreadPool(15);
		_scheduledExecutorService = Executors.newScheduledThreadPool(15);
	}

	public static synchronized ExecutionService getInstance() {
		if (_instance == null) {
			_instance = new ExecutionService();
		}
		
		return _instance;
	}
	
	public static double getNumberOfTaskInProgress() {
		return getInstance()._averageTime.getAverageTime();
	}

	public void execute (final Runnable runnable) {
		final long start = System.currentTimeMillis();
		
		_tasksInProgress.increment();
		
		_executorService.execute(new Runnable(){
			public void run() {
				runnable.run();
				_tasksInProgress.decrement();
				_averageTime.track(System.currentTimeMillis()-start);
			}
		});
	}
	
	public ScheduledExecutorService getScheduledExecutorService() {
		return _scheduledExecutorService;
	}
	
	public Counter getThreadCounter() {
		return _threadCounter;
	}

	class AverageTime {
		private long _count;
		private long _ms;
		
		public AverageTime() {
			_count = 0;
			_ms = 0;
		}
		
		public double getAverageTime() {
			return (double) _ms/_count;
		}
		
		public synchronized void track(long ms) {
			_count++;
			_ms+=ms;
		}
	} 
	
	public class Counter {
		private int _count;
		
		public Counter() {
			_count = 0;
		}
		
		public int getCount() {
			return _count;
		}
		
		public synchronized void increment() {
			_count++;
		}
		
		public synchronized void decrement() {
			_count--;
		}
	}
}
