package org.csstudio.platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ExecutorAccess {

	private ScheduledExecutorService _scheduledExecutorService;

	private ExecutorService _executorService;

	private static ExecutorAccess _instance;

	private ExecutorAccess() {
		_executorService = Executors.newFixedThreadPool(5);
		_scheduledExecutorService = Executors.newScheduledThreadPool(15);
	}

	public static synchronized ExecutorAccess getInstance() {
		if (_instance == null) {
			_instance = new ExecutorAccess();
		}
		return _instance;
	}

	public ExecutorService getExecutorService() {
		return _executorService;
	}

	public ScheduledExecutorService getScheduledExecutorService() {
		return _scheduledExecutorService;
	}

}
