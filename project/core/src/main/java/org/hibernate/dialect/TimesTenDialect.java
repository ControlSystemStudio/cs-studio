/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.hibernate.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.lock.*;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.OracleJoinFragment;

/**
 * A SQL dialect for TimesTen 5.1.
 * 
 * Known limitations:
 * joined-subclass support because of no CASE support in TimesTen
 * No support for subqueries that includes aggregation
 *  - size() in HQL not supported
 *  - user queries that does subqueries with aggregation
 * No CLOB/BLOB support 
 * No cascade delete support.
 * No Calendar support
 * No support for updating primary keys.
 * 
 * @author Sherry Listgarten and Max Andersen
 */
public class TimesTenDialect extends Dialect {
	
	public TimesTenDialect() {
		super();
		registerColumnType( Types.BIT, "TINYINT" );
		registerColumnType( Types.BIGINT, "BIGINT" );
		registerColumnType( Types.SMALLINT, "SMALLINT" );
		registerColumnType( Types.TINYINT, "TINYINT" );
		registerColumnType( Types.INTEGER, "INTEGER" );
		registerColumnType( Types.CHAR, "CHAR(1)" );
		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
		registerColumnType( Types.FLOAT, "FLOAT" );
		registerColumnType( Types.DOUBLE, "DOUBLE" );
		registerColumnType( Types.DATE, "DATE" );
		registerColumnType( Types.TIME, "TIME" );
		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
		registerColumnType( Types.VARBINARY, "VARBINARY($l)" );
		registerColumnType( Types.NUMERIC, "DECIMAL($p, $s)" );
		// TimesTen has no BLOB/CLOB support, but these types may be suitable 
		// for some applications. The length is limited to 4 million bytes.
        registerColumnType( Types.BLOB, "VARBINARY(4000000)" ); 
        registerColumnType( Types.CLOB, "VARCHAR(4000000)" );
	
		getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
		registerFunction( "lower", new StandardSQLFunction("lower") );
		registerFunction( "upper", new StandardSQLFunction("upper") );
		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
		registerFunction( "concat", new StandardSQLFunction("concat", Hibernate.STRING) );
		registerFunction( "mod", new StandardSQLFunction("mod") );
		registerFunction( "to_char", new StandardSQLFunction("to_char",Hibernate.STRING) );
		registerFunction( "to_date", new StandardSQLFunction("to_date",Hibernate.TIMESTAMP) );
		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", Hibernate.TIMESTAMP, false) );
		registerFunction( "getdate", new NoArgSQLFunction("getdate", Hibernate.TIMESTAMP, false) );
		registerFunction( "nvl", new StandardSQLFunction("nvl") );

	}
	
	public boolean dropConstraints() {
            return true;
	}
	
	public boolean qualifyIndexName() {
            return false;
	}

	public boolean supportsUnique() {
		return false;
	}
    
	public boolean supportsUniqueConstraintInCreateAlterTable() {
		return false;
	}
	
    public String getAddColumnString() {
            return "add";
	}

	public boolean supportsSequences() {
		return true;
	}

	public String getSelectSequenceNextValString(String sequenceName) {
		return sequenceName + ".nextval";
	}

	public String getSequenceNextValString(String sequenceName) {
		return "select first 1 " + sequenceName + ".nextval from sys.tables";
	}

	public String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}

	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName;
	}

	public String getQuerySequencesString() {
		return "select NAME from sys.sequences";
	}

	public JoinFragment createOuterJoinFragment() {
		return new OracleJoinFragment();
	}

	public String getCrossJoinSeparator() {
		return ", ";
	}

	// new methods in dialect3
	/*public boolean supportsForUpdateNowait() {
		return false;
	}*/
	
	public String getForUpdateString() {
		return "";
	}
	
	public boolean supportsColumnCheck() {
		return false;
	}

	public boolean supportsTableCheck() {
		return false;
	}
	
	public boolean supportsLimitOffset() {
		return false;
	}

	public boolean supportsVariableLimit() {
		return false;
	}

	public boolean supportsLimit() {
		return true;
	}

	public boolean useMaxForLimit() {
		return true;
	}

	public String getLimitString(String querySelect, int offset, int limit) {
		if ( offset > 0 ) {
			throw new UnsupportedOperationException( "query result offset is not supported" );
		}
		return new StringBuffer( querySelect.length() + 8 )
				.append( querySelect )
				.insert( 6, " first " + limit )
				.toString();
	}

	public boolean supportsCurrentTimestampSelection() {
		return true;
	}

	public String getCurrentTimestampSelectString() {
		return "select first 1 sysdate from sys.tables";
	}

	public boolean isCurrentTimestampSelectStringCallable() {
		return false;
	}

	public boolean supportsTemporaryTables() {
		return true;
	}

	public String generateTemporaryTableName(String baseTableName) {
		String name = super.generateTemporaryTableName(baseTableName);
		return name.length() > 30 ? name.substring( 1, 30 ) : name;
	}

	public String getCreateTemporaryTableString() {
		return "create global temporary table";
	}

	public String getCreateTemporaryTablePostfix() {
		return "on commit delete rows";
	}

	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
		// TimesTen has no known variation of a "SELECT ... FOR UPDATE" syntax...
		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
		}
		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
		}
		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
		}
		else if ( lockMode==LockMode.OPTIMISTIC) {
			return new OptimisticLockingStrategy( lockable, lockMode);
		}
		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
		}
		else if ( lockMode.greaterThan( LockMode.READ ) ) {
			return new UpdateLockingStrategy( lockable, lockMode );
		}
		else {
			return new SelectLockingStrategy( lockable, lockMode );
		}
	}

	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public boolean supportsEmptyInList() {
		return false;
	}
}
