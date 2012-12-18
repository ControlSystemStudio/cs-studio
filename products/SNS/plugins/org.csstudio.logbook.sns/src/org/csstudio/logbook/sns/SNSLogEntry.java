/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.util.Collection;
import java.util.Date;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.Tag;

/** SNS implementation of a {@link LogEntry}
 *  @author Kay Kasemir
 */
public class SNSLogEntry implements LogEntry
{
    final private Date date = new Date();
    final private Long id;
    final private LogEntry info;
    final private Collection<Attachment> attachments;
    
    public SNSLogEntry(final long id, final LogEntry entry, final Collection<Attachment> attachments)
    {
        this.id = id;
        this.info = entry;
        this.attachments = attachments;
    }

    @Override
    public Object getId()
    {
        return id;
    }

    @Override
    public String getText()
    {
        return info.getText();
    }

    @Override
    public String getOwner()
    {
        return info.getOwner();
    }

    @Override
    public Date getCreateDate()
    {
        return date;
    }

    @Override
    public Date getModifiedDate()
    {
        return date;
    }

    @Override
    public Collection<Attachment> getAttachment()
    {
        return attachments;
    }

    @Override
    public Collection<Tag> getTags()
    {
        return info.getTags();
    }

    @Override
    public Collection<Logbook> getLogbooks()
    {
        return info.getLogbooks();
    }

    @Override
    public Collection<Property> getProperties()
    {
        return info.getProperties();
    }
}
