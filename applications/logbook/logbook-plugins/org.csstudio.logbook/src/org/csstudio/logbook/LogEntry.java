package org.csstudio.logbook;

import java.util.Collection;
import java.util.Date;

/**
 * This interface represents a entry in the Logbook service.
 *
 * @author shroffk
 *
 */
public interface LogEntry {

    /**
     * The Id of the LogEntry
     *
     * @return Object - identifier for this logEntry
     */
    public Object getId();


    /**
     * The Level of the LegEntry
     *
     * @return String - log entry level
     */
    public String getLevel();

    /**
     *
     * @return String - the text of this logEntry
     */
    public String getText();

    /**
     * @return String - the owner of this logEntry
     */
    public String getOwner();

    /**
     * @return Date - representing the time this logEntry was created
     */
    public Date getCreateDate();

    /**
     * @return Date - representing the last time this logEntry was modified
     */
    public Date getModifiedDate();

    /**
     * @return Collection<{@link Attachment}> - the attached files or empty
     *         collection if no attachments present
     */
    public Collection<Attachment> getAttachment();

    /**
     * @return Collection<{@link Tag} - all the tags attached to this logEntry
     *         or empty collection is no tags present
     */
    public Collection<Tag> getTags();

    /**
     * @return Collection<{@link Logbook}> - all the logbooks attached to this
     *         logEntry
     */
    public Collection<Logbook> getLogbooks();

    /**
     * @return Collection<{@link Property}> - all the properties attached to
     *         this logEntry or empty collection if no properties present
     */
    public Collection<Property> getProperties();

}
