/**
 *
 */
package org.csstudio.logbook.ui.util;

import java.io.IOException;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;

import static org.csstudio.logbook.LogEntryBuilder.logEntry;

/**
 * A wrapper around the LogEntryBuilder explicitly meant of updating/modifying an existing log entries.
 *
 * @author shroffk
 *
 */
public class UpdateLogEntryBuilder {

    private final LogEntryBuilder logEntryBuilder;

    /**
     *
     * @param logEntry
     * @throws IOException
     */
    private UpdateLogEntryBuilder(LogEntry logEntry) throws IOException {
        logEntryBuilder = logEntry(logEntry);

    }

    /**
     *
     * @param logEntry
     * @return
     * @throws IOException
     */
    public static UpdateLogEntryBuilder createUpdateLogEntryBuilder(LogEntry logEntry) throws IOException{
        return new UpdateLogEntryBuilder(logEntry);
    }

    /**
     *
     * @return
     */
    public LogEntryBuilder getLogEntryBuilder(){
        return logEntryBuilder;
    }
}
