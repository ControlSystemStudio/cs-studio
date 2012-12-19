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

/** SNS logbook {@link Attachment}
 *  @author Kay Kasemir
 */
public class SNSAttachment implements Attachment
{
    final private String name;
    final private byte[] data;

    public SNSAttachment(final String name, final byte[] data)
    {
        this.name = name;
        this.data = data;
    }

    @Override
    public InputStream getInputStream()
    {
        return new ByteArrayInputStream(data);
    }

    @Override
    public String getFileName()
    {
        return name;
    }

    @Override
    public String getContentType()
    {
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
        return null;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
