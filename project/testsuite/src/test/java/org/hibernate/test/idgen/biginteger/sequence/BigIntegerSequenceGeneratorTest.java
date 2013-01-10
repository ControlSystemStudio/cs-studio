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
package org.hibernate.test.idgen.biginteger.sequence;

import junit.framework.Test;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.junit.functional.DatabaseSpecificFunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;

/**
 * {@inheritDoc}
 *
 * @author Steve Ebersole
 */
public class BigIntegerSequenceGeneratorTest extends DatabaseSpecificFunctionalTestCase {
	public BigIntegerSequenceGeneratorTest(String string) {
		super( string );
	}

	public String[] getMappings() {
		return new String[] { "idgen/biginteger/sequence/Mapping.hbm.xml" };
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( BigIntegerSequenceGeneratorTest.class );
	}

	@Override
	public boolean appliesTo(Dialect dialect) {
		return dialect.supportsSequences();
	}

	public void testBasics() {
		Session s = openSession();
		s.beginTransaction();
		Entity entity = new Entity( "BigInteger + sequence #1" );
		s.save( entity );
		Entity entity2 = new Entity( "BigInteger + sequence #2" );
		s.save( entity2 );
		s.getTransaction().commit();
		s.close();

// hsqldb defines different behavior for the initial select from a sequence
// then say oracle
//		assertEquals( BigInteger.valueOf( 1 ), entity.getId() );
//		assertEquals( BigInteger.valueOf( 2 ), entity2.getId() );

		s = openSession();
		s.beginTransaction();
		s.delete( entity );
		s.delete( entity2 );
		s.getTransaction().commit();
		s.close();

	}
}