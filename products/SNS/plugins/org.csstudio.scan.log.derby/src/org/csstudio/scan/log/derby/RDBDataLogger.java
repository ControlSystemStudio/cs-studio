/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.data.NumberScanSample;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;

/** Base for an RDB-based sample logger
 *
 *  <p>Can write and read data for any scan.
 *
 *  <p>Derived class needs to handle connection to specific RDB.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class RDBDataLogger
{
	final protected Connection connection;

	/** Device ID cache */
	final private Map<String, Integer> devices = new HashMap<String, Integer>();

	/** Re-used statement */
	private PreparedStatement insert_sample_statement = null;

	/** Initialize
 	 *  @throws Exception on error
	 */
	public RDBDataLogger() throws Exception
	{
		connection = connect();
	}

	/** Connect to RDB
	 *  @return Connection
	 *  @throws Exception on error
	 */
    abstract protected Connection connect() throws Exception;

	/** Create a new scan in the database
	 *  @param scan_name Name of the scan
	 *  @return Scan ID, unique within the database
	 *  @throws Exception on error
	 */
	public long createScan(final String scan_name) throws Exception
    {
		final PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO scans(name) VALUES (?)",
				Statement.RETURN_GENERATED_KEYS);
		try
		{
			statement.setString(1, scan_name);
			statement.executeUpdate();
			final ResultSet result = statement.getGeneratedKeys();
			try
			{
				if (! result.next())
					throw new Exception("Missing new scan ID");
				final long id = result.getLong(1);
				return id;
			}
			finally
			{
				result.close();
			}
		}
		finally
		{
			statement.close();
		}
    }

	/** Find (or create) a device in the database
	 *  @param device_name Name of the scan
	 *  @return Device ID, unique within the database
	 *  @throws Exception on error
	 */
	public int getDevice(final String device_name) throws Exception
    {
		// Check cache
		final Integer cached_id = devices.get(device_name);
		if (cached_id != null)
			return cached_id;
		// Locate existing device
		int id = findDevice(device_name);
		if (id > 0)
		{
			devices.put(device_name, id);
			return id;
		}
		// Insert new device
		final PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO devices(name) VALUES (?)",
				Statement.RETURN_GENERATED_KEYS);
		try
		{
			statement.setString(1, device_name);
			statement.executeUpdate();
			final ResultSet result = statement.getGeneratedKeys();
			try
			{
				if (! result.next())
					throw new Exception("Missing new device ID");
				id = result.getInt(1);
				devices.put(device_name, id);
				return id;
			}
			finally
			{
				result.close();
			}
		}
		finally
		{
			statement.close();
		}
    }

	/** Find a device in the database
	 *  @param device_name Name of the scan
	 *  @return Device ID found in database; -1 if not found
	 *  @throws SQLException on error
	 */
	private int findDevice(final String device_name) throws SQLException
    {
		final PreparedStatement statement = connection.prepareStatement(
				"SELECT id FROM devices WHERE name=?");
		try
		{
			statement.setString(1, device_name);
			final ResultSet result = statement.executeQuery();
			try
			{
				if (! result.next())
					return -1;
				return result.getInt(1);
			}
			finally
			{
				result.close();
			}
		}
		finally
		{
			statement.close();
		}
    }

	/** Log a sample
	 *  @param scan_id ID of associated scan
	 *  @param sample Sample to log
	 *  @throws Exception on error
	 */
    public void log(final long scan_id, final ScanSample sample) throws Exception
    {
    	final int device_id = getDevice(sample.getDeviceName());

    	if (insert_sample_statement == null)
			insert_sample_statement = connection.prepareStatement(
					"INSERT INTO samples(scan_id, device_id, serial, timestamp, number)" +
					" VALUES (?,?,?,?,?)");
		insert_sample_statement.setLong(1, scan_id);
		insert_sample_statement.setInt(2, device_id);
		insert_sample_statement.setLong(3, sample.getSerial());
		insert_sample_statement.setTimestamp(4, new Timestamp(sample.getTimestamp().getTime()));
		if (sample instanceof NumberScanSample)
			insert_sample_statement.setDouble(5, ((NumberScanSample)sample).getNumber().doubleValue());

		final int rows = insert_sample_statement.executeUpdate();
		if (rows != 1)
				throw new Exception("Sample insert affected " + rows + " rows");
    }

    /** Obtain data for a scan
     *  @param scan_id ID of the scan
     *  @return {@link ScanData}
     *  @throws Exception on error
     */
    public ScanData getScanData(final long scan_id) throws Exception
    {
    	final Map<String, List<ScanSample>> device_logs = new HashMap<String, List<ScanSample>>();

    	// Could fetch all samples for scan ID, but
    	// organizing the retrieval by device in case
    	// that's helpful later on
    	final String[] devices = getScanDevices(scan_id);
    	for (String device : devices)
    	{
    		final List<ScanSample> samples = getScanSamples(scan_id, device);
    		device_logs.put(device, samples);
    	}

		return new ScanData(device_logs);
    }

    /** Get samples
     *  @param scan_id ID of the scan
     *  @param device_name Name of the device
     *  @return Samples for that scan
     *  @throws Exception on error
     */
    private List<ScanSample> getScanSamples(final long scan_id, final String device_name) throws Exception
    {
		final List<ScanSample> samples = new ArrayList<ScanSample>();
		final PreparedStatement statement = connection.prepareStatement(
			"SELECT serial, timestamp, number FROM samples WHERE scan_id=? AND device_id=? ORDER BY serial");
		try
		{
			statement.setLong(1, scan_id);
			statement.setInt(2, getDevice(device_name));
			final ResultSet result = statement.executeQuery();
			while (result.next())
			{
				final long serial = result.getLong(1);
				final Date timestamp = result.getTimestamp(2);
				final double number = result.getDouble(3);
				samples.add(new NumberScanSample(device_name, timestamp, serial, number));
			}
			result.close();
		}
		finally
		{
			statement.close();
		}
		return samples;
    }

	/** Obtain devices that have data for a scan
     *  @param scan_id ID of the scan
     *  @return Device names
     *  @throws SQLException on error
     */
	private String[] getScanDevices(final long scan_id) throws SQLException
    {
		final List<String> devices = new ArrayList<String>();
		final PreparedStatement statement = connection.prepareStatement(
			"SELECT DISTINCT d.name FROM samples s JOIN devices d ON s.device_id = d.id  WHERE scan_id=?");
		try
		{
			statement.setLong(1, scan_id);
			final ResultSet result = statement.executeQuery();
			while (result.next())
				devices.add(result.getString(1));
			result.close();
		}
		finally
		{
			statement.close();
		}
		return devices.toArray(new String[devices.size()]);
    }

	/** Close database.
	 *  Must be called to release resources.
	 */
	public void close()
	{
		try
		{
			if (insert_sample_statement != null)
			{
				insert_sample_statement.close();
				insert_sample_statement = null;
			}
			connection.close();
		}
		catch (Exception ex)
		{
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error closing log RDB", ex);
		}
	}
}
