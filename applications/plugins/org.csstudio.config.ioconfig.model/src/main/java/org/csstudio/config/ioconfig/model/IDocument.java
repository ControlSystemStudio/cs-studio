package org.csstudio.config.ioconfig.model;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

public interface IDocument {

    /**
     * 
     * @return the Document Id key.
     */
    public abstract String getId();

    /**
     * 
     * @return the MIME type of this Document.
     */
    public abstract String getMimeType();

    public abstract String getAccountname();

    public abstract String getLogseverity();

    public abstract String getLinkId();

    public abstract String getSubject();

    public abstract String getDesclong();

    public abstract String getLinkForward();

    public abstract String getErroridentifyer();

    public abstract String getLocation();

    public abstract String getKeywords();

    /**
     * 
     * @return the created date of the Document.
     */
    public abstract Date getCreatedDate();

    public abstract Date getDeleteDate();

    public abstract Date getUpdateDate();

    public abstract Date getEntrydate();
    
    public abstract InputStream getImageData() throws SQLException;
}