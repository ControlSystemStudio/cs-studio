/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
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
 */
package org.hibernate.dialect;

import java.sql.SQLException;
import java.sql.Types;
import java.io.Serializable;

import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.StaleObjectStateException;
import org.hibernate.JDBCException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.lock.*;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;
import org.hibernate.util.ReflectHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An SQL dialect compatible with HSQLDB (HyperSQL).
 * <p/>
 * Note this version supports HSQLDB version 1.8 and higher, only.
 * <p/>
 * Enhancements to version 3.5.0 GA to provide basic support for both HSQLDB 1.8.x and 2.0
 * Should work with Hibernate 3.2 and later
 *
 * @author Christoph Sturm
 * @author Phillip Baird
 * @author Fred Toussi
 */
public class HSQLDialect extends Dialect {


	private static final Logger log = LoggerFactory.getLogger( HSQLDialect.class );

	/**
	 * version is 18 for 1.8 or 20 for 2.0
	 */
	private int hsqldbVersion = 18;


	public HSQLDialect() {
		super();

		try {
			Class props = ReflectHelper.classForName( "org.hsqldb.persist.HsqlDatabaseProperties" );
			String versionString = (String) props.getDeclaredField( "THIS_VERSION" ).get( null );

			hsqldbVersion = Integer.parseInt( versionString.substring( 0, 1 ) ) * 10;
			hsqldbVersion += Integer.parseInt( versionString.substring( 2, 3 ) );
		}
		catch ( Throwable e ) {
			// must be a very old version
		}

		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.BINARY, "binary" );
		registerColumnType( Types.BIT, "bit" );
        registerColumnType( Types.BOOLEAN, "boolean" );
		registerColumnType( Types.CHAR, "char($l)" );
		registerColumnType( Types.DATE, "date" );

		registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
		registerColumnType( Types.DOUBLE, "double" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.VARBINARY, "varbinary($l)" );

		if ( hsqldbVersion < 20 ) {
			registerColumnType( Types.NUMERIC, "numeric" );
		}
		else {
			registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
		}

