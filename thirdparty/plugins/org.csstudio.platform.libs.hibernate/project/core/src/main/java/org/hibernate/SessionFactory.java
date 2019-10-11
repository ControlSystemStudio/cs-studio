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
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.Referenceable;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.hibernate.engine.FilterDefinition;

/**
 * The main contract here is the creation of {@link Session} instances.  Usually
 * an application has a single {@link SessionFactory} instance and threads
 * servicing client requests obtain {@link Session} instances from this factory.
 * <p/>
 * The internal state of a {@link SessionFactory} is immutable.  Once it is created
 * this internal state is set.  This internal state includes all of the metadata
 * about Object/Relational Mapping.
 * <p/>
 * Implementors <strong>must</strong> be threadsafe.
 *
 * @see org.hibernate.cfg.Configuration
 *
 * @author Gavin King
 * @author Steve Ebersole
 */
public interface SessionFactory extends Referenceable, Serializable {
	/**
	 * Open a {@link Session}.
	 * <p/>
	 * JDBC {@link Connection connection(s} will be obtained from the
	 * configured {@link org.hibernate.connection.ConnectionProvider} as needed
	 * to perform requested work.
	 *
	 * @return The created session.
	 *
	 * @throws HibernateException Indicates a peroblem opening the session; pretty rare here.
	 */
	public org.hibernate.classic.Session openSession() throws HibernateException;

	/**
	 * Open a {@link Session}, utilizing the specified {@link Interceptor}.
	 * <p/>
	 * JDBC {@link Connection connection(s} will be obtained from the
	 * configured {@link org.hibernate.connection.ConnectionProvider} as needed
	 * to perform requested work.
	 *
	 * @param interceptor a session-scoped interceptor
	 *
	 * @return The created session.
	 *
	 * @throws HibernateException Indicates a peroblem opening the session; pretty rare here.
	 */
	public org.hibernate.classic.Session openSession(Interceptor interceptor) throws HibernateException;

	/**
	 * Open a {@link Session}, utilizing the specfied JDBC {@link Connection}.
	 * <p>
	 * Note that the second-level cache will be disabled if you supply a JDBC
	 * connection. Hibernate will not be able to track any statements you might
	 * have executed in the same transaction.  Consider implementing your own
	 * {@link org.hibernate.connection.ConnectionProvider} instead as a highly
	 * recommended alternative.
	 *
	 * @param connection a connection provided by the application.
	 *
	 * @return The created session.
	 */
	public org.hibernate.classic.Session openSession(Connection connection);

	/**
	 * Open a {@link Session}, utilizing the specfied JDBC {@link Connection} and
	 * specified {@link Interceptor}.
	 * <p>
	 * Note that the second-level cache will be disabled if you supply a JDBC
	 * connection. Hibernate will not be able to track any statements you might
	 * have executed in the same transaction.  Consider implementing your own
	 * {@link org.hibernate.connection.ConnectionProvider} instead as a highly
	 * recommended alternative.
	 *
	 * @param connection a connection provided by the application.
	 * @param interceptor a session-scoped interceptor
	 *
	 * @return The created session.
	 */
	public org.hibernate.classic.Session openSession(Connection connection, Interceptor interceptor);

	/**
	 * Obtains the current session.  The definition of what exactly "current"
	 * means controlled by the {@link org.hibernate.context.CurrentSessionContext} impl configured
	 * for use.
	 * <p/>
	 * Note that for backwards compatibility, if a {@link org.hibernate.context.CurrentSessionContext}
	 * is not configured but a JTA {@link org.hibernate.transaction.TransactionManagerLookup}
	 * is configured this will default to the {@link org.hibernate.context.JTASessionContext}
	 * impl.
	 *
	 * @return The current session.
	 *
	 * @throws HibernateException Indicates an issue locating a suitable current session.
	 */
	public org.hibernate.classic.Session getCurrentSession() throws HibernateException;

	/**
	 * Open a new stateless session.
	 *
	 * @return The created stateless session.
	 */
	public StatelessSession openStatelessSession();

	/**
	 * Open a new stateless session, utilizing the specified JDBC
	 * {@link Connection}.
	 *
	 * @param connection Connection provided by the application.
	 *
	 * @return The created stateless session.
	 */
	public StatelessSession openStatelessSession(Connection connection);

