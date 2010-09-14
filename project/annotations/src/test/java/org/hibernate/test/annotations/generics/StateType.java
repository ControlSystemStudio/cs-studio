//$Id: StateType.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.generics;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;

import org.hibernate.usertype.UserType;
import org.hibernate.HibernateException;

/**
 * @author Emmanuel Bernard
 */
public class StateType implements UserType {
	public int[] sqlTypes() {
		return new int[] {
			Types.INTEGER
		};
	}

	public Class returnedClass() {
		return State.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return x == y;
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		int result = rs.getInt( names[0] );
		if ( rs.wasNull() ) return null;
		return State.values()[result];
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull( index, Types.INTEGER );
		}
		else {
			st.setInt( index, ( (State) value ).ordinal() );
		}
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}
}
