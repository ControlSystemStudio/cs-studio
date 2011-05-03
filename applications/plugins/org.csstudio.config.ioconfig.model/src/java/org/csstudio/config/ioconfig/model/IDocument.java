package org.csstudio.config.ioconfig.model;

import java.io.InputStream;
import java.util.Date;

public interface IDocument {

    /**
     *
     * @return the Document Id key.
     */
    String getId();

    /**
     *
     * @return the MIME type of this Document.
     */
    String getMimeType();

    String getAccountname();

    String getLogseverity();

    String getLinkId();

    String getSubject();

    String getDesclong();

    String getLinkForward();

    String getErroridentifyer();

    String getLocation();

    String getKeywords();

    /**
     *
     * @return the created date of the Document.
     */
    Date getCreatedDate();

    Date getDeleteDate();

    Date getUpdateDate();

    Date getEntrydate();

    InputStream getImageData() throws PersistenceException;
}
