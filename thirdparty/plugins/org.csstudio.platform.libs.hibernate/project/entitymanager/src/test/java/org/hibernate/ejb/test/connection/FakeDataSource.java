//$Id: FakeDataSource.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.connection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * @author Emmanuel Bernard
 */
public class FakeDataSource implements DataSource {
	public Connection getConnection() throws SQLException {
		throw new FakeDataSourceException( "connection" );
	}

	public Connection getConnection(String username, String password) throws SQLException {
		throw new FakeDataSourceException( "connection with password" );
	}

	public PrintWriter getLogWriter() throws SQLException {
		throw new FakeDataSourceException( "getLogWriter" );
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new FakeDataSourceException( "setLogWriter" );
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		throw new FakeDataSourceException( "setLoginTimeout" );
	}

	public int getLoginTimeout() throws SQLException {
		throw new FakeDataSourceException( "getLoginTimeout" );
	}

	public <T> T unwrap(Class<T> tClass) throws SQLException {
		throw new UnsupportedOperationException("not yet supported");
	}

	public boolean isWrapperFor(Class<?> aClass) throws SQLException {
		return false;
	}
}
