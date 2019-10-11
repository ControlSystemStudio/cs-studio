/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
package org.hibernate.dialect.lock;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.Update;
import org.hibernate.LockMode;
import org.hibernate.HibernateException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.JDBCException;
import org.hibernate.PessimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A pessimistic locking strategy where the locks are obtained through update statements.
 * <p/>
 * This strategy is valid for LockMode.PESSIMISTIC_READ
 *
 * This class is a clone of UpdateLockingStrategy.
 *
 * @since 3.5
 *
 * @author Steve Ebersole
 * @author Scott Marlow
 */
public class PessimisticReadUpdateLockingStrategy implements LockingStrategy {
	private static final Logger log = LoggerFactory.getLogger( PessimisticReadUpdateLockingStrategy.class );

	private final Lockable lockable;
	private final LockMode lockMode;
	private final String sql;

	/**
	 * Construct a locking strategy based on SQL UPDATE statements.
	 *
	 * @param lockable The metadata for the entity to be locked.
	 * @param lockMode Indictates the type of lock to be acquired.  Note that
	 * read-locks are not valid for this strategy.
	 */
	public PessimisticReadUpdateLockingStrategy(Lockable lockable, LockMode lockMode) {
		this.lockable = lockable;
		this.lockMode = lockMode;
		if ( lockMode.lessThan( LockMode.PESSIMISTIC_READ ) ) {
			throw new HibernateException( "[" + lockMode + "] not valid for update statement" );
		}
		if ( !lockable.isVersioned() ) {
			log.warn( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
			this.sql = null;
		}
		else {
			this.sql = generateLockString();
		}
	}

   /**
	 * @see org.hibernate.dialect.lock.LockingStrategy#lock
	 */
	public void lock(
      Serializable id,
      Object version,
      Object object,
      int timeout, SessionImplementor session) throws StaleObjectStateException, JDBCException {
		if ( !lockable.isVersioned() ) {
			throw new HibernateException( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
		}
		SessionFactoryImplementor factory = session.getFactory();
		try {
			PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
			try {
				lockable.getVersionType().nullSafeSet( st, version, 1, session );
				int offset = 2;

				lockable.getIdentifierType().nullSafeSet( st, id, offset, session );
				offset += lockable.getIdentifierType().getColumnSpan( factory );

				if ( lockable.isVersioned() ) {
					lockable.getVersionType().nullSafeSet( st, version, offset, session );
				}

				int affected = st.executeUpdate();
				if ( affected < 0 ) {  // todo:  should this instead check for exactly one row modified?
					factory.getStatisticsImplementor().optimisticFailure( lockable.getEntityName() );
					throw new StaleObjectStateException( lockable.getEntityName(), id );
				}

			}
			finally {
				session.getBatcher().closeStatement( st );
			}

		}
		catch ( SQLException sqle ) {
			JDBCException e = JDBCExceptionHelper.convert(
					session.getFactory().getSQLExceptionConverter(),
					sqle,
					"could not lock: " + MessageHelper.infoString( lockable, id, session.getFactory() ),
					sql
				);
			throw new PessimisticLockException("could not obtain pessimistic lock", e, object);
		}
	}

	protected String generateLockString() {
		SessionFactoryImplementor factory = lockable.getFactory();
		Update update = new Update( factory.getDialect() );
		update.setTableName( lockable.getRootTableName() );
		update.addPrimaryKeyColumns( lockable.getRootTableIdentifierColumnNames() );
		update.setVersionColumnName( lockable.getVersionColumnName() );
		update.addColumn( lockable.getVersionColumnName() );
		if ( factory.getSettings().isCommentsEnabled() ) {
			update.setComment( lockMode + " lock " + lockable.getEntityName() );
		}
		return update.toStatementString();
	}

	protected LockMode getLockMode() {
		return lockMode;
	}
}