package org.csstudio.logbook;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * The interface for the logbook client.
 *
 * @author shroffk
 *
 */
public interface LogbookClient {

    /**
     * List all the logbooks
     *
     * @return a collection of all the logbooks available on the service
     * @throws Exception
     */
    public Collection<Logbook> listLogbooks() throws Exception;

    /**
     * List all the levels
     *
     * @return
     * @throws Exception
     */
    public List<String> listLevels() throws Exception;

    /**
     * Lists all the Tags
     *
     * @return a collection of all the available tags on the service
     * @throws Exception
     */
    public Collection<Tag> listTags() throws Exception;

    /**
     * Lists all Properties
     *
     * @return a collection of all the available properties on the service
     * @throws Exception
     */
    public Collection<Property> listProperties() throws Exception;

    /**
     * Lists all the attachments associated with the <tt>logId</tt>
     *
     * @param logId
     * @return a collection of all the attachments attachmed to the log
     *         identified by <tt>logId</tt>
     * @throws Exception
     */
    public Collection<Attachment> listAttachments(Object logId)
        throws Exception;

    /**
     * Get an inputStream to the attachment with name
     * <tt>attachmentFileName</tt> on log <tt>logId</tt>
     *
     * @param logId
     * @param attachment
     * @return InputStream for the attachment
     * @throws Exception
     */
    public InputStream getAttachment(Object logId, String attachmentFileName)
        throws Exception;

    /**
     * Find the logEntry with Id <tt>logId</tt>
     *
     * @param logId
     * @return return the logEntry with if <tt>logId</tt>
     * @throws Exception
     */
    public LogEntry findLogEntry(Object logId) throws Exception;

    /**
     * Find all the logentries with match the search criteria specified by the
     * <tt>search</tt> string
     *
     * The search String format should be as follow
     *
     * space separated search parameters, keywords & value should be separated
     * by a colon and multiple values for the same keyword should be separated
     * by comma.
     *
     * e.g. *Some Text* logbooks:Operations,Controls tags:Timing from:lastWeek to:today
     * e.g. logbooks:Operations from:lastMonth
     *
     * @return a collection of LogEntry
     * @throws Exception
     */
    public Collection<LogEntry> findLogEntries(String search) throws Exception;

    /**
     * Create the logEntry <tt>logEntry</tt>
     *
     * @param logEntry
     * @return the successfully created logEntry
     * @throws Exception
     */
    public LogEntry createLogEntry(LogEntry logEntry) throws Exception;

    /**
     * Update the logEntry
     *
     * @param logEntry
     *            - the new updated logEntry to replace the existing logEntry.
     * @return the successfully updated logEntry
     * @throws Exception
     */
    public LogEntry updateLogEntry(LogEntry logEntry) throws Exception;

    /**
     * Update a collection of logEntries
     *
     * @param logEntires
     * @throws Exception
     */
    public void updateLogEntries(Collection<LogEntry> logEntires)
        throws Exception;

    /**
     * Attach the file to the log identified by <tt>logId</tt>
     *
     * @param logId
     * @param file
     * @param attachment
     * @return the Attachment describing the successfully attached file.
     * @throws Exception
     */
    public Attachment addAttachment(Object logId, InputStream file, String name)
        throws Exception;
}
