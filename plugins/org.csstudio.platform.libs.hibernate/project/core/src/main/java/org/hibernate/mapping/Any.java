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

package org.hibernate.mapping;

import java.util.Map;

import org.hibernate.MappingException;
import org.hibernate.type.MetaType;
import org.hibernate.type.AnyType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;

/**
 * A Hibernate "any" type (ie. polymorphic association to
 * one-of-several tables).
 * @author Gavin King
 */
public class Any extends SimpleValue {

	private String identifierTypeName;
	private String metaTypeName = "string";
	private Map metaValues;

	public Any(Table table) {
		super(table);
	}

	public String getIdentifierType() {
		return identifierTypeName;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierTypeName = identifierType;
	}

	public Type getType() throws MappingException {
		return new AnyType(
			metaValues==null ?
				TypeFactory.heuristicType(metaTypeName) :
				new MetaType( metaValues, TypeFactory.heuristicType(metaTypeName) ),
				TypeFactory.heuristicType(identifierTypeName)
		);
	}

	public void setTypeByReflection(String propertyClass, String propertyName) {}

	public String getMetaType() {
		return metaTypeName;
	}

	public void setMetaType(String type) {
		metaTypeName = type;
	}

	public Map getMetaValues() {
		return metaValues;
	}

	public void setMetaValues(Map metaValues) {
		this.metaValues = metaValues;
	}

	public void setTypeUsingReflection(String className, String propertyName)
		throws MappingException {
	}

	public Object accept(ValueVisitor visitor) {
		return visitor.accept(this);
	}
}
