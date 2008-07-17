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
	Oracle10g("Oracle 10g", "oracle.jdbc.driver.OracleDriver", Oracle10gDialect.class),

	/**
	 * Treiber und Dialect für Oracle 9i.
	 */
	Oracle9i("Oracle 9i", "oracle.jdbc.driver.OracleDriver", Oracle9iDialect.class),

	/**
	 * Treiber und Dialect für Oracle 8i.
	 */
	Oracle8i("Oracle 8i", "oracle.jdbc.driver.OracleDriver", Oracle8iDialect.class),

	/**
	 * Treiber und Dialect für Derby.
	 */
	Derby("Apache Derby", "org.apache.derby.jdbc.ClientDriver", DerbyDialect.class);

	private final String driverName;
	private final Class<? extends Dialect> hibernateDialect;
	private final String humanReadableName;

	DatabaseType(
			final String humanReadableName,
			final String driverName,
			final Class<? extends Dialect> hibernateDialect) {
		this.humanReadableName = humanReadableName;
		Contract.requireNotNull("driverName", driverName);
		Contract.require(driverName.length() > 0, "driverName.length() > 0");
		Contract.requireNotNull("hibernateDialect", hibernateDialect);
		
		this.driverName = driverName;
		this.hibernateDialect = hibernateDialect;
	}

	public String getDriverName() {
		return this.driverName;
	}

	public String getHumanReadableName() {
		return this.humanReadableName;
	}

	public Class<? extends Dialect> getHibernateDialect() {
		return this.hibernateDialect;
	}
}
