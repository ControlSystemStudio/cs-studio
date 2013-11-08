/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns.elog;

import java.util.Date;
import java.util.List;

/** SNS ELog entry
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELogEntry
{
    final private long id;
    final private ELogPriority priority;
    final private String user;
    final private Date date;
    final private String title;
    final private String text;
    final private List<String> logbooks;
    final private List<ELogCategory> categories;
    final private List<ELogAttachment> images;
    final private List<ELogAttachment> attachments;

    public ELogEntry(final long id, final ELogPriority priority,
            final String user, final Date date,
            final String title, final String text,
            final List<String> logbooks,
            final List<ELogCategory> categories,
            final List<ELogAttachment> images,
            final List<ELogAttachment> attachments)
    {
        this.id = id;
        this.priority = priority;
        this.user = user;
        this.date = date;
        this.title = title;
        this.text = text;
        this.logbooks = logbooks;
        this.categories = categories;
        this.images = images;
        this.attachments = attachments;
    }
    
    public long getId()
    {
        return id;
    }

    public ELogPriority getPriority()
    {
        return priority;
    }

    public String getUser()
    {
        return user;
    }

    public Date getDate()
    {
        return date;
    }

    public String getTitle()
    {
        return title;
    }

    public String getText()
    {
        return text;
    }

    public List<String> getLogbooks()
    {
        return logbooks;
    }

    public List<ELogCategory> getCategories()
    {
        return categories;
    }

    public List<ELogAttachment> getImages()
    {
        return images;
    }

    public List<ELogAttachment> getAttachments()
    {
        return attachments;
    }

    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Title: ").append(title);
        buf.append(", by ").append(user);
        buf.append(" on ").append(date).append("\n");
        buf.append("Logbooks: ").append(logbooks).append("\n");
        buf.append("Categories: ").append(categories).append("\n");
        buf.append("Text:\n").append(text).append("\n");
        buf.append("Images: ").append(images).append("\n");
        buf.append("Attachments: ").append(attachments).append("\n");
        return buf.toString();
    }
}
