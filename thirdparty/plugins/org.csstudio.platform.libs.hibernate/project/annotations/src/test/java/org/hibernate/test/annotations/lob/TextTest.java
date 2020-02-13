//$Id: TextTest.java 18638 2010-01-26 20:11:51Z steve.ebersole@jboss.com $
/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
package org.hibernate.test.annotations.lob;

import junit.framework.AssertionFailedError;

import org.hibernate.Session;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.Sybase11Dialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.junit.RequiresDialect;
import org.hibernate.test.annotations.TestCase;
import org.hibernate.util.ArrayHelper;

/**
 * Tests eager materialization and mutation of long strings.
 * 
 * @author Steve Ebersole
 */
@RequiresDialect({SybaseASE15Dialect.class,SQLServerDialect.class,SybaseDialect.class,Sybase11Dialect.class})
public class TextTest extends TestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] { LongStringHolder.class };
	}

	private static final int LONG_STRING_SIZE = 10000;

	public void testBoundedLongStringAccess() {
		String original = buildRecursively(LONG_STRING_SIZE, 'x');
		String changed = buildRecursively(LONG_STRING_SIZE, 'y');

		Session s = openSession();
		s.beginTransaction();
		LongStringHolder entity = new LongStringHolder();
		s.save(entity);
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		entity = (LongStringHolder) s.get(LongStringHolder.class, entity
				.getId());
		assertNull(entity.getLongString());
		assertNull(entity.getName());
		assertNull(entity.getWhatEver());
		entity.setLongString(original);
		entity.setName(original.toCharArray());
		entity.setWhatEver(wrapPrimitive(original.toCharArray()));
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		entity = (LongStringHolder) s.get(LongStringHolder.class, entity
				.getId());
		assertEquals(LONG_STRING_SIZE, entity.getLongString().length());
		assertEquals(original, entity.getLongString());
		assertNotNull(entity.getName());
		assertEquals(LONG_STRING_SIZE, entity.getName().length);
		assertEquals(original.toCharArray(), entity.getName());
		assertNotNull(entity.getWhatEver());
		assertEquals(LONG_STRING_SIZE, entity.getWhatEver().length);
		assertEquals(original.toCharArray(), unwrapNonPrimitive(entity.getWhatEver()));
		entity.setLongString(changed);
		entity.setName(changed.toCharArray());
		entity.setWhatEver(wrapPrimitive(changed.toCharArray()));
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		entity = (LongStringHolder) s.get(LongStringHolder.class, entity
				.getId());
		assertEquals(LONG_STRING_SIZE, entity.getLongString().length());
		assertEquals(changed, entity.getLongString());
		assertNotNull(entity.getName());
		assertEquals(LONG_STRING_SIZE, entity.getName().length);
		assertEquals(changed.toCharArray(), entity.getName());
		assertNotNull(entity.getWhatEver());
		assertEquals(LONG_STRING_SIZE, entity.getWhatEver().length);
		assertEquals(changed.toCharArray(), unwrapNonPrimitive(entity.getWhatEver()));
		entity.setLongString(null);
		entity.setName(null);
		entity.setWhatEver(null);
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		entity = (LongStringHolder) s.get(LongStringHolder.class, entity
				.getId());
		assertNull(entity.getLongString());
		assertNull(entity.getName());
		assertNull(entity.getWhatEver());
		s.delete(entity);
		s.getTransaction().commit();
		s.close();
	}

	public static void assertEquals(char[] val1, char[] val2) {
		if (!ArrayHelper.isEquals(val1, val2)) {
			throw new AssertionFailedError("byte arrays did not match");
		}
	}

	private String buildRecursively(int size, char baseChar) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < size; i++) {
			buff.append(baseChar);
		}
		return buff.toString();
	}

	private Character[] wrapPrimitive(char[] bytes) {
		int length = bytes.length;
		Character[] result = new Character[length];
		for (int index = 0; index < length; index++) {
			result[index] = Character.valueOf(bytes[index]);
		}
		return result;
	}

	private char[] unwrapNonPrimitive(Character[] bytes) {
		int length = bytes.length;
		char[] result = new char[length];
		for (int i = 0; i < length; i++) {
			result[i] = bytes[i].charValue();
		}
		return result;
	}

	@Override
	protected String[] getAnnotatedPackages() {
		return new String[] { "org.hibernate.test.annotations.lob" };
	}

}
