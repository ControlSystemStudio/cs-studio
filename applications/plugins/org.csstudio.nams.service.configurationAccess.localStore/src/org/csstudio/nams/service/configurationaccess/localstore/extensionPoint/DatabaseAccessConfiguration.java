
package org.csstudio.nams.service.configurationaccess.localstore.extensionPoint;

import org.hibernate.dialect.Dialect;

/**
 * This is the configuration source to configure the database access of this
 * plug-in.
 */
public interface DatabaseAccessConfiguration {

	/**
	 * 
	 * @return The Driver class which must be accessible in current class path
	 *         configuration.
	 */
	public Class<? extends java.sql.Driver> getConnectionDriverClass();

	public String getDatabaseJDBCConnectionURL();

	public String getDatabaseUserName();

	public char[] getDatabaseUserPassword();

	/**
	 * 
	 * 
	 * <p>
	 * Note: This dependency to Hibernate is required, cause there are different
	 * dialects for the same database type by that an automatically selection is
	 * impossible or may effect performance requirements.
	 * </p>
	 * 
	 * @return The Dialect class which must be accessible in current class path
	 *         configuration and well known by Hibernate 3.
	 */
	public Class<? extends Dialect> getHibernateDialectOfDatabase();
}
