package org.csstudio.logbook.ui;


/**
 * The listener interface for receiving logEntryBuilder events.
 * The class that is interested in processing a logEntryBuilder
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addLogEntryBuilderListener<code> method. When
 * the logEntryBuilder event occurs, that object's appropriate
 * method is invoked.
 *
 * @see LogEntryBuilderEvent
 *
 * @author ITER-CSS
 */
public interface LogEntryBuilderListener {


        /**
         * Initialize save action.
         *
         * @param userName the user name
         */
        public void initializeSaveAction(String userName);

        /**
         * Save process status.
         *
         * @param state the state
         * @throws Exception
         */
        public void saveProcessStatus(LogEntryBuilderEnum state) throws Exception;


}
