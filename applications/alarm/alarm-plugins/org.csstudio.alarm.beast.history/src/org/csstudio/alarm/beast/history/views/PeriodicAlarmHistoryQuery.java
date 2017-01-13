package org.csstudio.alarm.beast.history.views;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.jobs.Job;

import com.sun.jersey.api.client.WebResource;

/**
 * PeriodicAlarmHistoryQuery, allows you to periodically query the alarm history
 * service using the same query and provide you with a notification when the
 * result changes. This is useful for implementing auto-refresh.
 *
 * @author Kunal Shroff
 *
 */
public class PeriodicAlarmHistoryQuery {

    private volatile AlarmHistoryQueryParameters query;
    private final WebResource client;
    private final TimeUnit timeUnit;
    private final int delay;

    // TODO add name to the thread pool like the logbook thread pool
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Job currentJob;

    private List<AlarmHistoryQueryListener> listeners = new CopyOnWriteArrayList<AlarmHistoryQueryListener>();

    /**
     * A class represents the result to a AlarmHistoryQuery.
     *
     * @author Kunal Shroff
     *
     */
    public static class AlarmHistoryResult {

        public final List<Map<String, String>> alarmMessages;
        public final Exception lastException;

        public AlarmHistoryResult(List<Map<String, String>> result, Exception lastException) {
            this.alarmMessages = Collections.unmodifiableList(result);
            this.lastException = lastException;
        }
    }

    /**
     * Create a new periodic query for the alarm history
     *
     * @param query the query
     * @param r
     * @param delay
     * @param minutes
     */
    public PeriodicAlarmHistoryQuery(AlarmHistoryQueryParameters query, WebResource r, int delay, TimeUnit timeUnit) {
        this.query = query;
        this.client = r;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public void setQuery(AlarmHistoryQueryParameters query) {
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

    private AlarmHistoryResult lastResult = null;

    private void execute() {
        try {
            if (currentJob != null) {
                // If a query is running, don't schedule new jobs.
                if (currentJob.getResult() == null || !currentJob.getResult().isOK()) {
                    return;
                }
            }
            currentJob = new AlarmHistoryQueryJob(query, client) {

                @Override
                void completedQuery(AlarmHistoryResult result) {
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
     * Adds a new AlarmHistoryQueryListener listener.
     *
     * @param listener a new AlarmHistoryQueryListener listener
     */
    public void addQueryListener(AlarmHistoryQueryListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the given AlarmHistoryQueryListener listener.
     *
     * @param listener a AlarmHistoryQueryListener listener
     */
    public void removeQueryListener(AlarmHistoryQueryListener listener) {
        listeners.remove(listener);
    }

    private void fireGetQueryResult(AlarmHistoryResult result) {
        for (AlarmHistoryQueryListener listener : this.listeners) {
            listener.queryExecuted(result);
        }
    }

}
