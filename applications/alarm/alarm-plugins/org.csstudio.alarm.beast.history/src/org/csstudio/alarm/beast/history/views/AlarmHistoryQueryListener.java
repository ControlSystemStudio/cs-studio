package org.csstudio.alarm.beast.history.views;

import org.csstudio.alarm.beast.history.views.PeriodicAlarmHistoryQuery.AlarmHistoryResult;

public interface AlarmHistoryQueryListener {

    /**
     * This method is called whenever a new set of alarm messages are obtained.
     *
     * @param result
     */
    public abstract void queryExecuted(AlarmHistoryResult result);
}
