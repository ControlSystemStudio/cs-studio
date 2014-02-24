//$Id: DoubleStringType.java 4599 2004-09-26 05:18:27Z oneovthafew $
package org.hibernate.test.legacy;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

public class DoubleStringType implements CompositeUserType {

	private static final int[] TYPES = { Types.VARCHAR, Types.VARCHAR };

	public int[] sqlTypes() {
		return TYPES;
	}

	public Class returnedClass() {
		return String[].class;
	}

	public boolean equals(Object x, Object y) {
		if (x==y) return true;
		if (x==null || y==null) return false;
		return ( (String[]) x )[0].equals( ( (String[]) y )[0] ) && ( (String[]) x )[1].equals( ( (String[]) y )[1] );
	}

	public int hashCode(Object x) throws HibernateException {
		String[] a = (String[]) x;
		return a[0].hashCode() + 31 * a[1].hashCode(); 
	}

	public Object deepCopy(Object x) {
		if (x==null) return null;
		String[] result = new String[2];
		String[] input = (String[]) x;
		result[0] = input[0];
		result[1] = input[1];
		return result;
	}

	public boolean isMutable() { return true; }

	public Object nullSafeGet(ResultSet rs,	String[] names, SessionImplementor session,	Object owner)
	throws HibernateException, SQLException {

		String first = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
		String second = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);

		return ( first==null && second==null ) ? null : new String[] { first, second };
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
	throws HibernateException, SQLException {

		String[] strings = (value==null) ? new String[2] : (String[]) value;

		Hibernate.STRING.nullSafeSet(st, strings[0], index);
		Hibernate.STRING.nullSafeSet(st, strings[1], index+1);
	}

	public String[] getPropertyNames() {
		return new String[] { "s1", "s2" };
	}

	public Type[] getPropertyTypes() {
		return new Type[] { Hibernate.STRING, Hibernate.STRING };
	}

	public Object getPropertyValue(Object component, int property) {
		return ( (String[]) component )[property];
	}

	public void setPropertyValue(
		Object component,
		int property,
		Object value) {

		( (String[]) component )[property] = (String) value;
	}

	public Object assemble(
		Serializable cached,
		SessionImplementor session,
		Object owner) {

		return deepCopy(cached);
	}

	public Serializable disassemble(Object value, SessionImplementor session) {
		return (Serializable) deepCopy(value);
	}
	
	public Object replace(Object original, Object target, SessionImplementor session, Object owner) 
	throws HibernateException {
		return original;
	}

}







