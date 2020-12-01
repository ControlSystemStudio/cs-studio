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
package org.hibernate.criterion;

import org.hibernate.type.Type;

/**
 * The <tt>criterion</tt> package may be used by applications as a framework for building
 * new kinds of <tt>Projection</tt>. However, it is intended that most applications will
 * simply use the built-in projection types via the static factory methods of this class.<br/>
 * <br/>
 * The factory methods that take an alias allow the projected value to be referred to by 
 * criterion and order instances.
 *
 * @see org.hibernate.Criteria
 * @see Restrictions factory methods for <tt>Criterion</tt> instances
 * @author Gavin King
 */
public final class Projections {

	private Projections() {
		//cannot be instantiated
	}
	
	/**
	 * Create a distinct projection from a projection
	 */
	public static Projection distinct(Projection proj) {
		return new Distinct(proj);
	}
	
	/**
	 * Create a new projection list
	 */
	public static ProjectionList projectionList() {
		return new ProjectionList();
	}
		
	/**
	 * The query row count, ie. <tt>count(*)</tt>
	 */
	public static Projection rowCount() {
		return new RowCountProjection();
	}
	
	/**
	 * A property value count
	 */
	public static CountProjection count(String propertyName) {
		return new CountProjection(propertyName);
	}
	
	/**
	 * A distinct property value count
	 */
	public static CountProjection countDistinct(String propertyName) {
		return new CountProjection(propertyName).setDistinct();
	}
	
	/**
	 * A property maximum value
	 */
	public static AggregateProjection max(String propertyName) {
		return new AggregateProjection("max", propertyName);
	}
	
	/**
	 * A property minimum value
	 */
	public static AggregateProjection min(String propertyName) {
		return new AggregateProjection("min", propertyName);
	}
	
	/**
	 * A property average value
	 */
	public static AggregateProjection avg(String propertyName) {
		return new AvgProjection(propertyName);
	}
	
	/**
	 * A property value sum
	 */
	public static AggregateProjection sum(String propertyName) {
		return new AggregateProjection("sum", propertyName);
	}
	
	/**
	 * A SQL projection, a typed select clause fragment
	 */
	public static Projection sqlProjection(String sql, String[] columnAliases, Type[] types) {
		return new SQLProjection(sql, columnAliases, types);
	}
	
	/**
	 * A grouping SQL projection, specifying both select clause and group by clause fragments
	 */
	public static Projection sqlGroupProjection(String sql, String groupBy, String[] columnAliases, Type[] types) {
		return new SQLProjection(sql, groupBy, columnAliases, types);
	}

	/**
	 * A grouping property value
	 */
	public static PropertyProjection groupProperty(String propertyName) {
		return new PropertyProjection(propertyName, true);
	}
	
	/**
	 * A projected property value
	 */
	public static PropertyProjection property(String propertyName) {
		return new PropertyProjection(propertyName);
	}
	
	/**
	 * A projected identifier value
	 */
	public static IdentifierProjection id() {
		return new IdentifierProjection();
	}
	
	/**
	 * Assign an alias to a projection, by wrapping it
	 */
	public static Projection alias(Projection projection, String alias) {
		return new AliasedProjection(projection, alias);
	}
}
