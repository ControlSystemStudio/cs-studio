// $Id: PropertyContainer.java 18583 2010-01-19 18:57:19Z epbernard $
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
 */
package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import javax.persistence.Access;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.util.StringHelper;

/**
 * A helper class to keep the {@code XProperty}s of a class ordered by access type.
 *
 * @author Hardy Ferentschik
 */
class PropertyContainer {

	private static final Logger log = LoggerFactory.getLogger( AnnotationBinder.class );
	private final XClass entityAtStake;
	private final TreeMap<String, XProperty> fieldAccessMap;
	private final TreeMap<String, XProperty> propertyAccessMap;
	private final XClass xClass;
	private final AccessType explicitClassDefinedAccessType;

	PropertyContainer(XClass clazz, XClass entityAtStake) {
		this.xClass = clazz;
		this.entityAtStake = entityAtStake;
		fieldAccessMap = initProperties( AccessType.FIELD );
		propertyAccessMap = initProperties( AccessType.PROPERTY );
		explicitClassDefinedAccessType = determineClassDefinedAccessStrategy();
		checkForJpaAccess();
	}

	public XClass getEntityAtStake() {
		return entityAtStake;
	}

	public XClass getDeclaringClass() {
		return xClass;
	}

	public AccessType getExplicitAccessStrategy() {
		return explicitClassDefinedAccessType;
	}

	public boolean hasExplicitAccessStrategy() {
		return !explicitClassDefinedAccessType.equals( AccessType.DEFAULT );
	}

	public Collection<XProperty> getProperties(AccessType accessType) {
		if ( AccessType.DEFAULT == accessType || AccessType.PROPERTY == accessType ) {
			return propertyAccessMap.values();
		}
		else {
			return fieldAccessMap.values();
		}
	}

	public void assertTypesAreResolvable(AccessType access) {
		TreeMap<String, XProperty> xprops;
		if ( AccessType.PROPERTY.equals( access ) || AccessType.DEFAULT.equals( access ) ) {
			xprops = propertyAccessMap;
		}
		else {
			xprops = fieldAccessMap;
		}
		for ( XProperty property : xprops.values() ) {
			if ( !property.isTypeResolved() && !discoverTypeWithoutReflection( property ) ) {
				String msg = "Property " + StringHelper.qualify( xClass.getName(), property.getName() ) +
						" has an unbound type and no explicit target entity. Resolve this Generic usage issue" +
						" or set an explicit target attribute (eg @OneToMany(target=) or use an explicit @Type";
				throw new AnnotationException( msg );
			}
		}
	}

	private void checkForJpaAccess() {
		List<XProperty> tmpList = new ArrayList<XProperty>();
		for ( XProperty property : fieldAccessMap.values() ) {
			Access access = property.getAnnotation( Access.class );
			if ( access == null ) {
				continue;
			}

			AccessType accessType = AccessType.getAccessStrategy( access.value() );
			if ( accessType == AccessType.PROPERTY ) {
				log.warn( "Placing @Access(AccessType.PROPERTY) on a field does not have any effect." );
				continue;
			}

			tmpList.add( property );
		}
		for ( XProperty property : tmpList ) {
			fieldAccessMap.remove( property.getName() );
			propertyAccessMap.put( property.getName(), property );
		}


		tmpList.clear();
		for ( XProperty property : propertyAccessMap.values() ) {
			Access access = property.getAnnotation( Access.class );
			if ( access == null ) {
				continue;
			}

			AccessType accessType = AccessType.getAccessStrategy( access.value() );
			if ( accessType == AccessType.FIELD ) {
				log.warn( "Placing @Access(AccessType.FIELD) on a field does not have any effect." );
				continue;
			}

			tmpList.add( property );
		}
		for ( XProperty property : tmpList ) {
			propertyAccessMap.remove( property.getName() );
			fieldAccessMap.put( property.getName(), property );
		}
	}

	private TreeMap<String, XProperty> initProperties(AccessType access) {
		//order so that property are used in the same order when binding native query
		TreeMap<String, XProperty> propertiesMap = new TreeMap<String, XProperty>();
		List<XProperty> properties = xClass.getDeclaredProperties( access.getType() );
		for ( XProperty property : properties ) {
			if ( mustBeSkipped( property ) ) {
				continue;
			}
			propertiesMap.put( property.getName(), property );
		}
		return propertiesMap;
	}

	private AccessType determineClassDefinedAccessStrategy() {
		AccessType classDefinedAccessType;

		AccessType hibernateDefinedAccessType = AccessType.DEFAULT;
		AccessType jpaDefinedAccessType = AccessType.DEFAULT;

		org.hibernate.annotations.AccessType accessType = xClass.getAnnotation( org.hibernate.annotations.AccessType.class );
		if ( accessType != null ) {
			hibernateDefinedAccessType = AccessType.getAccessStrategy( accessType.value() );
		}

		Access access = xClass.getAnnotation( Access.class );
		if ( access != null ) {
			jpaDefinedAccessType = AccessType.getAccessStrategy( access.value() );
		}

		if ( hibernateDefinedAccessType != AccessType.DEFAULT
				&& jpaDefinedAccessType != AccessType.DEFAULT
				&& hibernateDefinedAccessType != jpaDefinedAccessType ) {
			throw new MappingException(
					"@AccessType and @Access specified with contradicting values. Use of @Access only is recommended. "
			);
		}

		if ( hibernateDefinedAccessType != AccessType.DEFAULT ) {
			classDefinedAccessType = hibernateDefinedAccessType;
		}
		else {
			classDefinedAccessType = jpaDefinedAccessType;
		}
		return classDefinedAccessType;
	}

	private static boolean discoverTypeWithoutReflection(XProperty p) {
		if ( p.isAnnotationPresent( OneToOne.class ) && !p.getAnnotation( OneToOne.class )
				.targetEntity()
				.equals( void.class ) ) {
			return true;
		}
		else if ( p.isAnnotationPresent( OneToMany.class ) && !p.getAnnotation( OneToMany.class )
				.targetEntity()
				.equals( void.class ) ) {
			return true;
		}
		else if ( p.isAnnotationPresent( ManyToOne.class ) && !p.getAnnotation( ManyToOne.class )
				.targetEntity()
				.equals( void.class ) ) {
			return true;
		}
		else if ( p.isAnnotationPresent( ManyToMany.class ) && !p.getAnnotation( ManyToMany.class )
				.targetEntity()
				.equals( void.class ) ) {
			return true;
		}
		else if ( p.isAnnotationPresent( org.hibernate.annotations.Any.class ) ) {
			return true;
		}
		else if ( p.isAnnotationPresent( ManyToAny.class ) ) {
			if ( !p.isCollection() && !p.isArray() ) {
				throw new AnnotationException( "@ManyToAny used on a non collection non array property: " + p.getName() );
			}
			return true;
		}
		else if ( p.isAnnotationPresent( Type.class ) ) {
			return true;
		}
		else if ( p.isAnnotationPresent( Target.class ) ) {
			return true;
		}
		return false;
	}

	private static boolean mustBeSkipped(XProperty property) {
		//TODO make those hardcoded tests more portable (through the bytecode provider?)
		return property.isAnnotationPresent( Transient.class )
				|| "net.sf.cglib.transform.impl.InterceptFieldCallback".equals( property.getType().getName() )
				|| "org.hibernate.bytecode.javassist.FieldHandler".equals( property.getType().getName() );
	}
}


