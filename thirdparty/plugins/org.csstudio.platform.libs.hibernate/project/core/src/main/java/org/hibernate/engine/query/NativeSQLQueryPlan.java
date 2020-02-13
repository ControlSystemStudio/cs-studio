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
package org.hibernate.engine.query;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
import org.hibernate.action.BulkOperationCleanupAction;
import org.hibernate.engine.QueryParameters;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.TypedValue;
import org.hibernate.event.EventSource;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.loader.custom.sql.SQLCustomQuery;
import org.hibernate.type.Type;
import org.hibernate.util.ArrayHelper;

/**
 * Defines a query execution plan for a native-SQL query.
 *
 * @author Steve Ebersole
 */
public class NativeSQLQueryPlan implements Serializable {
	private final String sourceQuery;

	private final SQLCustomQuery customQuery;

	private static final Logger log = LoggerFactory.getLogger(NativeSQLQueryPlan.class);

	public NativeSQLQueryPlan(
			NativeSQLQuerySpecification specification,
			SessionFactoryImplementor factory) {
		this.sourceQuery = specification.getQueryString();

		customQuery = new SQLCustomQuery(
				specification.getQueryString(),
				specification.getQueryReturns(),
				specification.getQuerySpaces(),
				factory );
	}

	public String getSourceQuery() {
		return sourceQuery;
	}

	public SQLCustomQuery getCustomQuery() {
		return customQuery;
	}

	private int[] getNamedParameterLocs(String name) throws QueryException {
		Object loc = customQuery.getNamedParameterBindPoints().get( name );
		if ( loc == null ) {
			throw new QueryException(
					"Named parameter does not appear in Query: " + name,
					customQuery.getSQL() );
		}
		if ( loc instanceof Integer ) {
			return new int[] { ((Integer) loc ).intValue() };
		}
		else {
			return ArrayHelper.toIntArray( (List) loc );
		}
	}

	/**
	 * Perform binding of all the JDBC bind parameter values based on the user-defined
	 * positional query parameters (these are the '?'-style hibernate query
	 * params) into the JDBC {@link PreparedStatement}.
	 *
	 * @param st The prepared statement to which to bind the parameter values.
	 * @param queryParameters The query parameters specified by the application.
	 * @param start JDBC paramer binds are positional, so this is the position
	 * from which to start binding.
	 * @param session The session from which the query originated.
	 *
	 * @return The number of JDBC bind positions accounted for during execution.
	 *
	 * @throws SQLException Some form of JDBC error binding the values.
	 * @throws HibernateException Generally indicates a mapping problem or type mismatch.
	 */
	private int bindPositionalParameters(
			final PreparedStatement st,
			final QueryParameters queryParameters,
			final int start,
			final SessionImplementor session) throws SQLException {
		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
		int span = 0;
		for (int i = 0; i < values.length; i++) {
			types[i].nullSafeSet( st, values[i], start + span, session );
			span += types[i].getColumnSpan( session.getFactory() );
		}
		return span;
	}

	/**
	 * Perform binding of all the JDBC bind parameter values based on the user-defined
	 * named query parameters into the JDBC {@link PreparedStatement}.
	 *
	 * @param ps The prepared statement to which to bind the parameter values.
	 * @param namedParams The named query parameters specified by the application.
	 * @param start JDBC paramer binds are positional, so this is the position
	 * from which to start binding.
	 * @param session The session from which the query originated.
	 *
	 * @return The number of JDBC bind positions accounted for during execution.
	 *
	 * @throws SQLException Some form of JDBC error binding the values.
	 * @throws HibernateException Generally indicates a mapping problem or type mismatch.
	 */
	private int bindNamedParameters(
			final PreparedStatement ps,
			final Map namedParams,
			final int start,
			final SessionImplementor session) throws SQLException {
		if ( namedParams != null ) {
			// assumes that types are all of span 1
			Iterator iter = namedParams.entrySet().iterator();
			int result = 0;
			while ( iter.hasNext() ) {
				Map.Entry e = (Map.Entry) iter.next();
				String name = (String) e.getKey();
				TypedValue typedval = (TypedValue) e.getValue();
				int[] locs = getNamedParameterLocs( name );
				for (int i = 0; i < locs.length; i++) {
					if ( log.isDebugEnabled() ) {
						log.debug( "bindNamedParameters() "
								+ typedval.getValue() + " -> " + name + " ["
								+ (locs[i] + start ) + "]" );
					}
					typedval.getType().nullSafeSet( ps, typedval.getValue(),
							locs[i] + start, session );
				}
				result += locs.length;
			}
			return result;
		}
		else {
			return 0;
		}
	}

	protected void coordinateSharedCacheCleanup(SessionImplementor session) {
		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, getCustomQuery().getQuerySpaces() );

		if ( session.isEventSource() ) {
			( ( EventSource ) session ).getActionQueue().addAction( action );
		}
		else {
			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
		}
	}

	public int performExecuteUpdate(QueryParameters queryParameters,
			SessionImplementor session) throws HibernateException {

		coordinateSharedCacheCleanup( session );

		if(queryParameters.isCallable()) {
			throw new IllegalArgumentException("callable not yet supported for native queries");
		}

		int result = 0;
		PreparedStatement ps;
		try {
			queryParameters.processFilters( this.customQuery.getSQL(),
					session );
			String sql = queryParameters.getFilteredSQL();

			ps = session.getBatcher().prepareStatement( sql );

			try {
				int col = 1;
				col += bindPositionalParameters( ps, queryParameters, col,
						session );
				col += bindNamedParameters( ps, queryParameters
						.getNamedParameters(), col, session );
				result = ps.executeUpdate();
			}
			finally {
				if ( ps != null ) {
					session.getBatcher().closeStatement( ps );
				}
			}
		}
		catch (SQLException sqle) {
			throw JDBCExceptionHelper.convert( session.getFactory()
					.getSQLExceptionConverter(), sqle,
					"could not execute native bulk manipulation query", this.sourceQuery );
		}

		return result;
	}

}
