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
package org.hibernate.test.sql.hand.quotedidentifiers;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.junit.functional.DatabaseSpecificFunctionalTestCase;

/**
 * Test of various situations with native-sql queries and quoted identifiers
 *
 * @author Steve Ebersole
 */
public class NativeSqlAndQuotedIdentifiersTest extends DatabaseSpecificFunctionalTestCase {
	public NativeSqlAndQuotedIdentifiersTest(String string) {
		super( string );
	}

	public String[] getMappings() {
		return new String[] { "sql/hand/quotedidentifiers/Mappings.hbm.xml" };
	}

	@Override
	public boolean appliesTo(Dialect dialect) {
		return '\"' == dialect.openQuote();
	}

	@Override
	protected void prepareTest() throws Exception {
		if(sfi() == null) return;
		Session session = sfi().openSession();
		session.beginTransaction();
		session.save( new Person( "me" ) );
		session.getTransaction().commit();
		session.close();
	}

	@Override
	protected void cleanupTest() throws Exception {
		if(sfi() == null) return;
		Session session = sfi().openSession();
		session.beginTransaction();
		session.createQuery( "delete Person" ).executeUpdate();
		session.getTransaction().commit();
		session.close();
	}

	public void testCompleteScalarDiscovery() {
		Session session = openSession();
		session.beginTransaction();
		session.getNamedQuery( "query-person" ).list();
		session.getTransaction().commit();
		session.close();
	}

	public void testPartialScalarDiscovery() {
		Session session = openSession();
		session.beginTransaction();
		SQLQuery query = (SQLQuery) session.getNamedQuery( "query-person" );
		query.setResultSetMapping( "person-scalar" );
		query.list();
		session.getTransaction().commit();
		session.close();
	}

	public void testBasicEntityMapping() {
		Session session = openSession();
		session.beginTransaction();
		SQLQuery query = (SQLQuery) session.getNamedQuery( "query-person" );
		query.setResultSetMapping( "person-entity-basic" );
		query.list();
		session.getTransaction().commit();
		session.close();
	}

	public void testExpandedEntityMapping() {
		Session session = openSession();
		session.beginTransaction();
		SQLQuery query = (SQLQuery) session.getNamedQuery( "query-person" );
		query.setResultSetMapping( "person-entity-expanded" );
		query.list();
		session.getTransaction().commit();
		session.close();
	}
}