		//HSQL has no Blob/Clob support .... but just put these here for now!
		if ( hsqldbVersion < 20 ) {
			registerColumnType( Types.BLOB, "longvarbinary" );
			registerColumnType( Types.CLOB, "longvarchar" );
		}
		else {
			registerColumnType( Types.BLOB, "blob" );
			registerColumnType( Types.CLOB, "clob" );
		}

		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );

		registerFunction( "ascii", new StandardSQLFunction( "ascii", Hibernate.INTEGER ) );
		registerFunction( "char", new StandardSQLFunction( "char", Hibernate.CHARACTER ) );
		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
		registerFunction( "soundex", new StandardSQLFunction( "soundex", Hibernate.STRING ) );
		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
		registerFunction( "space", new StandardSQLFunction( "space", Hibernate.STRING ) );
		registerFunction( "rawtohex", new StandardSQLFunction( "rawtohex" ) );
		registerFunction( "hextoraw", new StandardSQLFunction( "hextoraw" ) );
		registerFunction( "str", new SQLFunctionTemplate( Hibernate.STRING, "cast(?1 as varchar(24))" ) );
		registerFunction( "user", new NoArgSQLFunction( "user", Hibernate.STRING ) );
		registerFunction( "database", new NoArgSQLFunction( "database", Hibernate.STRING ) );

		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", Hibernate.DATE, false ) );
		registerFunction( "current_date", new NoArgSQLFunction( "current_date", Hibernate.DATE, false ) );
		registerFunction( "curdate", new NoArgSQLFunction( "curdate", Hibernate.DATE ) );
		registerFunction(
				"current_timestamp", new NoArgSQLFunction( "current_timestamp", Hibernate.TIMESTAMP, false )
		);
		registerFunction( "now", new NoArgSQLFunction( "now", Hibernate.TIMESTAMP ) );
		registerFunction( "current_time", new NoArgSQLFunction( "current_time", Hibernate.TIME, false ) );
		registerFunction( "curtime", new NoArgSQLFunction( "curtime", Hibernate.TIME ) );
		registerFunction( "day", new StandardSQLFunction( "day", Hibernate.INTEGER ) );
		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", Hibernate.INTEGER ) );
		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", Hibernate.INTEGER ) );
		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", Hibernate.INTEGER ) );
		registerFunction( "month", new StandardSQLFunction( "month", Hibernate.INTEGER ) );
		registerFunction( "year", new StandardSQLFunction( "year", Hibernate.INTEGER ) );
		registerFunction( "week", new StandardSQLFunction( "week", Hibernate.INTEGER ) );
		registerFunction( "quarter", new StandardSQLFunction( "quarter", Hibernate.INTEGER ) );
		registerFunction( "hour", new StandardSQLFunction( "hour", Hibernate.INTEGER ) );
		registerFunction( "minute", new StandardSQLFunction( "minute", Hibernate.INTEGER ) );
		registerFunction( "second", new StandardSQLFunction( "second", Hibernate.INTEGER ) );
		registerFunction( "dayname", new StandardSQLFunction( "dayname", Hibernate.STRING ) );
		registerFunction( "monthname", new StandardSQLFunction( "monthname", Hibernate.STRING ) );

		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
		registerFunction( "sign", new StandardSQLFunction( "sign", Hibernate.INTEGER ) );

		registerFunction( "acos", new StandardSQLFunction( "acos", Hibernate.DOUBLE ) );
		registerFunction( "asin", new StandardSQLFunction( "asin", Hibernate.DOUBLE ) );
		registerFunction( "atan", new StandardSQLFunction( "atan", Hibernate.DOUBLE ) );
		registerFunction( "cos", new StandardSQLFunction( "cos", Hibernate.DOUBLE ) );
		registerFunction( "cot", new StandardSQLFunction( "cot", Hibernate.DOUBLE ) );
		registerFunction( "exp", new StandardSQLFunction( "exp", Hibernate.DOUBLE ) );
		registerFunction( "log", new StandardSQLFunction( "log", Hibernate.DOUBLE ) );
		registerFunction( "log10", new StandardSQLFunction( "log10", Hibernate.DOUBLE ) );
		registerFunction( "sin", new StandardSQLFunction( "sin", Hibernate.DOUBLE ) );
		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", Hibernate.DOUBLE ) );
		registerFunction( "tan", new StandardSQLFunction( "tan", Hibernate.DOUBLE ) );
		registerFunction( "pi", new NoArgSQLFunction( "pi", Hibernate.DOUBLE ) );
		registerFunction( "rand", new StandardSQLFunction( "rand", Hibernate.FLOAT ) );

		registerFunction( "radians", new StandardSQLFunction( "radians", Hibernate.DOUBLE ) );
		registerFunction( "degrees", new StandardSQLFunction( "degrees", Hibernate.DOUBLE ) );
		registerFunction( "roundmagic", new StandardSQLFunction( "roundmagic" ) );

		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
		registerFunction( "floor", new StandardSQLFunction( "floor" ) );

		// Multi-param dialect functions...
		registerFunction( "mod", new StandardSQLFunction( "mod", Hibernate.INTEGER ) );

		// function templates
		registerFunction( "concat", new VarArgsSQLFunction( Hibernate.STRING, "(", "||", ")" ) );

		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
	}

	public String getAddColumnString() {
		return "add column";
	}

	public boolean supportsIdentityColumns() {
		return true;
	}

	public String getIdentityColumnString() {
		return "generated by default as identity (start with 1)"; //not null is implicit
	}

	public String getIdentitySelectString() {
		return "call identity()";
	}

	public String getIdentityInsertString() {
		return hsqldbVersion < 20 ? "null" : "default";
	}

	public boolean supportsLockTimeouts() {
		return false;
	}

	public String getForUpdateString() {
		return "";
	}

	public boolean supportsUnique() {
		return false;
	}

	public boolean supportsLimit() {
		return true;
	}

	public String getLimitString(String sql, boolean hasOffset) {
		if ( hsqldbVersion < 20 ) {
			return new StringBuffer( sql.length() + 10 )
					.append( sql )
					.insert(
							sql.toLowerCase().indexOf( "select" ) + 6,
							hasOffset ? " limit ? ?" : " top ?"
					)
					.toString();
		}
		else {
			return new StringBuffer( sql.length() + 20 )
					.append( sql )
					.append( hasOffset ? " offset ? limit ?" : " limit ?" )
					.toString();
		}
	}

	public boolean bindLimitParametersFirst() {
		return hsqldbVersion < 20;
	}

	public boolean supportsIfExistsAfterTableName() {
		return true;
	}

	public boolean supportsColumnCheck() {
		return hsqldbVersion >= 20;
	}

	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsPooledSequences() {
		return true;
	}

	protected String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}

	protected String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName;
	}

	public String getSelectSequenceNextValString(String sequenceName) {
		return "next value for " + sequenceName;
	}

	public String getSequenceNextValString(String sequenceName) {
		return "call next value for " + sequenceName;
	}

	public String getQuerySequencesString() {
		// this assumes schema support, which is present in 1.8.0 and later...
		return "select sequence_name from information_schema.system_sequences";
	}

	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
		return hsqldbVersion < 20 ? EXTRACTER_18 : EXTRACTER_20;
	}

	private static ViolatedConstraintNameExtracter EXTRACTER_18 = new TemplatedViolatedConstraintNameExtracter() {

		/**
		 * Extract the name of the violated constraint from the given SQLException.
		 *
		 * @param sqle The exception that was the result of the constraint violation.
		 * @return The extracted constraint name.
		 */
		public String extractConstraintName(SQLException sqle) {
			String constraintName = null;

			int errorCode = JDBCExceptionHelper.extractErrorCode( sqle );

			if ( errorCode == -8 ) {
				constraintName = extractUsingTemplate(
						"Integrity constraint violation ", " table:", sqle.getMessage()
				);
			}
			else if ( errorCode == -9 ) {
				constraintName = extractUsingTemplate(
						"Violation of unique index: ", " in statement [", sqle.getMessage()
				);
			}
			else if ( errorCode == -104 ) {
				constraintName = extractUsingTemplate(
						"Unique constraint violation: ", " in statement [", sqle.getMessage()
				);
			}
			else if ( errorCode == -177 ) {
				constraintName = extractUsingTemplate(
						"Integrity constraint violation - no parent ", " table:",
						sqle.getMessage()
				);
			}
			return constraintName;
		}

	};

	/**
	 * HSQLDB 2.0 messages have changed
	 * messages may be localized - therefore use the common, non-locale element " table: "
	 */
	private static ViolatedConstraintNameExtracter EXTRACTER_20 = new TemplatedViolatedConstraintNameExtracter() {

		public String extractConstraintName(SQLException sqle) {
			String constraintName = null;

			int errorCode = JDBCExceptionHelper.extractErrorCode( sqle );

			if ( errorCode == -8 ) {
				constraintName = extractUsingTemplate(
						"; ", " table: ", sqle.getMessage()
				);
			}
			else if ( errorCode == -9 ) {
				constraintName = extractUsingTemplate(
						"; ", " table: ", sqle.getMessage()
				);
			}
			else if ( errorCode == -104 ) {
				constraintName = extractUsingTemplate(
						"; ", " table: ", sqle.getMessage()
				);
			}
			else if ( errorCode == -177 ) {
				constraintName = extractUsingTemplate(
						"; ", " table: ", sqle.getMessage()
				);
			}
			return constraintName;
		}
	};

	public String getSelectClauseNullString(int sqlType) {
		String literal;
		switch ( sqlType ) {
			case Types.VARCHAR:
			case Types.CHAR:
				literal = "cast(null as varchar(100))";
				break;
			case Types.DATE:
				literal = "cast(null as date)";
				break;
			case Types.TIMESTAMP:
				literal = "cast(null as timestamp)";
				break;
			case Types.TIME:
				literal = "cast(null as time)";
				break;
			default:
				literal = "cast(null as int)";
		}
		return literal;
	}

    public boolean supportsUnionAll() {
        return true;
    }

	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Hibernate uses this information for temporary tables that it uses for its own operations
	// therefore the appropriate strategy is taken with different versions of HSQLDB

	// All versions of HSQLDB support GLOBAL TEMPORARY tables where the table
	// definition is shared by all users but data is private to the session
	// HSQLDB 2.0 also supports session-based LOCAL TEMPORARY tables where
	// the definition and data is private to the session and table declaration
	// can happen in the middle of a transaction

	/**
	 * Does this dialect support temporary tables?
	 *
	 * @return True if temp tables are supported; false otherwise.
	 */
	public boolean supportsTemporaryTables() {
		return true;
	}

	/**
	 * With HSQLDB 2.0, the table name is qualified with MODULE to assist the drop
	 * statement (in-case there is a global name beginning with HT_)
	 *
	 * @param baseTableName The table name from which to base the temp table name.
	 *
	 * @return The generated temp table name.
	 */
	public String generateTemporaryTableName(String baseTableName) {
		if ( hsqldbVersion < 20 ) {
			return "HT_" + baseTableName;
		}
		else {
			return "MODULE.HT_" + baseTableName;
		}
	}

	/**
	 * Command used to create a temporary table.
	 *
	 * @return The command used to create a temporary table.
	 */
	public String getCreateTemporaryTableString() {
		if ( hsqldbVersion < 20 ) {
			return "create global temporary table";
		}
		else {
			return "declare local temporary table";
		}
	}

	/**
	 * No fragment is needed if data is not needed beyond commit, otherwise
	 * should add "on commit preserve rows"
	 *
	 * @return Any required postfix.
	 */
	public String getCreateTemporaryTablePostfix() {
		return "";
	}

	/**
	 * Command used to drop a temporary table.
	 *
	 * @return The command used to drop a temporary table.
	 */
	public String getDropTemporaryTableString() {
		return "drop table";
	}

	/**
	 * Different behaviour for GLOBAL TEMPORARY (1.8) and LOCAL TEMPORARY (2.0)
	 * <p/>
	 * Possible return values and their meanings:<ul>
	 * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
	 * in isolation.</li>
	 * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
	 * temporary table DDL in isolation.</li>
	 * <li><i>null</i> - defer to the JDBC driver response in regards to
	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
	 * </ul>
	 *
	 * @return see the result matrix above.
	 */
	public Boolean performTemporaryTableDDLInIsolation() {
		if ( hsqldbVersion < 20 ) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Do we need to drop the temporary table after use?
	 *
	 * todo - clarify usage by Hibernate
	 * Version 1.8 GLOBAL TEMPORARY table definitions persist beyond the end
	 * of the session (data is cleared). If there are not too many such tables,
	 * perhaps we can avoid dropping them and reuse the table next time?
	 *
	 * @return True if the table should be dropped.
	 */
	public boolean dropTemporaryTableAfterUse() {
		return true;
	}

	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * HSQLDB 1.8.x requires CALL CURRENT_TIMESTAMP but this should not
	 * be treated as a callable statement. It is equivalent to
	 * "select current_timestamp from dual" in some databases.
	 * HSQLDB 2.0 also supports VALUES CURRENT_TIMESTAMP
	 *
	 * @return True if the current timestamp can be retrieved; false otherwise.
	 */
	public boolean supportsCurrentTimestampSelection() {
		return true;
	}

	/**
	 * Should the value returned by {@link #getCurrentTimestampSelectString}
	 * be treated as callable.  Typically this indicates that JDBC escape
	 * syntax is being used...
	 *
	 * @return True if the {@link #getCurrentTimestampSelectString} return
	 *         is callable; false otherwise.
	 */
	public boolean isCurrentTimestampSelectStringCallable() {
		return false;
	}

	/**
	 * Retrieve the command used to retrieve the current timestamp from the
	 * database.
	 *
	 * @return The command.
	 */
	public String getCurrentTimestampSelectString() {
		return "call current_timestamp";
	}

	/**
	 * The name of the database-specific SQL function for retrieving the
	 * current timestamp.
	 *
	 * @return The function name.
	 */
	public String getCurrentTimestampSQLFunctionName() {
		// the standard SQL function name is current_timestamp...
		return "current_timestamp";
	}

	/**
	 * For HSQLDB 2.0, this is a copy of the base class implementation.
	 * For HSQLDB 1.8, only READ_UNCOMMITTED is supported.
	 *
	 * @param lockable The persister for the entity to be locked.
	 * @param lockMode The type of lock to be acquired.
	 *
	 * @return The appropriate locking strategy.
	 *
	 * @since 3.2
	 */
	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
		}
		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
			return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
		}
		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
			return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
		}
		else if ( lockMode == LockMode.OPTIMISTIC ) {
			return new OptimisticLockingStrategy( lockable, lockMode );
		}
		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
		}

		if ( hsqldbVersion < 20 ) {
			return new ReadUncommittedLockingStrategy( lockable, lockMode );
		}
		else {
			return new SelectLockingStrategy( lockable, lockMode );
		}
	}

	public static class ReadUncommittedLockingStrategy extends SelectLockingStrategy {
		public ReadUncommittedLockingStrategy(Lockable lockable, LockMode lockMode) {
			super( lockable, lockMode );
		}

		public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session)
				throws StaleObjectStateException, JDBCException {
			if ( getLockMode().greaterThan( LockMode.READ ) ) {
				log.warn( "HSQLDB supports only READ_UNCOMMITTED isolation" );
			}
			super.lock( id, version, object, timeout, session );
		}
	}

	public boolean supportsCommentOn() {
		return true;
	}

	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public boolean supportsEmptyInList() {
		return false;
	}

	/**
	 * todo - needs usage clarification
	 *
	 * If the SELECT statement is always part of a UNION, then the type of
	 * parameter is resolved by v. 2.0, but not v. 1.8 (assuming the other
	 * SELECT in the UNION has a column reference in the same position and
	 * can be type-resolved).
	 *
	 * On the other hand if the SELECT statement is isolated, all versions of
	 * HSQLDB require casting for "select ? from .." to work.
	 *
	 * @return True if select clause parameter must be cast()ed
	 *
	 * @since 3.2
	 */
	public boolean requiresCastingOfParametersInSelectClause() {
		return true;
	}

	/**
	 * For the underlying database, is READ_COMMITTED isolation implemented by
	 * forcing readers to wait for write locks to be released?
	 *
	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
	 */
	public boolean doesReadCommittedCauseWritersToBlockReaders() {
		return hsqldbVersion >= 20;
	}

	/**
	 * For the underlying database, is REPEATABLE_READ isolation implemented by
	 * forcing writers to wait for read locks to be released?
	 *
	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
	 */
	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
		return hsqldbVersion >= 20;
	}


	public boolean supportsLobValueChangePropogation() {
		return false;
	}

    public String toBooleanValueString(boolean bool) {
        return bool ? "true" : "false";
    }

	public boolean supportsTupleDistinctCounts() {
		return false;
	}
}
