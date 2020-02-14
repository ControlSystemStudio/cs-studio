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
package org.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.type.VersionType;

/**
 * Represents a replication strategy.
 *
 * @see Session#replicate(Object, ReplicationMode)
 * @author Gavin King
 */
public abstract class ReplicationMode implements Serializable {
	private final String name;
	private static final Map INSTANCES = new HashMap();

	public ReplicationMode(String name) {
		this.name=name;
	}
	public String toString() {
		return name;
	}
	public abstract boolean shouldOverwriteCurrentVersion(Object entity, Object currentVersion, Object newVersion, VersionType versionType);
	/**
	 * Throw an exception when a row already exists.
	 */
	public static final ReplicationMode EXCEPTION = new ReplicationMode("EXCEPTION") {
		public boolean shouldOverwriteCurrentVersion(Object entity, Object currentVersion, Object newVersion, VersionType versionType) {
			throw new AssertionFailure("should not be called");
		}
	};
	/**
	 * Ignore replicated entities when a row already exists.
	 */
	public static final ReplicationMode IGNORE = new ReplicationMode("IGNORE") {
		public boolean shouldOverwriteCurrentVersion(Object entity, Object currentVersion, Object newVersion, VersionType versionType) {
			return false;
		}
	};
	/**
	 * Overwrite existing rows when a row already exists.
	 */
	public static final ReplicationMode OVERWRITE = new ReplicationMode("OVERWRITE") {
		public boolean shouldOverwriteCurrentVersion(Object entity, Object currentVersion, Object newVersion, VersionType versionType) {
			return true;
		}
	};
	/**
	 * When a row already exists, choose the latest version.
	 */
	public static final ReplicationMode LATEST_VERSION = new ReplicationMode("LATEST_VERSION") {
		public boolean shouldOverwriteCurrentVersion(Object entity, Object currentVersion, Object newVersion, VersionType versionType) {
			if (versionType==null) return true; //always overwrite nonversioned data
			return versionType.getComparator().compare(currentVersion, newVersion) <= 0;
		}
	};

	static {
		INSTANCES.put( LATEST_VERSION.name, LATEST_VERSION );
		INSTANCES.put( IGNORE.name, IGNORE );
		INSTANCES.put( OVERWRITE.name, OVERWRITE );
		INSTANCES.put( EXCEPTION.name, EXCEPTION );
	}

	private Object readResolve() {
		return INSTANCES.get(name);
	}

}






