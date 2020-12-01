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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;

/**
 * <tt>date</tt>: A type that maps an SQL DATE to a Java Date.
 * @author Gavin King
 */
public class DateType extends MutableType implements IdentifierType, LiteralType {

	private static final String DATE_FORMAT = "dd MMMM yyyy";

	public Object get(ResultSet rs, String name) throws SQLException {
		return rs.getDate(name);
	}

	public Class getReturnedClass() {
		return java.util.Date.class;
	}

	public void set(PreparedStatement st, Object value, int index) throws SQLException {

		Date sqlDate;
		if ( value instanceof Date) {
			sqlDate = (Date) value;
		}
		else {
			sqlDate = new Date( ( (java.util.Date) value ).getTime() );
		}
		st.setDate(index, sqlDate);
	}

	public int sqlType() {
		return Types.DATE;
	}

	public boolean isEqual(Object x, Object y) {

		if (x==y) return true;
		if (x==null || y==null) return false;

		java.util.Date xdate = (java.util.Date) x;
		java.util.Date ydate = (java.util.Date) y;
		
		if ( xdate.getTime()==ydate.getTime() ) return true;
		
		Calendar calendar1 = java.util.Calendar.getInstance();
		Calendar calendar2 = java.util.Calendar.getInstance();
		calendar1.setTime( xdate );
		calendar2.setTime( ydate );

		return Hibernate.CALENDAR_DATE.isEqual(calendar1, calendar2);
	}

	public int getHashCode(Object x, EntityMode entityMode) {
		Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime( (java.util.Date) x );
		return Hibernate.CALENDAR_DATE.getHashCode(calendar, entityMode);
	}
	
	public String getName() { return "date"; }

	public String toString(Object val) {
		return new SimpleDateFormat(DATE_FORMAT).format( (java.util.Date) val );
	}

	public Object deepCopyNotNull(Object value) {
		return new Date( ( (java.util.Date) value ).getTime() );
	}

	public Object stringToObject(String xml) throws Exception {
		return DateFormat.getDateInstance().parse(xml);
	}

	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
		return '\'' + new Date( ( (java.util.Date) value ).getTime() ).toString() + '\'';
	}

	public Object fromStringValue(String xml) throws HibernateException {
		try {
			return new SimpleDateFormat(DATE_FORMAT).parse(xml);
		}
		catch (ParseException pe) {
			throw new HibernateException("could not parse XML", pe);
		}
	}
	
}





