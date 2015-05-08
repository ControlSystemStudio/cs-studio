/**
 *
 */
package org.csstudio.logbook.ui;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogbookClient;
import org.eclipse.core.runtime.jobs.Job;

/**
 * PeroidicLogQuery, allows you to periodically query the log service using the
 * same query and provide you with a notification when the result changes. This
 * is useful for implementing auto-refresh.
 *
 * @author Kunal Shroff
 *
 */
public class PeriodicLogQuery {

    private volatile String query;
    private final LogbookClient logbookClient;
    private final TimeUnit timeUnit;
    private final int delay;

    private ScheduledExecutorService scheduler = Executors
        .newScheduledThreadPool(1,
            org.csstudio.logbook.ui.Executors.namedPool("LogQueryPool"));

    private Job currentJob;

    private List<LogQueryListener> listeners = new CopyOnWriteArrayList<LogQueryListener>();


    /**
     * A class represents the result to a logbook query.
     *
     * @author Kunal Shroff
     *
     */
    public static class LogResult {
    public final List<LogEntry> logs;
    public final Exception lastException;

    public LogResult(List<LogEntry> result, Exception lastException) {
        this.logs = Collections.unmodifiableList(result);
        this.lastException = lastException;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((lastException == null) ? 0 : lastException.hashCode());
        result = prime * result + ((logs == null) ? 0 : logs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
        return true;
        if (obj == null)
        return false;
        if (getClass() != obj.getClass())
        return false;
        LogResult other = (LogResult) obj;
        if (lastException == null) {
        if (other.lastException != null)
            return false;
        } else if (!lastException.equals(other.lastException))
        return false;
        if (logs == null) {
        if (other.logs != null)
            return false;
        } else if (!logs.equals(other.logs))
        return false;
        return true;
    }

    }

    /**
     * Create a new periodic query for the logbook
     *
     * @param query the query
     * @param logbookClient
     * @param delay
     * @param minutes
     */
    public PeriodicLogQuery(String query, LogbookClient logbookClient, int delay, TimeUnit timeUnit) {
    this.query = query;
    this.logbookClient = logbookClient;
    this.delay = delay;
    this.timeUnit = timeUnit;
    }

    public void setQuery(String query) {
    this.query = query;
    execute();
    }

    public void stop() {
    if (currentJob != null) {
        currentJob.cancel();
    }
    scheduler.shutdownNow();
    }

    /**
     * Triggers a new execution of the query, and calls all the listeners as a
     * result.
     */
    public void start() {
    scheduler.scheduleWithFixedDelay(new Runnable() {

        @Override
        public void run() {
        execute();
        }
    }, 0, delay, timeUnit);
    }

    private LogResult lastResult = null;

    private void execute() {
    try {
        if (currentJob != null) {
        // If a query is running, don't schedule new jobs.
        if(currentJob.getResult() == null || !currentJob.getResult().isOK()){
            return;
        }
//        currentJob.cancel();
//        return;
        }
        currentJob = new LogQueryJob(query, logbookClient) {

        @Override
        void completedQuery(LogResult result) {
            // Only inform the listeners if the result has changed.
            if (result != null && !result.equals(lastResult)) {
            lastResult = result;
            fireGetQueryResult(result);
            }
        }
        };
        currentJob.schedule();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    /**
     * Adds a new LogQueryListener listener.
     *
     * @param listener
     *            a new LogQueryListener listener
     */
    public void addLogQueryListener(LogQueryListener listener) {
    listeners.add(listener);
    }

    /**
     * Removes the given LogQueryListener listener.
     *
     * @param listener
     *            a LogQueryListener listener
     */
    public void removeLogQueryListener(LogQueryListener listener) {
    listeners.remove(listener);
    }

    private void fireGetQueryResult(LogResult result) {
    for (LogQueryListener listener : this.listeners) {
        listener.queryExecuted(result);
    }
    }

}
