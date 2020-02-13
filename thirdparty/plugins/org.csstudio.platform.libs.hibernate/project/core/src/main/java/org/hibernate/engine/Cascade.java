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
package org.hibernate.engine;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.event.EventSource;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.util.CollectionHelper;

/**
 * Delegate responsible for, in conjunction with the various
 * {@link CascadingAction actions}, implementing cascade processing.
 *
 * @author Gavin King
 * @see CascadingAction
 */
public final class Cascade {

	/**
	 * A cascade point that occurs just after the insertion of the parent entity and
	 * just before deletion
	 */
	public static final int AFTER_INSERT_BEFORE_DELETE = 1;
	/**
	 * A cascade point that occurs just before the insertion of the parent entity and
	 * just after deletion
	 */
	public static final int BEFORE_INSERT_AFTER_DELETE = 2;
	/**
	 * A cascade point that occurs just after the insertion of the parent entity and
	 * just before deletion, inside a collection
	 */
	public static final int AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION = 3;
	/**
	 * A cascade point that occurs just after update of the parent entity
	 */
	public static final int AFTER_UPDATE = 0;
	/**
	 * A cascade point that occurs just before the session is flushed
	 */
	public static final int BEFORE_FLUSH = 0;
	/**
	 * A cascade point that occurs just after eviction of the parent entity from the
	 * session cache
	 */
	public static final int AFTER_EVICT = 0;
	/**
	 * A cascade point that occurs just after locking a transient parent entity into the
	 * session cache
	 */
	public static final int BEFORE_REFRESH = 0;
	/**
	 * A cascade point that occurs just after refreshing a parent entity
	 */
	public static final int AFTER_LOCK = 0;
	/**
	 * A cascade point that occurs just before merging from a transient parent entity into
	 * the object in the session cache
	 */
	public static final int BEFORE_MERGE = 0;


	private static final Logger log = LoggerFactory.getLogger( Cascade.class );


	private int cascadeTo;
	private EventSource eventSource;
	private CascadingAction action;

	public Cascade(final CascadingAction action, final int cascadeTo, final EventSource eventSource) {
		this.cascadeTo = cascadeTo;
		this.eventSource = eventSource;
		this.action = action;
	}

	private SessionFactoryImplementor getFactory() {
		return eventSource.getFactory();
	}

	/**
	 * Cascade an action from the parent entity instance to all its children.
	 *
	 * @param persister The parent's entity persister
	 * @param parent The parent reference.
	 * @throws HibernateException
	 */
	public void cascade(final EntityPersister persister, final Object parent)
	throws HibernateException {
		cascade( persister, parent, null );
	}

	/**
	 * Cascade an action from the parent entity instance to all its children.  This
	 * form is typicaly called from within cascade actions.
	 *
	 * @param persister The parent's entity persister
	 * @param parent The parent reference.
	 * @param anything Anything ;)   Typically some form of cascade-local cache
	 * which is specific to each CascadingAction type
	 * @throws HibernateException
	 */
	public void cascade(final EntityPersister persister, final Object parent, final Object anything)
			throws HibernateException {

		if ( persister.hasCascades() || action.requiresNoCascadeChecking() ) { // performance opt
			if ( log.isTraceEnabled() ) {
				log.trace( "processing cascade " + action + " for: " + persister.getEntityName() );
			}

			Type[] types = persister.getPropertyTypes();
			CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();
			EntityMode entityMode = eventSource.getEntityMode();
			boolean hasUninitializedLazyProperties = persister.hasUninitializedLazyProperties( parent, entityMode );
			for ( int i=0; i<types.length; i++) {
				final CascadeStyle style = cascadeStyles[i];
				final String propertyName = persister.getPropertyNames()[i];
				if ( hasUninitializedLazyProperties && persister.getPropertyLaziness()[i] && ! action.performOnLazyProperty() ) {
					//do nothing to avoid a lazy property initialization
					continue;
				}

				if ( style.doCascade( action ) ) {
					cascadeProperty(
						    parent,
					        persister.getPropertyValue( parent, i, entityMode ),
					        types[i],
					        style,
							propertyName,
					        anything,
					        false
					);
				}
				else if ( action.requiresNoCascadeChecking() ) {
					action.noCascade(
							eventSource,
							persister.getPropertyValue( parent, i, entityMode ),
							parent,
							persister,
							i
					);
				}
			}

			if ( log.isTraceEnabled() ) {
				log.trace( "done processing cascade " + action + " for: " + persister.getEntityName() );
			}
		}
	}

