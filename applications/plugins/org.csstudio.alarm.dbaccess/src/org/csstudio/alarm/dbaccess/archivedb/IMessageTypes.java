package org.csstudio.alarm.dbaccess.archivedb;

import javax.annotation.CheckForNull;

public interface IMessageTypes {

    /**
     * Get available types of messages.
     *
     * @return the message types
     */
    @CheckForNull
    public String[][] getMsgTypes();
}
