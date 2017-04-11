/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.XMLExport;
import org.csstudio.archive.config.XMLImport;
import org.csstudio.archive.config.rdb.RDBArchiveConfig;
import org.junit.Test;

/** JUnit demo of {@link XMLExport} and {@link XMLImport}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveExportImportDemo
{
    @Test
    public void testExport() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString("archive_rdb_url");
        final String user = settings.getString("archive_rdb_user");
        final String password = settings.getString("archive_rdb_password");
        final String schema = settings.getString("archive_rdb_schema");
        final String engine_name = settings.getString("archive_config");
        final String filename = settings.getString("tmp_file");
        if (url == null  ||  user == null  ||  password == null  ||  engine_name == null  ||
            filename == null)
        {
            System.out.println("Skipping test, no archive_rdb_url, user, password, tmp_file");
            return;
        }

        final File file = new File(filename);
        if (file.exists())
            file.delete();
        assertFalse(file.exists());
        final PrintStream out = new PrintStream(filename);
        try
        {
            new XMLExport().export(out, new RDBArchiveConfig(url, user, password, schema), engine_name);
        }
        finally
        {
            out.close();
        }
        assertTrue(file.exists());
        System.out.println("Created file " + file + ", " + file.length() + " bytes");
    }

    @Test
    public void testDelete() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString("archive_rdb_url");
        final String user = settings.getString("archive_rdb_user");
        final String password = settings.getString("archive_rdb_password");
        final String engine_name = settings.getString("archive_config");
        final String schema = settings.getString("archive_rdb_schema");
        if (url == null  ||  user == null  ||  password == null  ||  engine_name == null)
        {
            System.out.println("Skipping test, no archive_rdb_url, user, password");
            return;
        }

        final RDBArchiveConfig config = new RDBArchiveConfig(url, user, password, schema);
        try
        {
            EngineConfig engine = config.findEngine(engine_name);
            assertNotNull(engine);
            config.deleteEngine(engine);
            engine = config.findEngine(engine_name);
            assertNull(engine);
        }
        finally
        {
            config.close();
        }
    }

    @Test
    public void testImport() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString("archive_rdb_url");
        final String user = settings.getString("archive_rdb_user");
        final String password = settings.getString("archive_rdb_password");
        final String engine_name = settings.getString("archive_config");
        final String schema = settings.getString("archive_rdb_schema");
        final String filename = settings.getString("tmp_file");
        if (url == null  ||  user == null  ||  password == null  ||  engine_name == null ||
            filename == null)
        {
            System.out.println("Skipping test, no archive_rdb_url, user, password, filename");
            return;
        }

        final File file = new File(filename);
        assertTrue(file.exists());
        final XMLImport importer = new XMLImport(new RDBArchiveConfig(url, user, password, schema), true, false);
        try
        {
            final InputStream stream = new FileInputStream(file);
            importer.parse(stream, engine_name, "Demo", "http://localhost:4813");
        }
        finally
        {
            importer.close();
        }
    }
}
