/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import org.csstudio.archivereader.ArchiveReader;
import org.csstudio.archivereader.ArchiveReaderFactory;

/** The plugin.xml registers this factory for ArchiveReaders when the
 *  URL prefix indicates an RDB URL
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveReaderFactory implements ArchiveReaderFactory
{
    /** {@inheritDoc} */
    @Override
    public ArchiveReader getArchiveReader(final String url) throws Exception
    {
        // There used to be problems with empty user and password preference
        // settings.
        // On CSS startup, a restored Data Browser would launch multiple
        // archive retrieval jobs.
        // The first job's RDBArchiveReaderFactory call, trying to get the user name,
        // would cause the preference service to read the default preferences.
        // Meanwhile(!) a second archive retrieval job calling the RDBArchiveReaderFactory
        // would receive an empty user or password.
        // By locking on the plug-in instance, the first RDBArchiveReaderFactory
        // call will be able to complete the preference initialization
        // before a second instance tries to read preferences.
        // Using the plug-in instance as the lock also asserts that we're
        // running in a plug-in environment that supports preferences in the
        // first place.
        final Activator instance = Activator.getInstance();
        if (instance == null)
            throw new Exception("RDBArchiveReaderFactory requires Plugin infrastructure");
        synchronized (instance)
        {
            final String user = Preferences.getUser();
            final String password = Preferences.getPassword();
            final String schema = Preferences.getSchema();
            return new RDBArchiveReader(url, user,
                    password, schema, Preferences.getStoredProcedure());
        }
    }
}
