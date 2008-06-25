package org.csstudio.archive.rdb.internal.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.sql.Statement;

import org.csstudio.archive.rdb.Status;
import org.csstudio.archive.rdb.TestSetup;
import org.csstudio.archive.rdb.internal.SQL;
import org.csstudio.archive.rdb.internal.StatusCache;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** Test the StatusCache
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StatusCacheTest
{
	private static RDBUtil rdb;
	private static SQL sql;

	@BeforeClass
	public static void connect() throws Exception
	{
		rdb = RDBUtil.connect(TestSetup.URL);
		sql = new SQL(rdb.getDialect());
	}
	
	@AfterClass
	public static void disconnect()
	{
		rdb.close();
	}

	@Test
	public void testStatusCacheRead() throws Exception
	{
		StatusCache stati = new StatusCache(rdb, sql);
		// OK
		Status status = stati.find(1);
		System.out.println(status);
		assertEquals(1, status.getId());
		
		// Locate via ID, expecting the same instance
		Status another = stati.find(status.getId());
		assertSame(status, another);
	}

	@Test
	public void testStatusCacheWrite() throws Exception
	{
		StatusCache stati = new StatusCache(rdb, sql);
		String name = "UnknownTestStatus";
        Status status = stati.findOrCreate(name);
		System.out.println(status);
		assertEquals(name, status.getName());
		
		// Locate again, expecting the same instance
		Status another = stati.find(status.getId());
		assertSame(status, another);
		
		// Fix database for another test run
		Statement statement = rdb.getConnection().createStatement();
		int rows = statement.executeUpdate(
		        "DELETE FROM status WHERE status_id=" + another.getId());
		statement.close();
		assertEquals(1, rows);
		
		rdb.getConnection().commit();
	}
}
