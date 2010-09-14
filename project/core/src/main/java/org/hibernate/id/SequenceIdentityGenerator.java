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
package org.hibernate.id;

import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.id.insert.AbstractReturningDelegate;
import org.hibernate.id.insert.IdentifierGeneratingInsert;
import org.hibernate.dialect.Dialect;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.sql.Insert;
import org.hibernate.type.Type;
import org.hibernate.engine.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A generator which combines sequence generation with immediate retrieval
 * through JDBC3 {@link java.sql.Connection#prepareStatement(String, String[]) getGeneratedKeys}.
 * In this respect it works much like ANSI-SQL IDENTITY generation.
 * <p/>
 * This generator only known to work with newer Oracle drivers compiled for
 * JDK 1.4 (JDBC3).
 * <p/>
 * Note: Due to a bug in Oracle drivers, sql comments on these insert statements
 * are completely disabled.
 *
 * @author Steve Ebersole
 */
public class SequenceIdentityGenerator extends SequenceGenerator
		implements PostInsertIdentifierGenerator {

	private static final Logger log = LoggerFactory.getLogger( SequenceIdentityGenerator.class );

	public Serializable generate(SessionImplementor s, Object obj) {
		return IdentifierGeneratorHelper.POST_INSERT_INDICATOR;
	}

	public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(
			PostInsertIdentityPersister persister,
	        Dialect dialect,
	        boolean isGetGeneratedKeysEnabled) throws HibernateException {
		return new Delegate( persister, dialect, getSequenceName() );
	}

	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
		super.configure( type, params, dialect );
	}

	public static class Delegate extends AbstractReturningDelegate {
		private final Dialect dialect;
		private final String sequenceNextValFragment;
		private final String[] keyColumns;

		public Delegate(PostInsertIdentityPersister persister, Dialect dialect, String sequenceName) {
			super( persister );
			this.dialect = dialect;
			this.sequenceNextValFragment = dialect.getSelectSequenceNextValString( sequenceName );
			this.keyColumns = getPersister().getRootTableKeyColumnNames();
			if ( keyColumns.length > 1 ) {
				throw new HibernateException( "sequence-identity generator cannot be used with with multi-column keys" );
			}
		}

		public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
			NoCommentsInsert insert = new NoCommentsInsert( dialect );
			insert.addColumn( getPersister().getRootTableKeyColumnNames()[0], sequenceNextValFragment );
			return insert;
		}

		protected PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException {
			return session.getBatcher().prepareStatement( insertSQL, keyColumns );
		}

		protected Serializable executeAndExtract(PreparedStatement insert) throws SQLException {
			insert.executeUpdate();
			return IdentifierGeneratorHelper.getGeneratedIdentity(
					insert.getGeneratedKeys(),
			        getPersister().getIdentifierType()
			);
		}
	}

	public static class NoCommentsInsert extends IdentifierGeneratingInsert {
		public NoCommentsInsert(Dialect dialect) {
			super( dialect );
		}

		public Insert setComment(String comment) {
			// don't allow comments on these insert statements as comments totally
			// blow up the Oracle getGeneratedKeys "support" :(
			log.info( "disallowing insert statement comment for select-identity due to Oracle driver bug" );
			return this;
		}
	}
}
