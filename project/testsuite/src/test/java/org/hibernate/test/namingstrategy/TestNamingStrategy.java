package org.hibernate.test.namingstrategy;

import org.hibernate.cfg.DefaultNamingStrategy;

/**
 * @author Emmanuel Bernard
 */
public class TestNamingStrategy extends DefaultNamingStrategy {
	public String propertyToColumnName(String propertyName) {
		return "PTCN_" + propertyName;
	}

	public String columnName(String columnName) {
		return "CN_" + columnName;
	}

	public String logicalColumnName(String columnName, String
			propertyName) {
		return "LCN_" + super.logicalColumnName( columnName, propertyName );
	}
}
