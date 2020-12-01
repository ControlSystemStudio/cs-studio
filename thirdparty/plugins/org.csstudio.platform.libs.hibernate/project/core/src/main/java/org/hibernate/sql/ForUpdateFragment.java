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
package org.hibernate.sql;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.hibernate.LockMode;
import org.hibernate.QueryException;
import org.hibernate.LockOptions;
import org.hibernate.dialect.Dialect;
import org.hibernate.util.StringHelper;

/**
 * @author Gavin King
 */
public class ForUpdateFragment {
	private final StringBuffer aliases = new StringBuffer();
	private boolean isNowaitEnabled;
	private final Dialect dialect;
	private LockMode lockMode;
	private LockOptions lockOptions;

	public ForUpdateFragment(Dialect dialect) {
		this.dialect = dialect;
	}

	public ForUpdateFragment(Dialect dialect, LockOptions lockOptions, Map keyColumnNames) throws QueryException {
		this( dialect );
		LockMode upgradeType = null;
		Iterator iter = lockOptions.getAliasLockIterator();
		this.lockOptions =  lockOptions;

		if ( !iter.hasNext()) {  // no tables referenced
			final LockMode lockMode = lockOptions.getLockMode();
			if ( LockMode.READ.lessThan( lockMode ) ) {
				upgradeType = lockMode;
				this.lockMode = lockMode;
			}
		}

		while ( iter.hasNext() ) {
			final Map.Entry me = ( Map.Entry ) iter.next();
			final LockMode lockMode = ( LockMode ) me.getValue();
			if ( LockMode.READ.lessThan( lockMode ) ) {
				final String tableAlias = ( String ) me.getKey();
				if ( dialect.forUpdateOfColumns() ) {
					String[] keyColumns = ( String[] ) keyColumnNames.get( tableAlias ); //use the id column alias
					if ( keyColumns == null ) {
						throw new IllegalArgumentException( "alias not found: " + tableAlias );
					}
					keyColumns = StringHelper.qualify( tableAlias, keyColumns );
					for ( int i = 0; i < keyColumns.length; i++ ) {
						addTableAlias( keyColumns[i] );
					}
				}
				else {
					addTableAlias( tableAlias );
				}
				if ( upgradeType != null && lockMode != upgradeType ) {
					throw new QueryException( "mixed LockModes" );
				}
				upgradeType = lockMode;
			}
		}

		if ( upgradeType == LockMode.UPGRADE_NOWAIT ) {
			setNowaitEnabled( true );
		}
	}

	public ForUpdateFragment addTableAlias(String alias) {
		if ( aliases.length() > 0 ) {
			aliases.append( ", " );
		}
		aliases.append( alias );
		return this;
	}

	public String toFragmentString() {
		if ( lockOptions!= null ) {
			return dialect.getForUpdateString( aliases.toString(), lockOptions );
		}
		else if ( aliases.length() == 0) {
			if ( lockMode != null ) {
				return dialect.getForUpdateString( lockMode );
			}
			return "";
		}
		// TODO:  pass lockmode
		return isNowaitEnabled ?
				dialect.getForUpdateNowaitString( aliases.toString() ) :
				dialect.getForUpdateString( aliases.toString() );
	}

	public ForUpdateFragment setNowaitEnabled(boolean nowait) {
		isNowaitEnabled = nowait;
		return this;
	}
}
