package org.csstudio.logbook.ui;

import org.csstudio.logbook.ui.PeriodicLogQuery.LogResult;

/**
 * An interface for listener to the {@link PeriodicLogQuery}
 *
 * @author Kunal Shroff
 *
 */
public interface LogQueryListener {

    /**
     * This method is called whenever a new set of log results are obtained.
     *
     * @param result
     */
    public abstract void queryExecuted(LogResult result);

}