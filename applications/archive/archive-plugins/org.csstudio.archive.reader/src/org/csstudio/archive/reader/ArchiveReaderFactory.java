/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader;

/** Extension points implement this interface to provide
 *  instances of their ArchiveReader
 *  @author Kay Kasemir
 */
public interface ArchiveReaderFactory
{
    /** @param url URL that the ArchiveReader understands
     *  @return ArchiveReader for the URL
     *  @throws Exception on error
     */
    public ArchiveReader getArchiveReader(String url) throws Exception;
}
