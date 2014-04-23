/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.data;

import java.util.ArrayList;
import java.util.List;

public class RecordType {

	private final String name;
	private List<Field> fields;
	private List<Include> includes;

	public RecordType(String name) {
		this.name = name;
		this.fields = new ArrayList<Field>();
		this.includes = new ArrayList<Include>();
	}

	public void addField(Field f) {
		this.fields.add(f);
	}

	public void addInclude(Include inc) {
		this.includes.add(inc);
	}

	public String getName() {
		return name;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Include> getIncludes() {
		return includes;
	}

	public void setIncludes(List<Include> includes) {
		this.includes = includes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordType other = (RecordType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecordType [name=" + name + ", fields=" + fields
				+ ", includes=" + includes + "]";
	}

}