	/**
	 * Cascade an action to the child or children
	 */
	private void cascadeProperty(
			final Object parent,
			final Object child,
			final Type type,
			final CascadeStyle style,
			final String propertyName,
			final Object anything,
			final boolean isCascadeDeleteEnabled) throws HibernateException {

		if (child!=null) {
			if ( type.isAssociationType() ) {
				AssociationType associationType = (AssociationType) type;
				if ( cascadeAssociationNow( associationType ) ) {
					cascadeAssociation(
							parent,
							child,
							type,
							style,
							anything,
							isCascadeDeleteEnabled
						);
				}
			}
			else if ( type.isComponentType() ) {
				cascadeComponent( parent, child, (AbstractComponentType) type, propertyName, anything );
			}
		}
		else {
			// potentially we need to handle orphan deletes for one-to-ones here...
			if ( isLogicalOneToOne( type ) ) {
				// We have a physical or logical one-to-one and from previous checks we know we
				// have a null value.  See if the attribute cascade settings and action-type require
				// orphan checking
				if ( style.hasOrphanDelete() && action.deleteOrphans() ) {
					// value is orphaned if loaded state for this property shows not null
					// because it is currently null.
					final EntityEntry entry = eventSource.getPersistenceContext().getEntry( parent );
					if ( entry != null && entry.getStatus() != Status.SAVING ) {
						final Object loadedValue;
						if ( componentPathStack.isEmpty() ) {
							// association defined on entity
							loadedValue = entry.getLoadedValue( propertyName );
						}
						else {
							// association defined on component
							// 		todo : this is currently unsupported because of the fact that
							//		we do not know the loaded state of this value properly
							//		and doing so would be very difficult given how components and
							//		entities are loaded (and how 'loaded state' is put into the
							//		EntityEntry).  Solutions here are to either:
							//			1) properly account for components as a 2-phase load construct
							//			2) just assume the association was just now orphaned and
							// 				issue the orphan delete.  This would require a special
							//				set of SQL statements though since we do not know the
							//				orphaned value, something a delete with a subquery to
							// 				match the owner.
//							final EntityType entityType = (EntityType) type;
//							final String propertyPath = composePropertyPath( entityType.getPropertyName() );
							loadedValue = null;
						}
						if ( loadedValue != null ) {
							final String entityName = entry.getPersister().getEntityName();
							if ( log.isTraceEnabled() ) {
								final Serializable id = entry.getPersister().getIdentifier( loadedValue, eventSource );
								final String description = MessageHelper.infoString( entityName, id );
								log.trace( "deleting orphaned entity instance: " + description );
							}
							eventSource.delete( entityName, loadedValue, false, new HashSet() );
						}
					}
				}
			}
		}
	}

	/**
	 * Check if the association is a one to one in the logical model (either a shared-pk
	 * or unique fk).
	 *
	 * @param type The type representing the attribute metadata
	 *
	 * @return True if the attribute represents a logical one to one association
	 */
	private boolean isLogicalOneToOne(Type type) {
		return type.isEntityType() && ( (EntityType) type ).isLogicalOneToOne();
	}

	private String composePropertyPath(String propertyName) {
		if ( componentPathStack.isEmpty() ) {
			return propertyName;
		}
		else {
			StringBuffer buffer = new StringBuffer();
			Iterator itr = componentPathStack.iterator();
			while ( itr.hasNext() ) {
				buffer.append( itr.next() ).append( '.' );
			}
			buffer.append( propertyName );
			return buffer.toString();
		}
	}

	private Stack componentPathStack = new Stack();

	private boolean cascadeAssociationNow(AssociationType associationType) {
		return associationType.getForeignKeyDirection().cascadeNow(cascadeTo) &&
			( eventSource.getEntityMode()!=EntityMode.DOM4J || associationType.isEmbeddedInXML() );
	}

	private void cascadeComponent(
			final Object parent,
			final Object child,
			final AbstractComponentType componentType,
			final String componentPropertyName,
			final Object anything) {
		componentPathStack.push( componentPropertyName );
		Object[] children = componentType.getPropertyValues( child, eventSource );
		Type[] types = componentType.getSubtypes();
		for ( int i=0; i<types.length; i++ ) {
			final CascadeStyle componentPropertyStyle = componentType.getCascadeStyle(i);
			final String subPropertyName = componentType.getPropertyNames()[i];
			if ( componentPropertyStyle.doCascade(action) ) {
				cascadeProperty(
						parent,
						children[i],
						types[i],
						componentPropertyStyle,
						subPropertyName,
						anything,
						false
					);
			}
		}
		componentPathStack.pop();
	}

	private void cascadeAssociation(
			final Object parent,
			final Object child,
			final Type type,
			final CascadeStyle style,
			final Object anything,
			final boolean isCascadeDeleteEnabled) {
		if ( type.isEntityType() || type.isAnyType() ) {
			cascadeToOne( parent, child, type, style, anything, isCascadeDeleteEnabled );
		}
		else if ( type.isCollectionType() ) {
			cascadeCollection( parent, child, style, anything, (CollectionType) type );
		}
	}

