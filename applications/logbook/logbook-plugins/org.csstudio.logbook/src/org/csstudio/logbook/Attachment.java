/**
 *
 */
package org.csstudio.logbook;

import java.io.InputStream;

/**
 * The Interface to describe a file attachment to a LogEntry
 *
 * @author shroffk
 *
 */
public interface Attachment {

    /**
     * An input stream to the attached file
     * @return InputStream - to the attachment.
     */
    public InputStream getInputStream();

    /**
     * The name of the file Attached
     * @return String - name of the attached file.
     */
    public String getFileName();

    /**
     * The attachment type
     * @return String - the type of the attached file
     */
    public String getContentType();

    /**
     *
     * @return Boolean - if there is a thumbnail for this attachment
     */
    public Boolean getThumbnail();

    /**
     *
     * @return Long - the size of the file in bytes
     */
    public Long getFileSize();

}
