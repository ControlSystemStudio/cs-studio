/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns.elog;

/** ELog attachment
 * 
 *  <p>SNS ELog internally distinguishes between Image and non-Image
 *  attachment.
 *  This class wraps both.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELogAttachment
{
    final private boolean is_image;
    final private String name;
    final private String type;
    final private byte[] data;

    public ELogAttachment(final boolean is_image, final String name, final String type,
            final byte[] data)
    {
        this.is_image = is_image;
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public boolean isImage()
    {
        return is_image;
    }

    public String getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public byte[] getData()
    {
        return data;
    }
    
    @Override
    public String toString()
    {
        return (is_image ? "Image" : "Attachment") +
                " '" + name + "'" +
                " (Type " + type +
                ", " + data.length + " bytes)";
    }
}
