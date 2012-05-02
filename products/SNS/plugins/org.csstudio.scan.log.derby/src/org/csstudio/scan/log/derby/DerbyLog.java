/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Derby-based sample log
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DerbyLog extends RDBLog
{
	final private static String DEFAULT_DATABASE_DIRECTORY = "/tmp/scan_log_db";

	final private static String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

	private static String database_directory = DEFAULT_DATABASE_DIRECTORY;

	/** @param database_directory Directory where derby stores database files */
	public static void setDatabaseDirectory(final String database_directory)
	{
		DerbyLog.database_directory = database_directory;
	}

	/** Initialize
 	 *  @throws Exception on error
	 */
	public DerbyLog() throws Exception
	{
		if (! haveTables())
			createTables();
	}

	/** Connect to Derby RDB
	 *  @return Connection
	 *  @throws Exception on error
	 */
    @Override
    protected Connection connect() throws Exception
    {
    	// Path where Derby creates databases
    	System.setProperty("derby.system.home", database_directory);

    	// Debug options
    	// System.setProperty("derby.language.logStatementText", "true");
    	// System.setProperty("derby.language.logQueryPlan", "true");

    	// Load driver for 'embedded' Derby RDB
    	// newInstance is not needed the _first_ time
    	// this is called, but after the driver had
    	// been shut down, it will unregister then
    	// then newInstance is required to re-start
		Class.forName(DERBY_DRIVER).newInstance();

		// Connect
	    return DriverManager.getConnection("jdbc:derby:scan;create=true");
    }

	/** Check if database tables need to be created
	 *  @return <code>true</code> if tables appear to be present
	 *  @throws Exception on error
	 */
	private boolean haveTables() throws SQLException
	{
		final Statement statement = connection.createStatement();
		try
		{
			statement.executeQuery("SELECT * FROM scans WHERE id=1");
			return true;
		}
		catch (SQLException ex)
		{
			if ("42X05".equals(ex.getSQLState()))
				return false;
			throw ex;
		}
		finally
		{
			statement.close();
		}
	}

	/** Create database tables
	 *  @throws Exception on error
	 */
	private void createTables() throws Exception
	{
		final Statement statement = connection.createStatement();
		try
		{
			Logger.getLogger(getClass().getName()).info("Creating new database tables");

			final BufferedReader reader = new BufferedReader(new FileReader("dbd/scanlog.dbd"));
			StringBuilder cmd = new StringBuilder();

			String line = reader.readLine();
			while (line != null)
			{
				line = line.trim();
				// Skip comments
				if (! line.startsWith("#"))
				{
					if (line.endsWith(";"))
					{	// Found end of command
						cmd.append(line.substring(0, line.length()-1));
						if (cmd.length() > 0)
						{
							final String sql = cmd.toString();
							try
							{
								statement.execute(sql);
							}
							catch (SQLException ex)
							{
								Logger.getLogger(getClass().getName()).log(Level.INFO, "SQL failed: " + sql, ex);
							}
						}
						cmd = new StringBuilder();
					}
					else
						cmd.append(line);
				}
				line = reader.readLine();
			}
			reader.close();
		}
		finally
		{
			statement.close();
		}
	}

	/** Close database.
	 *  Must be called to release resources.
	 *  @throws Exception on error
	 */
	@Override
    public void close() throws Exception
	{
		super.close();

		if (DERBY_DRIVER.contains("EmbeddedDriver"))
		{	// Perform shutdown of embedded RDB
			try
			{
				// Strange: Shutdown is initiated via connection,
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
				// and success is reported via exception, so should not get here:
				throw new Exception("Derby did not report shutdown");
			}
			catch (SQLException ex)
			{
				if  (! "XJ015".equals(ex.getSQLState()))
					throw new Exception("Derby database shutdown failed", ex);
			}
		}
		// else: External RDB, simply disconnect
	}
}
