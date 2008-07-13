package org.csstudio.nams.service.configurationaccess.localstore.declaration;

/**
 * Database-Typen für den Datenban-Zugriff für die DTOs.
 *
 * XXX Draft of mz to be used for service factory.
 */
public enum DatabaseType {
	/**
	 * Treiber und Dialect für Oracle 10g.
	 */
	Oracle10g("oracle.jdbc.driver.OracleDriver",
			"org.hibernate.dialect.Oracle10gDialect"),
	/**
	 * Treiber und Dialect für Derby.
	 */
	Derby("org.apache.derby.jdbc.ClientDriver",
			"org.hibernate.dialect.DerbyDialect");

	private final String driverName;
	private final String hibernateDialect;

	DatabaseType(final String driverName, final String hibernateDialect) {
		this.driverName = driverName;
		this.hibernateDialect = hibernateDialect;
	}

	public String getDriverName() {
		return this.driverName;
	}

	public String getHibernateDialect() {
		return this.hibernateDialect;
	}
}
