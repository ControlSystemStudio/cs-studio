package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import org.csstudio.nams.common.contract.Contract;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9iDialect;

/**
 * Database-Typen für den Datenban-Zugriff für die DTOs.
 * 
 * XXX Draft of mz to be used for service factory.
 */
public enum DatabaseType {
	/**
	 * Treiber und Dialect für Oracle 10g.
	 */
	Oracle10g("oracle.jdbc.driver.OracleDriver", Oracle10gDialect.class),

	/**
	 * Treiber und Dialect für Oracle 9i.
	 */
	Oracle9i("oracle.jdbc.driver.OracleDriver", Oracle9iDialect.class),

	/**
	 * Treiber und Dialect für Oracle 8i.
	 */
	Oracle8i("oracle.jdbc.driver.OracleDriver", Oracle8iDialect.class),

	/**
	 * Treiber und Dialect für Derby.
	 */
	Derby("org.apache.derby.jdbc.ClientDriver", DerbyDialect.class);

	private final String driverName;
	private final Class<? extends Dialect> hibernateDialect;

	DatabaseType(final String driverName,
			final Class<? extends Dialect> hibernateDialect) {
		Contract.requireNotNull("driverName", driverName);
		Contract.require(driverName.length() > 0, "driverName.length() > 0");
		Contract.requireNotNull("hibernateDialect", hibernateDialect);
		
		this.driverName = driverName;
		this.hibernateDialect = hibernateDialect;
	}

	public String getDriverName() {
		return this.driverName;
	}

	public Class<? extends Dialect> getHibernateDialect() {
		return this.hibernateDialect;
	}
}
