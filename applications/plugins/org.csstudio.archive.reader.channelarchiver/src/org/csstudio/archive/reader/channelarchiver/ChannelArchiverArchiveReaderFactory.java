/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/** The plugin.xml registers this factory for ArchiveReaders when the
 *  URL prefix indicates a Channel Archiver URL
 *  @author Kay Kasemir
 */
public class ChannelArchiverArchiveReaderFactory implements
        ArchiveReaderFactory
{
    /** {@inheritDoc}*/
    @Override
    public ArchiveReader getArchiveReader(final String url) throws Exception
    {
        return new ChannelArchiverReader(url);
    }
}
