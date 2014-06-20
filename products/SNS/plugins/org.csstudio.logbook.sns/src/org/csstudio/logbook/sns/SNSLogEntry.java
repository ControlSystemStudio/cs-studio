/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.sns.elog.ELogEntry;

/** SNS implementation of a {@link LogEntry}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSLogEntry implements LogEntry
{
    final private ELogEntry entry;
    
    public SNSLogEntry(final ELogEntry entry)
    {
        this.entry = entry;
    }

    @Override
    public Object getId()
    {
        return entry.getId();
    }

    @Override
    public String getLevel()
    {
        return entry.getPriority().getName();
    }
    
    @Override
    public Collection<Logbook> getLogbooks()
    {
        return Converter.convertLogbooks(entry.getLogbooks());
    }

    @Override
    public String getText()
    {
        return entry.getTitle() + "\n" + entry.getText();
    }

    @Override
    public String getOwner()
    {
        return entry.getUser();
    }

    @Override
    public Date getCreateDate()
    {
        return entry.getDate();
    }

    @Override
    public Date getModifiedDate()
    {
        return entry.getDate();
    }

    @Override
    public Collection<Attachment> getAttachment()
    {
        return Converter.convertAttachments(entry.getImages(), entry.getAttachments());
    }

    @Override
    public Collection<Tag> getTags()
    {
        return Converter.convertCategories(entry.getCategories());
    }

    @Override
    public Collection<Property> getProperties()
    {
        return Collections.emptyList();
    }
    
    @Override
    public String toString()
    {
        return entry.toString();
    }
}
