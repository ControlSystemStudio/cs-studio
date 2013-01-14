package org.csstudio.logbook.ologviewer;

import edu.msu.nscl.olog.api.Log;
import edu.msu.nscl.olog.api.Olog;
import edu.msu.nscl.olog.api.OlogClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OlogQuery {

	private final OlogClient client;
	private final String query;
	private static Executor executor = Executors.newSingleThreadExecutor();

	private volatile Result result;
	// Guarded by this: will keep track whether a query is already running
	private boolean running = false;

	public static class Result {
		public final Exception exception;
		public final Collection<Log> logs;

		public Result(Exception exception, Collection<Log> logs) {
			this.exception = exception;
			this.logs = logs;
		}

	}

	public OlogQuery(String query) throws Exception {
		if (query == null || query.isEmpty())
			throw new IllegalArgumentException(
					"Log query cannot be null or empty.");
		this.query = query;
		this.client = Olog.getClient();
	}

	/**
	 * Executes the query and calls the listener with the result. If the query
	 * was already executed, the listener is called immediately with the result.
	 * 
	 * @param listener
	 */
	public void execute(OlogQueryListener listener) {
		addOlogQueryListener(listener);

		// Make a local copy to avoid synchronization
		Result localResult = result;

		// If the query was executed, just call the listener
		if (localResult != null) {
			listener.queryExecuted(localResult);
		} else {
			execute();
		}

	}

	/**
	 * Triggers a new execution of the query, and calls all the listeners as a
	 * result.
	 */
	public void refresh() {
		execute();
	}

	private void execute() {
		// If it's already running, do nothing
		synchronized (this) {
			if (running)
				return;
			running = true;
		}

		executor.execute(new Runnable() {

			@Override
			public void run() {
				Result localResult = null;
				try {
					Collection<Log> logs = client.findLogsBySearch(query);
					localResult = new Result(null, logs);
				} catch (Exception e) {
					localResult = new Result(e, null);
				} finally {
					result = localResult;
					synchronized (this) {
						running = false;
					}
					fireGetQueryResult(localResult);
				}
			}
		});
	}

	private List<OlogQueryListener> listeners = new CopyOnWriteArrayList<OlogQueryListener>();

	/**
	 * Adds a new listener that is called every time the query is executed.
	 * Note: if you want the listener to be called at least once, use
	 * {@link #execute(OlogQueryListener)}.
	 * 
	 * @param listener
	 *            a new listener
	 */
	public void addOlogQueryListener(OlogQueryListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public void removeOlogQueryListener(OlogQueryListener listener) {
		this.listeners.remove(listener);
	}

	private void fireGetQueryResult(Result result) {
		for (OlogQueryListener listener : this.listeners) {
			listener.queryExecuted(result);
		}
	}

}
