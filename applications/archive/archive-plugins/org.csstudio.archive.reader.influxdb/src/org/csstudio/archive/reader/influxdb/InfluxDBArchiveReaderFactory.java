/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb;

import java.util.logging.Level;

import org.csstudio.archive.influxdb.InfluxDBArchivePreferences;
import org.csstudio.archive.influxdb.InfluxDBDataSource;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/** The plugin.xml registers this factory for ArchiveReaders when the
 *  URL prefix indicates an InfluxDB URL
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class InfluxDBArchiveReaderFactory implements ArchiveReaderFactory
{
    /** {@inheritDoc} */
    @Override
    public ArchiveReader getArchiveReader(final String encoded_url) throws Exception
    {
        // There used to be problems with empty user and password preference
        // settings.
        // On CSS startup, a restored Data Browser would launch multiple
        // archive retrieval jobs.
        // The first job's ArchiveReaderFactory call, trying to get the user name,
        // would cause the preference service to read the default preferences.
        // Meanwhile(!) a second archive retrieval job calling the ArchiveReaderFactory
        // would receive an empty user or password.
        // By locking on the plug-in instance, the first ArchiveReaderFactory
        // call will be able to complete the preference initialization
        // before a second instance tries to read preferences.
        // Using the plug-in instance as the lock also asserts that we're
        // running in a plug-in environment that supports preferences in the
        // first place.

        final Activator instance = Activator.getInstance();
        if (instance == null)
            throw new Exception("InfluxDBArchiveReaderFactory requires Plugin infrastructure");

        String actual_url = null, user = null, password = null;
        try {
            InfluxDBDataSource ds = InfluxDBDataSource.decodeURL(encoded_url);
            actual_url = ds.getURL();
            user = ds.getArg(InfluxDBDataSource.USER_KEY);
            password = ds.getArg(InfluxDBDataSource.PASSW_KEY);
        } catch (Exception e) {
            actual_url = null;
        }

        synchronized (instance)
        {
            if (actual_url == null)
                actual_url = InfluxDBArchivePreferences.getURL();
            if (user == null)
                user = InfluxDBArchivePreferences.getUser();
            if (password == null)
                try {
                    password = InfluxDBArchivePreferences.getPassword();
                } catch (Exception e) {
                    Activator.getLogger().log(Level.WARNING, "Could not get InfluxDB pasword from secure store");
                    // e.printStackTrace();
                }

            if ((user != null) && (user.isEmpty()))
                user = null;
            if ((password != null) && (password.isEmpty()))
                password = null;

            return new InfluxDBArchiveReader(actual_url, user, password);
        }
    }
}
