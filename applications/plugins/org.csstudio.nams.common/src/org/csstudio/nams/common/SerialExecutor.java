package org.csstudio.nams.common;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

public class SerialExecutor implements Executor {
	private final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
	private final Executor executor;
	private Runnable active;
	
	public SerialExecutor(Executor executor) {
		this.executor = executor;
	}
	
	@Override
	public synchronized void execute(final Runnable runnable) {
		tasks.offer(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {
					scheduleNext();
				}
			}
		});
		if (active == null) {
			scheduleNext();
		}
	}

	private void scheduleNext() {
		if ((active = tasks.poll()) != null) {
			executor.execute(active);
		}
	}
}
