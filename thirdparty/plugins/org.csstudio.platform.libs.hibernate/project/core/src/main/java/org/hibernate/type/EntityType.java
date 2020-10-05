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
package org.hibernate.type;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.Node;
import org.hibernate.AssertionFailure;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.EntityUniqueKey;
import org.hibernate.engine.ForeignKeys;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.UniqueKeyLoadable;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.tuple.ElementWrapper;
import org.hibernate.util.ReflectHelper;

/**
 * Base for types which map associations to persistent entities.
 *
 * @author Gavin King
 */
public abstract class EntityType extends AbstractType implements AssociationType {

	private final String associatedEntityName;
	protected final String uniqueKeyPropertyName;
	protected final boolean isEmbeddedInXML;
	private final boolean eager;
	private final boolean unwrapProxy;

	private transient Class returnedClass;

	/**
	 * Constructs the requested entity type mapping.
	 *
	 * @param entityName The name of the associated entity.
	 * @param uniqueKeyPropertyName The property-ref name, or null if we
	 * reference the PK of the associated entity.
	 * @param eager Is eager fetching enabled.
	 * @param isEmbeddedInXML Should values of this mapping be embedded in XML modes?
	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
	 * says to return the "implementation target" of lazy prooxies; typically only possible
	 * with lazy="no-proxy".
	 */
	protected EntityType(
			String entityName,
			String uniqueKeyPropertyName,
			boolean eager,
			boolean isEmbeddedInXML,
			boolean unwrapProxy) {
		this.associatedEntityName = entityName;
		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
		this.isEmbeddedInXML = isEmbeddedInXML;
		this.eager = eager;
		this.unwrapProxy = unwrapProxy;
	}

	/**
	 * An entity type is a type of association type
	 *
	 * @return True.
	 */
	public boolean isAssociationType() {
		return true;
	}

	/**
	 * Explicitly, an entity type is an entity type ;)
	 *
	 * @return True.
	 */
	public final boolean isEntityType() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * Generates a string representation of this type.
	 *
	 * @return string rep
	 */
	public String toString() {
		return getClass().getName() + '(' + getAssociatedEntityName() + ')';
	}

	/**
	 * For entity types, the name correlates to the associated entity name.
	 */
	public String getName() {
		return associatedEntityName;
	}

	/**
	 * Does this association foreign key reference the primary key of the other table?
	 * Otherwise, it references a property-ref.
	 *
	 * @return True if this association reference the PK of the associated entity.
	 */
	public boolean isReferenceToPrimaryKey() {
		return uniqueKeyPropertyName==null;
	}

	public String getRHSUniqueKeyPropertyName() {
		return uniqueKeyPropertyName;
	}

	public String getLHSPropertyName() {
		return null;
	}

	public String getPropertyName() {
		return null;
	}

	/**
	 * The name of the associated entity.
	 *
	 * @return The associated entity name.
	 */
	public final String getAssociatedEntityName() {
		return associatedEntityName;
	}

