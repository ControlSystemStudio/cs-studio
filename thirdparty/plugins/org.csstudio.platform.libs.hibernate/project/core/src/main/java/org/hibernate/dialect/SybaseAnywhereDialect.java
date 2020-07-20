/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
 * third-party contributors as indicated by either @author tags or express
 * copyright attribution statements applied by the authors.  All
 * third-party contributions are distributed under license by Red Hat Inc.
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

/**
 * SQL Dialect for Sybase Anywhere
 * extending Sybase (Enterprise) Dialect
 * (Tested on ASA 8.x)
 * @author ?
 */
public class SybaseAnywhereDialect extends AbstractTransactSQLDialect {
	/**
	 * Sybase Anywhere syntax would require a "DEFAULT" for each column specified,
	 * but I suppose Hibernate use this syntax only with tables with just 1 column
	 */
	public String getNoColumnsInsertString() {
		return "values (default)";
	}

	/**
	 * ASA does not require to drop constraint before dropping tables, so disable it.
	 * <p/>
	 * NOTE : Also, the DROP statement syntax used by Hibernate to drop constraints is 
	 * not compatible with ASA.
	 */
	public boolean dropConstraints() {
		return false;
	}

	public boolean supportsInsertSelectIdentity() {
		return false;
	}
	
}