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
package org.hibernate.ejb.test.metadata;

import java.util.Set;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.IdentifiableType;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.ejb.metamodel.MetamodelImpl;
import org.hibernate.ejb.test.TestCase;
import org.hibernate.engine.SessionFactoryImplementor;

/**
 * @author Emmanuel Bernard
 */
public class MetadataTest extends TestCase {

	public void testBaseOfService() throws Exception {
		EntityManagerFactory emf = factory;
		assertNotNull( emf.getMetamodel() );
		final EntityType<Fridge> entityType = emf.getMetamodel().entity( Fridge.class );
		assertNotNull( entityType );
	}

	@SuppressWarnings({ "unchecked" })
	public void testBuildingMetamodelWithParameterizedCollection() {
		AnnotationConfiguration cfg = new AnnotationConfiguration( );
		configure( cfg );
		cfg.addAnnotatedClass( WithGenericCollection.class );
		cfg.buildMappings();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) cfg.buildSessionFactory();
		MetamodelImpl.buildMetamodel( cfg.getClassMappings(), sfi );
	}

	public void testLogicalManyToOne() throws Exception {
		final EntityType<JoinedManyToOneOwner> entityType = factory.getMetamodel().entity( JoinedManyToOneOwner.class );
		final SingularAttribute attr = entityType.getDeclaredSingularAttribute( "house" );
		assertEquals( Attribute.PersistentAttributeType.MANY_TO_ONE, attr.getPersistentAttributeType() );
		assertEquals( House.class, attr.getBindableJavaType() );
		final EntityType<House> houseType = factory.getMetamodel().entity( House.class );
		assertEquals( houseType.getBindableJavaType(), attr.getBindableJavaType() );
	}

	public void testEntity() throws Exception {
		final EntityType<Fridge> fridgeType = factory.getMetamodel().entity( Fridge.class );
		assertEquals( Fridge.class, fridgeType.getBindableJavaType() );
		assertEquals( Bindable.BindableType.ENTITY_TYPE, fridgeType.getBindableType() );
		SingularAttribute<Fridge,Integer> wrapped = fridgeType.getDeclaredSingularAttribute( "temperature", Integer.class );
		assertNotNull( wrapped );
		SingularAttribute<Fridge,Integer> primitive = fridgeType.getDeclaredSingularAttribute( "temperature", int.class );
		assertNotNull( primitive );
		assertNotNull( fridgeType.getDeclaredSingularAttribute( "temperature" ) );
		assertNotNull( fridgeType.getDeclaredAttribute( "temperature" ) );
		final SingularAttribute<Fridge, Long> id = fridgeType.getDeclaredId( Long.class );
		assertNotNull( id );
		assertTrue( id.isId() );
		try {
			fridgeType.getDeclaredId( java.util.Date.class );
			fail( "expecting failure" );
		}
		catch ( IllegalArgumentException ignore ) {
			// expected result
		}
		final SingularAttribute<? super Fridge, Long> id2 = fridgeType.getId( Long.class );
		assertNotNull( id2 );

		assertEquals( Fridge.class.getName(), fridgeType.getName() );
		assertEquals( Long.class, fridgeType.getIdType().getJavaType() );
		assertTrue( fridgeType.hasSingleIdAttribute() );
		assertFalse( fridgeType.hasVersionAttribute() );
		assertEquals( Type.PersistenceType.ENTITY, fridgeType.getPersistenceType() );

		assertEquals( 3, fridgeType.getDeclaredAttributes().size() );

		final EntityType<House> houseType = factory.getMetamodel().entity( House.class );
		assertTrue( houseType.hasSingleIdAttribute() );
		final SingularAttribute<House, House.Key> houseId = houseType.getDeclaredId( House.Key.class );
		assertNotNull( houseId );
		assertTrue( houseId.isId() );
		assertEquals( Attribute.PersistentAttributeType.EMBEDDED, houseId.getPersistentAttributeType() );

		final EntityType<Person> personType = factory.getMetamodel().entity( Person.class );
		assertFalse( personType.hasSingleIdAttribute() );
		final Set<SingularAttribute<? super Person,?>> ids = personType.getIdClassAttributes();
		assertNotNull( ids );
		assertEquals( 2, ids.size() );
		for (SingularAttribute<? super Person,?> localId : ids) {
			assertTrue( localId.isId() );
		}

		final EntityType<FoodItem> foodType = factory.getMetamodel().entity( FoodItem.class );
		assertTrue( foodType.hasVersionAttribute() );
		final SingularAttribute<? super FoodItem, Long> version = foodType.getVersion( Long.class );
		assertNotNull( version );
		assertTrue( version.isVersion() );
		assertEquals( 3, foodType.getDeclaredAttributes().size() );

	}

	public void testBasic() throws Exception {
		final EntityType<Fridge> entityType = factory.getMetamodel().entity( Fridge.class );
		final SingularAttribute<? super Fridge,Integer> singularAttribute = entityType.getDeclaredSingularAttribute(
				"temperature",
				Integer.class
		);
//		assertEquals( Integer.class, singularAttribute.getBindableJavaType() );
//		assertEquals( Integer.class, singularAttribute.getType().getJavaType() );
		assertEquals( int.class, singularAttribute.getBindableJavaType() );
		assertEquals( int.class, singularAttribute.getType().getJavaType() );
		assertEquals( Bindable.BindableType.SINGULAR_ATTRIBUTE, singularAttribute.getBindableType() );
		assertFalse( singularAttribute.isId() );
		assertFalse( singularAttribute.isOptional() );
		assertFalse( entityType.getDeclaredSingularAttribute( "brand", String.class ).isOptional() );
		assertEquals( Type.PersistenceType.BASIC, singularAttribute.getType().getPersistenceType() );
		final Attribute<? super Fridge, ?> attribute = entityType.getDeclaredAttribute( "temperature" );
		assertNotNull( attribute );
		assertEquals( "temperature", attribute.getName() );
		assertEquals( Fridge.class, attribute.getDeclaringType().getJavaType() );
		assertEquals( Attribute.PersistentAttributeType.BASIC, attribute.getPersistentAttributeType() );
//		assertEquals( Integer.class, attribute.getJavaType() );
		assertEquals( int.class, attribute.getJavaType() );
		assertFalse( attribute.isAssociation() );
		assertFalse( attribute.isCollection() );

		boolean found = false;
		for (Attribute<Fridge, ?> attr : entityType.getDeclaredAttributes() ) {
			if ("temperature".equals( attr.getName() ) ) {
				found = true;
				break;
			}
		}
		assertTrue( found );
	}

	public void testEmbeddable() throws Exception {
		final EntityType<House> entityType = factory.getMetamodel().entity( House.class );
		final SingularAttribute<? super House,Address> address = entityType.getDeclaredSingularAttribute(
				"address",
				Address.class
		);
		assertNotNull( address );
		assertEquals( Attribute.PersistentAttributeType.EMBEDDED, address.getPersistentAttributeType() );
		assertFalse( address.isCollection() );
		assertFalse( address.isAssociation() );
		final EmbeddableType<Address> addressType = (EmbeddableType<Address>) address.getType();
		assertEquals( Type.PersistenceType.EMBEDDABLE, addressType.getPersistenceType() );
		assertEquals( 3, addressType.getDeclaredAttributes().size() );
		assertTrue( addressType.getDeclaredSingularAttribute( "address1" ).isOptional() );
		assertFalse( addressType.getDeclaredSingularAttribute( "address2" ).isOptional() );

		final EmbeddableType<Address> directType = factory.getMetamodel().embeddable( Address.class );
		assertNotNull( directType );
		assertEquals( Type.PersistenceType.EMBEDDABLE, directType.getPersistenceType() );
	}

	public void testCollection() throws Exception {
		final EntityType<Garden> entiytype = factory.getMetamodel().entity( Garden.class );
		final Set<PluralAttribute<? super Garden, ?, ?>> attributes = entiytype.getPluralAttributes();
		assertEquals( 1, attributes.size() );
		PluralAttribute<? super Garden, ?, ?> flowers = attributes.iterator().next();
		assertTrue( flowers instanceof ListAttribute );
	}

	public void testElementCollection() throws Exception {
		final EntityType<House> entityType = factory.getMetamodel().entity( House.class );
		final SetAttribute<House,Room> rooms = entityType.getDeclaredSet( "rooms", Room.class );
		assertNotNull( rooms );
		assertTrue( rooms.isAssociation() );
		assertTrue( rooms.isCollection() );
		assertEquals( Attribute.PersistentAttributeType.ELEMENT_COLLECTION, rooms.getPersistentAttributeType() );
		assertEquals( Room.class, rooms.getBindableJavaType() );
		assertEquals( Bindable.BindableType.PLURAL_ATTRIBUTE, rooms.getBindableType() );
		assertEquals( Set.class, rooms.getJavaType() );
		assertEquals( PluralAttribute.CollectionType.SET, rooms.getCollectionType() );
		assertEquals( 3, entityType.getDeclaredPluralAttributes().size() );
		assertEquals( Type.PersistenceType.EMBEDDABLE, rooms.getElementType().getPersistenceType() );

		final MapAttribute<House,String,Room> roomsByName = entityType.getDeclaredMap(
				"roomsByName", String.class, Room.class
		);
		assertNotNull( roomsByName );
		assertEquals( String.class, roomsByName.getKeyJavaType() );
		assertEquals( Type.PersistenceType.BASIC, roomsByName.getKeyType().getPersistenceType() );
		assertEquals( PluralAttribute.CollectionType.MAP, roomsByName.getCollectionType() );

		final ListAttribute<House,Room> roomsBySize = entityType.getDeclaredList( "roomsBySize", Room.class );
		assertNotNull( roomsBySize );
		assertEquals( Type.PersistenceType.EMBEDDABLE, roomsBySize.getElementType().getPersistenceType() );
		assertEquals( PluralAttribute.CollectionType.LIST, roomsBySize.getCollectionType() );
	}

	public void testHierarchy() {
		final EntityType<Cat> cat = factory.getMetamodel().entity( Cat.class );
		assertNotNull( cat );
		assertEquals( 7, cat.getAttributes().size() );
		assertEquals( 1, cat.getDeclaredAttributes().size() );
		ensureProperMember(cat.getDeclaredAttributes());

		assertTrue( cat.hasVersionAttribute() );
		assertEquals( "version", cat.getVersion(Long.class).getName() );
		verifyDeclaredVersionNotPresent( cat );
		verifyDeclaredIdNotPresentAndIdPresent(cat);

		assertEquals( Type.PersistenceType.MAPPED_SUPERCLASS, cat.getSupertype().getPersistenceType() );
		MappedSuperclassType<Cattish> cattish = (MappedSuperclassType<Cattish>) cat.getSupertype();
		assertEquals( 6, cattish.getAttributes().size() );
		assertEquals( 1, cattish.getDeclaredAttributes().size() );
		ensureProperMember(cattish.getDeclaredAttributes());

		assertTrue( cattish.hasVersionAttribute() );
		assertEquals( "version", cattish.getVersion(Long.class).getName() );
		verifyDeclaredVersionNotPresent( cattish );
		verifyDeclaredIdNotPresentAndIdPresent(cattish);

		assertEquals( Type.PersistenceType.ENTITY, cattish.getSupertype().getPersistenceType() );
		EntityType<Feline> feline = (EntityType<Feline>) cattish.getSupertype();
		assertEquals( 5, feline.getAttributes().size() );
		assertEquals( 1, feline.getDeclaredAttributes().size() );
		ensureProperMember(feline.getDeclaredAttributes());

		assertTrue( feline.hasVersionAttribute() );
		assertEquals( "version", feline.getVersion(Long.class).getName() );
		verifyDeclaredVersionNotPresent( feline );
		verifyDeclaredIdNotPresentAndIdPresent(feline);

		assertEquals( Type.PersistenceType.MAPPED_SUPERCLASS, feline.getSupertype().getPersistenceType() );
		MappedSuperclassType<Animal> animal = (MappedSuperclassType<Animal>) feline.getSupertype();
		assertEquals( 4, animal.getAttributes().size() );
		assertEquals( 2, animal.getDeclaredAttributes().size() );
		ensureProperMember(animal.getDeclaredAttributes());

		assertTrue( animal.hasVersionAttribute() );
		assertEquals( "version", animal.getVersion(Long.class).getName() );
		verifyDeclaredVersionNotPresent( animal );
		assertEquals( "id", animal.getId(Long.class).getName() );
		final SingularAttribute<Animal, Long> id = animal.getDeclaredId( Long.class );
		assertEquals( "id", id.getName() );
		assertNotNull( id.getJavaMember() );

		assertEquals( Type.PersistenceType.MAPPED_SUPERCLASS, animal.getSupertype().getPersistenceType() );
		MappedSuperclassType<Thing> thing = (MappedSuperclassType<Thing>) animal.getSupertype();
		assertEquals( 2, thing.getAttributes().size() );
		assertEquals( 2, thing.getDeclaredAttributes().size() );
		ensureProperMember(thing.getDeclaredAttributes());
		final SingularAttribute<Thing, Double> weight = thing.getDeclaredSingularAttribute( "weight", Double.class );
		assertEquals( Double.class, weight.getJavaType() );

		assertEquals( "version", thing.getVersion(Long.class).getName() );
		final SingularAttribute<Thing, Long> version = thing.getDeclaredVersion( Long.class );
		assertEquals( "version", version.getName() );
		assertNotNull( version.getJavaMember() );
		assertNull( thing.getId( Long.class ) );

		assertNull( thing.getSupertype() );
	}

	public void testBackrefAndGenerics() throws Exception {
		final EntityType<Parent> parent = factory.getMetamodel().entity( Parent.class );
		assertNotNull( parent );
		final SetAttribute<? super Parent, ?> children = parent.getSet( "children" );
		assertNotNull( children );
		assertEquals( 1, parent.getPluralAttributes().size() );
		assertEquals( 4, parent.getAttributes().size() );
		final EntityType<Child> child = factory.getMetamodel().entity( Child.class );
		assertNotNull( child );
		assertEquals( 2, child.getAttributes().size() );
		final SingularAttribute<? super Parent, Parent.Relatives> attribute = parent.getSingularAttribute(
				"siblings", Parent.Relatives.class
		);
		final EmbeddableType<Parent.Relatives> siblings = (EmbeddableType<Parent.Relatives>) attribute.getType();
		assertNotNull(siblings);
		final SetAttribute<? super Parent.Relatives, ?> siblingsCollection = siblings.getSet( "siblings" );
		assertNotNull( siblingsCollection );
		final Type<?> collectionElement = siblingsCollection.getElementType();
		assertNotNull( collectionElement );
		assertEquals( collectionElement, child );
	}

	private void ensureProperMember(Set<?> attributes) {
		//we do not update the set so we are safe
		@SuppressWarnings( "unchecked" )
		final Set<Attribute<?, ?>> safeAttributes = ( Set<Attribute<?, ?>> ) attributes;
		for (Attribute<?,?> attribute : safeAttributes ) {
			final String name = attribute.getJavaMember().getName();
			assertNotNull( attribute.getJavaMember() );
			assertTrue( name.toLowerCase().endsWith( attribute.getName().toLowerCase() ) );
		}
	}

	private void verifyDeclaredIdNotPresentAndIdPresent(IdentifiableType<?> type) {
		assertEquals( "id", type.getId(Long.class).getName() );
		try {
			type.getDeclaredId(Long.class);
			fail("Should not have a declared id");
		}
		catch (IllegalArgumentException e) {
			//success
		}
	}

	private void verifyDeclaredVersionNotPresent(IdentifiableType<?> type) {
		try {
			type.getDeclaredVersion(Long.class);
			fail("Should not have a declared version");
		}
		catch (IllegalArgumentException e) {
			//success
		}
	}

	//todo test plural

	@Override
	public Class[] getAnnotatedClasses() {
		return new Class[]{
				Fridge.class,
				FoodItem.class,
				Person.class,
				House.class,
				Dog.class,
				Cat.class,
				Cattish.class,
				Feline.class,
				Garden.class,
				Flower.class,
				JoinedManyToOneOwner.class,
				Parent.class,
				Child.class
		};
	}

}