	/**
	 * Cascade an action to a collection
	 */
	private void cascadeCollection(
			final Object parent,
			final Object child,
			final CascadeStyle style,
			final Object anything,
			final CollectionType type) {
		CollectionPersister persister = eventSource.getFactory()
				.getCollectionPersister( type.getRole() );
		Type elemType = persister.getElementType();

		final int oldCascadeTo = cascadeTo;
		if ( cascadeTo==AFTER_INSERT_BEFORE_DELETE) {
			cascadeTo = AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION;
		}

		//cascade to current collection elements
		if ( elemType.isEntityType() || elemType.isAnyType() || elemType.isComponentType() ) {
			cascadeCollectionElements(
				parent,
				child,
				type,
				style,
				elemType,
				anything,
				persister.isCascadeDeleteEnabled()
			);
		}

		cascadeTo = oldCascadeTo;
	}

	/**
	 * Cascade an action to a to-one association or any type
	 */
	private void cascadeToOne(
			final Object parent,
			final Object child,
			final Type type,
			final CascadeStyle style,
			final Object anything,
			final boolean isCascadeDeleteEnabled) {
		final String entityName = type.isEntityType()
				? ( (EntityType) type ).getAssociatedEntityName()
				: null;
		if ( style.reallyDoCascade(action) ) { //not really necessary, but good for consistency...
			eventSource.getPersistenceContext().addChildParent(child, parent);
			try {
				action.cascade(eventSource, child, entityName, anything, isCascadeDeleteEnabled);
			}
			finally {
				eventSource.getPersistenceContext().removeChildParent(child);
			}
		}
	}

	/**
	 * Cascade to the collection elements
	 */
	private void cascadeCollectionElements(
			final Object parent,
			final Object child,
			final CollectionType collectionType,
			final CascadeStyle style,
			final Type elemType,
			final Object anything,
			final boolean isCascadeDeleteEnabled) throws HibernateException {
		// we can't cascade to non-embedded elements
		boolean embeddedElements = eventSource.getEntityMode()!=EntityMode.DOM4J ||
				( (EntityType) collectionType.getElementType( eventSource.getFactory() ) ).isEmbeddedInXML();
		
		boolean reallyDoCascade = style.reallyDoCascade(action) && 
			embeddedElements && child!=CollectionType.UNFETCHED_COLLECTION;
		
		if ( reallyDoCascade ) {
			if ( log.isTraceEnabled() ) {
				log.trace( "cascade " + action + " for collection: " + collectionType.getRole() );
			}
			
			Iterator iter = action.getCascadableChildrenIterator(eventSource, collectionType, child);
			while ( iter.hasNext() ) {
				cascadeProperty(
						parent,
						iter.next(), 
						elemType,
						style,
						null,
						anything, 
						isCascadeDeleteEnabled 
					);
			}
			
			if ( log.isTraceEnabled() ) {
				log.trace( "done cascade " + action + " for collection: " + collectionType.getRole() );
			}
		}
		
		final boolean deleteOrphans = style.hasOrphanDelete() && 
				action.deleteOrphans() && 
				elemType.isEntityType() && 
				child instanceof PersistentCollection; //a newly instantiated collection can't have orphans
		
		if ( deleteOrphans ) { // handle orphaned entities!!
			if ( log.isTraceEnabled() ) {
				log.trace( "deleting orphans for collection: " + collectionType.getRole() );
			}
			
			// we can do the cast since orphan-delete does not apply to:
			// 1. newly instantiated collections
			// 2. arrays (we can't track orphans for detached arrays)
			final String entityName = collectionType.getAssociatedEntityName( eventSource.getFactory() );
			deleteOrphans( entityName, (PersistentCollection) child );
			
			if ( log.isTraceEnabled() ) {
				log.trace( "done deleting orphans for collection: " + collectionType.getRole() );
			}
		}
	}

	/**
	 * Delete any entities that were removed from the collection
	 */
	private void deleteOrphans(String entityName, PersistentCollection pc) throws HibernateException {
		//TODO: suck this logic into the collection!
		final Collection orphans;
		if ( pc.wasInitialized() ) {
			CollectionEntry ce = eventSource.getPersistenceContext().getCollectionEntry(pc);
			orphans = ce==null ? 
					CollectionHelper.EMPTY_COLLECTION :
					ce.getOrphans(entityName, pc);
		}
		else {
			orphans = pc.getQueuedOrphans(entityName);
		}
		
		final Iterator orphanIter = orphans.iterator();
		while ( orphanIter.hasNext() ) {
			Object orphan = orphanIter.next();
			if (orphan!=null) {
				if ( log.isTraceEnabled() ) {
					log.trace("deleting orphaned entity instance: " + entityName);
				}
				eventSource.delete( entityName, orphan, false, new HashSet() );
			}
		}
	}

}
