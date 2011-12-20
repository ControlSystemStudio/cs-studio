package gov.bnl.channelfinder.api;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.csstudio.utility.channelfinder.CFClientManager;

public class ChannelQuery {

	private final ChannelFinderClient client;
	private final String query;
	private static Executor defaultQueryExecutor = Executors.newSingleThreadExecutor();
	private final Executor queryExecutor;
	
	/**
	 * The executor on which the queries are executed.
	 * 
	 * @return the current executor
	 */
	public static Executor getDefaultQueryExecutor() {
		return defaultQueryExecutor;
	}
	
	/**
	 * Change the executor on which the queries are executed.
	 * <p>
	 * TODO: it's not clear who has the responsibility of closing the old executor
	 * 
	 * @param defaultQueryExecutor the new executor
	 */
	public static void setDefaultQueryExecutor(Executor defaultQueryExecutor) {
		if (defaultQueryExecutor == null)
			throw new NullPointerException("Executor can't be null");
		ChannelQuery.defaultQueryExecutor = defaultQueryExecutor;
	}
	
	/**
	 * Result of the query. Groups both result and error so that it's an immutable
	 * and atomic combination.
	 * 
	 * @author carcassi
	 */
	public static class Result {
		public final Exception exception;	
		public final Collection<Channel> channels;
		
		public Result(Exception exception, Collection<Channel> channels) {
			this.exception = exception;
			this.channels = channels;
		}
		
	}
	
	// Guarded by this
	private volatile Result result;

	private List<ChannelQueryListener> listeners = new CopyOnWriteArrayList<ChannelQueryListener>();

	public static class Builder {
		private String query = null;
		private ChannelFinderClient client = CFClientManager.getClient();
		private Executor queryExecutor = defaultQueryExecutor;
		private Result result = null;

		private Builder(String query) {
			if (query == null)
				throw new IllegalArgumentException(
						"query string cannot be null");
			this.query = query;
		}

		public static Builder query(String query) {
			return new Builder(query);
		}

		public Builder using(ChannelFinderClient client) {
			if (client == null)
				throw new NullPointerException("Client can't be null");
			this.client = client;
			return this;
		}
		
		public Builder result(Collection<Channel> channels, Exception exception) {
			result = new Result(exception, channels);
			return this;
		}

		public Builder on(Executor executor) {
			if (executor == null)
				throw new NullPointerException("Executor can't be null");
			this.queryExecutor = executor;
			return this;
		}

		public ChannelQuery create() {
			return new ChannelQuery(this.query, this.client, this.queryExecutor, this.result);
		}
	}

	private ChannelQuery(String query, ChannelFinderClient client, Executor queryExecutor, Result result) {
		super();
		this.query = query;
		this.client = client;
		this.queryExecutor = queryExecutor;
		this.result = result;
	}

	public void addChannelQueryListener(ChannelQueryListener listener) {
		this.listeners.add(listener);
	}

	public void removeChannelQueryListener(ChannelQueryListener listener) {
		this.listeners.remove(listener);
	}

	private void fireGetQueryResult(Result result) {
		for (ChannelQueryListener listener : this.listeners) {
			listener.queryExecuted(result);
		}
	}
	
	public String getQuery() {
		return query;
	}

	public Result getResult() {
		return this.result;
	}
	
	public void execute(ChannelQueryListener listener) {
		addChannelQueryListener(listener);
		
		// Make a local copy to avoid synchronization
		Result localResult = result;
		if (localResult != null) {
			listener.queryExecuted(localResult);
			return;
		}
		
		execute();
	}
	
	public void refresh() {
		execute();
	}

	private void execute() {
		queryExecutor.execute(new Runnable() {

			@Override
			public void run() {
				Result localResult = null;
				try {
					Collection<Channel> channels = client.find(query);
					localResult = new Result(null, channels);
				} catch (Exception e) {
					localResult = new Result(e, null);
				} finally {
					result = localResult;
					fireGetQueryResult(localResult);
				}
			}
		});
	}
	
	@Override
	public int hashCode() {
		return getQuery().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChannelQuery) {
			return query.equals(((ChannelQuery) obj).getQuery());
		}
		
		return false;
	}

}
