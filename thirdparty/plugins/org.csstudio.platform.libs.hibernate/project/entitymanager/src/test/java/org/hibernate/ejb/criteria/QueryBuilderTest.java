/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2009, Red Hat Inc. or third-party contributors as
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
package org.hibernate.ejb.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.hibernate.ejb.criteria.predicate.ComparisonPredicate;
import org.hibernate.ejb.metamodel.Address;
import org.hibernate.ejb.metamodel.Alias;
import org.hibernate.ejb.metamodel.Country;
import org.hibernate.ejb.metamodel.CreditCard;
import org.hibernate.ejb.metamodel.Customer;
import org.hibernate.ejb.metamodel.Info;
import org.hibernate.ejb.metamodel.LineItem;
import org.hibernate.ejb.metamodel.MetamodelImpl;
import org.hibernate.ejb.metamodel.Order;
import org.hibernate.ejb.metamodel.Phone;
import org.hibernate.ejb.metamodel.Product;
import org.hibernate.ejb.metamodel.ShelfLife;
import org.hibernate.ejb.metamodel.Spouse;
import org.hibernate.ejb.test.TestCase;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class QueryBuilderTest extends TestCase {
	@Override
	public Class[] getAnnotatedClasses() {
		return new Class[] {
				Address.class,
				Alias.class,
				Country.class,
				CreditCard.class,
				Customer.class,
				Info.class,
				LineItem.class,
				Order.class,
				Phone.class,
				Product.class,
				ShelfLife.class,
				Spouse.class
		};
	}

	public void testEqualityComparisonLiteralConversion() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();

		CriteriaBuilderImpl cb = (CriteriaBuilderImpl) em.getCriteriaBuilder();
		MetamodelImpl mm = (MetamodelImpl) em.getMetamodel();

        CriteriaQuery<Integer> cquery = cb.createQuery( Integer.class );
		Root<Product> product = cquery.from( Product.class );
		EntityType<Product> Product_ = mm.entity( Product.class );

		cquery.select(
				cb.toInteger(
						product.get(
								Product_.getSingularAttribute("quantity", Integer.class))
				)
		);

		ComparisonPredicate predicate = (ComparisonPredicate) cb.equal(
				product.get( Product_.getSingularAttribute( "partNumber", Long.class ) ),
				373767373
		);
		assertEquals( Long.class, predicate.getRightHandOperand().getJavaType() );
		cquery.where( predicate );
		em.createQuery( cquery ).getResultList();

		predicate = (ComparisonPredicate) cb.ge(
				cb.length( product.get( Product_.getSingularAttribute( "name", String.class ) ) ),
				4L
		);
		assertEquals( Integer.class, predicate.getRightHandOperand().getJavaType() );
		cquery.where( predicate );
		em.createQuery( cquery ).getResultList();

		em.getTransaction().commit();
		em.close();
	}

	public void testEqualityComparisonEntityConversion() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Address address = new Address( "Street Id", "Fake Street", "Fake City", "Fake State", "Fake Zip" );
		Phone phone1 = new Phone( "1", "555", "0001", address );
		Phone phone2 = new Phone( "2", "555", "0002", address );
		Phone phone3 = new Phone( "3", "555", "0003", address );
		Phone phone4 = new Phone( "4", "555", "0004" );

		Collection<Phone> phones = new ArrayList<Phone>( 3 );
		phones.add( phone1 );
		phones.add( phone2 );
		phones.add( phone3 );

		address.setPhones( phones );
		em.persist( address );
		em.persist( phone4 );

		em.getTransaction().commit();


		em.getTransaction().begin();

		CriteriaBuilderImpl cb = (CriteriaBuilderImpl) em.getCriteriaBuilder();
		MetamodelImpl mm = (MetamodelImpl) em.getMetamodel();
		EntityType<Phone> Phone_ = mm.entity( Phone.class );

		CriteriaQuery<Phone> cquery = cb.createQuery( Phone.class );
		Root<Phone> phone = cquery.from( Phone.class );
		ComparisonPredicate predicate = (ComparisonPredicate) cb.equal(
				phone.get( Phone_.getSingularAttribute( "address", Address.class ) ),
				address
		);
		cquery.where( predicate );
		List<Phone> results = em.createQuery( cquery ).getResultList();

		assertEquals( 3, results.size() );
		em.getTransaction().commit();
		em.close();
	}

	public void testTypeConversion() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		CriteriaBuilderImpl cb = (CriteriaBuilderImpl) em.getCriteriaBuilder();
		MetamodelImpl mm = (MetamodelImpl) em.getMetamodel();
		EntityType<Product> Product_ = mm.entity( Product.class );

		// toFloat
        CriteriaQuery<Float> floatQuery = cb.createQuery( Float.class );
		Root<Product> product = floatQuery.from( Product.class );
		floatQuery.select(
				cb.toFloat(
						product.get(Product_.getSingularAttribute("quantity", Integer.class))
				)
		);
		em.createQuery( floatQuery ).getResultList();

		// toDouble
        CriteriaQuery<Double> doubleQuery = cb.createQuery(Double.class);
		product = doubleQuery.from( Product.class );
		doubleQuery.select(
				cb.toDouble(
						product.get(Product_.getSingularAttribute("quantity", Integer.class))
				)
		);
		em.createQuery( doubleQuery ).getResultList();

		em.getTransaction().commit();
		em.close();
	}

	public void testConstructor() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		CriteriaBuilderImpl cb = (CriteriaBuilderImpl) em.getCriteriaBuilder();
		MetamodelImpl mm = (MetamodelImpl) em.getMetamodel();

        CriteriaQuery<Customer> cquery = cb.createQuery(Customer.class);
		Root<Customer> customer = cquery.from(Customer.class);
		EntityType<Customer> Customer_ = customer.getModel();

		cquery.select(
				cb.construct(
						Customer.class,
						customer.get(Customer_.getSingularAttribute("id", String.class)),
						customer.get(Customer_.getSingularAttribute("name", String.class))
				)
		);
		TypedQuery<Customer> tq = em.createQuery(cquery);
		tq.getResultList();

		em.getTransaction().commit();
		em.close();
	}

	public void testDateTimeFunctions() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		CriteriaBuilderImpl cb = (CriteriaBuilderImpl) em.getCriteriaBuilder();
		MetamodelImpl mm = (MetamodelImpl) em.getMetamodel();

        CriteriaQuery<java.sql.Date> dateQuery = cb.createQuery(java.sql.Date.class);
		dateQuery.from( Customer.class );
		dateQuery.select( cb.currentDate() );
		em.createQuery( dateQuery ).getResultList();

        CriteriaQuery<java.sql.Time> timeQuery = cb.createQuery(java.sql.Time.class);
		timeQuery.from( Customer.class );
		timeQuery.select( cb.currentTime() );
		em.createQuery( timeQuery ).getResultList();

        CriteriaQuery<java.sql.Timestamp> tsQuery = cb.createQuery(java.sql.Timestamp.class);
		tsQuery.from( Customer.class );
		tsQuery.select( cb.currentTimestamp() );
		em.createQuery( tsQuery ).getResultList();

		em.getTransaction().commit();
		em.close();
	}
}
