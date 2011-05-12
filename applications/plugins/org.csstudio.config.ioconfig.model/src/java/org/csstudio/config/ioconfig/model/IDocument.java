package org.csstudio.config.ioconfig.model;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 04.05.2011
 */
public interface IDocument {

    /**
     *
     * @return the Document Id key.
     */
    @CheckForNull
    String getId();

    /**
     *
     * @return the MIME type of this Document.
     */
    @CheckForNull
    String getMimeType();

    @CheckForNull
    String getAccountname();

    @CheckForNull
    String getLogseverity();

    @CheckForNull
    String getLinkId();

    @CheckForNull
    String getSubject();

    @Nonnull
    String getDesclong();

    @Nonnull
    String getLinkForward();

    @CheckForNull
    String getErroridentifyer();

    @Nonnull
    String getLocation();

    @Nonnull
    String getKeywords();

    /**
     *
     * @return the created date of the Document.
     */
    @CheckForNull
    Date getCreatedDate();

    @CheckForNull
    Date getDeleteDate();

    @Nonnull
    Date getUpdateDate();

    @CheckForNull
    Date getEntrydate();

    @Nonnull
    InputStream getImageData() throws PersistenceException;
}
