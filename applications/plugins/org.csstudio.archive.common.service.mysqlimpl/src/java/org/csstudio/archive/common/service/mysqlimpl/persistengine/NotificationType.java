/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 11.04.2011
 */
public enum NotificationType {
    PERSIST_DATA_FAILED("MySQL Archive Failure on Data Rescue",
                        "Statements could not be written. Dump info to filesystem in file:\n");

    private String _subject;
    private String _textHeader;
    private String _textBody;

    /**
     * Constructor.
     */
    private NotificationType(@Nonnull final String subject) {
        this(subject, null);
    }
    /**
     * Constructor.
     */
    private NotificationType(@Nonnull final String subject,
                             @Nullable final String textHeader) {
        setSubject(subject);
        setTextHeader(textHeader);
    }

    public void setSubject(@Nonnull final String subject) {
        _subject = subject;
    }
    @Nonnull
    public String getSubject() {
        return _subject;
    }
    public void setTextHeader(@Nonnull final String textHeader) {
        _textHeader = textHeader;
    }
    public void setTextBody(@Nonnull final String textBody) {
        _textBody = textBody;
    }
    @Nonnull
    public String getText() {
        return _textHeader + _textBody;
    }

}
