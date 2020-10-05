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
package org.hibernate.cache;

/**
 * A contract for transactional cache implementations which support
 * optimistic locking of items within the cache.
 * <p/>
 * The optimisitic locking capabilities are only utilized for
 * the entity cache regions.
 * <p/>
 * Unlike the methods on the {@link Cache} interface, all the methods
 * here will only ever be called from access scenarios where versioned
 * data is actually a possiblity (i.e., entity data).  Be sure to consult
 * with {@link OptimisticCacheSource#isVersioned()} to determine whether
 * versioning is actually in effect.
 *
 * @author Steve Ebersole
 */
public interface OptimisticCache extends Cache {
	/**
	 * Indicates the "source" of the cached data.  Currently this will
	 * only ever represent an {@link org.hibernate.persister.entity.EntityPersister}.
	 * <p/>
	 * Made available to the cache so that it can access certain information
	 * about versioning strategy.
	 *
	 * @param source The source.
	 */
	public void setSource(OptimisticCacheSource source);

	/**
	 * Called during {@link CacheConcurrencyStrategy#insert} processing for
	 * transactional strategies.  Indicates we have just performed an insert
	 * into the DB and now need to cache that entity's data.
	 *
	 * @param key The cache key.
	 * @param value The data to be cached.
	 * @param currentVersion The entity's version; or null if not versioned.
	 */
	public void writeInsert(Object key, Object value, Object currentVersion);

	/**
	 * Called during {@link CacheConcurrencyStrategy#update} processing for
	 * transactional strategies.  Indicates we have just performed an update
	 * against the DB and now need to cache the updated state.
	 *
	 * @param key The cache key.
	 * @param value The data to be cached.
	 * @param currentVersion The entity's current version
	 * @param previousVersion The entity's previous version (before the update);
	 * or null if not versioned.
	 */
	public void writeUpdate(Object key, Object value, Object currentVersion, Object previousVersion);

	/**
	 * Called during {@link CacheConcurrencyStrategy#put} processing for
	 * transactional strategies.  Indicates we have just loaded an entity's
	 * state from the database and need it cached.
	 *
	 * @param key The cache key.
	 * @param value The data to be cached.
	 * @param currentVersion The entity's version; or null if not versioned.
	 */
	public void writeLoad(Object key, Object value, Object currentVersion);
}