	/**
	 * Retrieve the {@link ClassMetadata} associated with the given entity class.
	 *
	 * @param entityClass The entity class
	 *
	 * @return The metadata associated with the given entity; may be null if no such
	 * entity was mapped.
	 *
	 * @throws HibernateException Generally null is returned instead of throwing.
	 */
	public ClassMetadata getClassMetadata(Class entityClass);

	/**
	 * Retrieve the {@link ClassMetadata} associated with the given entity class.
	 *
	 * @param entityName The entity class
	 *
	 * @return The metadata associated with the given entity; may be null if no such
	 * entity was mapped.
	 *
	 * @throws HibernateException Generally null is returned instead of throwing.
	 * @since 3.0
	 */
	public ClassMetadata getClassMetadata(String entityName);

	/**
	 * Get the {@link CollectionMetadata} associated with the named collection role.
	 *
	 * @param roleName The collection role (in form [owning-entity-name].[collection-property-name]).
	 *
	 * @return The metadata associated with the given collection; may be null if no such
	 * collection was mapped.
	 *
	 * @throws HibernateException Generally null is returned instead of throwing.
	 */
	public CollectionMetadata getCollectionMetadata(String roleName);

	/**
	 * Retrieve the {@link ClassMetadata} for all mapped entities.
	 *
	 * @return A map containing all {@link ClassMetadata} keyed by the
	 * corresponding {@link String} entity-name.
	 *
	 * @throws HibernateException Generally empty map is returned instead of throwing.
	 *
	 * @since 3.0 changed key from {@link Class} to {@link String}.
	 */
	public Map getAllClassMetadata();

	/**
	 * Get the {@link CollectionMetadata} for all mapped collections
	 *
	 * @return a map from <tt>String</tt> to <tt>CollectionMetadata</tt>
	 *
	 * @throws HibernateException Generally empty map is returned instead of throwing.
	 */
	public Map getAllCollectionMetadata();

	/**
	 * Retrieve the statistics fopr this factory.
	 *
	 * @return The statistics.
	 */
	public Statistics getStatistics();

	/**
	 * Destroy this <tt>SessionFactory</tt> and release all resources (caches,
	 * connection pools, etc).
	 * <p/>
	 * It is the responsibility of the application to ensure that there are no
	 * open {@link Session sessions} before calling this method as the impact
	 * on those {@link Session sessions} is indeterminate.
	 * <p/>
	 * No-ops if already {@link #isClosed closed}.
	 *
	 * @throws HibernateException Indicates an issue closing the factory.
	 */
	public void close() throws HibernateException;

	/**
	 * Is this factory already closed?
	 *
	 * @return True if this factory is already closed; false otherwise.
	 */
	public boolean isClosed();

	/**
	 * Obtain direct access to the underlying cache regions.
	 *
	 * @return The direct cache access API.
	 */
	public Cache getCache();

	/**
	 * Evict all entries from the second-level cache. This method occurs outside
	 * of any transaction; it performs an immediate "hard" remove, so does not respect
	 * any transaction isolation semantics of the usage strategy. Use with care.
	 *
	 * @param persistentClass The entity class for which to evict data.
	 *
	 * @throws HibernateException Generally will mean that either that
	 * 'persisttentClass' did not name a mapped entity or a problem
	 * communicating with underlying cache impl.
	 *
	 * @deprecated Use {@link Cache#evictEntityRegion(Class)} accessed through
	 * {@link #getCache()} instead.
	 */
	public void evict(Class persistentClass) throws HibernateException;

	/**
	 * Evict an entry from the second-level  cache. This method occurs outside
	 * of any transaction; it performs an immediate "hard" remove, so does not respect
	 * any transaction isolation semantics of the usage strategy. Use with care.
	 *
	 * @param persistentClass The entity class for which to evict data.
	 * @param id The entity id
	 *
	 * @throws HibernateException Generally will mean that either that
	 * 'persisttentClass' did not name a mapped entity or a problem
	 * communicating with underlying cache impl.
	 *
	 * @deprecated Use {@link Cache#containsEntity(Class, Serializable)} accessed through
	 * {@link #getCache()} instead.
	 */
	public void evict(Class persistentClass, Serializable id) throws HibernateException;

