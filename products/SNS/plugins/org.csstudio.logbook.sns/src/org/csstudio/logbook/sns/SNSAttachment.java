/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.io.InputStream;

import org.csstudio.logbook.Attachment;

/** SNS logbook {@link Attachment}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSAttachment implements Attachment
{
    final private boolean is_image;
    final private long attachment_id;

    public SNSAttachment(final boolean is_image, final long attachment_id)
    {
        this.is_image = is_image;
        this.attachment_id = attachment_id;
    }

    @Override
    public InputStream getInputStream()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFileName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getContentType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean getThumbnail()
    {
        return false;
    }

    @Override
    public Long getFileSize()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String toString()
    {
        return (is_image ? "Image" : "Attachment") + " ID " + attachment_id;
    }
}
