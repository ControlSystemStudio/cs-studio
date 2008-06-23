package org.csstudio.archive.rdb.internal.test;

import static org.junit.Assert.*;

import java.sql.Statement;

import org.csstudio.archive.rdb.Severity;
import org.csstudio.archive.rdb.TestSetup;
import org.csstudio.archive.rdb.internal.SQL;
import org.csstudio.archive.rdb.internal.SeverityCache;
import org.csstudio.utility.rdb.RDBUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** Test the SeverityCache
 *  <p>
 *  Requires 'OK' severity in the RDB!
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SeverityCacheTest
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
	public void testSeverityCacheRead() throws Exception
	{
		SeverityCache severities = new SeverityCache(rdb, sql);
		// OK
		Severity severity = severities.find("OK");
		System.out.println(severity);
		assertEquals("OK", severity.toString());
		assertTrue(severity.isOK());
		
		// Locate via ID, expecting the same instance
		Severity another = severities.find(severity.getId());
		assertSame(severity, another);

		// INVALID
		severity = severities.find("INVALID");
		System.out.println(severity);
		assertEquals("INVALID", severity.toString());
		assertFalse(severity.isOK());
		assertTrue(severity.isInvalid());
		
		// Locate via ID
		another = severities.find(severity.getId());
		assertSame(severity, another);
	}

	@Test
	public void testSeverityCacheWrite() throws Exception
	{
		SeverityCache severities = new SeverityCache(rdb, sql);
		final String name = "UnknownTestSeverity";
        Severity severity = severities.findOrCreate(name);
		System.out.println(severity);
		assertEquals(name, severity.toString());
		
		// Locate again, expecting the same instance
		Severity another = severities.find(name);
		assertSame(severity, another);

		// Fix database for another test run
        Statement statement = rdb.getConnection().createStatement();
        int rows = statement.executeUpdate(
                "DELETE FROM severity WHERE severity_id=" + another.getId());
        statement.close();
        assertEquals(1, rows);
        
        rdb.getConnection().commit();
	}
}