	/**
	 * Evict all entries from the second-level cache. This method occurs outside
	 * of any transaction; it performs an immediate "hard" remove, so does not respect
	 * any transaction isolation semantics of the usage strategy. Use with care.
	 *
	 * @param entityName The entity name for which to evict data.
	 *
	 * @throws HibernateException Generally will mean that either that
	 * 'persisttentClass' did not name a mapped entity or a problem
	 * communicating with underlying cache impl.
	 *
	 * @deprecated Use {@link Cache#evictEntityRegion(String)} accessed through
	 * {@link #getCache()} instead.
	 */
	public void evictEntity(String entityName) throws HibernateException;

	/**
	 * Evict an entry from the second-level  cache. This method occurs outside
	 * of any transaction; it performs an immediate "hard" remove, so does not respect
	 * any transaction isolation semantics of the usage strategy. Use with care.
	 *
	 * @param entityName The entity name for which to evict data.
	 * @param id The entity id
	 *
	 * @throws HibernateException Generally will mean that either that
	 * 'persisttentClass' did not name a mapped entity or a problem
	 * communicating with underlying cache impl.
	 *
	 * @deprecated Use {@link Cache#evictEntity(String,Serializable)} accessed through
	 * {@link #getCache()} instead.
	 */
	public void evictEntity(String entityName, Serializable id) throws HibernateException;

	/**
	 * Evict all entries from the second-level cache. This method occurs outside
	 * of any transaction; it performs an immediate "hard" remove, so does not respect
	 * any transaction isolation semantics of the usage strategy. Use with care.
	 *
	 * @param roleName The name of the collection role whose regions should be evicted
	 *
	 * @throws HibernateException Generally will mean that either that
	 * 'roleName' did not name a mapped collection or a problem
	 * communicating with underlying cache impl.
	 *
	 * @deprecated Use {@link Cache#evictCollectionRegion(String)} accessed through
	 * {@link #getCache()} instead.
	 */
	public void evictCollection(String roleName) throws HibernateException;

	/**
	 * Evict an entry from the second-level cache. This method occurs outside
	 * of any transaction; it performs an immediate "hard" remove, so does not respect
	 * any transaction isolation semantics of the usage strategy. Use with care.
	 *
	 * @param roleName The name of the collection role
	 * @param id The id of the collection owner
	 *
	 * @throws HibernateException Generally will mean that either that
	 * 'roleName' did not name a mapped collection or a problem
	 * communicating with underlying cache impl.
	 *
	 * @deprecated Use {@link Cache#evictCollection(String,Serializable)} accessed through
	 * {@link #getCache()} instead.
	 */
	public void evictCollection(String roleName, Serializable id) throws HibernateException;

	/**
	 * Evict any query result sets cached in the named query cache region.
	 *
	 * @param cacheRegion The named query cache region from which to evict.
	 *
	 * @throws HibernateException Since a not-found 'cacheRegion' simply no-ops,
	 * this should indicate a problem communicating with underlying cache impl.
	 *
	 * @deprecated Use {@link Cache#evictQueryRegion(String)} accessed through
	 * {@link #getCache()} instead.
	 */
	public void evictQueries(String cacheRegion) throws HibernateException;

	/**
	 * Evict any query result sets cached in the default query cache region.
	 *
	 * @throws HibernateException Indicate a problem communicating with
	 * underlying cache impl.
	 *
	 * @deprecated Use {@link Cache#evictQueryRegions} accessed through
	 * {@link #getCache()} instead.
	 */
	public void evictQueries() throws HibernateException;

	/**
	 * Obtain a set of the names of all filters defined on this SessionFactory.
	 *
	 * @return The set of filter names.
	 */
	public Set getDefinedFilterNames();

	/**
	 * Obtain the definition of a filter by name.
	 *
	 * @param filterName The name of the filter for which to obtain the definition.
	 * @return The filter definition.
	 * @throws HibernateException If no filter defined with the given name.
	 */
	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException;

	/**
	 * Determine if this session factory contains a fetch profile definition
	 * registered under the given name.
	 *
	 * @param name The name to check
	 * @return True if there is such a fetch profile; false otherwise.
	 */
	public boolean containsFetchProfileDefinition(String name);
}
