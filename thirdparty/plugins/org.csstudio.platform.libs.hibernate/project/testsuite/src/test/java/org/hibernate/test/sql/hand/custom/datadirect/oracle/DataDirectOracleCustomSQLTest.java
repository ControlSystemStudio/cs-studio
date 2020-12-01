//$Id$
package org.hibernate.test.sql.hand.custom.datadirect.oracle;

import junit.framework.Test;

import org.hibernate.dialect.DataDirectOracle9Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.test.sql.hand.custom.CustomStoredProcTestSupport;

/**
 * Custom SQL tests for Oracle via the DataDirect drivers.
 * 
 * @author Max Rydahl Andersen
 */
public class DataDirectOracleCustomSQLTest extends CustomStoredProcTestSupport {
	
	public DataDirectOracleCustomSQLTest(String str) {
		super(str);
	}

	public String[] getMappings() {
		return new String[] { "sql/hand/custom/oracle/Mappings.hbm.xml", "sql/hand/custom/datadirect/oracle/StoredProcedures.hbm.xml" };
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( DataDirectOracleCustomSQLTest.class );
	}

	public boolean appliesTo(Dialect dialect) {
		return ( dialect instanceof DataDirectOracle9Dialect );
	}

}