	/**
	 * The name of the associated entity.
	 *
	 * @param factory The session factory, for resolution.
	 * @return The associated entity name.
	 */
	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
		return getAssociatedEntityName();
	}

	/**
	 * Retrieves the {@link Joinable} defining the associated entity.
	 *
	 * @param factory The session factory.
	 * @return The associated joinable
	 * @throws MappingException Generally indicates an invalid entity name.
	 */
	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
		return ( Joinable ) factory.getEntityPersister( associatedEntityName );
	}

	/**
	 * This returns the wrong class for an entity with a proxy, or for a named
	 * entity.  Theoretically it should return the proxy class, but it doesn't.
	 * <p/>
	 * The problem here is that we do not necessarily have a ref to the associated
	 * entity persister (nor to the session factory, to look it up) which is really
	 * needed to "do the right thing" here...
	 *
	 * @return The entiyt class.
	 */
	public final Class getReturnedClass() {
		if ( returnedClass == null ) {
			returnedClass = determineAssociatedEntityClass();
		}
		return returnedClass;
	}

	private Class determineAssociatedEntityClass() {
		try {
			return ReflectHelper.classForName( getAssociatedEntityName() );
		}
		catch ( ClassNotFoundException cnfe ) {
			return java.util.Map.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
	throws HibernateException, SQLException {
		return nullSafeGet( rs, new String[] {name}, session, owner );
	}

	/**
	 * {@inheritDoc}
	 */
	public final Object nullSafeGet(
			ResultSet rs,
			String[] names,
			SessionImplementor session,
			Object owner) throws HibernateException, SQLException {
		return resolve( hydrate(rs, names, session, owner), session, owner );
	}

	/**
	 * Two entities are considered the same when their instances are the same.
	 *
	 * @param x One entity instance
	 * @param y Another entity instance
	 * @param entityMode The entity mode.
	 * @return True if x == y; false otherwise.
	 */
	public final boolean isSame(Object x, Object y, EntityMode entityMode) {
		return x == y;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compare(Object x, Object y, EntityMode entityMode) {
		return 0; //TODO: entities CAN be compared, by PK, fix this! -> only if/when we can extract the id values....
	}

	/**
	 * {@inheritDoc}
	 */
	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) {
		return value; //special case ... this is the leaf of the containment graph, even though not immutable
	}

	/**
	 * {@inheritDoc}
	 */
	public Object replace(
			Object original,
			Object target,
			SessionImplementor session,
			Object owner,
			Map copyCache) throws HibernateException {
		if ( original == null ) {
			return null;
		}
		Object cached = copyCache.get(original);
		if ( cached != null ) {
			return cached;
		}
		else {
			if ( original == target ) {
				return target;
			}
			if ( session.getContextEntityIdentifier( original ) == null  &&
					ForeignKeys.isTransient( associatedEntityName, original, Boolean.FALSE, session ) ) {
				final Object copy = session.getFactory().getEntityPersister( associatedEntityName )
						.instantiate( null, session );
				//TODO: should this be Session.instantiate(Persister, ...)?
				copyCache.put( original, copy );
				return copy;
			}
			else {
				Object id = getIdentifier( original, session );
				if ( id == null ) {
					throw new AssertionFailure("non-transient entity has a null id");
				}
				id = getIdentifierOrUniqueKeyType( session.getFactory() )
						.replace(id, null, session, owner, copyCache);
				return resolve( id, session, owner );
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) {
		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
		if ( !persister.canExtractIdOutOfEntity() ) {
			return super.getHashCode(x, entityMode);
		}

		final Serializable id;
		if (x instanceof HibernateProxy) {
			id = ( (HibernateProxy) x ).getHibernateLazyInitializer().getIdentifier();
		}
		else {
			final Class mappedClass = persister.getMappedClass( entityMode );
			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
				id = persister.getIdentifier(x, entityMode);
			}
			else {
				id = (Serializable) x;
			}
		}
		return persister.getIdentifierType().getHashCode(id, entityMode, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory) {
		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
		if ( !persister.canExtractIdOutOfEntity() ) {
			return super.isEqual(x, y, entityMode);
		}

		final Class mappedClass = persister.getMappedClass( entityMode );
		Serializable xid;
		if (x instanceof HibernateProxy) {
			xid = ( (HibernateProxy) x ).getHibernateLazyInitializer()
					.getIdentifier();
		}
		else {
			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
				xid = persister.getIdentifier(x, entityMode);
			}
			else {
				//JPA 2 case where @IdClass contains the id and not the associated entity
				xid = (Serializable) x;
			}
		}

		Serializable yid;
		if (y instanceof HibernateProxy) {
			yid = ( (HibernateProxy) y ).getHibernateLazyInitializer()
					.getIdentifier();
		}
		else {
			if ( mappedClass.isAssignableFrom( y.getClass() ) ) {
				yid = persister.getIdentifier(y, entityMode);
			}
			else {
				//JPA 2 case where @IdClass contains the id and not the associated entity
				yid = (Serializable) y;
			}
		}

		return persister.getIdentifierType()
				.isEqual(xid, yid, entityMode, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmbeddedInXML() {
		return isEmbeddedInXML;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isXMLElement() {
		return isEmbeddedInXML;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
		if ( !isEmbeddedInXML ) {
			return getIdentifierType(factory).fromXMLNode(xml, factory);
		}
		else {
			return xml;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
		if ( !isEmbeddedInXML ) {
			getIdentifierType(factory).setToXMLNode(node, value, factory);
		}
		else {
			Element elt = (Element) value;
			replaceNode( node, new ElementWrapper(elt) );
		}
	}

	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
	throws MappingException {
		if ( isReferenceToPrimaryKey() ) { //TODO: this is a bit arbitrary, expose a switch to the user?
			return "";
		}
		else {
			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
		}
	}

	/**
	 * Resolve an identifier or unique key value
	 */
	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
		if ( isNotEmbedded( session ) ) {
			return value;
		}

		if ( value == null ) {
			return null;
		}
		else {
			if ( isNull( owner, session ) ) {
				return null; //EARLY EXIT!
			}

			if ( isReferenceToPrimaryKey() ) {
				return resolveIdentifier( (Serializable) value, session );
			}
			else {
				return loadByUniqueKey( getAssociatedEntityName(), uniqueKeyPropertyName, value, session );
			}
		}
	}

	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
		return factory.getEntityPersister( associatedEntityName ).getIdentifierType();
	}

	protected final Object getIdentifier(Object value, SessionImplementor session) throws HibernateException {
		if ( isNotEmbedded(session) ) {
			return value;
		}

		if ( isReferenceToPrimaryKey() ) {
			return ForeignKeys.getEntityIdentifierIfNotUnsaved( getAssociatedEntityName(), value, session ); //tolerates nulls
		}
		else if ( value == null ) {
			return null;
		}
		else {
			EntityPersister entityPersister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
			Object propertyValue = entityPersister.getPropertyValue( value, uniqueKeyPropertyName, session.getEntityMode() );
			// We now have the value of the property-ref we reference.  However,
			// we need to dig a little deeper, as that property might also be
			// an entity type, in which case we need to resolve its identitifier
			Type type = entityPersister.getPropertyType( uniqueKeyPropertyName );
			if ( type.isEntityType() ) {
				propertyValue = ( ( EntityType ) type ).getIdentifier( propertyValue, session );
			}

			return propertyValue;
		}
	}

	protected boolean isNotEmbedded(SessionImplementor session) {
		return !isEmbeddedInXML && session.getEntityMode()==EntityMode.DOM4J;
	}

	/**
	 * Get the identifier value of an instance or proxy.
	 * <p/>
	 * Intended only for loggin purposes!!!
	 *
	 * @param object The object from which to extract the identifier.
	 * @param persister The entity persister
	 * @param entityMode The entity mode
	 * @return The extracted identifier.
	 */
	private static Serializable getIdentifier(Object object, EntityPersister persister, EntityMode entityMode) {
		if (object instanceof HibernateProxy) {
			HibernateProxy proxy = (HibernateProxy) object;
			LazyInitializer li = proxy.getHibernateLazyInitializer();
			return li.getIdentifier();
		}
		else {
			return persister.getIdentifier( object, entityMode );
		}
	}

	/**
	 * Generate a loggable representation of an instance of the value mapped by this type.
	 *
	 * @param value The instance to be logged.
	 * @param factory The session factory.
	 * @return The loggable string.
	 * @throws HibernateException Generally some form of resolution problem.
	 */
	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
		if ( value == null ) {
			return "null";
		}
		
		EntityPersister persister = factory.getEntityPersister( associatedEntityName );
		StringBuffer result = new StringBuffer().append( associatedEntityName );

		if ( persister.hasIdentifierProperty() ) {
			final EntityMode entityMode = persister.guessEntityMode( value );
			final Serializable id;
			if ( entityMode == null ) {
				if ( isEmbeddedInXML ) {
					throw new ClassCastException( value.getClass().getName() );
				}
				id = ( Serializable ) value;
			}
			else {
				id = getIdentifier( value, persister, entityMode );
			}
			
			result.append( '#' )
				.append( persister.getIdentifierType().toLoggableString( id, factory ) );
		}
		
		return result.toString();
	}

	/**
	 * Is the association modeled here defined as a 1-1 in the database (physical model)?
	 *
	 * @return True if a 1-1 in the database; false otherwise.
	 */
	public abstract boolean isOneToOne();

	/**
	 * Is the association modeled here a 1-1 according to the logical moidel?
	 *
	 * @return True if a 1-1 in the logical model; false otherwise.
	 */
	public boolean isLogicalOneToOne() {
		return isOneToOne();
	}

	/**
	 * Convenience method to locate the identifier type of the associated entity.
	 *
	 * @param factory The mappings...
	 * @return The identifier type
	 */
	Type getIdentifierType(Mapping factory) {
		return factory.getIdentifierType( getAssociatedEntityName() );
	}

	/**
	 * Convenience method to locate the identifier type of the associated entity.
	 *
	 * @param session The originating session
	 * @return The identifier type
	 */
	Type getIdentifierType(SessionImplementor session) {
		return getIdentifierType( session.getFactory() );
	}

	/**
	 * Determine the type of either (1) the identifier if we reference the
	 * associated entity's PK or (2) the unique key to which we refer (i.e.
	 * the property-ref).
	 *
	 * @param factory The mappings...
	 * @return The appropriate type.
	 * @throws MappingException Generally, if unable to resolve the associated entity name
	 * or unique key property name.
	 */
	public final Type getIdentifierOrUniqueKeyType(Mapping factory) throws MappingException {
		if ( isReferenceToPrimaryKey() ) {
			return getIdentifierType(factory);
		}
		else {
			Type type = factory.getReferencedPropertyType( getAssociatedEntityName(), uniqueKeyPropertyName );
			if ( type.isEntityType() ) {
				type = ( ( EntityType ) type).getIdentifierOrUniqueKeyType( factory );
			}
			return type;
		}
	}

	/**
	 * The name of the property on the associated entity to which our FK
	 * refers
	 *
	 * @param factory The mappings...
	 * @return The appropriate property name.
	 * @throws MappingException Generally, if unable to resolve the associated entity name
	 */
	public final String getIdentifierOrUniqueKeyPropertyName(Mapping factory)
	throws MappingException {
		if ( isReferenceToPrimaryKey() ) {
			return factory.getIdentifierPropertyName( getAssociatedEntityName() );
		}
		else {
			return uniqueKeyPropertyName;
		}
	}
	
	protected abstract boolean isNullable();

	/**
	 * Resolve an identifier via a load.
	 *
	 * @param id The entity id to resolve
	 * @param session The orginating session.
	 * @return The resolved identifier (i.e., loaded entity).
	 * @throws org.hibernate.HibernateException Indicates problems performing the load.
	 */
	protected final Object resolveIdentifier(Serializable id, SessionImplementor session) throws HibernateException {
		boolean isProxyUnwrapEnabled = unwrapProxy &&
				session.getFactory()
						.getEntityPersister( getAssociatedEntityName() )
						.isInstrumented( session.getEntityMode() );

		Object proxyOrEntity = session.internalLoad(
				getAssociatedEntityName(),
				id,
				eager,
				isNullable() && !isProxyUnwrapEnabled
		);

		if ( proxyOrEntity instanceof HibernateProxy ) {
			( ( HibernateProxy ) proxyOrEntity ).getHibernateLazyInitializer()
					.setUnwrap( isProxyUnwrapEnabled );
		}

		return proxyOrEntity;
	}

	protected boolean isNull(Object owner, SessionImplementor session) {
		return false;
	}

	/**
	 * Load an instance by a unique key that is not the primary key.
	 *
	 * @param entityName The name of the entity to load
	 * @param uniqueKeyPropertyName The name of the property defining the uniqie key.
	 * @param key The unique key property value.
	 * @param session The originating session.
	 * @return The loaded entity
	 * @throws HibernateException generally indicates problems performing the load.
	 */
	public Object loadByUniqueKey(
			String entityName, 
			String uniqueKeyPropertyName, 
			Object key, 
			SessionImplementor session) throws HibernateException {
		final SessionFactoryImplementor factory = session.getFactory();
		UniqueKeyLoadable persister = ( UniqueKeyLoadable ) factory.getEntityPersister( entityName );

		//TODO: implement caching?! proxies?!

		EntityUniqueKey euk = new EntityUniqueKey(
				entityName, 
				uniqueKeyPropertyName, 
				key, 
				getIdentifierOrUniqueKeyType( factory ),
				session.getEntityMode(), 
				session.getFactory()
		);

		final PersistenceContext persistenceContext = session.getPersistenceContext();
		Object result = persistenceContext.getEntity( euk );
		if ( result == null ) {
			result = persister.loadByUniqueKey( uniqueKeyPropertyName, key, session );
		}
		return result == null ? null : persistenceContext.proxyFor( result );
	}

}
