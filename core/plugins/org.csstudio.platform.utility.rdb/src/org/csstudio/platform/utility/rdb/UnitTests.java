package org.csstudio.platform.utility.rdb;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Test;

/** Test and demo of the RDBUtil
 *  @author Kay Kasemir
 */
public class UnitTests
{
	@Test
	public void connect() throws Exception
	{
		// Connect to Oracle or MySQL RDB
		RDBUtil rdb = RDBUtil.connect(TestSetup.URL);
		System.out.println(rdb.getDialect());
		assertEquals(RDBUtil.Dialect.Oracle, rdb.getDialect());
		
		// work with the RDB
		final Connection connection = rdb.getConnection();
		PreparedStatement stmt;
		if (rdb.getDialect() == RDBUtil.Dialect.Oracle)
		    stmt = connection.prepareStatement("SELECT * FROM some_table"); //$NON-NLS-1$
		else
		    stmt = connection.prepareStatement("SELECT * FROM other_table"); //$NON-NLS-1$
		//....
		stmt.close();
		
		// Cleanup
		rdb.close();
	}
}
