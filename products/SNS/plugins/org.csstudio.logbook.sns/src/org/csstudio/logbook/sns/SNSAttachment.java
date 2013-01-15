/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.sns.elog.ELogAttachment;

/** SNS logbook {@link Attachment}
 *  @author Kay Kasemir
 */
public class SNSAttachment implements Attachment
{
    final private ELogAttachment attachment;

    public SNSAttachment(final ELogAttachment attachment)
    {
        this.attachment = attachment;
    }

    @Override
    public InputStream getInputStream()
    {
        return new ByteArrayInputStream(attachment.getData());
    }

    @Override
    public String getFileName()
    {
        return attachment.getName();
    }

    @Override
    public String getContentType()
    {
        return attachment.getType();
    }

    @Override
    public Boolean getThumbnail()
    {
        return attachment.isImage();
    }

    @Override
    public Long getFileSize()
    {
        return Long.valueOf(attachment.getData().length);
    }
    
    @Override
    public String toString()
    {
        return attachment.toString();
    }
}
